package org.smallbox.faraway.client.controller.room;

import org.smallbox.faraway.client.controller.AbsInfoLuaController;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.module.world.model.Parcel;
import org.smallbox.faraway.modules.room.RoomModule;
import org.smallbox.faraway.modules.room.model.CellRoom;
import org.smallbox.faraway.modules.room.model.RoomModel;

import java.util.Queue;

@GameObject
public class RoomInfoCellController extends AbsInfoLuaController<RoomModel> {
    @Inject private RoomModule roomModule;
    @Inject private Data data;

    @Override
    protected void onDisplayUnique(RoomModel room) {
        setVisible(true);
    }

    @Override
    protected void onDisplayMultiple(Queue<RoomModel> objects) {
    }

    @Override
    public RoomModel getObjectOnParcel(Parcel parcel) {
        RoomModel room = roomModule.getRoom(parcel);
        return room instanceof CellRoom ? room : null;
    }

}
