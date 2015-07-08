package org.smallbox.faraway.game.manager;

import org.smallbox.faraway.JobHelper;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.area.GardenAreaModel;
import org.smallbox.faraway.game.model.item.ResourceModel;

import java.util.ArrayList;
import java.util.List;

import static org.smallbox.faraway.game.model.item.ItemInfo.ItemInfoPlant.*;

/**
 * Created by Alex on 05/07/2015.
 */
public class PlantManager extends BaseManager {
    private List<ResourceModel> _plants = new ArrayList<>();

    public PlantManager() {
        _updateInterval = 10;
    }

    @Override
    protected void onCreate() {
        Game.getWorldManager().getResources().forEach(resource -> {
            if (resource.getInfo().plant != null) {
                _plants.add(resource);
            }
        });
    }

    @Override
    protected void onUpdate(int tick) {
        double light = Game.getWorldManager().getLight() * 100;
        double temperature = Game.getWorldManager().getTemperature();
        for (ResourceModel resource: _plants) {
            if (resource.getParcel().isExterior()) {
                // Growing
                if (resource.getQuantity() < resource.getInfo().plant.mature) {
                    grow(resource, light, temperature);
                }
                // Plan to gather
                else if (resource.getParcel().getArea() != null && resource.getParcel().getArea() instanceof GardenAreaModel) {
                    JobHelper.addGather(resource);
                }
            }
        }
    }

    public void grow(ResourceModel resource, double light, double temperature) {
        GrowingInfo info = resource.getInfo().plant.growing;

        if (info.exceptional != null && canGrow(info.exceptional, light, temperature)) {
            resource.addQuantity(info.exceptional.value);
            resource.setGrowRate(info.exceptional.value);
            return;
        }
        if (info.regular != null && canGrow(info.regular, light, temperature)) {
            resource.addQuantity(info.regular.value);
            resource.setGrowRate(info.regular.value);
            return;
        }
        if (info.partial != null && canGrow(info.partial, light, temperature)) {
            resource.addQuantity(info.partial.value);
            resource.setGrowRate(info.partial.value);
            return;
        }
        if (info.stasis != null && canGrow(info.stasis, light, temperature)) {
            resource.addQuantity(info.stasis.value);
            resource.setGrowRate(info.stasis.value);
            return;
        }
        resource.addQuantity(-0.1);
        resource.setGrowRate(-0.1);
    }

    private boolean canGrow(GrowingInfoEntry infoEntry, double light, double temperature) {
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
