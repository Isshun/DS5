package org.smallbox.faraway.module.flora;

import org.smallbox.faraway.core.module.ModuleObserver;
import org.smallbox.faraway.core.world.model.PlantModel;

public interface FloraModuleObserver extends ModuleObserver {
    void onRemovePlant(PlantModel plant);
    void onAddPlant(PlantModel plant);
}
