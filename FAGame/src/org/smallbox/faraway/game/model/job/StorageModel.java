package org.smallbox.faraway.game.model.job;

import org.smallbox.faraway.util.Utils;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.item.ConsumableModel;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.ui.AreaModel;
import org.smallbox.faraway.ui.AreaType;

/**
 * Created by Alex on 13/06/2015.
 */
public class StorageModel extends AreaModel {

    public StorageModel(AreaType type) {
        super(type);
    }

    public ParcelModel getNearestFreeParcel(ConsumableModel consumable, int x, int y) {
        int bestDistance = Integer.MAX_VALUE;
        ParcelModel bestParcel = null;
        for (ParcelModel parcel: _parcels) {
            if (parcel.getConsumable() == null || (parcel.getConsumable().getInfo() == consumable.getInfo() && parcel.getConsumable().getQuantity() + consumable.getQuantity() <= GameData.config.storageMaxQuantity)) {
                int distance = Utils.getDistance(parcel, x, y);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestParcel = parcel;
                }
            }
        }
        return bestParcel;
    }

    @Override
    public boolean isStorage() {
        return true;
    }

}
