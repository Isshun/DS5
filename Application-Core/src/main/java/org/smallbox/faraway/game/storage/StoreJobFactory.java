package org.smallbox.faraway.game.storage;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.game.character.model.CharacterInventoryExtra;
import org.smallbox.faraway.game.character.model.CharacterSkillExtra;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.consumable.Consumable;
import org.smallbox.faraway.game.consumable.ConsumableModule;
import org.smallbox.faraway.game.dig.factory.DigJobFactory;
import org.smallbox.faraway.game.job.JobModel;
import org.smallbox.faraway.game.job.task.TechnicalTask;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.SurroundedPattern;
import org.smallbox.faraway.game.world.WorldHelper;
import org.smallbox.faraway.util.GameException;

@GameObject
public class StoreJobFactory {
    @Inject private ConsumableModule consumableModule;

    public JobModel createJob(StorageArea storageArea, Consumable consumable) {
        ItemInfo consumableInfo = consumable.getInfo();
        Parcel targetParcel = storageArea.getParcels().stream()
                .filter(parcel -> consumableModule.parcelAcceptConsumable(parcel, consumable))
                .findFirst()
                .orElse(null);

        if (targetParcel != null) {
            StoreJob job = new StoreJob(targetParcel);
            consumable.setStoreJob(job);

            job.sourceConsumable = consumable;
            job.storageArea = storageArea;
            job.setMainLabel("Store");
            job.setIcon("data/graphics/jobs/ic_store.png");
            job.setSkillType(CharacterSkillExtra.SkillType.STORE);
            job.setColor(new Color(0xbb391eff));
            job.setAcceptedParcel(WorldHelper.getParcelAround(consumable.getParcel(), SurroundedPattern.SQUARE));

            // Init
//            job.addInitTask(j -> job.targetConsumable = consumableModule.addConsumable(consumableInfo, 0, targetParcel)); // Create consumable with 0 quantity (to book parcel on storage area)
//            job.addInitTask(j -> job.targetConsumable.setStoreJob(job)); // Set store job on source consumable
//            job.addOnStartTask(new TechnicalTask(j -> job.setAcceptedParcel(WorldHelper.getParcelAround(job.sourceConsumable.getParcel(), SurroundedPattern.SQUARE)))); // Set store job on source consumable

            // Find storage parcel
            job.addTask(new TechnicalTask(j -> job.storageParcel = storageArea.getNearestFreeParcel(consumableModule, job.sourceConsumable)));

            // Move to consumable
            job.addTask(new TechnicalTask(j -> job.setAcceptedParcel(WorldHelper.getParcelAround(job.sourceConsumable.getParcel(), SurroundedPattern.SQUARE))));

            // Take consumable
            job.addTask(new TechnicalTask(j -> job.inventoryConsumable = takeConsumable(job.sourceConsumable, job.getCharacter())));

            // Move to storage parcel
            job.addTask(new TechnicalTask(j -> job.setAcceptedParcel(WorldHelper.getParcelAround(job.storageParcel, SurroundedPattern.SQUARE))));

            // Drop consumable
            job.addTask(new TechnicalTask(j -> dropConsumable(job, consumable.getInfo()))); // Ajoute les composants à la zone de stockage

            // Close
//            job.addCloseTask(j -> job.targetConsumable.removeStoreJob(job)); // Remove job on targetConsumable when job is closed
            job.addCloseTask(j -> job.sourceConsumable.removeStoreJob(job)); // Remove job on sourceConsumable when job is closed

            job.onNewInit();

            return job;
        }

        throw new GameException(DigJobFactory.class, "Unable to create store job");
    }

    /**
     * Take consumable from parcel and move them to character's inventory
     */
    private Consumable takeConsumable(Consumable consumable, CharacterModel character) {
        consumableModule.removeConsumable(consumable);
        return character.getExtra(CharacterInventoryExtra.class).addInventory(consumable.getInfo(), consumable.getTotalQuantity());
    }

    /**
     * Drop consumable from character's inventory to storageArea's parcel
     */
    private void dropConsumable(StoreJob storeJob, ItemInfo itemInfo) {
        int extraQuantity = consumableModule.addQuantity(storeJob.storageParcel, storeJob.inventoryConsumable.getTotalQuantity(), itemInfo);
        storeJob.inventoryConsumable.setQuantity(extraQuantity);
        storeJob.getCharacter().getExtra(CharacterInventoryExtra.class).updateInventory();
    }

}
