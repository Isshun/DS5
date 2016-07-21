package org.smallbox.faraway.module.world;

import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.engine.module.ModuleObserver;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;

/**
 * Created by Alex on 18/07/2016.
 */
public interface WorldModuleObserver extends ModuleObserver {
    default MapObjectModel putObject(ParcelModel parcel, ItemInfo itemInfo, int data, boolean complete) { return null; }
    default void onAddParcel(ParcelModel parcel) {}
    default void onAddItem(ParcelModel parcel, ItemModel item) {}
    default void onRemoveItem(ParcelModel parcel, ItemModel item) {}

    default void onMouseMove(int parcelX, int parcelY, int floor) {}
    default void onMousePress(int parcelX, int parcelY, int floor, GameEventListener.MouseButton button) {}
    default void onMouseRelease(int parcelX, int parcelY, int floor, GameEventListener.MouseButton button) {}

    default void onOverParcel(ParcelModel parcel) {}
}
