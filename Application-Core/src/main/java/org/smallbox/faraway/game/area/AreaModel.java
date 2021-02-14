package org.smallbox.faraway.game.area;

import org.smallbox.faraway.client.gameAction.OnSelectParcelListener;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.game.world.ObjectModel;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.SurroundedPattern;
import org.smallbox.faraway.game.world.WorldHelper;
import org.smallbox.faraway.util.UUIDUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AreaModel extends ObjectModel implements OnSelectParcelListener {
    protected final List<Parcel>   _parcels = new ArrayList<>();
    private final int                   _id;
    private int                         _x;
    private int                         _y;
    private int                         _z;

    public AreaModel() {
        _id = UUIDUtils.getUUID();
    }

    public void addParcel(Parcel parcel) {
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
            for (Parcel parcel : _parcels) {
                if (parcel.x == x && parcel.y == y) {
                    return true;
                }
            }
        }
        return false;
    }

    public Parcel getBaseParcel() {
        return !_parcels.isEmpty() ? _parcels.get(0) : null;
    }

    public abstract boolean isAccepted(ItemInfo itemInfo);
    public abstract void            setAccept(ItemInfo itemInfo, boolean isAccepted);
    public void                     setFloor(int floor) { _z = floor; }

    public Collection<Parcel>  getParcels() { return _parcels; }
    public String                   getName() { return "Area #n"; }
    public int                      getX() { return _x; }
    public int                      getY() { return _y; }
    public int                      getFloor() { return _z; }
    public int                      getId() { return _id; }

    public boolean                  isStorage() { return false; }
    public boolean                  isHome() { return false; }
    public boolean                  isEmpty() { return _parcels.isEmpty(); }

    public void removeParcel(Parcel parcel) {
        _parcels.remove(parcel);
    }

    public AreaTypeInfo getInfo() {
        return getClass().getAnnotation(AreaTypeInfo.class);
    }

    public boolean haveParcel(Parcel parcel) {
        return _parcels.contains(parcel);
    }

    @Override
    public void onParcelSelected(Parcel parcel) {
    }

    public boolean haveParcelNextTo(Parcel targetParcel) {
        return _parcels.stream().anyMatch(parcel -> WorldHelper.isSurrounded(SurroundedPattern.CROSS, parcel, targetParcel));
    }
}