package org.smallbox.faraway.modules.plant;

import org.smallbox.faraway.GameTaskManager;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.Inject;
import org.smallbox.faraway.core.dependencyInjector.GameObject;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.area.AreaModule;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.plant.model.PlantItem;
import org.smallbox.faraway.modules.world.WorldModule;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Alex on 02/03/2017.
 */
@GameObject
public class PlantModule extends GameModule {

    @Inject
    private Data data;

    @Inject
    private WorldModule worldModule;

    @Inject
    private ConsumableModule consumableModule;

    @Inject
    private JobModule jobModule;

    @Inject
    private AreaModule areaModule;

    @Inject
    private GameTaskManager gameTaskManager;

    private Collection<PlantItem> _plants = new ConcurrentLinkedQueue<>();

    @Override
    public void onGameCreate(Game game) {
        areaModule.addAreaClass(GardenArea.class);
    }

    @Override
    public void onModuleUpdate(Game game) {
        // Fait pousser les plantes
        _plants.stream()
                .filter(plant -> plant.task == null && plant.hasSeed() && computeGrowingInfo(plant))
                .forEach(plant -> gameTaskManager.startTask(new PlantGrowTask(plant)));

        // Fait pousser les plantes
        _plants.stream()
                .filter(plant -> plant.hasSeed() && computeGrowingInfo(plant))
                .forEach(plant -> plant.grow(getHourInterval()));

        // Récolte les plantes arrivées à maturité
        _plants.stream()
                .filter(plant -> plant.getJob() == null)
                .filter(plant -> plant.getMaturity() >= 1)
                .forEach(plant -> BasicHarvestJob.create(consumableModule, jobModule, plant));

        // TODO: ajout auto de la graine
        _plants.forEach(plant -> plant.setSeed(true));

        _plants.forEach(plant -> Application.gameServer.serialize("UPDATE", "PLANT", plant._id, plant));
    }

    public void addPlant(String plantName, int x, int y, int z) { addPlant(data.getItemInfo(plantName), WorldHelper.getParcel(x, y, z)); }
    public void addPlant(ItemInfo item, int x, int y, int z) { addPlant(item, WorldHelper.getParcel(x, y, z)); }
    public void addPlant(String plantName, ParcelModel parcel) { addPlant(data.getItemInfo(plantName), parcel); }

    /**
     * Add plant on parcel
     *
     * @param item ItemInfo
     * @param parcel ParcelModel
     */
    public void addPlant(ItemInfo item, ParcelModel parcel) {

        // Remove existing plant
        _plants.removeIf(plant -> plant.getParcel() == parcel);

        // Add new one
        PlantItem plant = new PlantItem(item);
        plant.setParcel(parcel);
        plant.setMaturity(0);
        _plants.add(plant);
    }

    private boolean computeGrowingInfo(PlantItem plant) {
        ParcelModel parcel = plant.getParcel();
        plant.setGrowingInfo(null);
        double bestValue = -1;
        for (ItemInfo.ItemInfoPlant.GrowingInfo growingInfo: plant.getInfo().plant.states) {
            if (growingInfo.value > bestValue && canGrow(growingInfo, parcel.getLight(), worldModule.getTemperature(parcel))) {
                plant.setGrowingInfo(growingInfo);
                bestValue = growingInfo.value;
            }
        }
        return plant.getGrowingInfo() != null;
    }

    private boolean canGrow(ItemInfo.ItemInfoPlant.GrowingInfo infoEntry, double light, double temperature) {

        // Check light
        if (infoEntry.light != null && (light < infoEntry.light[0] || light > infoEntry.light[1])) {
            return false;
        }

        // Check temperature
        if (infoEntry.temperature != null && (temperature < infoEntry.temperature[0] || temperature > infoEntry.temperature[1])) {
            return false;
        }

        // Return true if all checks pass
        return true;
    }

    public Collection<PlantItem> getPlants() {
        return _plants;
    }

    public PlantItem getPlant(ParcelModel parcel) {
        Optional<PlantItem> optional = _plants.stream().filter(plant -> plant.getParcel() == parcel).findAny();
        return optional.isPresent() ? optional.get() : null;
    }

}