package org.smallbox.faraway.game.storage;

import org.smallbox.faraway.client.controller.area.AreaInfoStorageController;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.applicationEvent.OnInit;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameUpdate;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.path.PathManager;
import org.smallbox.faraway.game.area.AreaModule;
import org.smallbox.faraway.game.area.AreaModuleBase;
import org.smallbox.faraway.game.consumable.Consumable;
import org.smallbox.faraway.game.consumable.ConsumableModule;
import org.smallbox.faraway.game.job.JobModule;
import org.smallbox.faraway.game.world.WorldModule;

import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@GameObject
public class StorageModule extends AreaModuleBase<StorageArea> {
    @Inject private AreaInfoStorageController areaInfoStorageController;
    @Inject private WorldModule worldModule;
    @Inject private ConsumableModule consumableModule;
    @Inject private JobModule jobModule;
    @Inject private AreaModule areaModule;
    @Inject private PathManager pathManager;
    @Inject private StoreJobFactory storeJobFactory;

    private final Queue<Consumable> _checkQueue = new ConcurrentLinkedQueue<>();

    @OnInit
    public void init() {
        areaModule.addAreaClass(StorageArea.class);
    }

    @OnGameUpdate
    public void onGameUpdate() {

        if (_checkQueue.isEmpty()) {
            _checkQueue.addAll(consumableModule.getAll());
        }

        Consumable consumable = _checkQueue.poll();
        if (consumable != null && consumable.getActualQuantity() > 0) {
            checkConsumable(consumable);
        }

    }

    private void checkConsumable(Consumable consumable) {
        StoreJob storeJob = consumable.getStoreJob();
        StorageArea storageArea = areas.stream()
                .filter(area -> area.haveParcel(storeJob != null ? storeJob.getTargetParcel() : consumable.getParcel()))
                .findFirst().orElse(null);

        StorageArea bestArea = getBestArea(consumable.getInfo(), consumable.getActualQuantity());

        if (bestArea != null) {

            // Consumable have no StoreJob or StoreArea
            if (storageArea == null && storeJob == null) {
                jobModule.add(storeJobFactory.createJob(bestArea, consumable));
            }

            // Consumable have StoreJob or StoreArea but not the best one
            else if (storageArea != bestArea && (storeJob == null || storeJob.storageArea != bestArea)) {

                if (storeJob != null) {
                    jobModule.remove(storeJob);
                }

                jobModule.add(storeJobFactory.createJob(bestArea, consumable));
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
                .forEach(job -> jobModule.remove(job));
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
