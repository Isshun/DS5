package org.smallbox.faraway.modules.item;

import org.smallbox.faraway.core.engine.module.ModuleObserver;
import org.smallbox.faraway.core.module.world.model.Parcel;

public interface ItemModuleObserver extends ModuleObserver {
    default void onAddItem(Parcel parcel, UsableItem item) {}
    default void onRemoveItem(Parcel parcel, UsableItem item) {}
}
