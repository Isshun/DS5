package org.smallbox.faraway.module.extra;

import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.module.world.model.resource.ResourceModel;
import org.smallbox.faraway.core.module.GameModule;
import org.smallbox.faraway.core.module.java.ModuleHelper;

import java.util.ArrayList;
import java.util.List;

import static org.smallbox.faraway.core.data.ItemInfo.ItemInfoPlant.GrowingInfo;

/**
 * Created by Alex on 05/07/2015.
 */
public class FloraModule extends GameModule {
    private List<ResourceModel>     _plants = new ArrayList<>();

    @Override
    protected void onLoaded() {
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

        // Growing
        _plants.stream().filter(ResourceModel::isPlant).filter(resource -> resource.getParcel().isExterior()).forEach(resource -> {
            if (resource.getPlant().hasSeed()) {
                GrowingInfo growingInfo = getGrowingInfo(resource, light, temperature);
                if (growingInfo != null) {
                    resource.getPlant().grow(growingInfo);
                }
            }
        });
    }

    private GrowingInfo getGrowingInfo(ResourceModel resource, double light, double temperature) {
        GrowingInfo bestState = null;
        double bestValue = -1;
        for (GrowingInfo state: resource.getInfo().plant.states) {
            if (state.value > bestValue && canGrow(state, light, temperature)) {
                bestState = state;
                bestValue = state.value;
            }
        }
        return bestState;
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
