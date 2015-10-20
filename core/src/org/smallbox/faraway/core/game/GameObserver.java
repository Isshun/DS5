package org.smallbox.faraway.core.game;

import org.smallbox.faraway.core.data.ReceiptGroupInfo;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.model.area.AreaModel;
import org.smallbox.faraway.core.game.model.area.AreaType;
import org.smallbox.faraway.core.game.model.character.base.CharacterModel;
import org.smallbox.faraway.core.game.model.item.*;
import org.smallbox.faraway.core.game.model.job.abs.JobModel;

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
    default void onSelectReceipt(ReceiptGroupInfo receipt) {}
    default void onOverParcel(ParcelModel parcel) {}
    default void onDeselect() {}
    default void onStartGame() {}
    default void onLog(String tag, String message) {}
    default void onAddArea(AreaType type, int fromX, int fromY, int toX, int toY) {}
    default void onRemoveArea(AreaType type, int fromX, int fromY, int toX, int toY) {}
    default void onJobCreate(JobModel job) {}
    default void onCustomEvent(String tag, Object object) {}
    default void onKeyPress(GameEventListener.Key key) {}
}
