package org.smallbox.faraway;

import org.smallbox.faraway.model.item.ConsumableModel;
import org.smallbox.faraway.model.item.ResourceModel;
import org.smallbox.faraway.model.item.StructureModel;
import org.smallbox.faraway.model.item.ItemModel;

/**
 * Created by Alex on 06/06/2015.
 */
public interface WorldObserver {
    default void onAddStructure(StructureModel structure){}
    default void onAddItem(ItemModel item){}
    default void onAddConsumable(ConsumableModel consumable){}
    default void onAddResource(ResourceModel resource) {}
    default void onRemoveItem(ItemModel item){}
    default void onRemoveConsumable(ConsumableModel consumable){}
    default void onRemoveStructure(StructureModel structure){}
    default void onRemoveResource(){}
}
