package org.smallbox.faraway.modules.room.model;

import org.smallbox.faraway.core.module.world.model.Parcel;

@RoomTypeInfo(label = "Sickbay")
public class SickbayRoom extends RoomModel {

    public SickbayRoom(RoomType type, int floor, Parcel baseParcel) {
        super(type, floor, baseParcel);
    }

}
