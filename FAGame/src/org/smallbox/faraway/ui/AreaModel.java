package org.smallbox.faraway.ui;

import org.smallbox.faraway.model.item.ParcelModel;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Alex on 13/06/2015.
 */
public class AreaModel {
    protected final Set<ParcelModel>    _parcels = new HashSet<>();
    private final AreaType              _type;

    public AreaModel(AreaType type) {
        _type = type;
    }

    public void addParcel(ParcelModel parcel) {
        if (!_parcels.contains(parcel)) {
            _parcels.add(parcel);
            parcel.setArea(this);
        }
    }

    public boolean contains(int x, int y) {
        for (ParcelModel parcel: _parcels) {
            if (parcel.getX() == x && parcel.getY() == y) {
                return true;
            }
        }
        return false;
    }

    public Collection<ParcelModel> getParcels() {
        return _parcels;
    }

    public String getName() {
        return "Storage Area #n";
    }

    public boolean isStorage() {
        return false;
    }

    public AreaType getType() {
        return _type;
    }
}
