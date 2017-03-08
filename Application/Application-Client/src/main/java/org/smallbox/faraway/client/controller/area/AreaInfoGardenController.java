package org.smallbox.faraway.client.controller.area;

import org.smallbox.faraway.client.controller.AbsInfoLuaController;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.area.AreaModel;
import org.smallbox.faraway.modules.area.AreaModule;
import org.smallbox.faraway.modules.plant.GardenArea;

import java.util.List;

/**
 * Created by Alex on 26/04/2016.
 */
public class AreaInfoGardenController extends AbsInfoLuaController<AreaModel> {

    @BindModule
    private AreaModule areaModule;

    @BindLua
    private UIList listPlants;

    @BindComponent
    private Data data;

    @Override
    protected void onDisplayUnique(AreaModel area) {
        setVisible(true);

        listPlants.clear();
        data.items.stream()
                .filter(item -> "plant".equals(item.type))
                .forEach(item ->
                        listPlants.addView(UILabel.create(null)
                                .setText(item.label)
                                .setTextSize(14)
                                .setTextColor(0xB4D4D3)
                                .setSize(200, 20)));
    }

    @Override
    protected void onDisplayMultiple(List<AreaModel> list) {
    }

    @Override
    protected AreaModel getObjectOnParcel(ParcelModel parcel) {
        AreaModel area = areaModule.getArea(parcel);
        return area instanceof GardenArea ? area : null;
    }
}
