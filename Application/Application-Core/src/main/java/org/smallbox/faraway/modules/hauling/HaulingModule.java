package org.smallbox.faraway.modules.hauling;

import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.area.model.StorageAreaModel;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.area.AreaModule;
import org.smallbox.faraway.modules.consumable.BasicHaulJobToParcel;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.world.WorldModule;

import java.util.Collections;
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

    @Override
    public void onGameCreate(Game game) {
    }

    @Override
    public void onModuleUpdate(Game game) {

        jobModule.getJobs().stream()
                .filter(job -> job.getStatus() == JobModel.JobStatus.INITIALIZED)
                .filter(job -> job instanceof BasicHaulJobToParcel)
                .forEach(job -> jobModule.removeJob(job));

        List<ParcelModel> storageParcels = areaModule.getAreas().stream()
                .filter(area -> area instanceof StorageAreaModel)
                .map(area -> (StorageAreaModel)area)
                .flatMap(area -> area.getParcels().stream())
                .collect(Collectors.toList());

        // Crée les hauling jobs pour les consomables hors d'une parcel de stockage
        consumableModule.getConsumables().stream()
                .filter(consumable -> consumable.getFreeQuantity() > 0)
                .filter(consumable -> !storageParcels.contains(consumable.getParcel()))
                .forEach(consumable ->
                        storageParcels.stream().filter(parcel -> consumableModule.getConsumable(parcel) == null).findAny().ifPresent(parcel -> {
                            BasicHaulJobToParcel.toParcel(
                                    consumableModule,
                                    jobModule,
                                    consumable.getInfo(),
                                    Collections.singletonList(consumable),
                                    parcel,
                                    consumable.getFreeQuantity());
                        }));

    }

    /**
     * Supprime les jobs n'ayants pas démarrés
     */
    private void cancelDuplicateJobs() {
        // Supprime les jobs à mutualiser (jobs ayants des consomables avec des resources libre)
        jobModule.getJobs().stream()
                .filter(job -> job instanceof BasicHaulJobToParcel)
                .map(job -> (BasicHaulJobToParcel)job)
                .filter(job -> job.getStatus() == JobModel.JobStatus.INITIALIZED || job.getStatus() == JobModel.JobStatus.WAITING)
                .filter(job -> job.getConsumables().stream().anyMatch(consumable -> consumable.getFreeQuantity() > 0))
                .forEach(job -> jobModule.removeJob(job));

    }

}
