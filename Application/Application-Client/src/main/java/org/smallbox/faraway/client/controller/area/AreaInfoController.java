package org.smallbox.faraway.client.controller.area;

import org.smallbox.faraway.client.controller.AbsInfoLuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.area.AreaModel;
import org.smallbox.faraway.modules.area.AreaModule;

import java.util.Queue;

/**
 * Created by Alex on 26/04/2016.
 */
public class AreaInfoController extends AbsInfoLuaController<AreaModel> {

    @BindComponent
    private UIEventManager uiEventManager;

    @BindModule
    private AreaModule areaModule;

    @BindLua
    private UILabel lbName;

    @BindLua
    private UILabel lbLabel;

    @BindLua
    private UILabel lbParcels;

    @Override
    public void onReloadUI() {
        uiEventManager.registerSelection(this);
    }

    @Override
    protected void onDisplayUnique(AreaModel area) {
        lbName.setText(area.getName());

        lbLabel.setText("Type: " + area.getInfo().label());
        lbParcels.setText("Parcels: " + area.getParcels());
    }

    @Override
    protected void onDisplayMultiple(Queue<AreaModel> objects) {
        lbLabel.setText("MULTIPLE");
    }

    @Override
    public AreaModel getObjectOnParcel(ParcelModel parcel) {
        return areaModule.getArea(parcel);
    }
}
