package org.smallbox.faraway.modules.consumable;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.CharacterTalentExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.job.JobTaskReturn;
import org.smallbox.faraway.util.CollectionUtils;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Job déplacant les consomables vers les zones de stockage
 */
public class BasicHaulJobToParcel extends BasicHaulJob {

    private Collection<ConsumableItem> _targetConsumables;
    private int _haulingQuantity;
    private ConsumableModule _consumableModule;
    protected Collection<ConsumableModule.ConsumableJobLock> _locks = new ConcurrentLinkedQueue<>();

    /**
     * Apporte les composants sur la parcel
     *
     * @param consumableModule ConsumableModule
     * @param jobModule
     * @param itemInfo ItemInfo
     * @param targetConsumables Map<ConsumableItem, Integer>
     * @param targetParcel ParcelModel
     * @param haulingQuantity int     @return BasicHaulJob
     */
    public static void toParcel(ConsumableModule consumableModule, JobModule jobModule, ItemInfo itemInfo, Collection<ConsumableItem> targetConsumables, ParcelModel targetParcel, int haulingQuantity) {

        if (CollectionUtils.isEmpty(targetConsumables)) {
            throw new RuntimeException("Collection cannot be empty");
        }

        jobModule.createJob(BasicHaulJobToParcel.class, null, targetParcel, job -> {

            job.init(itemInfo, haulingQuantity, targetParcel, targetConsumables, consumableModule);
            job.setMainLabel(String.format("Haul %s (x%d) to storage", itemInfo.label, haulingQuantity));


            return true;
        });

    }

    /**
     * Ajout des locks pour chaques consomables
     * Le lock des consomables se fait au lancement du job et non à la création afin de ne pas bloquer les autres jobs (par ex craft)
     *
     * @return true
     */
    @Override
    public boolean onNewStart() {

        for (ConsumableItem consumable: _targetConsumables) {

            ConsumableModule.ConsumableJobLock lock = _consumableModule.lock(this, consumable, consumable.getFreeQuantity());

            // Certains composants n'ont pas pu être réservés (par ex un craft job lancé entre temps à déjà réservé les composants)
            if (lock == null) {
                return false;
            }

            // Ajoute le lock au job
            _locks.add(lock);

        }

        // Déplace le personnage vers chaque consomable et l'ajoute à son inventaire
        _locks.forEach(lock -> {

            // Déplace le personnage à l'emplacement des composants
            addTask("Haul " + lock.consumable.getLabel(), character -> {
                if (lock.consumable.getParcel() != null) {
                    return character.moveTo(lock.consumable.getParcel()) ? JobTaskReturn.COMPLETE : JobTaskReturn.CONTINUE;
                }
                return JobTaskReturn.INVALID;
            });

            // Ajoute les composants à l'inventaire du personnage
            addTask("Add " + lock.consumable.getLabel() + " to inventory", character -> {
                _consumableModule.takeConsumable(lock);
                character.addInventory(lock.consumable.getInfo(), lock.quantity);
                return JobTaskReturn.COMPLETE;
            });

        });

        // Apporte les composants à la zone de stockage
        addTask("Move to storage", character2 ->
                character2.moveTo(_targetParcel) ? JobTaskReturn.COMPLETE : JobTaskReturn.CONTINUE);

        // Ajoute les composants à la zone de stockage
        addTechnicalTask("Drop consumable to storage", character2 ->
                _locks.forEach(lock -> {
                    ConsumableItem consumable = character2.takeInventory(lock.consumable.getInfo(), lock.quantity);
                    _consumableModule.addConsumable(consumable.getInfo(), consumable.getFreeQuantity(), _targetParcel);
                }));

        return true;
    }

    public BasicHaulJobToParcel(ItemInfo.ItemInfoAction itemInfoAction, ParcelModel parcelModel) {
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
        return CharacterTalentExtra.TalentType.GATHER;
    }

    protected void init(ItemInfo itemInfo, int haulingQuantity, ParcelModel targetParcel, Collection<ConsumableItem> targetConsumables, ConsumableModule consumableModule) {
        _consumableModule = consumableModule;
        _targetParcel = targetParcel;
        _consumableInfo = itemInfo;
        _haulingQuantity = haulingQuantity;
        _targetConsumables = targetConsumables;
    }

    public Collection<ConsumableItem> getConsumables() {
        return _targetConsumables;
    }

}
