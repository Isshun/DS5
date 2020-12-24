package org.smallbox.faraway.modules.dig;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.area.AreaModule;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.world.WorldModule;
import org.smallbox.faraway.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@GameObject
public class DigModule extends GameModule {

    @Inject
    private WorldModule worldModule;

    @Inject
    private JobModule jobModule;

    @Inject
    private ConsumableModule consumableModule;

    @Inject
    private AreaModule areaModule;

    @Inject
    private DigJobFactory digJobFactory;

    @OnInit
    public void init() {
        areaModule.addAreaClass(DigArea.class);

//        worldModule.getParcelList().stream()
//                .filter(parcel -> parcel.z == 1)
//                .filter(parcel -> !_parcels.containsKey(parcel))
//                .filter(parcel -> parcel.getRockInfo() != null)
//                .forEach(parcel -> {
//                    BasicDigJob digJob = BasicDigJob.create(consumableModule, jobModule, worldModule, parcel);
//                    if (digJob != null) {
//                        _parcels.put(parcel, digJob);
//                    }
//                });
    }

    @Override
    protected void onModuleUpdate(Game game) {
        List<ParcelModel> parcelInDigArea = areaModule.getParcelsByType(DigArea.class);
        List<ParcelModel> parcelInDigJob = jobModule.getJobs().stream()
                .filter(job -> job instanceof DigJob)
                .map(JobModel::getJobParcel)
                .collect(Collectors.toList());

        // Create missing dig job
        parcelInDigArea.stream()
                .filter(parcel -> parcel.getRockInfo() != null)
                .filter(parcel -> CollectionUtils.notContains(parcelInDigJob, parcel))
                .forEach(parcel -> jobModule.addJob(digJobFactory.createJob(parcel)));
    }

}
