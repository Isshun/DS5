package org.smallbox.faraway.modules.consumable;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.CharacterInventoryExtra;
import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.item.UsableItem;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.job.JobTaskReturn;
import org.smallbox.faraway.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
                    job.setMainLabel(String.format("Haul %s", consumable.getInfo().label)));

            return true;
        });

    }

    @Override
    public boolean onNewInit() {

        // Ajout des locks pour chaques consomables
        List<ConsumableModule.ConsumableJobLock> locks = new ArrayList<>();
        for (Map.Entry<ConsumableItem, Integer> entry: _targetConsumables.entrySet()) {
            Log.info(BasicHaulJob.class, "lock for consumable: " + entry.getKey() + " -> " + _consumableModule.getLocks());
            locks.add(_consumableModule.lock(this, entry.getKey(), entry.getValue()));
        }
        if (locks.contains(null)) {
            Log.warning(BasicHaulJob.class, "Certains composants n'ont pas pu être réservés");
            return false;
        }

        // Traite chaque composant
        locks.forEach(lock -> {

            // Déplace le personnage à l'emplacement des composants
            if (lock.consumable.getParcel() != null) {
                addMoveTask("Move to consumable", lock.consumable.getParcel());
            }

            // Ajoute les composants à l'inventaire du personnage
            addTask("Add " + lock.consumable.getLabel() + " to inventory", (character, hourInterval) -> {
                _consumableModule.createConsumableFromLock(lock);
                character.getExtra(CharacterInventoryExtra.class).addInventory(lock.consumable.getInfo(), lock.quantity);
                return JobTaskReturn.TASK_COMPLETE;
            });

        });

        // Apporte les composants à la fabrique
        addMoveTask("Bring back to factory", _targetParcel);

//        // Charge les comnposants dans la fabrique
//        addTechnicalTask("Load factory", character ->
//                _targetConsumables.forEach((initialConsumable, quantity) -> {
//                    ConsumableItem consumable = character.getExtra(CharacterInventoryExtra.class).takeInventory(initialConsumable.getInfo(), quantity);
//                    _item.addInventory(consumable);
//                }));

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
        if (!character.hasExtra(CharacterInventoryExtra.class)) {
            return JobCheckReturn.ABORT;
        }

        return JobCheckReturn.OK;
    }

    @Override
    public boolean checkCharacterAccepted(CharacterModel character) {

        // Character have no skill
        if (!character.hasExtra(CharacterSkillExtra.class) || !character.getExtra(CharacterSkillExtra.class).hasSkill(CharacterSkillExtra.SkillType.CRAFT)) {
            return false;
        }

        // Character have no inventory
        if (!character.hasExtra(CharacterInventoryExtra.class)) {
            return false;
        }

        // Character is qualified for job
        return true;

    }

    @Override
    public CharacterSkillExtra.SkillType getSkillType() {
        return CharacterSkillExtra.SkillType.CRAFT;
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
