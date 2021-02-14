package org.smallbox.faraway.game.plant;

import org.smallbox.faraway.client.gameAction.GameActionManager;
import org.smallbox.faraway.client.gameAction.GameActionMode;
import org.smallbox.faraway.client.shortcut.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnGameUpdate;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.ThreadManager;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.GenericGameModule;
import org.smallbox.faraway.game.area.AreaModule;
import org.smallbox.faraway.game.consumable.ConsumableModule;
import org.smallbox.faraway.game.job.JobModule;
import org.smallbox.faraway.game.plant.model.PlantItem;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.WorldHelper;
import org.smallbox.faraway.game.world.WorldModule;

import java.util.Optional;
import java.util.stream.IntStream;

@GameObject
public class PlantModule extends GenericGameModule<PlantItem> {
    @Inject private ThreadManager threadManager;
    @Inject private DataManager dataManager;
    @Inject private WorldModule worldModule;
    @Inject private ConsumableModule consumableModule;
    @Inject private JobModule jobModule;
    @Inject private AreaModule areaModule;
    @Inject private GameActionManager gameActionManager;
    @Inject private HarvestAction harvestAction;

    @OnInit
    public void init() {
        areaModule.addAreaClass(GardenArea.class);
    }

    @OnGameUpdate
    public void onGameUpdate() {
//        // Fait pousser les plantes
//        modelList.stream()
//                .filter(plant -> plant.task == null && plant.hasSeed() && computeGrowingInfo(plant))
//                .forEach(plant -> gameTaskManager.startTask(new PlantGrowTask(plant)));

        // Fait pousser les plantes
        modelList.stream()
                .filter(plant -> computeGrowingInfo(plant))
                .forEach(plant -> plant.grow(threadManager.getHourInterval()));

//        // Récolte les plantes arrivées à maturité
//        modelList.stream()
//                .filter(plant -> plant.getJob() == null)
//                .filter(plant -> plant.getMaturity() >= 1)
//                .forEach(plant -> jobModule.add(harvestJobFactory.create(plant)));

        // TODO: ajout auto de la graine
        modelList.forEach(plant -> plant.setSeed(true));
    }

    public void addPlant(String plantName, int x, int y, int z) { addPlant(dataManager.getItemInfo(plantName), WorldHelper.getParcel(x, y, z)); }
    public void addPlant(ItemInfo item, int x, int y, int z) { addPlant(item, WorldHelper.getParcel(x, y, z)); }
    public void addPlant(String plantName, Parcel parcel) { addPlant(dataManager.getItemInfo(plantName), parcel); }

    /**
     * Add plant on parcel
     *
     * @param item ItemInfo
     * @param parcel ParcelModel
     */
    public void addPlant(ItemInfo item, Parcel parcel) {

        // Remove existing plant
        modelList.removeIf(plant -> plant.getParcel() == parcel);

        // Add new one
        IntStream.range(0, item.plant.grid).forEach(gridPosition -> {
            PlantItem plant = new PlantItem(item);
            plant.setParcel(parcel);
            plant.setMaturity(0);
            plant.setGridPosition(gridPosition);
            modelList.add(plant);
        });
    }

    private boolean computeGrowingInfo(PlantItem plant) {
        Parcel parcel = plant.getParcel();
        plant.setGrowingInfo(null);
        double bestValue = -1;
        for (ItemInfo.ItemInfoPlant.GrowingInfo growingInfo: plant.getInfo().plant.states) {
            if (growingInfo.value > bestValue && canGrow(growingInfo, parcel.getLight(), worldModule.getTemperature(parcel))) {
                plant.setGrowingInfo(growingInfo);
                bestValue = growingInfo.value;
            }
        }

        ItemInfo.ItemInfoPlant.GrowingInfo growingInfo = plant.getInfo().plant.states.stream().filter(g -> g.name.equals("regular")).findFirst().orElse(null);

        if (growingInfo != null) {
            plant.setMaturity(plant.getMaturity() + 0.005 * growingInfo.value);
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

    public PlantItem getPlant(Parcel parcel) {
        Optional<PlantItem> optional = modelList.stream().filter(plant -> plant.getParcel() == parcel).findAny();
        return optional.orElse(null);
    }

    @GameShortcut("action/harvest")
    public void harvestMode() {
        gameActionManager.setAreaAction(GameActionMode.ADD_AREA, harvestAction);
    }

}
