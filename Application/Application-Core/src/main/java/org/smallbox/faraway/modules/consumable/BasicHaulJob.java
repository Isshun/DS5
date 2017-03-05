package org.smallbox.faraway.modules.consumable;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.CharacterTalentExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.hauling.HaulingModule;
import org.smallbox.faraway.modules.item.UsableItem;
import org.smallbox.faraway.modules.item.factory.ItemFactoryModel;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.job.JobTaskReturn;
import org.smallbox.faraway.util.CollectionUtils;
import org.smallbox.faraway.util.Log;

import java.util.Map;

/**
 * Created by Alex on 09/12/2016.
 */
public class BasicHaulJob extends JobModel {

    private ItemInfo _consumableInfo;
    private Map<ConsumableItem, Integer> _targetConsumables;
    private int _haulingQuantity;
    private ItemFactoryModel _factory;

    public int getHaulingQuantity() { return _haulingQuantity; }
    public ItemInfo getConsumableInfo() { return _consumableInfo; }
    public Map<ConsumableItem, Integer> getConsumables() { return _targetConsumables; }

    private interface EndJobStrategy {
        void execute(BasicHaulJob job);
    }

    /**
     * Apporte les composants sur la parcel
     *
     * @param consumableModule ConsumableModule
     * @param jobModule
     *@param itemInfo ItemInfo
     * @param targetConsumables Map<ConsumableItem, Integer>
     * @param targetParcel ParcelModel
     * @param haulingQuantity int     @return BasicHaulJob
     */
    public static BasicHaulJob toParcel(ConsumableModule consumableModule, JobModule jobModule, ItemInfo itemInfo, Map<ConsumableItem, Integer> targetConsumables, ParcelModel targetParcel, int haulingQuantity) {

        Log.debug(BasicHaulJob.class, "ToParcel (item: %s, targetConsumables: %s, targetParcel: %s)", itemInfo.label, targetConsumables, targetParcel);

        return create(consumableModule, jobModule, itemInfo, targetConsumables, haulingQuantity, targetParcel, job -> {

            // Apporte les composants à la fabrique
            job.addTask("Move to storage", character -> character.moveTo(targetParcel) ? JobTaskReturn.COMPLETE : JobTaskReturn.CONTINUE);

            // Charge les comnposants dans la fabrique
            job.addTechnicalTask("Drop consumable to storage", character -> {
                ConsumableItem consumable = character.getInventory(itemInfo, haulingQuantity);
                consumableModule.addConsumable(itemInfo, consumable.getQuantity(), targetParcel);
            });

        });
    }

    /**
     * Apporte les composants à la factory
     *
     * @param consumableModule ConsumableModule
     * @param itemInfo ItemInfo
     * @param targetConsumables Map<ConsumableItem, Integer>
     * @param item UsableItem
     * @param haulingQuantity int
     * @return BasicHaulJob
     */
    public static BasicHaulJob toFactory(ConsumableModule consumableModule, JobModule jobModule, ItemInfo itemInfo, Map<ConsumableItem, Integer> targetConsumables, UsableItem item, int haulingQuantity) {

        return create(consumableModule, jobModule, itemInfo, targetConsumables, haulingQuantity, item.getParcel(), job -> {

            job._factory = item.getFactory();

            // Apporte les composants à la fabrique
            job.addTask("Bring back to factory", character -> character.moveTo(item.getParcel()) ? JobTaskReturn.COMPLETE : JobTaskReturn.CONTINUE);

            // Charge les comnposants dans la fabrique
            job.addTechnicalTask("Load factory", character -> item.addInventory(character.getInventory(itemInfo, item.getFactory().getQuantityNeeded(itemInfo))));

        });
    }

    private static BasicHaulJob create(ConsumableModule consumableModule, JobModule jobModule, ItemInfo itemInfo, Map<ConsumableItem, Integer> targetConsumables, int haulingQuantity, ParcelModel targetParcel, EndJobStrategy endJobStrategy) {
        if (CollectionUtils.isEmpty(targetConsumables)) {
            throw new RuntimeException("Collection cannot be empty");
        }

        BasicHaulJob job = jobModule.createJob(BasicHaulJob.class, null, targetParcel);
        job.init(itemInfo, haulingQuantity, targetParcel, targetConsumables);

        for (Map.Entry<ConsumableItem, Integer> entry: targetConsumables.entrySet()) {
            ConsumableItem consumable = entry.getKey();
            int quantity = entry.getValue();

            Log.warning(HaulingModule.class, "lock for consumable: " + consumable + " -> " + consumableModule.getLocks());

            ConsumableModule.ConsumableJobLock lock = consumableModule.lock(job, consumable, quantity);
            if (lock == null) {
                Log.warning(BasicHaulJob.class, "Certains composants n'ont pas pu être réservés");
                job.abort();
                return null;
            }

            // Déplace le personnage à l'emplacement des composants
            job.addTask("Haul " + consumable.getLabel(), character -> {
                if (consumable.getParcel() != null) {
                    return character.moveTo(consumable.getParcel()) ? JobTaskReturn.COMPLETE : JobTaskReturn.CONTINUE;
                }
                return JobTaskReturn.INVALID;
            });

            // Ajoute les composants à l'inventaire du personnage
            job.addTask("Add " + consumable.getLabel() + " to inventory", character -> {
                ConsumableItem newConsumable = consumableModule.takeConsumable(lock);
                if (newConsumable.getQuantity() != quantity) {
                    return JobTaskReturn.INVALID;
                }

                character.addInventory(newConsumable.getInfo(), newConsumable.getQuantity());
                return JobTaskReturn.COMPLETE;
            });

        }

        endJobStrategy.execute(job);

        job.ready();

        return job;
    }

    private void init(ItemInfo itemInfo, int haulingQuantity, ParcelModel targetParcel, Map<ConsumableItem, Integer> targetConsumables) {
        _mainLabel = "Haul " + itemInfo.label;
        _targetParcel = targetParcel;
        _consumableInfo = itemInfo;
        _haulingQuantity = haulingQuantity;
        _targetConsumables = targetConsumables;
    }


    public BasicHaulJob(ItemInfo.ItemInfoAction itemInfoAction, ParcelModel parcelModel) {
        super(itemInfoAction, parcelModel);
    }

    @Override
    protected JobCheckReturn onCheck(CharacterModel character) {
        return JobCheckReturn.OK;
    }

    @Override
    protected JobActionReturn onAction(CharacterModel character) {
        return null;
    }

    @Override
    public CharacterTalentExtra.TalentType getTalentNeeded() {
        return CharacterTalentExtra.TalentType.BUILD;
    }

    @Override
    protected void onAbort() {
        if (_factory != null) {
            _factory.clear();
        }
    }

    @Override
    public String getLabel() {
        return _label;
    }

    public ItemFactoryModel getFactory() {
        return _factory;
    }
}
