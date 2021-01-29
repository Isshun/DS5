package org.smallbox.faraway.client.debug.interpreter.moduleInterpreter.impl;

import org.smallbox.faraway.client.debug.interpreter.moduleInterpreter.ConsoleCommand;
import org.smallbox.faraway.client.debug.interpreter.moduleInterpreter.ConsoleInterpreterBase;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.game.plant.PlantModule;
import org.smallbox.faraway.game.plant.model.PlantItem;
import org.smallbox.faraway.game.world.WorldModule;

@GameObject
public class PlantModuleConsoleInterpreter extends ConsoleInterpreterBase {
    @Inject private PlantModule plantModule;
    @Inject private WorldModule worldModule;
    @Inject private DataManager dataManager;

    @ConsoleCommand("add")
    public String getInfo() {
        PlantItem plantItem = new PlantItem(dataManager.getItemInfo("base.plant.tree_1"));
        plantItem.setParcel(worldModule.getRandom(worldModule.getFloors() - 1));
        plantModule.add(plantItem);
        return "done";
    }

    @ConsoleCommand("tree2")
    public String addTree2() {
        PlantItem plantItem = new PlantItem(dataManager.getItemInfo("base.plant.tree_2"));
        plantItem.setParcel(worldModule.getRandom(worldModule.getFloors() - 1));
        plantModule.add(plantItem);
        return "done";
    }

    @ConsoleCommand("wheat")
    public String addWheat() {
        PlantItem plantItem = new PlantItem(dataManager.getItemInfo("base.plant.wheat"));
        plantItem.setParcel(worldModule.getRandom(worldModule.getFloors() - 1));
        plantModule.add(plantItem);
        return "done";
    }

}
