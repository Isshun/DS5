package org.smallbox.faraway.modules.room.model;

import org.smallbox.faraway.core.module.world.model.ParcelModel;

@RoomTypeInfo(label = "Technical")
public class TechnicalRoom extends RoomModel {

    public TechnicalRoom(RoomType type, int floor, ParcelModel baseParcel) {
        super(type, floor, baseParcel);
    }

}
