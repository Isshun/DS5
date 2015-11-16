package org.smallbox.faraway.module.world;

import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.JobHelper;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.module.area.AreaModule;
import org.smallbox.faraway.core.game.module.area.model.GardenAreaModel;
import org.smallbox.faraway.core.game.module.job.model.GatherJob;
import org.smallbox.faraway.core.game.module.world.model.resource.PlantModel;
import org.smallbox.faraway.core.module.GameModule;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.module.java.ModuleManager;

import java.util.ArrayList;
import java.util.List;

import static org.smallbox.faraway.core.data.ItemInfo.ItemInfoPlant.GrowingInfo;

/**
 * Created by Alex on 05/07/2015.
 */
public class GardenModule extends GameModule {
    private List<PlantModel>     _plants = new ArrayList<>();

    @Override
    protected void onLoaded(Game game) {
//        _temperatureModule = (TemperatureModule) ModuleManager.getInstance().getModule(TemperatureModule.class);
        ModuleHelper.getWorldModule().getPlants().forEach(resource -> {
            if (resource.getInfo().plant != null) {
                _plants.add(resource);
            }
        });
    }

    @Override
    protected boolean loadOnStart() {
        return Data.config.manager.flora;
    }

    @Override
    protected void onUpdate(int tick) {
        AreaModule areaModule = (AreaModule)ModuleManager.getInstance().getModule(AreaModule.class);
        if (areaModule != null) {
            areaModule.getAreas().stream().filter(area -> area instanceof GardenAreaModel).forEach(area -> {
                GardenAreaModel garden = (GardenAreaModel)area;
                if (garden.getAccepted() != null) {
                    garden.getParcels().forEach(parcel -> {
                        PlantModel plant = parcel.getPlant();

                        // Reset field if resource is missing
                        if (plant == null) {
                            garden.resetField(parcel);
                        }

                        //  Plan to cut / remove resource if distinct from garden accepted resource
                        if (plant != null && plant.getInfo() != garden.getAccepted() && plant.getJob() == null) {
                            garden.cleanField(parcel);
                        }

                        // Gather plant
                        if (plant != null) {

                            // Decrease nourish value
                            plant.setNourish(Math.max(0, plant.getNourish() - plant.getInfo().plant.nourish));

                            // Launch gather job if non-exists
                            if (plant.getJob() == null) {

                                // Plan to harvest
                                if (plant.isMature()) {
                                    JobHelper.addGather(plant, GatherJob.Mode.HARVEST);
                                }

                                // Plan to plant seed
                                else if (!plant.hasSeed()) {
                                    JobHelper.addGather(plant, GatherJob.Mode.PLANT_SEED);
                                }

                                // Plan to nourish
                                else if (plant.getNourish() < 0.75) {
                                    JobHelper.addGather(plant, GatherJob.Mode.NOURISH);
                                }
                            }
                        }
                    });
                }
            });
        }
//        double temperature = _temperatureModule.getTemperature();
        // Growing
// Plan to gather
        _plants.stream().filter(resource -> resource.getParcel().isExterior()).forEach(plant -> {

            // Launch jobs on garden
            if (plant.getJob() == null && plant.getParcel().getArea() != null && plant.getParcel().getArea() instanceof GardenAreaModel) {
                GardenAreaModel garden = (GardenAreaModel)plant.getParcel().getArea();

                // Decrease nourish value
                plant.setNourish(Math.max(0, plant.getNourish() - plant.getInfo().plant.nourish));

                // Launch additional grow if the plant has been nourished
                if (plant.hasGrowingInfo() && plant.getNourish() > 0.25) {
                    plant.grow();
                }
                if (plant.hasGrowingInfo() && plant.getNourish() > 0.5) {
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
                if (plant.getNourish() < 0.75) {
                    JobHelper.addGather(plant, GatherJob.Mode.NOURISH);
                }
            }
        });
    }

    private boolean canGrow(GrowingInfo infoEntry, double light, double temperature) {
        return light >= infoEntry.light[0] && light <= infoEntry.light[1]
                && temperature >= infoEntry.temperature[0] && temperature <= infoEntry.temperature[1];
    }

    @Override
    public void onAddPlant(PlantModel resource) {
        if (resource.getInfo().plant != null) {
            _plants.add(resource);
        }
    }

    @Override
    public void onRemovePlant(PlantModel plant) {
        if (plant.getInfo().plant != null) {
            _plants.remove(plant);
        }
    }
}
