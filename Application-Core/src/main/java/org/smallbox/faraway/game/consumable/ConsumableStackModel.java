package org.smallbox.faraway.game.consumable;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;

import java.util.Collection;
import java.util.LinkedList;

public class ConsumableStackModel {
    private final Collection<ConsumableItem>   _consumables;
    private final ItemInfo                      _itemInfo;
    private final int                           _maxStack;

    public ConsumableStackModel(ItemInfo itemInfo) {
        _consumables = new LinkedList<>();
        _itemInfo = itemInfo;
        _maxStack = 100;
    }

    public Collection<ConsumableItem> getConsumables() { return _consumables; }
    public ItemInfo getItemInfo() { return _itemInfo; }
    public int getMaxStack() { return _maxStack; }

    public void addConsumable(ConsumableItem consumable) {
        _consumables.add(consumable);
    }
}
