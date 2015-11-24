package org.smallbox.faraway.module.extra;

import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.java.ModuleHelper;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.PlantModel;

import java.util.Collection;

import static org.smallbox.faraway.core.game.modelInfo.ItemInfo.ItemInfoPlant.GrowingInfo;

/**
 * Created by Alex on 05/07/2015.
 */
public class FloraModule extends ModuleBase {
    private Collection<PlantModel> _plants;

    @Override
    protected void onLoaded(Game game) {
        _plants = ModuleHelper.getWorldModule().getPlants();
    }

    @Override
    protected boolean loadOnStart() {
        return true;
    }

    @Override
    protected void onUpdate(int tick) {
        _plants.forEach(plant -> {
            ParcelModel parcel = plant.getParcel();

            // Growing
            if (plant.hasSeed() && computeGrowingInfo(plant, parcel)) {
                plant.grow();
            }

            // Add oxygen to room
            if (parcel.hasRoom()) {
                parcel.getRoom().setOxygen(parcel.getRoom().getOxygen() + plant.getInfo().plant.oxygen);
            }
        });
    }

    private boolean computeGrowingInfo(PlantModel plant, ParcelModel parcel) {
        plant.setGrowingInfo(null);
        double bestValue = -1;
        for (GrowingInfo growingInfo: plant.getInfo().plant.states) {
            if (growingInfo.value > bestValue && canGrow(growingInfo, parcel.getLight(), parcel.getTemperature())) {
                plant.setGrowingInfo(growingInfo);
                bestValue = growingInfo.value;
            }
        }
        return plant.getGrowingInfo() != null;
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
