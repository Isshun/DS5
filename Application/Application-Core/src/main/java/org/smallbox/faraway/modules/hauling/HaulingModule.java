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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
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

    private Collection<BasicHaulJobToParcel> _jobs = new ConcurrentLinkedQueue<>();

    @Override
    public void onGameCreate(Game game) {
    }

    @Override
    public void onGameUpdate(Game game) {
        ParcelModel storageParcel = worldModule.getParcel(10, 10, 1);

        // Identifie les jobs ayants des consomables avec des resources libre (pour mutualisation)
        List<BasicHaulJobToParcel> jobsToRemove = _jobs.stream()
                .filter(job -> job.getStatus() == JobModel.JobStatus.INITIALIZED || job.getStatus() == JobModel.JobStatus.WAITING)
                .filter(job -> job.getConsumables().entrySet().stream().anyMatch(entry -> entry.getKey().getFreeQuantity() > 0))
                .collect(Collectors.toList());

        // Supprime les jobs à mutualiser
        jobsToRemove.forEach(job -> jobModule.removeJob(job));
        _jobs.removeAll(jobsToRemove);

        // Crée les hauling jobs
        consumableModule.getConsumables().stream()
                .filter(consumable -> consumable.getFreeQuantity() > 0)
                .filter(consumable -> consumable.getParcel() != storageParcel)
                .forEach(consumable -> {
                    BasicHaulJobToParcel job = BasicHaulJobToParcel.toParcel(
                            consumableModule,
                            jobModule,
                            consumable.getInfo(),
                            Collections.singletonMap(consumable, consumable.getFreeQuantity()),
                            storageParcel,
                            consumable.getFreeQuantity());
                    if (job != null) {
                        _jobs.add(job);
                    }
                });

    }

}
