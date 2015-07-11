package org.smallbox.faraway.game.model.item;

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
