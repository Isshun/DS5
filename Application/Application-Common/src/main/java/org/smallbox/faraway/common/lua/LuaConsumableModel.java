package org.smallbox.faraway.common.lua;

import org.smallbox.faraway.common.modelInfo.ItemInfo;

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
