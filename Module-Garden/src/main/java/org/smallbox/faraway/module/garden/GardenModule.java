package org.smallbox.faraway.module.garden;

import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.JobHelper;
import org.smallbox.faraway.core.module.job.model.GatherJob;
import org.smallbox.faraway.core.module.world.model.PlantModel;
import org.smallbox.faraway.module.area.AreaModule;

public class GardenModule extends GameModule {

    @BindModule
    private AreaModule _areaModule;

    @Override
    protected void onGameUpdate(Game game, int tick) {
        if (_areaModule != null && _areaModule.getGardens() != null) {
            _areaModule.getGardens().forEach(garden -> {
                if (garden.getCurrent() != null) {
                    garden.getParcels().forEach(parcel -> {
                        PlantModel plant = parcel.getPlant();

                        // Reset field if resource is missing
                        if (plant == null) {
                            garden.resetField(parcel);
                            return;
                        }

                        //  Plan to cut / remove resource if distinct from garden accepted resource
                        if (plant.getInfo() != garden.getCurrent() && plant.getJob() == null) {
                            garden.cleanField(parcel);
                            return;
                        }

                        // Decrease nourish value
                        if (plant.hasGrowingInfo() && plant.getMaturity() < 1) {
                            plant.setNourish(Math.max(0, plant.getNourish() - (plant.getInfo().plant.nourish * plant.getGrowingInfo().value)));
                        }

                        // Launch additional grow if the plant has been nourished
                        if (plant.hasGrowingInfo() && plant.getNourish() > 0) {
                            plant.grow();
                        }
                        if (plant.hasGrowingInfo() && plant.getNourish() > 0.25) {
                            plant.grow();
                        }

                        // Plan to harvest
                        if (plant.isMature()) {
                            JobHelper.addGather(plant, GatherJob.Mode.HARVEST);
                        }

                        // Plan to plant seed
                        else if (!plant.hasSeed()) {
                            JobHelper.addGather(plant, GatherJob.Mode.PLANT_SEED);
                        }

                        // Plan to nourish
                        if (plant.getNourish() < 0.5) {
                            JobHelper.addGather(plant, GatherJob.Mode.NOURISH);
                        }
                    });
                }
            });
        }
    }
}
