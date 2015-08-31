package org.smallbox.faraway.game.module.extra;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.helper.JobHelper;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.area.GardenAreaModel;
import org.smallbox.faraway.game.model.item.ResourceModel;
import org.smallbox.faraway.game.module.GameModule;
import org.smallbox.faraway.game.module.world.TemperatureModule;

import java.util.ArrayList;
import java.util.List;

import static org.smallbox.faraway.game.model.item.ItemInfo.ItemInfoPlant.GrowingInfo;

/**
 * Created by Alex on 05/07/2015.
 */
public class FloraModule extends GameModule {
    private List<ResourceModel>     _plants = new ArrayList<>();
    private TemperatureModule _temperatureModule;

    public FloraModule() {
        _updateInterval = 10;
    }

    @Override
    protected void onLoaded() {
        _temperatureModule = (TemperatureModule)Game.getInstance().getModule(TemperatureModule.class);
        Game.getWorldManager().getResources().forEach(resource -> {
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
        double light = Game.getWorldManager().getLight() * 100;
        double temperature = _temperatureModule.getTemperature();
        for (ResourceModel resource: _plants) {
            if (resource.getParcel().isExterior()) {
                // Growing
                if (resource.getQuantity() < resource.getInfo().plant.mature) {
                    grow(resource, light, temperature);
                }
                // Plan to gather
                else if (resource.getParcel().getArea() != null && resource.getParcel().getArea() instanceof GardenAreaModel) {
                    JobHelper.addGather(resource, false);
                }
            }
        }
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
            resource.addQuantity(growing * bestValue);
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
