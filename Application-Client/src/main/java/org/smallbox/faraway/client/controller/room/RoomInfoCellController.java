package org.smallbox.faraway.client.controller.room;

import org.smallbox.faraway.client.controller.AbsInfoLuaController;
import org.smallbox.faraway.core.dependencyInjector.GameObject;
import org.smallbox.faraway.core.dependencyInjector.Inject;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.room.RoomModule;
import org.smallbox.faraway.modules.room.model.CellRoom;
import org.smallbox.faraway.modules.room.model.RoomModel;

import java.util.Queue;

/**
 * Created by Alex on 26/04/2016.
 */
@GameObject
public class RoomInfoCellController extends AbsInfoLuaController<RoomModel> {

    @Inject
    private RoomModule roomModule;

    @Inject
    private Data data;

    @Override
    protected void onDisplayUnique(RoomModel room) {
        setVisible(true);
    }

    @Override
    protected void onDisplayMultiple(Queue<RoomModel> objects) {
    }

    @Override
    public RoomModel getObjectOnParcel(ParcelModel parcel) {
        RoomModel room = roomModule.getRoom(parcel);
        return room instanceof CellRoom ? room : null;
    }

}
