package org.smallbox.faraway.ui;

import org.smallbox.faraway.model.item.ParcelModel;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Alex on 13/06/2015.
 */
public class AreaModel {
    private final Set<ParcelModel>  _parcels = new HashSet<>();
    private final AreaType          _type;

    public AreaModel(AreaType type) {
        _type = type;
    }

    public void addParcel(ParcelModel parcel) {
        _parcels.add(parcel);
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
}
