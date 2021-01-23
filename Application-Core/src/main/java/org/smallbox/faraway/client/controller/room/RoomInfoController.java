package org.smallbox.faraway.client.controller.room;

import org.smallbox.faraway.client.controller.AbsInfoLuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.module.world.model.Parcel;
import org.smallbox.faraway.modules.room.RoomModule;
import org.smallbox.faraway.modules.room.model.RoomModel;

import java.util.Queue;

@GameObject
public class RoomInfoController extends AbsInfoLuaController<RoomModel> {
    @Inject private RoomModule roomModule;

    @BindLua
    private UILabel lbName;

    @BindLua
    private UILabel lbLabel;

    @BindLua
    private UILabel lbParcels;

    @Override
    protected void onDisplayUnique(RoomModel room) {
        lbName.setText(room.getName());

        lbLabel.setText("Type: " + room.getInfo().label());
        lbParcels.setText("Parcels: " + room.getParcels());
    }

    @Override
    protected void onDisplayMultiple(Queue<RoomModel> objects) {
        lbLabel.setText("MULTIPLE");
    }

    @Override
    public RoomModel getObjectOnParcel(Parcel parcel) {
        return roomModule.getRoom(parcel);
    }
}
