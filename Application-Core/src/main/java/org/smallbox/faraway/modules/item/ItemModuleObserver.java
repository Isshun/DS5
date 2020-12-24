package org.smallbox.faraway.modules.item;

import org.smallbox.faraway.core.engine.module.ModuleObserver;
import org.smallbox.faraway.core.module.world.model.ParcelModel;

public interface ItemModuleObserver extends ModuleObserver {
    default void onAddItem(ParcelModel parcel, UsableItem item) {}
    default void onRemoveItem(ParcelModel parcel, UsableItem item) {}
}
