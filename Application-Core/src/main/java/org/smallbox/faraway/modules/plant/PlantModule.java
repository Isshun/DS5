package org.smallbox.faraway.modules.plant;

import org.smallbox.faraway.GameTaskManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;
import org.smallbox.faraway.core.engine.module.GenericGameModule;
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

import java.util.Optional;

@GameObject
public class PlantModule extends GenericGameModule<PlantItem> {

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

    @Inject
    private HarvestJobFactory harvestJobFactory;

    @OnInit
    public void init() {
        areaModule.addAreaClass(GardenArea.class);
    }

    @Override
    public void onModuleUpdate(Game game) {
        // Fait pousser les plantes
        modelList.stream()
                .filter(plant -> plant.task == null && plant.hasSeed() && computeGrowingInfo(plant))
                .forEach(plant -> gameTaskManager.startTask(new PlantGrowTask(plant)));

        // Fait pousser les plantes
        modelList.stream()
                .filter(plant -> plant.hasSeed() && computeGrowingInfo(plant))
                .forEach(plant -> plant.grow(getHourInterval()));

        // Récolte les plantes arrivées à maturité
        modelList.stream()
                .filter(plant -> plant.getJob() == null)
                .filter(plant -> plant.getMaturity() >= 1)
                .forEach(plant -> jobModule.add(harvestJobFactory.create(plant)));

        // TODO: ajout auto de la graine
        modelList.forEach(plant -> plant.setSeed(true));
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
        modelList.removeIf(plant -> plant.getParcel() == parcel);

        // Add new one
        PlantItem plant = new PlantItem(item);
        plant.setParcel(parcel);
        plant.setMaturity(0);
        modelList.add(plant);
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

    public PlantItem getPlant(ParcelModel parcel) {
        Optional<PlantItem> optional = modelList.stream().filter(plant -> plant.getParcel() == parcel).findAny();
        return optional.orElse(null);
    }

}
