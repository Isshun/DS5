package org.smallbox.faraway.core.game;

import org.smallbox.faraway.game.world.Parcel;

public interface GameObserver {
    default void onCustomEvent(String tag, Object object) {}
    default void onCancelJobs(Parcel parcel, Object object) {}
}
