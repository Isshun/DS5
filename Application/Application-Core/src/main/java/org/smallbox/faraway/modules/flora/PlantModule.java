package org.smallbox.faraway.modules.flora;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.flora.model.PlantItem;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.world.WorldModule;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Alex on 02/03/2017.
 */
public class PlantModule extends GameModule {

    @BindModule
    private WorldModule worldModule;

    @BindModule
    private ConsumableModule consumableModule;

    @BindModule
    private JobModule jobModule;

    private Collection<PlantItem> _plants = new ConcurrentLinkedQueue<>();

    @Override
    public void onGameCreate(Game game) {
        worldModule.getParcelList().stream()
//                .filter(parcel -> parcel.x == 10 && parcel.y == 10 && parcel.z == 1)
                .filter(parcel -> Math.random() > 0.95)
                .forEach(parcel -> {
                    PlantItem plant = new PlantItem(Application.data.getItemInfo("base.plant.rice"));
                    plant.setParcel(parcel);
                    plant.setMaturity(Math.random());
                    _plants.add(plant);
                });
    }

    @Override
    public void onGameUpdate(Game game) {
        // Fait pousser les plantes
        _plants.stream()
                .filter(plant -> plant.hasSeed() && computeGrowingInfo(plant))
                .forEach(PlantItem::grow);

        // Récolte les plantes arrivées à maturité
        _plants.stream()
                .filter(plant -> plant.getJob() == null)
                .filter(plant -> plant.getMaturity() >= 1)
                .forEach(plant -> {
                    BasicHarvestJob job = BasicHarvestJob.create(consumableModule, plant);
                    plant.setJob(job);
                    jobModule.addJob(job);
                });

        // TODO: ajout auto de la graine
        _plants.forEach(plant -> plant.setSeed(true));
    }

    private boolean computeGrowingInfo(PlantItem plant) {
        ParcelModel parcel = plant.getParcel();
        plant.setGrowingInfo(null);
        double bestValue = -1;
        for (ItemInfo.ItemInfoPlant.GrowingInfo growingInfo: plant.getInfo().plant.states) {
            if (growingInfo.value > bestValue && canGrow(growingInfo, parcel.getLight(), parcel.getTemperature())) {
                plant.setGrowingInfo(growingInfo);
                bestValue = growingInfo.value;
            }
        }
        return plant.getGrowingInfo() != null;
    }

    private boolean canGrow(ItemInfo.ItemInfoPlant.GrowingInfo infoEntry, double light, double temperature) {
        if (infoEntry.light != null && (light < infoEntry.light[0] || light > infoEntry.light[1])) {
            return false;
        }

        if (infoEntry.temperature != null && (temperature < infoEntry.temperature[0] || temperature > infoEntry.temperature[1])) {
            return false;
        }

        return true;
    }

    public Collection<PlantItem> getPlants() {
        return _plants;
    }

    public PlantItem getPlant(ParcelModel parcel) {
        Optional<PlantItem> optional = _plants.stream().filter(plant -> plant.getParcel() == parcel).findAny();
        return optional.isPresent() ? optional.get() : null;
    }

    private PlantItem putPlant(ParcelModel parcel, ItemInfo itemInfo, int matterSupply) {
        // Put item on floor
        PlantItem plant = new PlantItem(itemInfo);
        for (int i = 0; i < plant.getWidth(); i++) {
            for (int j = 0; j < plant.getHeight(); j++) {
                parcel.setPlant(plant);
                plant.setParcel(parcel);
            }
        }
        _plants.add(plant);

        Application.pathManager.resetAround(parcel);

//        notifyObservers(observer -> observer.onAddPlant(plant));

        return plant;
    }

    public void removePlant(PlantItem plant) {
        if (plant != null) {
            if (plant.getParcel().getPlant() == plant) {
                plant.getParcel().setPlant(null);
            }

            // TODO ?
            if (plant.getInfo().plant != null) {
                _plants.remove(plant);
            }

            _plants.remove(plant);

            Application.pathManager.resetAround(plant.getParcel());

//            notifyObservers(observer -> observer.onRemovePlant(plant));
        }
    }

}
