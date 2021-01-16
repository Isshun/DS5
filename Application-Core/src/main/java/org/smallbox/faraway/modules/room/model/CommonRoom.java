package org.smallbox.faraway.modules.room.model;

import org.smallbox.faraway.core.module.world.model.Parcel;

@RoomTypeInfo(label = "Common")
public class CommonRoom extends RoomModel {

    public CommonRoom(RoomType type, int floor, Parcel baseParcel) {
        super(type, floor, baseParcel);
    }

}
