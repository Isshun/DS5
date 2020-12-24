package org.smallbox.faraway.modules.consumable;

import org.smallbox.faraway.core.engine.module.ModuleObserver;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.ParcelModel;

public interface ConsumableModuleObserver extends ModuleObserver {
    default void onAddConsumable(ParcelModel parcel, ConsumableItem consumable) {}
    default void onUpdateQuantity(ParcelModel parcel, ConsumableItem consumable, int quantityBefore, int quantityAfter) {}
    default void onRemoveConsumable(ParcelModel parcel, ConsumableItem consumable) {}
    default void onSelectConsumable(ConsumableItem consumable) {}
    default void onDeselectConsumable(ConsumableItem consumable) {}
}
