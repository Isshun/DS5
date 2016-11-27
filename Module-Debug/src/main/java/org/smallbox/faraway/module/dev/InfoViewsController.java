package org.smallbox.faraway.module.dev;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.BindLua;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.module.character.controller.LuaController;
import org.smallbox.faraway.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.ui.engine.views.widgets.View;

/**
 * Created by Alex on 30/08/2015.
 */
public class InfoViewsController extends LuaController {

    @BindLua
    private UIList viewsList;

    @BindLua
    private View header;

    @Override
    protected void onGameUpdate(Game game) {
        viewsList.clear();

        Application.uiManager.getRootViews().forEach(view -> {
            if (!view.getViews().isEmpty()) {
                View child = view.getViews().get(0);

                UILabel label = new UILabel(null);
                label.setText((view.isVisible() && child.isVisible() ? "[*] " : "[ ] ") + child.getName());
                label.setSize(100, 13);
                label.setTextSize(12);
                viewsList.addView(label);
            }
        });

        header.setOnClickListener(event -> viewsList.toggleVisible());
    }
}
