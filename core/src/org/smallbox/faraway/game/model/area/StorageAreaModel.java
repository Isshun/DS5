package org.smallbox.faraway.game.model.area;

import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.item.ConsumableModel;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.util.Utils;

/**
 * Created by Alex on 13/06/2015.
 */
public class StorageAreaModel extends AreaModel {
    private static int _count;
    private int _nb;
    private int _priority;

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
//            if (parcel.getConsumable() == null || (parcel.getConsumable().getInfo() == consumable.getInfo() && parcel.getConsumable().getQuantity() + consumable.getQuantity() <= Math.max(GameData.config.storageMaxQuantity, consumable.getInfo().stack))) {
            if (parcel.getConsumable() == null || (parcel.getConsumable().getInfo() == consumable.getInfo() && parcel.getConsumable().getQuantity() < Math.max(GameData.config.storageMaxQuantity, consumable.getInfo().stack))) {
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
