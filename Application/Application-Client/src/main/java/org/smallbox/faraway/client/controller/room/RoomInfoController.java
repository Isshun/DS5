package org.smallbox.faraway.client.controller.room;

import org.smallbox.faraway.client.controller.AbsInfoLuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.room.RoomModule;
import org.smallbox.faraway.modules.room.model.RoomModel;

import java.util.List;

/**
 * Created by Alex on 26/04/2016.
 */
public class RoomInfoController extends AbsInfoLuaController<RoomModel> {

    @BindModule
    private RoomModule roomModule;

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
    protected void onDisplayMultiple(List<RoomModel> list) {
        lbLabel.setText("MULTIPLE");
    }

    @Override
    protected RoomModel getObjectOnParcel(ParcelModel parcel) {
        return roomModule.getRoom(parcel);
    }
}
