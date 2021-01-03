package org.smallbox.faraway.modules.storage;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.CharacterInventoryExtra;
import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.dig.DigJobFactory;
import org.smallbox.faraway.modules.job.JobModel;

@GameObject
public class StoreJobFactory {

    @Inject
    private ConsumableModule consumableModule;

    public JobModel createJob(StorageArea storageArea, ConsumableItem consumable) {
        ItemInfo consumableInfo = consumable.getInfo();
        ParcelModel targetParcel = storageArea.getParcels().stream()
                .filter(parcel -> consumableModule.parcelAcceptConsumable(parcel, consumable))
                .findFirst()
                .orElse(null);

        if (targetParcel != null) {
            StoreJob job = new StoreJob();

            job.sourceConsumable = consumable;
            job._targetParcel = consumable.getParcel();
            consumable.setStoreJob(job);
            job.setMainLabel("Store");
            job.setIcon("[base]/graphics/jobs/ic_haul.png");
            job.setSkillType(CharacterSkillExtra.SkillType.STORE);
            job.setColor(new Color(0xbb391eff));

            // Init
            job.addInitTask(() -> job.targetConsumable = consumableModule.addConsumable(consumableInfo, 0, targetParcel)); // Create consumable with 0 quantity (to book parcel on storage area)
            job.addInitTask(() -> job.targetConsumable.setStoreJob(job)); // Set store job on source consumable
            job.addInitTask(() -> job.sourceConsumable.setStoreJob(job)); // Set store job on source consumable

            // Job
            job.addMoveTask("Move to consumable", () -> job.sourceConsumable.getParcel()); // Move character to sourceConsumable
            job.addTechnicalTask(() -> takeConsumable(job.sourceConsumable, job.getCharacter())); // Move consumable to character's inventory
            job.addMoveTask("Move to storage", () -> job.targetConsumable.getParcel()); // Apporte les composants à la zone de stockage
            job.addTechnicalTask(() -> dropConsumable(job.targetConsumable, job.getCharacter())); // Ajoute les composants à la zone de stockage

            // Close
            job.addCloseTask(() -> job.targetConsumable.removeStoreJob(job)); // Remove job on targetConsumable when job is closed
            job.addCloseTask(() -> job.sourceConsumable.removeStoreJob(job)); // Remove job on sourceConsumable when job is closed

            job.onNewInit();

            return job;
        }

        throw new GameException(DigJobFactory.class, "Unable to create store job");
    }

    /**
     * Take consumable from parcel and move them to character's inventory
     */
    private void takeConsumable(ConsumableItem consumable, CharacterModel character) {
        consumableModule.removeConsumable(consumable);
        character.getExtra(CharacterInventoryExtra.class).addInventory(consumable.getInfo(), consumable.getTotalQuantity());
    }

    /**
     * Drop consumable from character's inventory to storageArea's parcel
     */
    private void dropConsumable(ConsumableItem targetConsumable, CharacterModel character) {
        ConsumableItem inventoryConsumable = character.getExtra(CharacterInventoryExtra.class).takeInventory(targetConsumable.getInfo());
        targetConsumable.addQuantity(inventoryConsumable.getTotalQuantity());
    }

}
