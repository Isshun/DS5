package org.smallbox.faraway.client.controller.room;

import org.smallbox.faraway.client.controller.AbsInfoLuaController;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.room.RoomModule;
import org.smallbox.faraway.modules.room.model.QuarterRoom;
import org.smallbox.faraway.modules.room.model.RoomModel;

import java.util.List;

/**
 * Created by Alex on 26/04/2016.
 */
public class RoomInfoQuarterController extends AbsInfoLuaController<RoomModel> {

    @BindModule
    private RoomModule roomModule;

    @BindComponent
    private Data data;

    @Override
    protected void onDisplayUnique(RoomModel room) {
        setVisible(true);
    }

    @Override
    protected void onDisplayMultiple(List<RoomModel> list) {
    }

    @Override
    protected RoomModel getObjectOnParcel(ParcelModel parcel) {
        RoomModel room = roomModule.getRoom(parcel);
        return room instanceof QuarterRoom ? room : null;
    }

}
