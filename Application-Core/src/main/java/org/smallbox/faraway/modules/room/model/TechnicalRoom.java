package org.smallbox.faraway.modules.room.model;

import org.smallbox.faraway.core.module.world.model.Parcel;

@RoomTypeInfo(label = "Technical")
public class TechnicalRoom extends RoomModel {

    public TechnicalRoom(RoomType type, int floor, Parcel baseParcel) {
        super(type, floor, baseParcel);
    }

}
