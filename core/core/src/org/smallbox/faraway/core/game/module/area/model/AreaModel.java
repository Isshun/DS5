package org.smallbox.faraway.core.game.module.area.model;

import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;

import java.util.*;

/**
 * Created by Alex on 13/06/2015.
 */
public class AreaModel {
    protected final Set<ParcelModel>    _parcels = new HashSet<>();
    private final int                   _typeIndex;
    protected Map<ItemInfo, Boolean>    _items;
    private final AreaType              _type;
    private int                         _x;
    private int                         _y;

    public AreaModel(AreaType type) {
        _type = type;
        _typeIndex = type.ordinal();
        _items = new HashMap<>();
    }

    public void addParcel(ParcelModel parcel) {
        if (!_parcels.contains(parcel)) {
            _parcels.add(parcel);
            parcel.setArea(this);
            _x = parcel.x;
            _y = parcel.y;
        }
    }

    public boolean contains(int x, int y) {
        for (ParcelModel parcel: _parcels) {
            if (parcel.x == x && parcel.y == y) {
                return true;
            }
        }
        return false;
    }

    public Collection<ParcelModel> getParcels() {
        return _parcels;
    }

    public String getName() {
        return "Area #n";
    }

    public boolean isStorage() {
        return false;
    }

    public boolean isHome() {
        return false;
    }

    public AreaType getType() { return _type; }
    public int getTypeIndex() { return _typeIndex; }

    public void removeParcel(ParcelModel parcel) {
        _parcels.remove(parcel);
    }

    public int getX() {
        return _x;
    }

    public int getY() {
        return _y;
    }

    public boolean accept(ItemInfo itemInfo) {
        for (Map.Entry<ItemInfo, Boolean> entry: _items.entrySet()) {
            if (itemInfo.instanceOf(entry.getKey()) && entry.getValue()) {
                return true;
            }
        }
        return false;
    }

    public void setAccept(ItemInfo itemInfo, boolean isAccepted) {
        _items.put(itemInfo, isAccepted);
    }

    public Map<ItemInfo, Boolean> getItemsAccepts() {
        return _items;
    }

}
