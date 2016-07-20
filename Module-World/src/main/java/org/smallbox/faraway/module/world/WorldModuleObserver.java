package org.smallbox.faraway.module.world;

import org.smallbox.faraway.core.engine.module.ModuleObserver;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;

/**
 * Created by Alex on 18/07/2016.
 */
public interface WorldModuleObserver extends ModuleObserver {
    MapObjectModel putObject(ParcelModel parcel, ItemInfo itemInfo, int data, boolean complete);
    void onAddParcel(ParcelModel parcel);
    void onAddItem(ParcelModel parcel, ItemModel item);
    void onRemoveItem(ParcelModel parcel, ItemModel item);
}
