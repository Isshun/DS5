package org.smallbox.faraway.core;

import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.ItemInfo;

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
