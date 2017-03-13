package org.smallbox.faraway.modules.room.model;

import org.smallbox.faraway.core.module.world.model.ParcelModel;

/**
 * Created by Alex on 08/03/2017.
 */
@RoomTypeInfo(label = "Common")
public class CommonRoom extends RoomModel {

    public CommonRoom(RoomType type, int floor, ParcelModel baseParcel) {
        super(type, floor, baseParcel);
    }

}
