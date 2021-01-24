package org.smallbox.faraway.game.room.model;

import org.smallbox.faraway.game.world.Parcel;

@RoomTypeInfo(label = "Technical")
public class TechnicalRoom extends RoomModel {

    public TechnicalRoom(RoomType type, int floor, Parcel baseParcel) {
        super(type, floor, baseParcel);
    }

}
