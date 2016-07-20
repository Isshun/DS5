package org.smallbox.faraway.module.item;

import org.smallbox.faraway.core.engine.module.ModuleObserver;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;

/**
 * Created by Alex on 19/07/2016.
 */
public interface ItemModuleObserver extends ModuleObserver {
    void onAddConsumable(ParcelModel parcel, ConsumableModel consumable);
    void onRemoveConsumable(ParcelModel parcel, ConsumableModel consumable);
}
