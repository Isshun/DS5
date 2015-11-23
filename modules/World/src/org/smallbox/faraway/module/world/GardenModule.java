package org.smallbox.faraway.module.world;

import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.JobHelper;
import org.smallbox.faraway.core.game.module.area.AreaModule;
import org.smallbox.faraway.core.game.module.job.model.GatherJob;
import org.smallbox.faraway.core.game.module.world.model.PlantModel;

/**
 * Created by Alex on 05/07/2015.
 */
public class GardenModule extends GameModule {
    @Override
    protected void onLoaded(Game game) {
    }

    @Override
    protected boolean loadOnStart() {
        return true;
    }

    @Override
    protected void onUpdate(int tick) {
        AreaModule areaModule = (AreaModule)ModuleManager.getInstance().getModule(AreaModule.class);
        if (areaModule != null && areaModule.getGardens() != null) {
            areaModule.getGardens().forEach(garden -> {
                if (garden.getAccepted() != null) {
                    garden.getParcels().forEach(parcel -> {
                        PlantModel plant = parcel.getPlant();

                        // Reset field if resource is missing
                        if (plant == null) {
                            garden.resetField(parcel);
                            return;
                        }

                        //  Plan to cut / remove resource if distinct from garden accepted resource
                        if (plant.getInfo() != garden.getAccepted() && plant.getJob() == null) {
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
