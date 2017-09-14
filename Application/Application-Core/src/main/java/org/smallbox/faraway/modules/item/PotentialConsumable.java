package org.smallbox.faraway.modules.item;

import org.smallbox.faraway.common.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;

/**
 * Created by Alex on 17/10/2015.
 */
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
