package org.smallbox.faraway.game.item;

import org.smallbox.faraway.core.module.ModuleObserver;
import org.smallbox.faraway.game.world.Parcel;

public interface ItemModuleObserver extends ModuleObserver {
    default void onAddItem(Parcel parcel, UsableItem item) {}
    default void onRemoveItem(Parcel parcel, UsableItem item) {}
}
