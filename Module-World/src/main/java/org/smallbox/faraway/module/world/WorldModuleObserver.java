package org.smallbox.faraway.module.world;

import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.engine.module.ModuleObserver;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.ui.MouseEvent;

/**
 * Created by Alex on 18/07/2016.
 */
public interface WorldModuleObserver extends ModuleObserver {
    default MapObjectModel putObject(ParcelModel parcel, ItemInfo itemInfo, int data, boolean complete) { return null; }

    default void onAddParcel(ParcelModel parcel) {}
    default void onOverParcel(ParcelModel parcel) {}

    default void onMouseMove(MouseEvent event, int parcelX, int parcelY, int floor) {}
    default void onMousePress(MouseEvent event, int parcelX, int parcelY, int floor, GameEventListener.MouseButton button) {}
    default void onMouseRelease(MouseEvent event, int parcelX, int parcelY, int floor, GameEventListener.MouseButton button) {}
}
