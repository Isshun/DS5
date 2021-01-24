package org.smallbox.faraway.game.storage;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.game.consumable.ConsumableItem;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.area.AreaModel;
import org.smallbox.faraway.game.area.AreaTypeInfo;
import org.smallbox.faraway.game.consumable.ConsumableModule;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@GameObject
@AreaTypeInfo(label = "Storage", color = 0xff0000ff)
public class StorageArea extends AreaModel {
    private static int                  _count;
    private final int                         _index;
    private int                         _priority = 1;
    protected Set<ItemInfo> _items;
    @Inject private StorageModule storageModule;

    public StorageArea() {
        _index = ++_count;
        _items = new HashSet<>();
    }

    @Override
    public void setAccept(ItemInfo itemInfo, boolean isAccepted) {
        if (isAccepted) {
            _items.add(itemInfo);
        } else {
            _items.remove(itemInfo);
        }
    }

    public Collection<ItemInfo> getItemsAccepts() { return _items; }

    public boolean isAccepted(ItemInfo itemInfo) {
//        return _items.stream().anyMatch(itemInfo::instanceOf);
        return true;
    }

    public boolean isAccepted(Collection<ConsumableItem> consumables) {
//        return consumables.stream().allMatch(consumable -> isAccepted(consumable.getInfo()));
        return true;
    }

    public boolean isAccepted(ConsumableItem consumable) {
//        return consumables.stream().allMatch(consumable -> isAccepted(consumable.getInfo()));
        return true;
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

    public Parcel getNearestFreeParcel(ConsumableItem consumable) {
        for (Parcel parcel: _parcels) {
            if (parcel.hasItem(ConsumableItem.class) && parcel.accept(consumable.getInfo(), 1)) {
                return parcel;
            }
        }
        for (Parcel parcel: _parcels) {
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
    public void addParcel(Parcel parcel) {
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

    public boolean hasFreeSpace(ConsumableModule consumableModule, ItemInfo itemInfo, int quantity) {
        return _parcels.stream().anyMatch(parcel -> consumableModule.parcelAcceptConsumable(parcel, itemInfo, quantity));
    }

    @Override
    public void onParcelSelected(Parcel parcel) {
        storageModule.addParcel(parcel);
    }

//    public boolean hasFreeSpace(ItemInfo info, int quantity) {
//        for (ParcelModel parcel: _parcels) {
//            if (!parcel.hasConsumable() || info.stack - parcel.getItem(ConsumableItem.class).getQuantity() >= quantity) {
//                return true;
//            }
//        }
//        return false;
//    }
}
