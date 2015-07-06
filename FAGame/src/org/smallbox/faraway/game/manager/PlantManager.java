package org.smallbox.faraway.game.manager;

import org.smallbox.faraway.JobManagerHelper;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.item.ResourceModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 05/07/2015.
 */
public class PlantManager extends BaseManager {
    private List<ResourceModel> _plants = new ArrayList<>();

    public PlantManager() {
        _updateInterval = 10;
    }

    @Override
    protected void onUpdate(int tick) {
        double light = Game.getWorldManager().getLight() * 100;
        double temperature = Game.getWorldManager().getTemperature();
        for (ResourceModel resource: _plants) {
            if (resource.getParcel().isExterior()) {
                // Growing
                if (resource.getQuantity() < resource.getInfo().plant.mature) {
                    grow(resource.getParcel(), resource, light, temperature);
                }
                // Plan to gather
                else {
                    JobManagerHelper.addGather(resource);
                }
            }
        }
    }

    public void grow(ParcelModel parcel, ResourceModel resource, double light, double temperature) {
        if (canGrow(resource.getInfo().plant.growing.exceptional, light, temperature)) {
            parcel.getResource().addQuantity(0.075);
            parcel.getResource().setGrowRate(0.075);
            return;
        }
        if (canGrow(resource.getInfo().plant.growing.regular, light, temperature)) {
            parcel.getResource().addQuantity(0.05);
            parcel.getResource().setGrowRate(0.05);
            return;
        }
        if (canGrow(resource.getInfo().plant.growing.partial, light, temperature)) {
            parcel.getResource().addQuantity(0.025);
            parcel.getResource().setGrowRate(0.025);
            return;
        }
        if (canGrow(resource.getInfo().plant.growing.stasis, light, temperature)) {
            parcel.getResource().setGrowRate(0);
            return;
        }
        parcel.getResource().addQuantity(-0.1);
        parcel.getResource().setGrowRate(-0.1);
    }

    private boolean canGrow(ItemInfo.ItemInfoPlant.GrowingInfoEntry infoEntry, double light, double temperature) {
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
