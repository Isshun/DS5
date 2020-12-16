package org.smallbox.faraway.modules.storing;

import org.smallbox.faraway.core.dependencyInjector.Inject;
import org.smallbox.faraway.core.dependencyInjector.GameObject;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.path.PathManager;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.modules.area.AreaModule;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.consumable.StorageArea;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.world.WorldModule;

import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Alex on 02/03/2017.
 */
@GameObject
public class StoringModule extends GameModule {

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

    private Queue<ConsumableItem> _checkQueue = new ConcurrentLinkedQueue<>();

    @Override
    public void onGameCreate(Game game) {
        areaModule.addAreaClass(StorageArea.class);
    }

    @Override
    public void onModuleUpdate(Game game) {

        if (_checkQueue.isEmpty()) {
            _checkQueue.addAll(consumableModule.getConsumables());
        }

        ConsumableItem consumable = _checkQueue.poll();
        if (consumable != null && consumable.getFreeQuantity() > 0) {

            BasicStoreJob storeJob = jobModule.getJobs(BasicStoreJob.class)
                    .filter(job -> job.haveConsumable(consumable))
                    .findFirst().orElse(null);

            StorageArea storageArea = areaModule.getArea(StorageArea.class)
                    .filter(area -> area.haveParcel(storeJob != null ? storeJob.getTargetParcel() : consumable.getParcel()))
                    .findFirst().orElse(null);

            StorageArea bestArea = getBestArea(consumable.getInfo(), consumable.getFreeQuantity());

            // Le consomable n'a pas de zone de stockage ni de StoreJob
            // -> Crée un StoreJob
            if (storageArea == null) {
                createStoreJob(bestArea, consumable);
            }

            // Le consomable est déjà dans un zone de stockage ou dans un StoreJob.
            // -> Trouve la meilleur zone de stockage possible et vérifie qu'elle corresponde à la zone actuelle
            // -> Crée un StoreJob
            else if (bestArea != null && bestArea.getPriority() > storageArea.getPriority()) {

                if (storeJob != null) {
                    jobModule.removeJob(storeJob);
                }

                createStoreJob(bestArea, consumable);
            }

        }

    }

    private void createStoreJob(StorageArea storageArea, ConsumableItem consumable) {

        // Crée le job sur la première parcel disponible
        if (storageArea != null) {
            storageArea.getParcels().stream()
                    .filter(parcel -> consumableModule.parcelAcceptConsumable(parcel, consumable))
                    .findFirst()
                    .ifPresent(parcel -> BasicStoreJob.toParcel(consumableModule, jobModule, consumable, parcel, storageArea));
        }
    }

    private StorageArea getBestArea(ItemInfo itemInfo, int quantity) {
        return areaModule.getAreas(StorageArea.class)

                // Trie les zone de stockage par priorité
                .sorted(Comparator.comparingInt(StorageArea::getPriority).reversed())

                // Filtre pour ne garder que les zones de stockage qui accepte l'objet
                .filter(area -> area.isAccepted(itemInfo))

                // Filtre pour ne garder que les zones de stockage ayant suffisament de place
                .filter(area -> area.hasFreeSpace(consumableModule, itemInfo, quantity))

                .findFirst().orElse(null);
    }

    /**
     * Supprime les jobs n'ayants pas démarrés
     */
    private void cancelDuplicateJobs() {
        // Supprime les jobs à mutualiser (jobs ayants des consomables avec des resources libre)
        jobModule.getJobs().stream()
                .filter(job -> job instanceof BasicStoreJob)
                .map(job -> (BasicStoreJob)job)
                .filter(job -> job.getStatus() == JobModel.JobStatus.JOB_INITIALIZED || job.getStatus() == JobModel.JobStatus.JOB_WAITING)
                .filter(job -> job.getConsumables().stream().anyMatch(consumable -> consumable.getFreeQuantity() > 0))
                .forEach(job -> jobModule.removeJob(job));

    }

    /**
     *
     * @param area
     */
    public void notifyRulesChange(StorageArea area) {

        // Annule les jobs contenant des consumables non compatible avec les zone de stockage
        jobModule.getJobs(BasicStoreJob.class)
                .filter(job -> area.getParcels().contains(job.getTargetParcel()))
                .filter(job -> !area.isAccepted(job.getConsumables()))
                .forEach(job -> jobModule.removeJob(job));
    }
}
