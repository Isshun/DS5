package org.smallbox.faraway.core.game.module.world.model.item;

import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;

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
