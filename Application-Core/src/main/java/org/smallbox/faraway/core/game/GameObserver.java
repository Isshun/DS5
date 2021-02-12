package org.smallbox.faraway.core.game;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.world.model.MapObjectModel;
import org.smallbox.faraway.game.job.JobModel;
import org.smallbox.faraway.game.world.Parcel;

public interface GameObserver {
    default void onChangeGround(Parcel parcel){}
    default void onRemoveRock(Parcel parcel){}
    default void onJobCreate(JobModel job) {}
    default void onCustomEvent(String tag, Object object) {}
    default void onGamePaused() {}
    default void onGameResume() {}
    default void onGameStart(Game game) {}
    default void onGameStop(Game game) {}
    default void onGameUpdate() {}
    default void onGameLongUpdate(Game game) {}
    default void onDisplayChange(String displayName, boolean isVisible) {}
    default void onCancelJobs(Parcel parcel, Object object) {}

    default void putObject(Parcel parcel, ItemInfo itemInfo, int data, boolean complete) {}
    default void removeObject(MapObjectModel mapObjectModel) {}
}
