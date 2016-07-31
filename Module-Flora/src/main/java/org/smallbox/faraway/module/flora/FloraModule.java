package org.smallbox.faraway.module.flora;

import org.smallbox.faraway.core.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.PlantModel;
import org.smallbox.faraway.module.world.WorldModule;
import org.smallbox.faraway.module.world.WorldModuleObserver;

import java.util.Collection;
import java.util.LinkedList;

import static org.smallbox.faraway.core.game.modelInfo.ItemInfo.ItemInfoPlant.GrowingInfo;

/**
 * Created by Alex on 05/07/2015.
 */
public class FloraModule extends GameModule<FloraModuleObserver> {
    @BindModule("base.module.world")
    private WorldModule _world;

    private Collection<PlantModel> _plants;

    @Override
    protected void onGameCreate(Game game) {
//        game.getRenders().addSubJob(new WorldGroundRenderer(this));
        game.getRenders().add(new FloraTopRenderer(this));
//        getSerializers().addSubJob(new WorldModuleSerializer(this));

        _plants = new LinkedList<>();

        _world.addObserver(new WorldModuleObserver() {
// TODO
            //            @Override
//            public void onRemoveResource(MapObjectModel mapObject) {
//                if (mapObject instanceof PlantModel) {
//                    removeResource((PlantModel) mapObject);
//                }
//            }
//
//            @Override
//            public void onAddResource(MapObjectModel resource) {
//                if (resource instanceof PlantModel && resource.getInfo().plant != null) {
//                    _plants.addSubJob((PlantModel) resource);
//                }
//            }

            @Override
            public PlantModel putObject(ParcelModel parcel, ItemInfo itemInfo, int data, boolean complete) {
                if (itemInfo.isPlant) {
                    return putPlant(parcel, itemInfo, data);
                }
                return null;
            }
        });
    }

    @Override
    protected void onGameUpdate(Game game, int tick) {
        // Growing
        _plants.forEach(plant -> {
            if (plant.hasSeed() && computeGrowingInfo(plant)) {
                plant.grow();
            }
        });
    }

    private boolean computeGrowingInfo(PlantModel plant) {
        ParcelModel parcel = plant.getParcel();
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

    private PlantModel putPlant(ParcelModel parcel, ItemInfo itemInfo, int matterSupply) {
        // Put item on floor
        PlantModel plant = new PlantModel(itemInfo);
        for (int i = 0; i < plant.getWidth(); i++) {
            for (int j = 0; j < plant.getHeight(); j++) {
                movePlantToParcel(parcel, plant);
            }
        }
        _plants.add(plant);

        PathManager.getInstance().resetAround(parcel);

        notifyObservers(observer -> observer.onAddPlant(plant));

        return plant;
    }

    private void movePlantToParcel(ParcelModel parcel, PlantModel resource) {
        parcel.setPlant(resource);
        if (resource != null) {
            resource.setParcel(parcel);
        }
    }

    public void removeResource(PlantModel plant) {
        if (plant != null) {
            if (plant.getParcel().getPlant() == plant) {
                plant.getParcel().setPlant(null);
            }

            // TODO ?
            if (plant.getInfo().plant != null) {
                _plants.remove(plant);
            }

            _plants.remove(plant);

            PathManager.getInstance().resetAround(plant.getParcel());

            notifyObservers(observer -> observer.onRemovePlant(plant));
        }
    }

    public Collection<PlantModel> getPlants() {
        return _plants;
    }
}
