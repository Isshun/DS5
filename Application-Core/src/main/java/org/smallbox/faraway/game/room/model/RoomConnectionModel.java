package org.smallbox.faraway.game.room.model;

import org.smallbox.faraway.game.world.Parcel;

import java.util.ArrayList;
import java.util.List;

public class RoomConnectionModel {
    public final RoomModel          _room;
    public final List<Parcel>  _parcels = new ArrayList<>();
    public double                   _permeability;

    public RoomConnectionModel(RoomModel room) {
        this._room = room;
    }

    public boolean isEmpty() { return _parcels.isEmpty(); }
    public RoomModel getRoom() { return _room; }
    public double getPermeability() { return _permeability; }
    public double getBorderSize() { return _parcels.size(); }
    public void addParcel(Parcel parcel) { _parcels.add(parcel); }
    public List<Parcel> getParcels() { return _parcels; }
}
