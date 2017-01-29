package org.smallbox.faraway.modules.item;

import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.core.engine.module.ModuleObserver;
import org.smallbox.faraway.core.module.world.model.ParcelModel;

/**
 * Created by Alex on 19/07/2016.
 */
public interface ItemModuleObserver extends ModuleObserver {
    default void onAddItem(ParcelModel parcel, UsableItem item) {}
    default void onRemoveItem(ParcelModel parcel, UsableItem item) {}
    default void onSelectItem(GameEvent event, UsableItem item) {}
    default void onDeselectItem(UsableItem item) {}
}
