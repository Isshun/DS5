package org.smallbox.faraway.module.dev;

import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.lua.BindLua;

public class InfoViewsController extends LuaController {

    @BindLua
    private UIList viewsList;

    @BindLua
    private View header;

    @Override
    public void onGameUpdate(Game game) {
        viewsList.clear();

        ApplicationClient.uiManager.getRootViews().forEach(view -> {
            if (!view.getViews().isEmpty()) {
                View child = view.getViews().get(0);

                UILabel label = new UILabel(null);
                label.setText((view.isVisible() && child.isVisible() ? "[*] " : "[ ] ") + child.getName());
                label.setSize(100, 13);
                label.setTextSize(12);
                viewsList.addView(label);
            }
        });
    }
}
