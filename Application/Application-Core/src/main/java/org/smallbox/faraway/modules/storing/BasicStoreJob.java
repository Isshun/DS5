package org.smallbox.faraway.modules.storing;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.CharacterInventoryExtra;
import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.consumable.StorageArea;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.job.JobTaskReturn;
import org.smallbox.faraway.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;

/**
 * Job storing consomables to storage area
 */
public class BasicStoreJob extends JobModel {

    /**
     * Consumables to store
     */
    protected Collection<ConsumableItem> _targetConsumables;

    /**
     * Consumable module
     */
    private ConsumableModule _consumableModule;

    /**
     * True if all consumables has been locked succefully (set during onFirstStart)
     */
    private boolean _isAllConsumablesLocked;

    private StorageArea _storageArea;

    public Collection<ConsumableItem> getConsumables() { return _targetConsumables; }

    /**
     * Apporte les composants sur la parcel
     *
     * @param consumableModule Consumable module
     * @param jobModule Job module
     * @param targetConsumable consomable à déplacer
     * @param storingParcel parcel ou stocker le consomable
     */
    public static void toParcel(ConsumableModule consumableModule, JobModule jobModule, ConsumableItem targetConsumable, ParcelModel storingParcel, StorageArea storageArea) {
        toParcel(consumableModule, jobModule, Collections.singleton(targetConsumable), storingParcel, storageArea);
    }

    /**
     * Apporte les composants sur la parcel
     *
     * @param consumableModule Consumable module
     * @param jobModule Job module
     * @param targetConsumables consomables à déplacer
     * @param storingParcel parcel ou stocker les consomables
     */
    public static void toParcel(ConsumableModule consumableModule, JobModule jobModule, Collection<ConsumableItem> targetConsumables, ParcelModel storingParcel, StorageArea storageArea) {

        if (CollectionUtils.isEmpty(targetConsumables)) {
            throw new RuntimeException("Collection cannot be empty");
        }

        jobModule.createJob(BasicStoreJob.class, null, storingParcel, job -> {

            job._consumableModule = consumableModule;
            job._targetParcel = storingParcel;
            job._storageArea = storageArea;
            job._startParcel = targetConsumables.stream().findFirst().get().getParcel();
            job._targetConsumables = targetConsumables;

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
        for (ConsumableItem consumable: _targetConsumables) {
            if (_consumableModule.lock(this, consumable, consumable.getFreeQuantity()) == null) {

                // Si certains composants n'ont pas pu être réservés annule les locks et le job
                _consumableModule.cancelLock(this);
                return false;
            }
        }

        _isAllConsumablesLocked = true;

        // Déplace le personnage vers chaque consomable et l'ajoute à son inventaire
        _targetConsumables.forEach(targetConsumable -> {

            // Déplace le personnage à l'emplacement des composants
            addTask("Haul " + targetConsumable.getLabel(), (character, hourInterval) -> {
                if (targetConsumable.getParcel() != null) {
                    return character.moveTo(targetConsumable.getParcel()) ? JobTaskReturn.TASK_COMPLETE : JobTaskReturn.TASK_CONTINUE;
                }
                return JobTaskReturn.TASK_ERROR;
            });

            // Ajoute les composants à l'inventaire du personnage
            addTask("Add " + targetConsumable.getLabel() + " to inventory", (character, hourInterval) -> {
                // Update lock to take all consumables
                _targetConsumables.forEach(consumable -> _consumableModule.addToLock(this, consumable, consumable.getFreeQuantity()));

                ConsumableItem inventoryConsumable = _consumableModule.createConsumableFromLock(this, targetConsumable);
                character.getExtra(CharacterInventoryExtra.class).addInventory(inventoryConsumable.getInfo(), inventoryConsumable.getTotalQuantity());
                return JobTaskReturn.TASK_COMPLETE;
            });

        });

        // Apporte les composants à la zone de stockage
        addMoveTask("Move to storage", _targetParcel);

        // Ajoute les composants à la zone de stockage
        addTechnicalTask("Drop consumable to storage", character ->
                _targetConsumables.forEach(targetConsumable -> {
                    ConsumableItem consumable = character.getExtra(CharacterInventoryExtra.class).takeInventory(targetConsumable.getInfo());
                    _consumableModule.addConsumable(consumable.getInfo(), consumable.getFreeQuantity(), _targetParcel);
                })
        );

        return true;
    }

    @Override
    protected void onUpdate() {
        StringBuilder sb = new StringBuilder();
        _targetConsumables.forEach(consumable ->
                sb.append(String.format("Store %s to %s", consumable.getInfo().label, _storageArea.getName()))
        );
        setMainLabel(sb.toString());
    }

    public BasicStoreJob(ItemInfo.ItemInfoAction itemInfoAction, ParcelModel parcelModel) {
        super(itemInfoAction, parcelModel);
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
        if (!character.hasExtra(CharacterSkillExtra.class) || !character.getExtra(CharacterSkillExtra.class).hasSkill(CharacterSkillExtra.SkillType.STORE)) {
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
    public void onClose() {
        _consumableModule.cancelLock(this);

        if (_character != null) {
            _character.getExtra(CharacterInventoryExtra.class).getAll().forEach((itemInfo, quantity) -> _consumableModule.addConsumable(itemInfo, quantity, _character.getParcel()));
            _character.getExtra(CharacterInventoryExtra.class).clear();
        }
    }

    public boolean haveConsumable(ConsumableItem consumable) {
        return _targetConsumables.contains(consumable);
    }

    @Override
    public CharacterSkillExtra.SkillType getSkillType() {
        return CharacterSkillExtra.SkillType.STORE;
    }

    protected boolean onCheck() {
        return true;
    }

}
