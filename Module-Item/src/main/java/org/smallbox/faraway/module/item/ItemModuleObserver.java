package org.smallbox.faraway.module.item;

import org.smallbox.faraway.core.engine.module.ModuleObserver;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.module.consumable.ConsumableModule;
import org.smallbox.faraway.module.item.item.ItemModel;

/**
 * Created by Alex on 19/07/2016.
 */
public interface ItemModuleObserver extends ModuleObserver {
    default void onAddItem(ParcelModel parcel, ItemModel item) {}
    default void onRemoveItem(ParcelModel parcel, ItemModel item) {}
    default void onSelectItem(ItemModel item) {}
}
