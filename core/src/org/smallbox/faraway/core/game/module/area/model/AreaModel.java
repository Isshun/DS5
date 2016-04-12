package org.smallbox.faraway.core.game.module.area.model;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.util.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Alex on 13/06/2015.
 */
public abstract class AreaModel {
    protected final List<ParcelModel>   _parcels = new ArrayList<>();
    private final int                   _typeIndex;
    private final int                   _id;
    private final AreaType              _type;
    private int                         _x;
    private int                         _y;
    private int                         _z;

    public AreaModel(AreaType type) {
        _id = Utils.getUUID();
        _type = type;
        _typeIndex = type.ordinal();
    }

    public void addParcel(ParcelModel parcel) {
        if (!_parcels.contains(parcel)) {
            _parcels.add(parcel);
            parcel.setArea(this);
            _z = parcel.z;
            _x = parcel.x;
            _y = parcel.y;
        }
    }

    public boolean contains(int x, int y, int z) {
        if (_z == z) {
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

    public abstract boolean         accept(ItemInfo itemInfo);
    public abstract void            setAccept(ItemInfo itemInfo, boolean isAccepted);
    public void                     setFloor(int floor) { _z = floor; }

    public Collection<ParcelModel>  getParcels() { return _parcels; }
    public String                   getName() { return "Area #n"; }
    public AreaType                 getType() { return _type; }
    public String                   getTypeName() { return _type.name(); }
    public int                      getTypeIndex() { return _typeIndex; }
    public int                      getX() { return _x; }
    public int                      getY() { return _y; }
    public int                      getFloor() { return _z; }
    public int                      getId() { return _id; }

    public boolean                  isStorage() { return false; }
    public boolean                  isHome() { return false; }
    public boolean                  isEmpty() { return _parcels.isEmpty(); }

    public void removeParcel(ParcelModel parcel) {
        _parcels.remove(parcel);
    }
}