package org.smallbox.faraway.modules.area;

import org.smallbox.faraway.common.ObjectModel;
import org.smallbox.faraway.common.UUIDUtils;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ParcelModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AreaModel extends ObjectModel {
    protected final List<ParcelModel>   _parcels = new ArrayList<>();
    private final int                   _id;
    private int                         _x;
    private int                         _y;
    private int                         _z;

    public AreaModel() {
        _id = UUIDUtils.getUUID();
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

    public abstract boolean isAccepted(ItemInfo itemInfo);
    public abstract void            setAccept(ItemInfo itemInfo, boolean isAccepted);
    public void                     setFloor(int floor) { _z = floor; }

    public Collection<ParcelModel>  getParcels() { return _parcels; }
    public String                   getName() { return "Area #n"; }
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

    public AreaTypeInfo getInfo() {
        return getClass().getAnnotation(AreaTypeInfo.class);
    }

    public boolean haveParcel(ParcelModel parcel) {
        return _parcels.contains(parcel);
    }

    public void execute(ParcelModel parcel) {

    }

    public boolean haveParcelNextTo(ParcelModel targetParcel) {
        return _parcels.stream().anyMatch(parcel -> WorldHelper.isSurroundedCross(parcel, targetParcel));
    }
}