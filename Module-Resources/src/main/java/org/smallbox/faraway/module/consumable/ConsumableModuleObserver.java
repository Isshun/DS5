package org.smallbox.faraway.module.consumable;

import org.smallbox.faraway.core.engine.module.ModuleObserver;
import org.smallbox.faraway.core.world.model.ConsumableModel;
import org.smallbox.faraway.core.world.model.ParcelModel;

public interface ConsumableModuleObserver extends ModuleObserver {
    void onAddConsumable(ParcelModel parcel, ConsumableModel consumable);
    void onRemoveConsumable(ParcelModel parcel, ConsumableModel consumable);
}
