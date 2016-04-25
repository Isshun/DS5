package org.smallbox.faraway.core.game.module.world.model;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;

/**
 * Created by Alex on 11/07/2015.
 */
public class ComponentModel {
    public final ItemInfo           info;
    public final int                neededQuantity;
    public int                      availableQuantity;

    public ComponentModel(ItemInfo info, int quantity) {
        this.info = info;
        this.neededQuantity = quantity;
    }
}
