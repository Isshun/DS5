package org.smallbox.faraway.core.game.module.area.model;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

/**
 * Created by Alex on 13/06/2015.
 */
public class StorageAreaModel extends AreaModel {
    private static int                  _count;
    private int                         _index;
    private int                         _priority = 1;
    protected Map<ItemInfo, Boolean>    _items;

    public StorageAreaModel() {
        super(AreaType.STORAGE);
        _index = ++_count;
        _items = Data.getData().consumables.stream().collect(Collectors.toMap(item -> item, b -> false));
    }

    @Override
    public void setAccept(ItemInfo itemInfo, boolean isAccepted) {
        _items.put(itemInfo, isAccepted);
        Application.getInstance().notify(observer -> observer.onStorageRulesChanged(this));
    }

    public Map<ItemInfo, Boolean>   getItemsAccepts() { return _items; }

    public boolean accept(ItemInfo itemInfo) {
        for (Map.Entry<ItemInfo, Boolean> entry: _items.entrySet()) {
            if (itemInfo.instanceOf(entry.getKey()) && entry.getValue()) {
                return true;
            }
        }
        return false;
    }

    public ParcelModel getNearestFreeParcel(ConsumableModel consumable) {
        return getNearestFreeParcel(consumable, consumable.getParcel());
    }

    public ParcelModel getFreeParcel(ConsumableModel consumable) {
        ParcelModel bestParcel = null;
        for (ParcelModel parcel: _parcels) {
            if (parcel.getItem() == null && parcel.getPlant() == null && (parcel.getStructure() == null || parcel.getStructure().isFloor())) {
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
        for (ParcelModel parcel: _parcels) {
            if (parcel.accept(consumable.getInfo(), 1)) {
                return parcel;
            }
        }
        return null;
    }

//    public ParcelModel getNearestFreeParcel(ConsumableModel consumable, ParcelModel consumableParcel) {
//        int bestDistance = Integer.MAX_VALUE;
//        ParcelModel bestParcel = null;
//        for (ParcelModel parcel: _parcels) {
//            // Storage parcel have similar consumable
//            if (parcel.getConsumable() != null && parcel.getConsumable().getInfo() == consumable.getInfo() && parcel.getConsumable().getQuantity() < Math.max(Application.getInstance().getConfig().game.storageMaxQuantity, consumable.getInfo().stack)) {
//                bestParcel = parcel;
//                break;
//            }
//
//            // Storage parcel is free
//            if (parcel.getConsumable() == null && parcel.getItem() == null && parcel.getPlant() == null && (parcel.getStructure() == null || parcel.getStructure().isFloor())) {
//                int distance = WorldHelper.getDistance(parcel, consumableParcel);
//                if (distance < bestDistance) {
//                    bestDistance = distance;
//                    bestParcel = parcel;
//                }
//            }
//        }
//        return bestParcel;
//    }

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
        return "Storage Area #" + _index;
    }

    public int getPriority() { return _priority; }
    public void setPriority(int priority) { _priority = priority; }

    public boolean hasFreeSpace(ItemInfo info, int quantity) {
        for (ParcelModel parcel: _parcels) {
            if (!parcel.hasConsumable() || info.stack - parcel.getConsumable().getQuantity() >= quantity) {
                return true;
            }
        }
        return false;
    }
}