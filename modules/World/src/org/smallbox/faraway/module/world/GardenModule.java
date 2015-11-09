package org.smallbox.faraway.module.world;

import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.JobHelper;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.module.area.AreaModule;
import org.smallbox.faraway.core.game.module.area.model.GardenAreaModel;
import org.smallbox.faraway.core.game.module.job.model.GatherJob;
import org.smallbox.faraway.core.game.module.world.model.resource.PlantExtra;
import org.smallbox.faraway.core.game.module.world.model.resource.ResourceModel;
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
    private List<ResourceModel>     _plants = new ArrayList<>();

    @Override
    protected void onLoaded(Game game) {
//        _temperatureModule = (TemperatureModule) ModuleManager.getInstance().getModule(TemperatureModule.class);
        ModuleHelper.getWorldModule().getResources().forEach(resource -> {
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
        double light = ModuleHelper.getWorldModule().getLight() * 100;
        double temperature = 35;

        AreaModule areaModule = (AreaModule)ModuleManager.getInstance().getModule(AreaModule.class);
        if (areaModule != null) {
            areaModule.getAreas().stream().filter(area -> area instanceof GardenAreaModel).forEach(area -> {
                GardenAreaModel garden = (GardenAreaModel)area;
                if (garden.getAccepted() != null) {
                    garden.getParcels().forEach(parcel -> {
                        ResourceModel resource = parcel.getResource();

                        // Reset field if resource is missing
                        if (resource == null) {
                            garden.resetField(parcel);
                        }

                        //  Plan to cut / remove resource if distinct from garden accepted resource
                        if (resource != null && resource.getInfo() != garden.getAccepted() && resource.getJob() == null) {
                            garden.cleanField(parcel);
                        }

                        // Gather plant
                        if (resource != null && resource.isPlant()) {
                            PlantExtra plant = resource.getPlant();

                            // Decrease nourish value
                            plant.setNourish(Math.max(0, plant.getNourish() - resource.getInfo().plant.nourish));

                            // Launch gather job if non-exists
                            if (resource.getJob() == null) {

                                // Plan to harvest
                                if (plant.isMature()) {
                                    JobHelper.addGather(resource, GatherJob.Mode.HARVEST);
                                }

                                // Plan to plant seed
                                else if (!plant.hasSeed()) {
                                    JobHelper.addGather(resource, GatherJob.Mode.PLANT_SEED);
                                }

                                // Plan to nourish
                                else if (plant.getNourish() < 0.75) {
                                    JobHelper.addGather(resource, GatherJob.Mode.NOURISH);
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
        _plants.stream().filter(ResourceModel::isPlant).filter(resource -> resource.getParcel().isExterior()).forEach(resource -> {
            PlantExtra plant = resource.getPlant();

            // Launch jobs on garden
            if (resource.getJob() == null && resource.getParcel().getArea() != null && resource.getParcel().getArea() instanceof GardenAreaModel) {
                GardenAreaModel garden = (GardenAreaModel)resource.getParcel().getArea();

                // Decrease nourish value
                plant.setNourish(Math.max(0, plant.getNourish() - resource.getInfo().plant.nourish));

                // Launch additional grow if the plant has been nourished
                if (plant.getGrowState() != null && resource.getPlant().getNourish() > 0.25) {
                    resource.getPlant().grow(plant.getGrowState());
                }
                if (plant.getGrowState() != null && resource.getPlant().getNourish() > 0.5) {
                    resource.getPlant().grow(plant.getGrowState());
                }

                // Plan to harvest
                if (plant.isMature()) {
                    JobHelper.addGather(resource, GatherJob.Mode.HARVEST);
                }

                // Plan to plant seed
                else if (!plant.hasSeed()) {
                    JobHelper.addGather(resource, GatherJob.Mode.PLANT_SEED);
                }

                // Plan to nourish
                if (plant.getNourish() < 0.75) {
                    JobHelper.addGather(resource, GatherJob.Mode.NOURISH);
                }
            }
        });
    }

    public void grow(ResourceModel resource, double light, double temperature) {
        GrowingInfo bestState = null;
        double bestValue = -1;
        for (GrowingInfo state: resource.getInfo().plant.states) {
            if (state.value > bestValue && canGrow(state, light, temperature)) {
                bestState = state;
                bestValue = state.value;
            }
        }

        if (bestState != null) {
            resource.getPlant().grow(bestState);

            // Plant in garden grow 3x faster
            if (resource.getPlant().getNourish() > 0.25) {
                resource.getPlant().grow(bestState);
                resource.getPlant().grow(bestState);
            }
        }
    }

    private boolean canGrow(GrowingInfo infoEntry, double light, double temperature) {
        return light >= infoEntry.light[0] && light <= infoEntry.light[1]
                && temperature >= infoEntry.temperature[0] && temperature <= infoEntry.temperature[1];
    }

    @Override
    public void onAddResource(ResourceModel resource) {
        if (resource.getInfo().plant != null) {
            _plants.add(resource);
        }
    }

    @Override
    public void onRemoveResource(ResourceModel resource) {
        if (resource.getInfo().plant != null) {
            _plants.remove(resource);
        }
    }
}
