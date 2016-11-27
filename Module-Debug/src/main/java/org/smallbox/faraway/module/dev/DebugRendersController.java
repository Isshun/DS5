package org.smallbox.faraway.module.dev;

import org.smallbox.faraway.BindManager;
import org.smallbox.faraway.core.engine.renderer.MainRenderer;
import org.smallbox.faraway.core.game.BindLua;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.module.character.controller.LuaController;
import org.smallbox.faraway.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.ui.engine.views.widgets.View;

/**
 * Created by Alex on 24/11/2016.
 */
public class DebugRendersController extends LuaController {
    @BindManager
    private MainRenderer mainRenderer;

    @BindLua
    private UIList list;

    @BindLua
    private View header;

    @Override
    protected void onGameUpdate(Game game) {
        list.clear();

        mainRenderer.getRenders().forEach(render -> {
            UILabel label = new UILabel(null);
            label.setText(render.getClass().getSimpleName() + " " + render.getLastDrawDelay());
            label.setSize(100, 13);
            label.setTextSize(12);
            list.addView(label);
        });

        header.setOnClickListener(event -> list.toggleVisible());
    }
}
