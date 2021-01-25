package org.smallbox.faraway.game.storage;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.game.character.model.CharacterInventoryExtra;
import org.smallbox.faraway.game.character.model.CharacterSkillExtra;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.consumable.ConsumableItem;
import org.smallbox.faraway.game.consumable.ConsumableModule;
import org.smallbox.faraway.game.dig.DigJobFactory;
import org.smallbox.faraway.game.job.JobModel;
import org.smallbox.faraway.game.job.task.TechnicalTask;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.SurroundedPattern;
import org.smallbox.faraway.game.world.WorldHelper;
import org.smallbox.faraway.util.GameException;

@GameObject
public class StoreJobFactory {
    @Inject private ConsumableModule consumableModule;

    public JobModel createJob(StorageArea storageArea, ConsumableItem consumable) {
        ItemInfo consumableInfo = consumable.getInfo();
        Parcel targetParcel = storageArea.getParcels().stream()
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
            job.addInitTask(j -> job.targetConsumable = consumableModule.addConsumable(consumableInfo, 0, targetParcel)); // Create consumable with 0 quantity (to book parcel on storage area)
            job.addInitTask(j -> job.targetConsumable.setStoreJob(job)); // Set store job on source consumable
            job.addInitTask(j -> job.sourceConsumable.setStoreJob(job)); // Set store job on source consumable
            job.addInitTask(j -> job.setAcceptedParcel(WorldHelper.getParcelAround(job.sourceConsumable.getParcel(), SurroundedPattern.SQUARE))); // Set store job on source consumable

            // Job
            job.addTask(new TechnicalTask(j -> {
                takeConsumable(job.sourceConsumable, job.getCharacter());
                job.setAcceptedParcel(WorldHelper.getParcelAround(job.targetConsumable.getParcel(), SurroundedPattern.SQUARE));
            }));

            job.addTask(new TechnicalTask(j -> dropConsumable(job.targetConsumable, job.getCharacter()))); // Ajoute les composants à la zone de stockage

            // Close
            job.addCloseTask(j -> job.targetConsumable.removeStoreJob(job)); // Remove job on targetConsumable when job is closed
            job.addCloseTask(j -> job.sourceConsumable.removeStoreJob(job)); // Remove job on sourceConsumable when job is closed

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
