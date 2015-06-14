package org.smallbox.faraway.model.job;

import org.smallbox.faraway.manager.Utils;
import org.smallbox.faraway.model.item.ConsumableModel;
import org.smallbox.faraway.model.item.ParcelModel;
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
            if (parcel.getConsumable() == null || parcel.getConsumable().getInfo() == consumable.getInfo()) {
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
