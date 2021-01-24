package org.smallbox.faraway.client.controller.area;

import org.smallbox.faraway.client.controller.AbsInfoLuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.client.ui.event.UIEventManager;
import org.smallbox.faraway.client.ui.widgets.UILabel;
import org.smallbox.faraway.client.ui.widgets.UIList;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.area.AreaModel;
import org.smallbox.faraway.game.area.AreaModule;
import org.smallbox.faraway.game.plant.GardenArea;
import org.smallbox.faraway.game.plant.PlantModule;

import java.util.Queue;

@GameObject
public class AreaInfoGardenController extends AbsInfoLuaController<AreaModel> {

    @Inject
    protected GameSelectionManager gameSelectionManager;
    @Inject private UIEventManager uiEventManager;
    @Inject private AreaModule areaModule;
    @Inject private PlantModule plantModule;

    @BindLua
    private UIList listPlants;
    @Inject private DataManager dataManager;
    @Inject private AreaInfoController areaInfoController;

    @Override
    public void onReloadUI() {
        gameSelectionManager.registerSelection(this, areaInfoController);
    }

    @Override
    protected void onDisplayUnique(AreaModel area) {
        setVisible(true);

        listPlants.removeAllViews();
        dataManager.items.stream()
                .filter(item -> "plant".equals(item.type))
                .forEach(item ->
                        listPlants.addView(UILabel.create(null)
                                .setText(item.label)
                                .setTextSize(14)
                                .setTextColor(0xB4D4D3FF)
                                .setSize(200, 20)
                                .getEvents().setOnClickListener(() ->
                                        area.getParcels().forEach(parcel -> plantModule.addPlant(item, parcel)))));
    }

    @Override
    protected void onDisplayMultiple(Queue<AreaModel> objects) {
    }

    @Override
    public AreaModel getObjectOnParcel(Parcel parcel) {
        AreaModel area = areaModule.getArea(parcel);
        return area instanceof GardenArea ? area : null;
    }
}
