package org.smallbox.faraway.client.controller.area;

import org.smallbox.faraway.client.controller.AbsInfoLuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.client.ui.event.UIEventManager;
import org.smallbox.faraway.client.ui.widgets.UILabel;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.game.area.AreaModel;
import org.smallbox.faraway.game.area.AreaModule;
import org.smallbox.faraway.game.world.Parcel;

import java.util.Queue;

@GameObject
public class AreaInfoController extends AbsInfoLuaController<AreaModel> {

    @Inject
    protected GameSelectionManager gameSelectionManager;
    @Inject private UIEventManager uiEventManager;
    @Inject private AreaModule areaModule;

    @BindLua
    private UILabel lbName;

    @BindLua
    private UILabel lbLabel;

    @BindLua
    private UILabel lbParcels;

    @Override
    public void onReloadUI() {
        gameSelectionManager.registerSelection(this);
    }

//    @OnGameSelectAction(AreaModel.class)
//    private void onSelectArea(AreaModel area) {
//        lbName.setText(area.getName());
//
//        lbLabel.setText("Type: " + area.getInfo().label());
//        lbParcels.setText("Parcels: " + area.getParcels());
//    }

    @Override
    protected void onDisplayUnique(AreaModel areaModel) {
    }

    @Override
    protected void onDisplayMultiple(Queue<AreaModel> objects) {
        lbLabel.setText("MULTIPLE");
    }

    @Override
    public AreaModel getObjectOnParcel(Parcel parcel) {
        return areaModule.getArea(parcel);
    }
}
