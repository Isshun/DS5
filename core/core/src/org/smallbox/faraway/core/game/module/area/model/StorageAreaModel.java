package org.smallbox.faraway.core.game.module.area.model;

import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.ItemInfo;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.util.Utils;

/**
 * Created by Alex on 13/06/2015.
 */
public class StorageAreaModel extends AreaModel {
    private static int  _count;

    private int         _nb;
    private int         _priority;

    public StorageAreaModel() {
        super(AreaType.STORAGE);

        _nb = ++_count;

        for (ItemInfo itemInfo: GameData.getData().items) {
            if (itemInfo.isConsumable) {
                setAccept(itemInfo, false);
            }
        }
    }

    public ParcelModel getNearestFreeParcel(ConsumableModel consumable) {
        return getNearestFreeParcel(consumable, consumable.getParcel());
    }

    public ParcelModel getNearestFreeParcel(ConsumableModel consumable, ParcelModel consumableParcel) {
        int bestDistance = Integer.MAX_VALUE;
        ParcelModel bestParcel = null;
        for (ParcelModel parcel: _parcels) {
            // Storage parcel have similar consumable
            if (parcel.getConsumable() != null && parcel.getConsumable().getInfo() == consumable.getInfo() && parcel.getConsumable().getQuantity() < Math.max(GameData.config.storageMaxQuantity, consumable.getInfo().stack)) {
                bestParcel = parcel;
                break;
            }

            // Storage parcel is free
            if (parcel.getConsumable() == null && parcel.getItem() == null && parcel.getResource() == null && (parcel.getStructure() == null || parcel.getStructure().isFloor())) {
                int distance = Utils.getDistance(parcel, consumableParcel);
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
        return "Storage Area #" + _nb;
    }

    public int getPriority() { return _priority; }
    public void setPriority(int priority) { _priority = priority; }

    public boolean hasFreeSpace(ItemInfo info, int quantity) {
        return true;
    }
}
