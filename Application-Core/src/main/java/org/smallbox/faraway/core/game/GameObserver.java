package org.smallbox.faraway.core.game;

import org.smallbox.faraway.core.game.model.planet.PlanetInfo;
import org.smallbox.faraway.core.game.modelInfo.BindingInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.module.world.model.Parcel;
import org.smallbox.faraway.core.module.world.model.StructureItem;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.storage.StorageArea;

public interface GameObserver {
    default void onObjectComplete(MapObjectModel mapObjectModel){}
    default void onChangeGround(Parcel parcel){}
//    default void onRemoveItem(ParcelModel parcel, UsableItem item){}
//    default void onRemoveConsumable(ConsumableItem consumable){}
//    default void onRemoveStructure(ParcelModel parcel, StructureItem structure){}
//    default void onRemovePlant(PlantItem plant){}
    default void onRemoveRock(Parcel parcel){}
    default void onRefreshStructure(StructureItem structure) {}
    default void onLog(String tag, String message) {}
    default void onJobCreate(JobModel job) {}
    default void onCustomEvent(String tag, Object object) {}
    default void onStorageRulesChanged(StorageArea storageAreaModel) {}
    default void onDayTimeChange(PlanetInfo.DayTime dayTime) {}
    default void onBindingPress(BindingInfo binding) {}
    default void onGamePaused() {}
    default void onGameResume() {}
    default void onGameStart(Game game) {}
    default void onGameStop(Game game) {}
    default void onGameUpdate(Game game) {}
    default void onGameLongUpdate(Game game) {}
    default void onGameRender(Game game) {}
//    default void onInjectDependency(Object object) {}
    default void onDisplayChange(String displayName, boolean isVisible) {}
//    default void onOpenQuest(QuestModel quest) {}
    default void onCancelJobs(Parcel parcel, Object object) {}

    default void putObject(Parcel parcel, ItemInfo itemInfo, int data, boolean complete) {}
    default void removeObject(MapObjectModel mapObjectModel) {}
}
