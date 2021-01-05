package org.smallbox.faraway.modules.plant;

import org.smallbox.faraway.GameSerializer;
import org.smallbox.faraway.GameTask;
import org.smallbox.faraway.modules.plant.model.PlantItem;

import static org.smallbox.faraway.util.Utils.hour;

public class PlantGrowTask extends GameTask {

    private final PlantItem plant;

    public PlantGrowTask(PlantItem plant) {
        super("PLANT_GROW", "Grow plant", hour(1));
        this.plant = plant;
    }

    @Override
    public void onStart() {
        plant.task = this;
    }

    @Override
    public void onUpdate() {
        plant.grow(0.5);
    }

    @Override
    public void onClose() {
        assert plant.task == this;

        plant.task = null;
    }

}
