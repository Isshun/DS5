package org.smallbox.faraway.core.game.module.area.model;

import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;

/**
 * Created by Alex on 13/06/2015.
 */
public class StorageAreaModel extends AreaModel {
    private static int  _count;

    private int         _nb;
    private int         _priority = 1;

    public StorageAreaModel() {
        super(AreaType.STORAGE);

        _nb = ++_count;

        Data.getData().consumables.forEach(itemInfo -> setAccept(itemInfo, false));
    }

    public ParcelModel getNearestFreeParcel(ConsumableModel consumable) {
        return getNearestFreeParcel(consumable, consumable.getParcel());
    }

    public ParcelModel getFreeParcel(ConsumableModel consumable) {
        ParcelModel bestParcel = null;
        for (ParcelModel parcel: _parcels) {
            if (parcel.getItem() == null && parcel.getResource() == null && (parcel.getStructure() == null || parcel.getStructure().isFloor())) {
                if (parcel.getConsumable() == null && bestParcel == null) {
                    bestParcel = parcel;
                }
                if (parcel.getConsumable() != null && parcel.getConsumable().getInfo() == consumable.getInfo() && parcel.getConsumable().getQuantity() < consumable.getInfo().stack) {
                    return parcel;
                }
            }
        }
        return bestParcel;
    }

    public ParcelModel getNearestFreeParcel(ConsumableModel consumable, ParcelModel consumableParcel) {
        int bestDistance = Integer.MAX_VALUE;
        ParcelModel bestParcel = null;
        for (ParcelModel parcel: _parcels) {
            // Storage parcel have similar consumable
            if (parcel.getConsumable() != null && parcel.getConsumable().getInfo() == consumable.getInfo() && parcel.getConsumable().getQuantity() < Math.max(Data.config.storageMaxQuantity, consumable.getInfo().stack)) {
                bestParcel = parcel;
                break;
            }

            // Storage parcel is free
            if (parcel.getConsumable() == null && parcel.getItem() == null && parcel.getResource() == null && (parcel.getStructure() == null || parcel.getStructure().isFloor())) {
                int distance = WorldHelper.getDistance(parcel, consumableParcel);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestParcel = parcel;
                }
            }
        }
        return bestParcel;
    }

    @Override
    public void setAccept(ItemInfo itemInfo, boolean isAccepted) {
        super.setAccept(itemInfo, isAccepted);
        Game.getInstance().notify(observer -> observer.onStorageRulesChanged(this));
    }

    @Override
    public void addParcel(ParcelModel parcel) {
        super.addParcel(parcel);

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
