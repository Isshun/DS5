package org.smallbox.faraway.module.extra;

import org.smallbox.faraway.core.game.helper.JobHelper;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.module.area.model.GardenAreaModel;
import org.smallbox.faraway.core.game.module.world.model.resource.ResourceModel;
import org.smallbox.faraway.core.module.GameModule;
import org.smallbox.faraway.core.module.java.ModuleHelper;
//import org.smallbox.faraway.module.world.TemperatureModule;

import java.util.ArrayList;
import java.util.List;

import static org.smallbox.faraway.core.data.ItemInfo.ItemInfoPlant.GrowingInfo;

/**
 * Created by Alex on 05/07/2015.
 */
public class FloraModule extends GameModule {

    private List<ResourceModel>     _plants = new ArrayList<>();
//    private TemperatureModule       _temperatureModule;

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
        _plants.stream().filter(ResourceModel::isPlant).filter(resource -> resource.getParcel().isExterior()).forEach(resource -> {
            grow(resource, light, temperature);

            // Plan to gather
            if (resource.getParcel().getArea() != null && resource.getParcel().getArea() instanceof GardenAreaModel) {
                JobHelper.addGather(resource);
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
