package org.smallbox.faraway.modules.storing;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.job.JobTaskReturn;
import org.smallbox.faraway.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Job déplacant les consomables vers les zones de stockage
 */
public class BasicStoreJob extends JobModel {

    protected Map<ConsumableItem, Integer> _targetConsumables;
    protected Collection<ConsumableModule.ConsumableJobLock> _locks = new ConcurrentLinkedQueue<>();
    private ConsumableModule _consumableModule;

    public Map<ConsumableItem, Integer> getConsumables() { return _targetConsumables; }

    /**
     * Apporte les composants sur la parcel
     *
     * @param consumableModule ConsumableModule
     * @param jobModule
     * @param targetConsumables Map<ConsumableItem, Integer>
     * @param targetParcel ParcelModel
     */
    public static void toParcel(ConsumableModule consumableModule, JobModule jobModule, Map<ConsumableItem, Integer> targetConsumables, ParcelModel targetParcel) {

        if (CollectionUtils.isEmpty(targetConsumables)) {
            throw new RuntimeException("Collection cannot be empty");
        }

        jobModule.createJob(BasicStoreJob.class, null, targetParcel, job -> {

            job._consumableModule = consumableModule;
            job._targetParcel = targetParcel;
            job._targetConsumables = targetConsumables;

            targetConsumables.forEach((consumable, quantity) ->
                    job.setMainLabel(String.format("Store %s (x%d)", consumable.getInfo().label, quantity)));

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
    public boolean onFirstStart() {

        // TODO: maj de la quantité

        // Ajoute pour chaque composant un lock au job
        _targetConsumables.forEach((consumable, quantity) -> {
            ConsumableModule.ConsumableJobLock lock = _consumableModule.lock(this, consumable, quantity);
            if (lock != null) {
                _locks.add(lock);
            }
        });

        // Si certains composants n'ont pas pu être réservés annule les locks et le job
        if (_locks.size() != _targetConsumables.size()) {
            _locks.forEach(lock -> _consumableModule.cancelLock(lock));
            return false;
        }

        // Déplace le personnage vers chaque consomable et l'ajoute à son inventaire
        _locks.forEach(lock -> {

            // Déplace le personnage à l'emplacement des composants
            addTask("Haul " + lock.consumable.getLabel(), character -> {
                if (lock.consumable.getParcel() != null) {
                    return character.moveTo(lock.consumable.getParcel()) ? JobTaskReturn.TASK_COMPLETE : JobTaskReturn.TASK_CONTINUE;
                }
                return JobTaskReturn.TASK_ERROR;
            });

            // Ajoute les composants à l'inventaire du personnage
            addTask("Add " + lock.consumable.getLabel() + " to inventory", character -> {
                _consumableModule.takeConsumable(lock);
                character.addInventory(lock.consumable.getInfo(), lock.quantity);
                return JobTaskReturn.TASK_COMPLETE;
            });

        });

        // Apporte les composants à la zone de stockage
        addTask("Move to storage", character2 ->
                character2.moveTo(_targetParcel) ? JobTaskReturn.TASK_COMPLETE : JobTaskReturn.TASK_CONTINUE);

        // Ajoute les composants à la zone de stockage
        addTechnicalTask("Drop consumable to storage", character2 ->
                _locks.forEach(lock -> {
                    ConsumableItem consumable = character2.takeInventory(lock.consumable.getInfo(), lock.quantity);
                    _consumableModule.addConsumable(consumable.getInfo(), consumable.getFreeQuantity(), _targetParcel);
                }));

        return true;
    }

    public BasicStoreJob(ItemInfo.ItemInfoAction itemInfoAction, ParcelModel parcelModel) {
        super(itemInfoAction, parcelModel);
    }

    @Override
    protected JobCheckReturn onCheck(CharacterModel character) {
        return JobCheckReturn.OK;
    }

    @Override
    public CharacterSkillExtra.SkillType getSkillNeeded() {
        return CharacterSkillExtra.SkillType.STORE;
    }

}
