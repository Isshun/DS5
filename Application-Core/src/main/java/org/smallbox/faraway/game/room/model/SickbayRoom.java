package org.smallbox.faraway.game.room.model;

import org.smallbox.faraway.game.world.Parcel;

@RoomTypeInfo(label = "Sickbay")
public class SickbayRoom extends RoomModel {

    public SickbayRoom(RoomType type, int floor, Parcel baseParcel) {
        super(type, floor, baseParcel);
    }

}
