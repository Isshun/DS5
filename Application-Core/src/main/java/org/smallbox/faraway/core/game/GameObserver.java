package org.smallbox.faraway.core.game;

import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.model.planet.PlanetInfo;
import org.smallbox.faraway.core.game.modelInfo.BindingInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.area.model.AreaModel;
import org.smallbox.faraway.core.module.area.model.AreaType;
import org.smallbox.faraway.core.module.area.model.StorageAreaModel;
import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.core.module.world.model.StructureModel;

/**
 * Created by Alex on 06/06/2015.
 */
public interface GameObserver {
    default void onReloadUI(){}
    default void onRefreshUI(int frame){}
    default void onObjectComplete(MapObjectModel mapObjectModel){}
    default void onChangeGround(ParcelModel parcel){}
//    default void onRemoveItem(ParcelModel parcel, ItemModel item){}
//    default void onRemoveConsumable(ConsumableModel consumable){}
//    default void onRemoveStructure(ParcelModel parcel, StructureModel structure){}
//    default void onRemovePlant(PlantModel plant){}
    default void onRemoveRock(ParcelModel parcel){}
    default void onRefreshStructure(StructureModel structure) {}
    default void onHourChange(int hour){}
    default void onDayChange(int day) {}
    default void onYearChange(int year) {}
    default void onSelectArea(AreaModel area) {}
    default boolean onSelectCharacter(CharacterModel character) {return false;}
    default boolean onSelectParcel(ParcelModel parcel) {return false;}
//    default void onOverParcel(ParcelModel parcel) {}
    default void onDeselect() {}
    default void onLog(String tag, String message) {}
    default void onAddArea(AreaType type, int fromX, int fromY, int toX, int toY, int floor) {}
    default void onRemoveArea(AreaType type, int fromX, int fromY, int toX, int toY, int floor) {}
    default void onJobCreate(JobModel job) {}
    default void onCustomEvent(String tag, Object object) {}
    default void onKeyPress(GameEventListener.Key key) {}
    default void onKeyEvent(GameEventListener.Action action, GameEventListener.Key key, GameEventListener.Modifier modifier) {}
    default void onStorageRulesChanged(StorageAreaModel storageAreaModel) {}
    default void onDayTimeChange(PlanetInfo.DayTime dayTime) {}
    default void onSpeedChange(int speed) {}
    default void onBindingPress(BindingInfo binding) {}
    default void onGamePaused() {}
    default void onGameResume() {}
    default void onGameStart(Game game) {}
    default void onGameCreate(Game game) {}
    default void onGameUpdate(Game game) {}
    default void onGameRender(Game game) {}
    default void onGameLoad(GameInfo gameInfo, GameInfo.GameSaveInfo gameSaveInfo) {}
    default void onInjectDependency(Object object) {}
    default void onFloorUp() {}
    default void onFloorDown() {}
    default void onFloorChange(int floor) {}
    default void onDisplayChange(String displayName, boolean isVisible) {}
//    default void onOpenQuest(QuestModel quest) {}
    default void onCancelJobs(ParcelModel parcel, Object object) {}

    default void putObject(ParcelModel parcel, ItemInfo itemInfo, int data, boolean complete) {}
    default void removeObject(MapObjectModel mapObjectModel) {}

    default void onMouseMove(GameEvent event) {}
    default void onMousePress(GameEvent event) {}
    default void onMouseRelease(GameEvent event) {}

    default void onClickOnMap(GameEvent mouseEvent) {}
    default void onClickOnParcel(ParcelModel parcel) {}

    default void onClick(int x, int y) {}
}
