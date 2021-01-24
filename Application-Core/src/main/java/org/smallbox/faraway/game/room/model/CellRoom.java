package org.smallbox.faraway.game.room.model;

import org.smallbox.faraway.game.world.Parcel;

@RoomTypeInfo(label = "Cell")
public class CellRoom extends RoomModel {

    public CellRoom(RoomType type, int floor, Parcel baseParcel) {
        super(type, floor, baseParcel);
    }

}
