package org.smallbox.faraway.modules.storing;

import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.path.PathManager;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.area.AreaModule;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.consumable.StorageArea;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.world.WorldModule;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Alex on 02/03/2017.
 */
public class StoringModule extends GameModule {

    @BindModule
    private WorldModule worldModule;

    @BindModule
    private ConsumableModule consumableModule;

    @BindModule
    private JobModule jobModule;

    @BindModule
    private AreaModule areaModule;

    @BindModule
    private PathManager pathManager;

    @Override
    public void onGameCreate(Game game) {
        areaModule.addAreaClass(StorageArea.class);
    }

    @Override
    public void onModuleUpdate(Game game) {

        // Récupère tous les consomables déjà concernés par des StoreJob
        List<ConsumableItem> consumablesInStoreJob = jobModule.getJobs(BasicStoreJob.class)
                .flatMap(job -> job.getConsumables().keySet().stream())
                .collect(Collectors.toList());

        // Récupère tous les consomables déjà concernés par des StoreJob
        List<ParcelModel> storageParcels = areaModule.getAreasParcels(StorageArea.class)
                .collect(Collectors.toList());

        // Crée les storing jobs pour les consomables hors d'une parcel de stockage
        consumableModule.getConsumables().stream()
                .filter(consumable -> consumable.getFreeQuantity() > 0)
                .filter(consumable -> !consumablesInStoreJob.contains(consumable))
                .filter(consumable -> !storageParcels.contains(consumable.getParcel()))
                .forEach(consumable -> {

                    areaModule.getAreas().stream()
                            .filter(area -> area instanceof StorageArea)
                            .map(area -> (StorageArea)area)
                            .filter(area -> area.isAccepted(consumable.getInfo()))
                            .flatMap(area -> area.getParcels().stream())
                            .filter(parcel -> consumableModule.parcelAcceptConsumable(parcel, consumable))
                            .sorted(Comparator.comparingInt(o -> pathManager.getDistance(o, consumable.getParcel())))
                            .findFirst()
                            .ifPresent(parcel ->
                                    BasicStoreJob.toParcel(
                                            consumableModule,
                                            jobModule,
                                            Collections.singletonMap(consumable, consumable.getFreeQuantity()),
                                            parcel));

                });

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
                .filter(job -> job.getConsumables().keySet().stream().anyMatch(consumable -> consumable.getFreeQuantity() > 0))
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
                .filter(job -> !area.isAccepted(job.getConsumables().keySet()))
                .forEach(job -> jobModule.removeJob(job));
    }
}
