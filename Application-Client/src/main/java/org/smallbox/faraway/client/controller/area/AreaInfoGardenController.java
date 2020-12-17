package org.smallbox.faraway.client.controller.area;

import org.smallbox.faraway.client.SelectionManager;
import org.smallbox.faraway.client.controller.AbsInfoLuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaController;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.dependencyInjector.Inject;
import org.smallbox.faraway.core.dependencyInjector.GameObject;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.area.AreaModel;
import org.smallbox.faraway.modules.area.AreaModule;
import org.smallbox.faraway.modules.plant.GardenArea;
import org.smallbox.faraway.modules.plant.PlantModule;

import java.util.Queue;

/**
 * Created by Alex on 26/04/2016.
 */
@GameObject
public class AreaInfoGardenController extends AbsInfoLuaController<AreaModel> {

    @Inject
    protected SelectionManager selectionManager;

    @Inject
    private UIEventManager uiEventManager;

    @Inject
    private AreaModule areaModule;

    @Inject
    private PlantModule plantModule;

    @BindLua
    private UIList listPlants;

    @Inject
    private Data data;

    @Inject
    private AreaInfoController areaInfoController;

    @Override
    public void onReloadUI() {
        selectionManager.registerSelection(this, areaInfoController);
    }

    @Override
    protected void onDisplayUnique(AreaModel area) {
        setVisible(true);

        listPlants.removeAllViews();
        data.items.stream()
                .filter(item -> "plant".equals(item.type))
                .forEach(item ->
                        listPlants.addView(UILabel.create(null)
                                .setText(item.label)
                                .setTextSize(14)
                                .setTextColor(0xB4D4D3FF)
                                .setSize(200, 20)
                                .setOnClickListener((x, y) ->
                                        area.getParcels().forEach(parcel -> plantModule.addPlant(item, parcel)))));
    }

    @Override
    protected void onDisplayMultiple(Queue<AreaModel> objects) {
    }

    @Override
    public AreaModel getObjectOnParcel(ParcelModel parcel) {
        AreaModel area = areaModule.getArea(parcel);
        return area instanceof GardenArea ? area : null;
    }
}