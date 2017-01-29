package org.smallbox.faraway.core.game;

import org.smallbox.faraway.core.game.model.planet.PlanetInfo;
import org.smallbox.faraway.core.game.modelInfo.BindingInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.area.model.AreaType;
import org.smallbox.faraway.core.module.area.model.StorageAreaModel;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.core.module.world.model.StructureItem;

/**
 * Created by Alex on 06/06/2015.
 */
public interface GameObserver {
    default void onObjectComplete(MapObjectModel mapObjectModel){}
    default void onChangeGround(ParcelModel parcel){}
//    default void onRemoveItem(ParcelModel parcel, UsableItem item){}
//    default void onRemoveConsumable(ConsumableItem consumable){}
//    default void onRemoveStructure(ParcelModel parcel, StructureItem structure){}
//    default void onRemovePlant(PlantModel plant){}
    default void onRemoveRock(ParcelModel parcel){}
    default void onRefreshStructure(StructureItem structure) {}
    default void onHourChange(int hour){}
    default void onDayChange(int day) {}
    default void onYearChange(int year) {}
    default void onLog(String tag, String message) {}
    default void onAddArea(AreaType type, int fromX, int fromY, int toX, int toY, int floor) {}
    default void onRemoveArea(AreaType type, int fromX, int fromY, int toX, int toY, int floor) {}
    default void onJobCreate(JobModel job) {}
    default void onCustomEvent(String tag, Object object) {}
    default void onStorageRulesChanged(StorageAreaModel storageAreaModel) {}
    default void onDayTimeChange(PlanetInfo.DayTime dayTime) {}
    default void onSpeedChange(int speed) {}
    default void onBindingPress(BindingInfo binding) {}
    default void onGamePaused() {}
    default void onGameResume() {}
    default void onGameStart(Game game) {}
    default void onGameCreate(Game game) {}
    default void onGameUpdate(Game game) {}
    default void onGameLoad(GameInfo gameInfo, GameInfo.GameSaveInfo gameSaveInfo) {}
    default void onInjectDependency(Object object) {}
    default void onDisplayChange(String displayName, boolean isVisible) {}
//    default void onOpenQuest(QuestModel quest) {}
    default void onCancelJobs(ParcelModel parcel, Object object) {}

    default void putObject(ParcelModel parcel, ItemInfo itemInfo, int data, boolean complete) {}
    default void removeObject(MapObjectModel mapObjectModel) {}
}