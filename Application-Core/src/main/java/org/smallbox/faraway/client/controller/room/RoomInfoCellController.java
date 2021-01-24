package org.smallbox.faraway.client.controller.room;

import org.smallbox.faraway.client.controller.AbsInfoLuaController;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.room.RoomModule;
import org.smallbox.faraway.game.room.model.CellRoom;
import org.smallbox.faraway.game.room.model.RoomModel;

import java.util.Queue;

@GameObject
public class RoomInfoCellController extends AbsInfoLuaController<RoomModel> {
    @Inject private RoomModule roomModule;
    @Inject private DataManager dataManager;

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
