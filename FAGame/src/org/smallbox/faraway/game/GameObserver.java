package org.smallbox.faraway.game;

import org.smallbox.faraway.game.manager.QuestManager;
import org.smallbox.faraway.game.model.item.ConsumableModel;
import org.smallbox.faraway.game.model.item.ItemModel;
import org.smallbox.faraway.game.model.item.ResourceModel;
import org.smallbox.faraway.game.model.item.StructureModel;

/**
 * Created by Alex on 06/06/2015.
 */
public interface GameObserver {
    default void onAddStructure(StructureModel structure){}
    default void onAddItem(ItemModel item){}
    default void onAddConsumable(ConsumableModel consumable){}
    default void onAddResource(ResourceModel resource) {}
    default void onRemoveItem(ItemModel item){}
    default void onRemoveConsumable(ConsumableModel consumable){}
    default void onRemoveStructure(StructureModel structure){}
    default void onRemoveResource(ResourceModel resource){}
    default void onHourChange(int hour){}
    default boolean hasBeenInitialized() {return true;}
    default void onOpenQuest(QuestManager.QuestModel quest) {}
    default void onCloseQuest(QuestManager.QuestModel quest) {}
}
