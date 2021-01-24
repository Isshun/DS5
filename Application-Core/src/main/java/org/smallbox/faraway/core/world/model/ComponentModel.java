package org.smallbox.faraway.core.world.model;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;

public class ComponentModel {
    public final ItemInfo           info;
    public final int                neededQuantity;
    public int                      availableQuantity;

    public ComponentModel(ItemInfo info, int quantity) {
        this.info = info;
        this.neededQuantity = quantity;
    }
}
