package org.smallbox.faraway.game.item;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.game.consumable.ConsumableItem;

public class PotentialConsumable {
    public final ItemInfo           itemInfo;
    public final ConsumableItem consumable;
    public final int                distance;

    public PotentialConsumable(ConsumableItem consumable, int distance) {
        this.itemInfo = consumable.getInfo();
        this.consumable = consumable;
        this.distance = distance;
    }
}
