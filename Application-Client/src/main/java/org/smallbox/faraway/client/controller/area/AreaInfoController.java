package org.smallbox.faraway.client.controller.area;

import org.smallbox.faraway.client.SelectionManager;
import org.smallbox.faraway.client.controller.AbsInfoLuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.area.AreaModel;
import org.smallbox.faraway.modules.area.AreaModule;

import java.util.Queue;

/**
 * Created by Alex on 26/04/2016.
 */
@GameObject
public class AreaInfoController extends AbsInfoLuaController<AreaModel> {

    @Inject
    protected SelectionManager selectionManager;

    @Inject
    private UIEventManager uiEventManager;

    @Inject
    private AreaModule areaModule;

    @BindLua
    private UILabel lbName;

    @BindLua
    private UILabel lbLabel;

    @BindLua
    private UILabel lbParcels;

    @Override
    public void onReloadUI() {
        selectionManager.registerSelection(this);
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
