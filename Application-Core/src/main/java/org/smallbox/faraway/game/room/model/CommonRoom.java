package org.smallbox.faraway.game.room.model;

import org.smallbox.faraway.game.world.Parcel;

@RoomTypeInfo(label = "Common")
public class CommonRoom extends RoomModel {

    public CommonRoom(RoomType type, int floor, Parcel baseParcel) {
        super(type, floor, baseParcel);
    }

}
