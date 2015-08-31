package org.smallbox.faraway.game;

import org.smallbox.faraway.game.model.area.AreaType;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.*;
import org.smallbox.faraway.game.module.extra.QuestModule;

/**
 * Created by Alex on 06/06/2015.
 */
public interface GameObserver {
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
    default void onOpenQuest(QuestModule.QuestModel quest) {}
    default void onCloseQuest(QuestModule.QuestModel quest) {}
    default void onSelectCharacter(CharacterModel character) {}
    default void onSelectParcel(ParcelModel parcel) {}
    default void onSelectItem(ItemModel item) {}
    default void onSelectResource(ResourceModel resource) {}
    default void onSelectConsumable(ConsumableModel consumable) {}
    default void onSelectStructure(StructureModel structure) {}
    default void onDeselect() {}
    default void onStartGame() {}
    default void onLog(String tag, String message) {}
    default void onAddArea(AreaType type, int fromX, int fromY, int toX, int toY) {}
    default void onRemoveArea(AreaType type, int fromX, int fromY, int toX, int toY) {}
}
