package org.smallbox.faraway.modules.storage;

import org.smallbox.faraway.client.controller.area.AreaInfoStorageController;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.path.PathManager;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.modules.area.AreaModule;
import org.smallbox.faraway.modules.area.AreaModuleBase;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.world.WorldModule;

import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@GameObject
public class StorageModule extends AreaModuleBase<StorageArea> {

    @Inject
    private WorldModule worldModule;

    @Inject
    private ConsumableModule consumableModule;

    @Inject
    private JobModule jobModule;

    @Inject
    private AreaModule areaModule;

    @Inject
    private PathManager pathManager;

    @Inject
    private AreaInfoStorageController areaInfoStorageController;

    @Inject
    private StoreJobFactory storeJobFactory;

    private Queue<ConsumableItem> _checkQueue = new ConcurrentLinkedQueue<>();

    @OnInit
    public void init() {
        areaModule.addAreaClass(StorageArea.class);
    }

    @Override
    public void onModuleUpdate(Game game) {

        if (_checkQueue.isEmpty()) {
            _checkQueue.addAll(consumableModule.getAll());
        }

        ConsumableItem consumable = _checkQueue.poll();
        if (consumable != null && consumable.getFreeQuantity() > 0) {
            checkConsumable(consumable);
        }

    }

    private void checkConsumable(ConsumableItem consumable) {
        StoreJob storeJob = consumable.getStoreJob();
        StorageArea storageArea = areas.stream()
                .filter(area -> area.haveParcel(storeJob != null ? storeJob.getStorageParcel() : consumable.getParcel()))
                .findFirst().orElse(null);

        StorageArea bestArea = getBestArea(consumable.getInfo(), consumable.getFreeQuantity());

        if (bestArea != null) {

            // Consumable have no StoreJob or StoreArea
            if (storageArea == null && storeJob == null) {
                jobModule.addJob(storeJobFactory.createJob(bestArea, consumable));
            }

            // Consumable have StoreJob or StoreArea but not the best one
            else if (storageArea != bestArea) {

                if (storeJob != null) {
                    jobModule.removeJob(storeJob);
                }

                jobModule.addJob(storeJobFactory.createJob(bestArea, consumable));
            }

        }
    }

    private StorageArea getBestArea(ItemInfo itemInfo, int quantity) {
        return areas.stream()

                // Trie les zone de stockage par priorité
                .sorted(Comparator.comparingInt(StorageArea::getPriority).reversed())

                // Filtre pour ne garder que les zones de stockage qui accepte l'objet
                .filter(area -> area.isAccepted(itemInfo))

                // Filtre pour ne garder que les zones de stockage ayant suffisament de place
                .filter(area -> area.hasFreeSpace(consumableModule, itemInfo, quantity))

                .findFirst().orElse(null);
    }

    public void notifyRulesChange(StorageArea area) {

        // Annule les jobs contenant des consumables non compatible avec les zone de stockage
        jobModule.getJobs(StoreJob.class)
                .filter(job -> area.getParcels().contains(job.getTargetParcel()))
                .filter(job -> !area.isAccepted(job.sourceConsumable))
                .forEach(job -> jobModule.removeJob(job));
    }

    @Override
    public StorageArea onNewArea() {
        return new StorageArea();
    }

//    @Override
//    protected void onSelectArea(StorageArea area) {
//        areaInfoStorageController.displayArea(area);
//    }

}
