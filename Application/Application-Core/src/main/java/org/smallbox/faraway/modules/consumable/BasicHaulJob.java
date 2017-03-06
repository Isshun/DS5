package org.smallbox.faraway.modules.consumable;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.CharacterTalentExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.hauling.HaulingModule;
import org.smallbox.faraway.modules.item.factory.ItemFactoryModel;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.job.JobTaskReturn;
import org.smallbox.faraway.util.CollectionUtils;
import org.smallbox.faraway.util.Log;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Alex on 09/12/2016.
 */
public abstract class BasicHaulJob extends JobModel {

    private ItemInfo _consumableInfo;
    private Map<ConsumableItem, Integer> _targetConsumables;
    private int _haulingQuantity;
    protected ItemFactoryModel _factory;
    protected Collection<ConsumableModule.ConsumableJobLock> _locks = new ConcurrentLinkedQueue<>();
    private ConsumableModule _consumableModule;

    public int getHaulingQuantity() { return _haulingQuantity; }
    public ItemInfo getConsumableInfo() { return _consumableInfo; }
    public Map<ConsumableItem, Integer> getConsumables() { return _targetConsumables; }

    protected void setMainLabel(String mainLabel) {
        _mainLabel = mainLabel;
    }

    protected boolean create(ConsumableModule consumableModule, JobModule jobModule, ItemInfo itemInfo, Map<ConsumableItem, Integer> targetConsumables, int haulingQuantity, ParcelModel targetParcel) {
        if (CollectionUtils.isEmpty(targetConsumables)) {
            throw new RuntimeException("Collection cannot be empty");
        }

        _consumableModule = consumableModule;

        for (Map.Entry<ConsumableItem, Integer> entry: targetConsumables.entrySet()) {
            ConsumableItem consumable = entry.getKey();
            int quantity = entry.getValue();

            Log.warning(HaulingModule.class, "lock for consumable: " + consumable + " -> " + consumableModule.getLocks());

            ConsumableModule.ConsumableJobLock lock = consumableModule.lock(this, consumable, quantity);
            if (lock == null) {
                Log.warning(BasicHaulJob.class, "Certains composants n'ont pas pu être réservés");
                return false;
            }

            // Ajoute le lock au job
            _locks.add(lock);

            // Déplace le personnage à l'emplacement des composants
            this.addTask("Haul " + consumable.getLabel(), character -> {
                if (consumable.getParcel() != null) {
                    return character.moveTo(consumable.getParcel()) ? JobTaskReturn.COMPLETE : JobTaskReturn.CONTINUE;
                }
                return JobTaskReturn.INVALID;
            });

            // Ajoute les composants à l'inventaire du personnage
            this.addTask("Add " + consumable.getLabel() + " to inventory", character -> {
                ConsumableItem newConsumable = consumableModule.takeConsumable(lock);
                if (newConsumable.getFreeQuantity() != quantity) {
                    return JobTaskReturn.INVALID;
                }

                character.addInventory(newConsumable.getInfo(), newConsumable.getFreeQuantity());
                return JobTaskReturn.COMPLETE;
            });

        }

        return true;
    }

    protected void init(ItemInfo itemInfo, int haulingQuantity, ParcelModel targetParcel, Map<ConsumableItem, Integer> targetConsumables) {
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
    protected void onClose() {
        _locks.forEach(lock -> _consumableModule.cancelLock(lock));
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
