package org.smallbox.faraway.modules.world;

import org.smallbox.faraway.core.engine.module.ModuleObserver;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.module.world.model.Parcel;

public interface WorldModuleObserver extends ModuleObserver {
    default MapObjectModel putObject(Parcel parcel, ItemInfo itemInfo, int data, boolean complete) { return null; }

    default void onAddParcel(Parcel parcel) {}
    default void onOverParcel(Parcel parcel) {}

//    default void onMouseMove(GameEvent event, int parcelX, int parcelY, int floor) {}
//    default void onMousePress(GameEvent event, int parcelX, int parcelY, int floor, GameEventListener.MouseButton button) {}
//    default void onMouseRelease(GameEvent event, int parcelX, int parcelY, int floor, GameEventListener.MouseButton button) {}
}
