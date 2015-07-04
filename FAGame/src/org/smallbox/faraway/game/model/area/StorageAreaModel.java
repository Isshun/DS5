package org.smallbox.faraway.game.model.area;

import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.item.ConsumableModel;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.util.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 13/06/2015.
 */
public class StorageAreaModel extends AreaModel {

    public StorageAreaModel() {
        super(AreaType.STORAGE);

        for (ItemInfo itemInfo: GameData.getData().items) {
            if (itemInfo.isConsumable || itemInfo.isResource) {
                setAccept(itemInfo, false);
            }
        }
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

    @Override
    public String getName() {
        return "Storage Area #n";
    }

}
