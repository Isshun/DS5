package org.smallbox.faraway.core.game.module.world.controller;

import org.smallbox.faraway.core.game.BindLua;
import org.smallbox.faraway.core.game.module.character.controller.LuaController;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.views.widgets.UILabel;

/**
 * Created by Alex on 26/04/2016.
 */
public class WorldConsumableController extends LuaController {
    @BindLua private UILabel        lbName;
    @BindLua private UILabel        lbQuantity;

    @Override
    protected void onCreate() {
    }

    public void select(ConsumableModel consumable) {
        UserInterface.getInstance().findById("base.ui.panel_main").setVisible(false);

        getView().setVisible(true);

        lbName.setText(consumable.getLabel());
        lbQuantity.setText("Quantity: " + consumable.getQuantity());
    }
}
