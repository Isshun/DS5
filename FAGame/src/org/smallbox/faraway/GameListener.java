package org.smallbox.faraway;

import org.smallbox.faraway.model.item.ConsumableModel;
import org.smallbox.faraway.model.item.ResourceModel;
import org.smallbox.faraway.model.item.StructureModel;
import org.smallbox.faraway.model.item.ItemModel;

/**
 * Created by Alex on 06/06/2015.
 */
public class GameListener {
    public void onAddStructure(StructureModel structure){}
    public void onAddItem(ItemModel item){}
    public void onAddConsumable(ConsumableModel consumable){}
    public void onAddResource(ResourceModel resource) {}
    public void onRemoveItem(){}
    public void onRemoveConsumable(){}
    public void onRemoveStructure(){}
    public void onRemoveResource(){}
}
