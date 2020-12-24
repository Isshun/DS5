package org.smallbox.faraway.core.engine.lua;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;

public class LuaConsumableModel {
    public ItemInfo     itemInfo;
    public int          quantity;

    public LuaConsumableModel(ItemInfo itemInfo, int quantity) {
        this.itemInfo = itemInfo;
        this.quantity = quantity;
    }
}
