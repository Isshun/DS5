package org.smallbox.faraway;

import org.smallbox.faraway.client.controller.BindLuaController;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.ui.engine.views.widgets.UICheckBox;
import org.smallbox.faraway.core.config.Config;
import org.smallbox.faraway.core.lua.BindLua;
import org.smallbox.faraway.module.mainPanel.MainPanelController;

/**
 * Created by Alex on 03/12/2016.
 */
public class DebugController extends LuaController {

    @BindLua
    private UICheckBox cbDebugView;

    @BindLuaController
    private MainPanelController mainPanelController;

    @Override
    public void onReloadUI() {
        mainPanelController.addShortcut("Debug", this);

        cbDebugView.setOnCheckListener(checked -> Config.onDebugView = checked);
    }
}
