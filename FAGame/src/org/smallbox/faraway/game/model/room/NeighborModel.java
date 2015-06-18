package org.smallbox.faraway.game.model.room;

import org.smallbox.faraway.game.model.item.ParcelModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 18/06/2015.
 */
public class NeighborModel {
    public RoomModel            room;
    public List<ParcelModel>    parcels;
    public double               sealing;

    public NeighborModel(RoomModel room) {
        this.room = room;
        this.parcels = new ArrayList<>();
    }
}
