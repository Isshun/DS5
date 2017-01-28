package org.smallbox.faraway.core.module.room.model;

import org.smallbox.faraway.core.module.world.model.ParcelModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 18/06/2015.
 */
public class RoomConnectionModel {
    public final RoomModel          _room;
    public final List<ParcelModel>  _parcels = new ArrayList<>();
    public double                   _permeability;

    public RoomConnectionModel(RoomModel room) {
        this._room = room;
    }

    public boolean isEmpty() { return _parcels.isEmpty(); }
    public RoomModel getRoom() { return _room; }
    public double getPermeability() { return _permeability; }
    public double getBorderSize() { return _parcels.size(); }
    public void addParcel(ParcelModel parcel) { _parcels.add(parcel); }
    public List<ParcelModel> getParcels() { return _parcels; }
}
