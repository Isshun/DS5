package org.smallbox.faraway.module.mainPanel;

import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.BindLua;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.module.character.controller.LuaController;
import org.smallbox.faraway.client.ui.engine.OnClickListener;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIGrid;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;

/**
 * Created by Alex on 15/08/2016.
 */
public class MainPanelController extends LuaController {
    @BindLua
    private UIGrid mainGrid;

    @Override
    protected void onGameUpdate(Game game) {
    }

    @Override
    public void onKeyPress(GameEventListener.Key key) {
        if (key == GameEventListener.Key.ESCAPE) {
            setVisible(true);
        }
    }

    @Override
    public void onClickOnMap(GameEvent mouseEvent) {
        setVisible(true);
    }

    public void addShortcut(String label, OnClickListener clickListener) {
        mainGrid.addView(UILabel.create(null)
                .setText(label)
                .setTextSize(18)
                .setPadding(10)
                .setSize(170, 40)
                .setBackgroundColor(0x349394)
                .setFocusBackgroundColor(0x25c9cb)
                .setOnClickListener(clickListener));
    }
}
