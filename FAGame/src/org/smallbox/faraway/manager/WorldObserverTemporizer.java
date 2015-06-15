package org.smallbox.faraway.manager;

import org.smallbox.faraway.GameObserver;
import org.smallbox.faraway.model.item.ConsumableModel;
import org.smallbox.faraway.model.item.ItemModel;
import org.smallbox.faraway.model.item.ResourceModel;
import org.smallbox.faraway.model.item.StructureModel;

/**
 * Created by Alex on 13/06/2015.
 */
public class WorldObserverTemporizer implements GameObserver {

    @Override
    public void onAddStructure(StructureModel structure){}

    @Override
    public void onAddItem(ItemModel item){}

    @Override
    public void onAddConsumable(ConsumableModel consumable){}

    @Override
    public void onAddResource(ResourceModel resource) {}

    @Override
    public void onRemoveItem(ItemModel item){}

    @Override
    public void onRemoveConsumable(ConsumableModel consumable){}

    @Override
    public void onRemoveStructure(StructureModel structure){}

    @Override
    public void onRemoveResource(){}
}
