package org.smallbox.faraway.core.game;

import org.smallbox.faraway.core.data.BindingInfo;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.model.WeatherInfo;
import org.smallbox.faraway.core.game.model.planet.PlanetInfo;
import org.smallbox.faraway.core.game.module.area.model.AreaModel;
import org.smallbox.faraway.core.game.module.area.model.AreaType;
import org.smallbox.faraway.core.game.module.area.model.StorageAreaModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.model.*;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.game.module.world.model.resource.ResourceModel;

/**
 * Created by Alex on 06/06/2015.
 */
public interface GameObserver {
    default void onReloadUI(){}
    default void onRefreshUI(){}
    default void onAddCharacter(CharacterModel character){}
    default void onAddStructure(StructureModel structure){}
    default void onAddItem(ItemModel item){}
    default void onAddConsumable(ConsumableModel consumable){}
    default void onAddResource(ResourceModel resource) {}
    default void onStructureComplete(StructureModel structure){}
    default void onItemComplete(ItemModel item){}
    default void onRemoveItem(ItemModel item){}
    default void onRemoveConsumable(ConsumableModel consumable){}
    default void onRemoveStructure(StructureModel structure){}
    default void onRemoveResource(ResourceModel resource){}
    default void onRefreshItem(ItemModel item) {}
    default void onRefreshStructure(StructureModel structure) {}
    default void onHourChange(int hour){}
    default void onDayChange(int day) {}
    default void onYearChange(int year) {}
//    default void onOpenQuest(QuestModel quest) {}
//    default void onCloseQuest(QuestModel quest) {}
    default void onSelectArea(AreaModel area) {}
    default void onSelectCharacter(CharacterModel character) {}
    default void onSelectParcel(ParcelModel parcel) {}
    default void onSelectItem(ItemModel item) {}
    default void onSelectResource(ResourceModel resource) {}
    default void onSelectConsumable(ConsumableModel consumable) {}
    default void onSelectStructure(StructureModel structure) {}
    default void onSelectNetwork(NetworkObjectModel network) {}
    default void onSelectReceipt(ReceiptGroupInfo receipt) {}
    default void onOverParcel(ParcelModel parcel) {}
    default void onDeselect() {}
    default void onGameStart() {}
    default void onLog(String tag, String message) {}
    default void onAddArea(AreaType type, int fromX, int fromY, int toX, int toY) {}
    default void onRemoveArea(AreaType type, int fromX, int fromY, int toX, int toY) {}
    default void onJobCreate(JobModel job) {}
    default void onCustomEvent(String tag, Object object) {}
    default void onKeyPress(GameEventListener.Key key) {}
    default void onWeatherChange(WeatherInfo weather) {}
    default void onTemperatureChange(double temperature) {}
    default void onLightChange(double light, long color) {}
    default void onStorageRulesChanged(StorageAreaModel storageAreaModel) {}
    default void onJobQuit(JobModel job, CharacterModel character) {}
    default void onJobFinish(JobModel job) {}
    default void onDayTimeChange(PlanetInfo.DayTime dayTime) {}
    default void onSpeedChange(int speed) {}
    default void onBindingPress(BindingInfo binding) {}
    default void onAddNetworkObject(NetworkObjectModel networkObject) {}
    default void onRemoveNetworkObject(NetworkObjectModel networkObject) {}
}
