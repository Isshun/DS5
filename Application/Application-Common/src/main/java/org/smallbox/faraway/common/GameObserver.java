package org.smallbox.faraway.common;

/**
 * Created by Alex on 06/06/2015.
 */
public interface GameObserver {
//    default void onObjectComplete(MapObjectModel mapObjectModel){}
//    default void onChangeGround(ParcelModel parcel){}
//    default void onRemoveItem(ParcelModel parcel, UsableItem item){}
//    default void onRemoveConsumable(ConsumableItem consumable){}
//    default void onRemoveStructure(ParcelModel parcel, StructureItem structure){}
//    default void onRemovePlant(PlantItem plant){}
//    default void onRemoveRock(ParcelModel parcel){}
//    default void onRefreshStructure(StructureItem structure) {}
//    default void onLog(String tag, String message) {}
//    default void onJobCreate(JobModel job) {}
//    default void onCustomEvent(String tag, Object object) {}
//    default void onStorageRulesChanged(StorageArea storageAreaModel) {}
//    default void onDayTimeChange(PlanetInfo.DayTime dayTime) {}
//    default void onBindingPress(BindingInfo binding) {}
    default void onGamePaused() {}
    default void onGameResume() {}
    default void onGameStart() {}
    default void onGameInit() {}
    default void onGameInitLayers() {}
    default void onGameUpdate() {}
    default void onInjectDependency(Object object) {}
//    default void onDisplayChange(String displayName, boolean isVisible) {}
//    default void onOpenQuest(QuestModel quest) {}
//    default void onCancelJobs(ParcelModel parcel, Object object) {}

//    default void putObject(ParcelModel parcel, ItemInfo itemInfo, int data, boolean complete) {}
//    default void removeObject(MapObjectModel mapObjectModel) {}
}
