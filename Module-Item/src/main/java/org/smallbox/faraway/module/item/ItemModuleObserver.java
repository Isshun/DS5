package org.smallbox.faraway.module.item;

import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.core.engine.module.ModuleObserver;
import org.smallbox.faraway.core.module.world.model.ParcelModel;

/**
 * Created by Alex on 19/07/2016.
 */
public interface ItemModuleObserver extends ModuleObserver {
    default void onAddItem(ParcelModel parcel, ItemModel item) {}
    default void onRemoveItem(ParcelModel parcel, ItemModel item) {}
    default void onSelectItem(GameEvent event, ItemModel item) {}
    default void onDeselectItem(ItemModel item) {}
}
