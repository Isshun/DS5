package org.smallbox.faraway.game.consumable;

import org.smallbox.faraway.core.module.ModuleObserver;
import org.smallbox.faraway.game.world.Parcel;

public interface ConsumableModuleObserver extends ModuleObserver {
    default void onAddConsumable(Parcel parcel, Consumable consumable) {}
    default void onUpdateQuantity(Parcel parcel, Consumable consumable, int quantityBefore, int quantityAfter) {}
    default void onRemoveConsumable(Parcel parcel, Consumable consumable) {}
    default void onSelectConsumable(Consumable consumable) {}
    default void onDeselectConsumable(Consumable consumable) {}
}
