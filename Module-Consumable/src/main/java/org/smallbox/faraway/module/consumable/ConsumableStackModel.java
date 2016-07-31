package org.smallbox.faraway.module.consumable;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by Alex on 29/07/2016.
 */
public class ConsumableStackModel {
    private final Collection<ConsumableModel>   _consumables;
    private final ItemInfo                      _itemInfo;
    private final int                           _maxStack;

    public ConsumableStackModel(ItemInfo itemInfo) {
        _consumables = new LinkedList<>();
        _itemInfo = itemInfo;
        _maxStack = 100;
    }

    public Collection<ConsumableModel> getConsumables() { return _consumables; }
    public ItemInfo getItemInfo() { return _itemInfo; }
    public int getMaxStack() { return _maxStack; }

    public void addConsumable(ConsumableModel consumable) {
        _consumables.add(consumable);
    }
}
