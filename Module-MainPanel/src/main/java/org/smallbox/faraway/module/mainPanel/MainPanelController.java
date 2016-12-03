package org.smallbox.faraway.module.mainPanel;

import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIGrid;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.lua.BindLua;

/**
 * Created by Alex on 15/08/2016.
 */
public class MainPanelController extends LuaController {

    @BindLua
    private UIGrid mainGrid;

    private LuaController _openController;

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

    @Override
    public void onKeyEvent(GameEventListener.Action action, GameEventListener.Key key, GameEventListener.Modifier modifier) {
        if (_openController != null && key == GameEventListener.Key.ESCAPE) {
            _openController.setVisible(false);
            _openController = null;
        }
    }

    public void addShortcut(String label, LuaController controller) {
        mainGrid.addView(UILabel.create(null)
                .setText(label)
                .setTextSize(18)
                .setPadding(10)
                .setSize(170, 40)
                .setBackgroundColor(0x349394)
                .setFocusBackgroundColor(0x25c9cb)
                .setOnClickListener(event -> {
                    _openController = controller;
                    _openController.setVisible(true);
                }));
    }
}
