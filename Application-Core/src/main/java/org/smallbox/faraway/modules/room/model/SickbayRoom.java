package org.smallbox.faraway.modules.room.model;

import org.smallbox.faraway.core.module.world.model.ParcelModel;

@RoomTypeInfo(label = "Sickbay")
public class SickbayRoom extends RoomModel {

    public SickbayRoom(RoomType type, int floor, ParcelModel baseParcel) {
        super(type, floor, baseParcel);
    }

}
