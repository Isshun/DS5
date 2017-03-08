package org.smallbox.faraway.modules.consumable;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.modules.area.AreaTypeInfo;
import org.smallbox.faraway.modules.area.AreaModel;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.ParcelModel;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Alex on 13/06/2015.
 */
@AreaTypeInfo(label = "Storage")
public class StorageArea extends AreaModel {
    private static int                  _count;
    private int                         _index;
    private int                         _priority = 1;
    protected Map<ItemInfo, Boolean>    _items;

    public StorageArea() {
        _index = ++_count;
        _items = Application.data.consumables.stream().collect(Collectors.toMap(item -> item, b -> false));
    }

    @Override
    public void setAccept(ItemInfo itemInfo, boolean isAccepted) {
        _items.put(itemInfo, isAccepted);
        Application.notify(observer -> observer.onStorageRulesChanged(this));
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

//    public ParcelModel getFreeParcel(ConsumableItem consumable) {
//        ParcelModel bestParcel = null;
//        for (ParcelModel parcel: _parcels) {
//            if (parcel.getItem() == null && parcel.getPlant() == null && (parcel.getStructure() == null || parcel.getStructure().isFloor())) {
//                if (parcel.getConsumable() == null && bestParcel == null) {
//                    bestParcel = parcel;
//                }
//                if (parcel.getConsumable() != null && parcel.getConsumable().getInfo() == consumable.getInfo() && parcel.getConsumable().getQuantity() < consumable.getInfo().stack) {
//                    return parcel;
//                }
//            }
//        }
//        return bestParcel;
//    }

    public ParcelModel getNearestFreeParcel(ConsumableItem consumable) {
        for (ParcelModel parcel: _parcels) {
            if (parcel.hasItem(ConsumableItem.class) && parcel.accept(consumable.getInfo(), 1)) {
                return parcel;
            }
        }
        for (ParcelModel parcel: _parcels) {
            if (parcel.accept(consumable.getInfo(), 1)) {
                return parcel;
            }
        }
        return null;
    }

//    public ParcelModel getNearestFreeParcel(ConsumableItem consumable, ParcelModel consumableParcel) {
//        int bestDistance = Integer.MAX_VALUE;
//        ParcelModel bestParcel = null;
//        for (ParcelModel parcel: _parcels) {
//            // Storage parcel have similar consumable
//            if (parcel.getConsumable() != null && parcel.getConsumable().getInfo() == consumable.getInfo() && parcel.getConsumable().getQuantity() < Math.max(Application.config.game.storageMaxQuantity, consumable.getInfo().stack)) {
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

//    public boolean hasFreeSpace(ItemInfo info, int quantity) {
//        for (ParcelModel parcel: _parcels) {
//            if (!parcel.hasConsumable() || info.stack - parcel.getItem(ConsumableItem.class).getQuantity() >= quantity) {
//                return true;
//            }
//        }
//        return false;
//    }
}