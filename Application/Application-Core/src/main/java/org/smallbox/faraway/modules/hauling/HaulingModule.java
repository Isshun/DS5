package org.smallbox.faraway.modules.hauling;

import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.consumable.BasicHaulJobToParcel;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.world.WorldModule;

import java.util.Collections;

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

    @Override
    public void onGameCreate(Game game) {
    }

    @Override
    public void onGameUpdate(Game game) {
        ParcelModel storageParcel = worldModule.getParcel(10, 10, 1);

//        cancelDuplicateJobs();

        // Crée les hauling jobs pour les consomables hors d'une parcel de stockage
        consumableModule.getConsumables().stream()
                .filter(consumable -> consumable.getFreeQuantity() > 0)
                .filter(consumable -> consumable.getParcel() != storageParcel)
                .forEach(consumable -> {
                    BasicHaulJobToParcel.toParcel(
                            consumableModule,
                            jobModule,
                            consumable.getInfo(),
                            Collections.singletonList(consumable),
                            storageParcel,
                            consumable.getFreeQuantity());
                });

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
