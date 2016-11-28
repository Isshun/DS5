package org.smallbox.faraway.module.consumable;

import org.smallbox.faraway.core.engine.module.ModuleObserver;
import org.smallbox.faraway.core.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;

/**
 * Created by Alex on 19/07/2016.
 */
public interface ConsumableModuleObserver extends ModuleObserver {
    default void onAddConsumable(ParcelModel parcel, ConsumableModel consumable) {}
    default void onRemoveConsumable(ParcelModel parcel, ConsumableModel consumable) {}
    default void onSelectConsumable(ConsumableModel consumable) {}
    default void onDeselectConsumable(ConsumableModel consumable) {}
}
