package org.smallbox.faraway.module.flora;

import org.smallbox.faraway.core.engine.module.ModuleObserver;
import org.smallbox.faraway.core.game.module.world.model.PlantModel;

/**
 * Created by Alex on 18/07/2016.
 */
public interface FloraModuleObserver extends ModuleObserver {
    void onRemovePlant(PlantModel plant);
    void onAddPlant(PlantModel plant);
}
