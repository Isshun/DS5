package org.smallbox.faraway.modules.room.model;

import org.smallbox.faraway.core.module.world.model.Parcel;

@RoomTypeInfo(label = "Cell")
public class CellRoom extends RoomModel {

    public CellRoom(RoomType type, int floor, Parcel baseParcel) {
        super(type, floor, baseParcel);
    }

}
