package org.smallbox.faraway.core.game.module.area.controller;

import org.smallbox.faraway.core.game.BindLua;
import org.smallbox.faraway.core.game.module.area.model.GardenAreaModel;
import org.smallbox.faraway.core.game.module.character.controller.LuaController;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.ui.engine.views.widgets.UIList;

/**
 * Created by Alex on 26/04/2016.
 */
public class AreaGardenInfoController extends LuaController {
    @BindLua private UIList             listAcceptedPlant;

    @Override
    protected void onCreate() {
    }

    public void select(GardenAreaModel garden) {
        UserInterface.getInstance().findById("base.ui.panel_main").setVisible(false);
        UserInterface.getInstance().findById("base.ui.panel_areas").setVisible(false);
        getView().setVisible(true);

        displayAcceptedItem(garden);
    }

    private void displayAcceptedItem(GardenAreaModel garden) {
        listAcceptedPlant.clear();
        garden.getPotentials().forEach(itemInfo -> {
            UILabel label = new UILabel(null);
            label.setText((garden.getCurrent() == itemInfo ? "[x] " : "[ ] ") + itemInfo.label);
            label.setTextSize(12);
            label.setPadding(5);
            label.setOnClickListener(() -> {
                garden.setAccept(itemInfo, true);
            });
            listAcceptedPlant.addView(label);
        });
    }
}