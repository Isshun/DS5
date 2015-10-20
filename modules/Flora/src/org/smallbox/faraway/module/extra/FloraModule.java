package org.smallbox.faraway.module.extra;

import org.smallbox.faraway.core.game.helper.JobHelper;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.model.area.GardenAreaModel;
import org.smallbox.faraway.core.game.model.item.ResourceModel;
import org.smallbox.faraway.core.game.module.GameModule;
import org.smallbox.faraway.core.game.module.ModuleHelper;
import org.smallbox.faraway.core.module.ModuleInfo;
//import org.smallbox.faraway.module.world.TemperatureModule;

import java.util.ArrayList;
import java.util.List;

import static org.smallbox.faraway.core.game.model.item.ItemInfo.ItemInfoPlant.GrowingInfo;

/**
 * Created by Alex on 05/07/2015.
 */
public class FloraModule extends GameModule {

    private List<ResourceModel>     _plants = new ArrayList<>();
//    private TemperatureModule       _temperatureModule;

    public FloraModule() {
        _info.id = "base_flora";
        _info.name = "base_flora";
        _info.type = "java";
        _info.version = 0.1;
        _updateInterval = 10;
    }

    @Override
    protected void onLoaded() {
//        _temperatureModule = (TemperatureModule) ModuleManager.getInstance().getModule(TemperatureModule.class);
        ModuleHelper.getWorldModule().getResources().forEach(resource -> {
            if (resource.getInfo().plant != null) {
                _plants.add(resource);
            }
        });
    }

    @Override
    protected boolean loadOnStart() {
        return GameData.config.manager.flora;
    }

    @Override
    protected void onUpdate(int tick) {
        double light = ModuleHelper.getWorldModule().getLight() * 100;
        double temperature = 35;
//        double temperature = _temperatureModule.getTemperature();
        // Growing
// Plan to gather
        _plants.stream().filter(resource -> resource.getParcel().isExterior()).forEach(resource -> {
            grow(resource, light, temperature);

            // Plan to gather
            if (resource.getParcel().getArea() != null && resource.getParcel().getArea() instanceof GardenAreaModel) {
                JobHelper.addGather(resource);
            }
        });
    }

    public void grow(ResourceModel resource, double light, double temperature) {
        double growing = resource.getInfo().plant.growing;

        GrowingInfo bestState = null;
        double bestValue = -1;
        for (GrowingInfo state: resource.getInfo().plant.states) {
            if (state.value > bestValue && canGrow(state, light, temperature)) {
                bestState = state;
                bestValue = state.value;
            }
        }

        if (bestState != null) {
            resource.setQuantity(Math.min(resource.getInfo().plant.mature, resource.getQuantity() + (growing * bestValue)));
            resource.setGrowRate(bestValue);
            resource.setGrowState(bestState);
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
