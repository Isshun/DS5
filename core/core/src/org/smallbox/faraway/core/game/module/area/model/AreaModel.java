package org.smallbox.faraway.core.game.module.area.model;

import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;

import java.util.*;

/**
 * Created by Alex on 13/06/2015.
 */
public class AreaModel {
    protected final List<ParcelModel>   _parcels = new ArrayList<>();
    private final int                   _typeIndex;
    protected Map<ItemInfo, Boolean>    _items;
    private final AreaType              _type;
    private int                         _x;
    private int                         _y;
    private int                         _floor;

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

    public boolean contains(int x, int y, int z) {
        if (_floor == z) {
            for (ParcelModel parcel : _parcels) {
                if (parcel.x == x && parcel.y == y) {
                    return true;
                }
            }
        }
        return false;
    }

    public ParcelModel getBaseParcel() {
        return !_parcels.isEmpty() ? _parcels.get(0) : null;
    }

    public void                     setAccept(ItemInfo itemInfo, boolean isAccepted) { _items.put(itemInfo, isAccepted); }
    public void                     setFloor(int floor) { _floor = floor; }

    public Collection<ParcelModel>  getParcels() {
        return _parcels;
    }
    public Map<ItemInfo, Boolean>   getItemsAccepts() { return _items; }
    public String                   getName() { return "Area #n"; }
    public AreaType                 getType() { return _type; }
    public String                   getTypeName() { return _type.name(); }
    public int                      getTypeIndex() { return _typeIndex; }
    public int                      getX() { return _x; }
    public int                      getY() { return _y; }
    public int                      getFloor() { return _floor; }

    public boolean                  isStorage() { return false; }
    public boolean                  isHome() { return false; }

    public void removeParcel(ParcelModel parcel) {
        _parcels.remove(parcel);
    }

    public boolean accept(ItemInfo itemInfo) {
        for (Map.Entry<ItemInfo, Boolean> entry: _items.entrySet()) {
            if (itemInfo.instanceOf(entry.getKey()) && entry.getValue()) {
                return true;
            }
        }
        return false;
    }
}