package org.smallbox.faraway.engine.lua;

import org.smallbox.faraway.game.model.item.ItemInfo;

/**
 * Created by Alex on 20/06/2015.
 */
public class LuaConsumableModel {
    public ItemInfo     itemInfo;
    public int          quantity;

    public LuaConsumableModel(ItemInfo itemInfo, int quantity) {
        this.itemInfo = itemInfo;
        this.quantity = quantity;
    }
}
