package org.smallbox.faraway.module.dev.controller;

import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.BindLua;
import org.smallbox.faraway.core.game.module.character.controller.LuaController;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.ui.engine.views.widgets.UILabel;

/**
 * Created by Alex on 31/07/2016.
 */
public class DebugConsoleController extends LuaController {
    @BindLua
    private UILabel lbEntry;

    @Override
    public void onKeyPress(GameEventListener.Key key) {
        if (lbEntry.isVisible()) {
            if (key == GameEventListener.Key.ENTER) {
                Log.info("Execute: " + lbEntry.getText());
                lbEntry.setText("");
                lbEntry.setVisible(false);
            } else {
                lbEntry.setText((lbEntry.getText() != null ? lbEntry.getText() : "") + key.toString());
            }
        } else {
            if (key == GameEventListener.Key.ENTER) {
                lbEntry.setVisible(true);
            }
        }
    }
}