package org.smallbox.faraway.modules.hauling;

import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.path.PathManager;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.area.AreaModule;
import org.smallbox.faraway.modules.consumable.BasicStoreJob;
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
public class HaulingModule extends GameModule {

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

//        jobModule.getJobs().stream()
//                .filter(job -> job.getStatus() == JobModel.JobStatus.INITIALIZED)
//                .filter(job -> job instanceof BasicStoreJob)
//                .forEach(job -> jobModule.removeJob(job));

        List<ConsumableItem> consumablesInStoreJob = jobModule.getJobs().stream()
                .filter(job -> job instanceof BasicStoreJob)
                .map(job -> (BasicStoreJob)job)
                .flatMap(job -> job.getConsumables().keySet().stream())
                .collect(Collectors.toList());

        List<ParcelModel> storageParcels = areaModule.getAreas().stream()
                .filter(area -> area instanceof StorageArea)
                .map(area -> (StorageArea)area)
                .flatMap(area -> area.getParcels().stream())
                .collect(Collectors.toList());

        // Crée les hauling jobs pour les consomables hors d'une parcel de stockage
        consumableModule.getConsumables().stream()
                .filter(consumable -> consumable.getFreeQuantity() > 0)
                .filter(consumable -> !consumablesInStoreJob.contains(consumable))
                .filter(consumable -> !storageParcels.contains(consumable.getParcel()))
                .forEach(consumable ->

                        storageParcels.stream()
                                .filter(parcel -> consumableModule.parcelAcceptConsumable(parcel, consumable))
                                .sorted(Comparator.comparingInt(o -> pathManager.getDistance(o, consumable.getParcel())))
                                .findFirst()
                                .ifPresent(parcel ->
                                        BasicStoreJob.toParcel(
                                                consumableModule,
                                                jobModule,
                                                Collections.singletonMap(consumable, consumable.getFreeQuantity()),
                                                parcel)));

    }

    /**
     * Supprime les jobs n'ayants pas démarrés
     */
    private void cancelDuplicateJobs() {
        // Supprime les jobs à mutualiser (jobs ayants des consomables avec des resources libre)
        jobModule.getJobs().stream()
                .filter(job -> job instanceof BasicStoreJob)
                .map(job -> (BasicStoreJob)job)
                .filter(job -> job.getStatus() == JobModel.JobStatus.INITIALIZED || job.getStatus() == JobModel.JobStatus.WAITING)
                .filter(job -> job.getConsumables().keySet().stream().anyMatch(consumable -> consumable.getFreeQuantity() > 0))
                .forEach(job -> jobModule.removeJob(job));

    }

}
