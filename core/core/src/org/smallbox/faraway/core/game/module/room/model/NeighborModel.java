package org.smallbox.faraway.core.game.module.room.model;

import org.smallbox.faraway.core.game.module.world.model.ParcelModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 18/06/2015.
 */
public class NeighborModel {
    public final RoomModel          _room;
    public final List<ParcelModel>  _parcels = new ArrayList<>();
    public double                   _borderValue;

    public NeighborModel(RoomModel room) {
        this._room = room;
    }

    public boolean isEmpty() { return _parcels.isEmpty(); }
    public RoomModel getRoom() { return _room; }
    public double getBorderValue() { return _borderValue; }
    public double getBorderSize() { return _parcels.size(); }
    public List<ParcelModel> getParcels() { return _parcels; }
}
