package org.smallbox.faraway.core.game.module.world.controller;

import org.smallbox.faraway.core.game.BindLua;
import org.smallbox.faraway.core.game.module.character.controller.LuaController;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.ui.engine.views.widgets.UILabel;

/**
 * Created by Alex on 11/07/2016.
 */
public class WorldInfoParcel2Controller extends LuaController {
    @BindLua
    private UILabel lbName;

    @Override
    protected void onCreate() {
    }

    public void select(ParcelModel parcel) {
        lbName.setText("Hello");
    }
}
