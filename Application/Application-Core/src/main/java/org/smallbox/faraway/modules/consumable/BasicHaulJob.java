package org.smallbox.faraway.modules.consumable;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.item.UsableItem;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.job.JobTaskReturn;
import org.smallbox.faraway.util.Log;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Alex on 09/12/2016.
 */
public class BasicHaulJob extends JobModel {

    private ConsumableModule _consumableModule;
    private MapObjectModel _item;
    protected Map<ConsumableItem, Integer> _targetConsumables;
    protected Collection<ConsumableModule.ConsumableJobLock> _locks = new ConcurrentLinkedQueue<>();

    public Map<ConsumableItem, Integer> getConsumables() { return _targetConsumables; }

    public int getHaulingQuantity() { return _targetConsumables.values().stream().mapToInt(Integer::intValue).sum(); }

    /**
     * Apporte les composants à la factory
     *
     * @param consumableModule ConsumableModule
     * @param targetConsumables Map<ConsumableItem, Integer>
     * @param item UsableItem
     * @return BasicHaulJob
     */
    public static BasicHaulJob toFactory(ConsumableModule consumableModule, JobModule jobModule, Map<ConsumableItem, Integer> targetConsumables, MapObjectModel item) {

        return jobModule.createJob(BasicHaulJob.class, null, item.getParcel(), job -> {
            job.initHaul(item.getParcel(), targetConsumables, item, consumableModule);
            job._startParcel = targetConsumables.keySet().stream().findFirst().get().getParcel();

            targetConsumables.forEach((consumable, quantity) ->
                    job.setMainLabel(String.format("Haul %s to %s", consumable.getInfo().label, item.getInfo().label)));

            return true;
        });

    }

    @Override
    public boolean onNewInit() {

        // Ajout des locks pour chaques consomables
        for (Map.Entry<ConsumableItem, Integer> entry: _targetConsumables.entrySet()) {
            ConsumableItem consumable = entry.getKey();
            int quantity = entry.getValue();

            Log.info(BasicHaulJob.class, "lock for consumable: " + consumable + " -> " + _consumableModule.getLocks());

            ConsumableModule.ConsumableJobLock lock = _consumableModule.lock(this, consumable, quantity);
            if (lock == null) {
                Log.warning(BasicHaulJob.class, "Certains composants n'ont pas pu être réservés");
                return false;
            }

            // Déplace le personnage à l'emplacement des composants
            addTask("Haul " + lock.consumable.getLabel(), (character, hourInterval) -> {
                if (lock.consumable.getParcel() != null) {
                    return character.moveTo(lock.consumable.getParcel()) ? JobTaskReturn.TASK_COMPLETE : JobTaskReturn.TASK_CONTINUE;
                }
                return JobTaskReturn.TASK_ERROR;
            });

            // Ajoute les composants à l'inventaire du personnage
            addTask("Add " + lock.consumable.getLabel() + " to inventory", (character, hourInterval) -> {
                _consumableModule.takeConsumable(lock);
                character.addInventory(lock.consumable.getInfo(), lock.quantity);
                return JobTaskReturn.TASK_COMPLETE;
            });
        }

        // Apporte les composants à la fabrique
        addTask("Bring back to factory", (character, hourInterval) ->
                character.moveTo(_targetParcel) ? JobTaskReturn.TASK_COMPLETE : JobTaskReturn.TASK_CONTINUE);

        // Charge les comnposants dans la fabrique
        addTechnicalTask("Load factory", character ->
                _targetConsumables.forEach((initialConsumable, quantity) -> {
                    ConsumableItem consumable = character.takeInventory(initialConsumable.getInfo(), quantity);
                    _item.addInventory(consumable);
                }));

        return true;
    }

    public BasicHaulJob(ItemInfo.ItemInfoAction itemInfoAction, ParcelModel parcelModel) {
        super(itemInfoAction, parcelModel);
    }

    public MapObjectModel getItem() {
        return _item;
    }

    public void initHaul(ParcelModel targetParcel, Map<ConsumableItem, Integer> targetConsumables, MapObjectModel item, ConsumableModule consumableModule) {
        _item = item;
        _targetParcel = targetParcel;
        _targetConsumables = targetConsumables;
        _consumableModule = consumableModule;
    }

    @Override
    protected JobCheckReturn onCheck(CharacterModel character) {
        return JobCheckReturn.OK;
    }

    @Override
    public CharacterSkillExtra.SkillType getSkillNeeded() {
        return CharacterSkillExtra.SkillType.BUILD;
    }

    @Override
    public String getLabel() {
        return _label;
    }

    @Override
    protected void onClose() {
        if (_item instanceof UsableItem && ((UsableItem) _item).getFactory() != null) {
            ((UsableItem)_item).getFactory().clear();
        }
        _locks.forEach(lock -> _consumableModule.cancelLock(lock));
    }

}
