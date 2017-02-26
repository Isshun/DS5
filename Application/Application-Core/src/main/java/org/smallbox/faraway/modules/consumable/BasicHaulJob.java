package org.smallbox.faraway.modules.consumable;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.character.model.CharacterTalentExtra;
import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.item.UsableItem;
import org.smallbox.faraway.modules.job.JobTaskReturn;

/**
 * Created by Alex on 09/12/2016.
 */
public class BasicHaulJob extends JobModel {

    private final ItemInfo _consumableInfo;
    private int _haulingQuantity;
    private ConsumableItem _consumable;

    public int getHaulingQuantity() { return _haulingQuantity; }
    public ConsumableItem getHaulingConsumable() { return _consumable; }
    public ItemInfo getConsumableInfo() { return _consumableInfo; }

    public static BasicHaulJob toFactory(ConsumableModule consumableModule, ConsumableItem targetConsumable, UsableItem item, int haulingQuantity) {
        BasicHaulJob job = new BasicHaulJob(targetConsumable, haulingQuantity, targetConsumable.getParcel());
        ItemInfo itemInfo = targetConsumable.getInfo();

        // Réservation des composants au premier lancement de la tache
        job.addTask("Lock " + targetConsumable.getLabel(), character -> Application.moduleManager.getModule(ConsumableModule.class).lock(job, targetConsumable, haulingQuantity) ? JobTaskReturn.COMPLETE : JobTaskReturn.INVALID);

        // Déplace le personnage à l'emplacement des composants
        job.addTask("Haul " + targetConsumable.getLabel(), character -> character.moveTo(targetConsumable.getParcel()) ? JobTaskReturn.COMPLETE : JobTaskReturn.CONTINUE);

        // Ajoute les composants à l'inventaire du personnage
        job.addTask("Add " + targetConsumable.getLabel() + " to inventory", character -> {
            ConsumableItem newConsumable = consumableModule.removeConsumable(targetConsumable, haulingQuantity);
            character.addInventory(newConsumable);
            return newConsumable.getQuantity() == haulingQuantity ? JobTaskReturn.COMPLETE : JobTaskReturn.INVALID;
        });

        // Dévérouille les composants préalablement réservé
        job.addTask("Unlock " + targetConsumable.getLabel(), character -> Application.moduleManager.getModule(ConsumableModule.class).unlock(job, targetConsumable) ? JobTaskReturn.COMPLETE : JobTaskReturn.INVALID);

        // Apporte les composants à la fabrique
        job.addTask("Bring back " + targetConsumable.getLabel(), character -> character.moveTo(item.getParcel()) ? JobTaskReturn.COMPLETE : JobTaskReturn.CONTINUE);

        // Charge les comnposants dans la fabrique
        job.addTask("Load factory", character -> {
            item.addInventory(character.getInventory(itemInfo, item.getFactory().getQuantityNeeded(itemInfo)));
            return JobTaskReturn.COMPLETE;
        });

        return job;
    }

    public static BasicHaulJob toParcel(ConsumableModule consumableModule, ConsumableItem consumable, ParcelModel targetParcel, int haulingQuantity) {
        BasicHaulJob job = new BasicHaulJob(consumable, haulingQuantity, consumable.getParcel());

        // Réservation des composants au premier lancement de la tache
        job.addTask("Lock " + consumable.getLabel(), character -> Application.moduleManager.getModule(ConsumableModule.class).lock(job, consumable, haulingQuantity) ? JobTaskReturn.COMPLETE : JobTaskReturn.INVALID);

        // Déplace le personnage à l'emplacement des composants
        job.addTask("Haul " + consumable.getLabel(), character -> character.moveTo(consumable.getParcel()) ? JobTaskReturn.COMPLETE : JobTaskReturn.CONTINUE);

        // Ajoute les composants à l'inventaire du personnage
        job.addTask("Add " + consumable.getLabel() + " to inventory", character -> {
            ConsumableItem newConsumable = consumableModule.removeConsumable(consumable, haulingQuantity);
            character.addInventory(newConsumable);
            return newConsumable.getQuantity() == haulingQuantity ? JobTaskReturn.COMPLETE : JobTaskReturn.INVALID;
        });

        // Dévérouille les composants préalablement réservé
        job.addTask("Unlock " + consumable.getLabel(), character -> Application.moduleManager.getModule(ConsumableModule.class).unlock(job, consumable) ? JobTaskReturn.COMPLETE : JobTaskReturn.INVALID);

        // Apporte les composants à la fabrique
        job.addTask("Bring back " + consumable.getLabel(), character -> character.moveTo(targetParcel) ? JobTaskReturn.COMPLETE : JobTaskReturn.CONTINUE);

        // Charge les comnposants dans la fabrique
        job.addTask("Load factory", character -> {
            int availableQuantity = character.getInventoryQuantity(consumable.getInfo());
            Application.moduleManager.getModule(ConsumableModule.class).putConsumable(targetParcel, consumable.getInfo(), availableQuantity);
            character.setInventoryQuantity(consumable.getInfo(), 0);
            return JobTaskReturn.COMPLETE;
        });

        return job;
    }

    public BasicHaulJob(ConsumableItem consumable, int haulingQuantity, ParcelModel targetParcel) {
        _targetParcel = targetParcel;
        _consumable = consumable;
        _consumableInfo = consumable.getInfo();
        _haulingQuantity = haulingQuantity;
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
    public String getLabel() {
        return _label;
    }

    @Override
    public String toString() { return "Haul " + _consumable + " to " + _targetParcel; }
}
