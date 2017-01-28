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

    private LuaController _currentPaneController;

    @Override
    public boolean onKeyPress(GameEventListener.Key key) {
        if (key == GameEventListener.Key.ESCAPE && !isVisible()) {
            setVisible(true);
            return true;
        }
        return false;
    }

    @Override
    public void onClickOnMap(GameEvent mouseEvent) {
        setVisible(true);
    }

    @Override
    public void onKeyEvent(GameEventListener.Action action, GameEventListener.Key key, GameEventListener.Modifier modifier) {
        if (_currentPaneController != null && key == GameEventListener.Key.ESCAPE) {
            _currentPaneController.setVisible(false);
            _currentPaneController = null;
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
                    _currentPaneController = controller;
                    _currentPaneController.setVisible(true);
                }));
    }

    public void setCurrentController(LuaController controller) {
        _currentPaneController = controller;
    }

    public LuaController getCurrentController() {
        return _currentPaneController;
    }
}
