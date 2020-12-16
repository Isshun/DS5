package org.smallbox.faraway.modules.dig;

import org.smallbox.faraway.core.dependencyInjector.Inject;
import org.smallbox.faraway.core.dependencyInjector.GameObject;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.area.AreaModule;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.world.WorldModule;
import org.smallbox.faraway.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by Alex on 28/02/2017.
 */
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

    private Map<ParcelModel, BasicDigJob> _parcels = new ConcurrentHashMap<>();

    @Override
    public void onGameCreate(Game game) {

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
                .filter(job -> job instanceof BasicDigJob)
                .map(job -> ((BasicDigJob)job).getDigParcel())
                .collect(Collectors.toList());

        // Create missing dig job
        parcelInDigArea.stream()
                .filter(parcel -> parcel.getRockInfo() != null)
                .filter(parcel -> CollectionUtils.notContains(parcelInDigJob, parcel))
                .forEach(this::createMissingJob);
    }

    private void createMissingJob(ParcelModel parcel) {
        BasicDigJob.create(consumableModule, jobModule, worldModule, parcel);
    }

}
