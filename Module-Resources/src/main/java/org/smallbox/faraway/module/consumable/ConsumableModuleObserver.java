package org.smallbox.faraway.module.consumable;

import org.smallbox.faraway.core.engine.module.ModuleObserver;
import org.smallbox.faraway.core.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;

/**
 * Created by Alex on 19/07/2016.
 */
public interface ConsumableModuleObserver extends ModuleObserver {
    void onAddConsumable(ParcelModel parcel, ConsumableModel consumable);
    void onRemoveConsumable(ParcelModel parcel, ConsumableModel consumable);
}
