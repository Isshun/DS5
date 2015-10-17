package org.smallbox.faraway;

import org.smallbox.faraway.game.model.item.ConsumableModel;
import org.smallbox.faraway.game.model.item.ItemInfo;

/**
 * Created by Alex on 17/10/2015.
 */
public class PotentialConsumable {
    public final ItemInfo           itemInfo;
    public final ConsumableModel    consumable;
    public final int                distance;

    public PotentialConsumable(ConsumableModel consumable, int distance) {
        this.itemInfo = consumable.getInfo();
        this.consumable = consumable;
        this.distance = distance;
    }
}
