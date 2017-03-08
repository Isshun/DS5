package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameInfo;
import org.smallbox.faraway.core.game.GameObserverPriority;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;

/**
 * Created by Alex on 04/12/2016.
 */
@GameObserverPriority(GameObserverPriority.Priority.LOW)
public class GameMenuController extends LuaController {

    @Override
    public boolean onKeyPress(GameEventListener.Key key) {
//        if (key == GameEventListener.Key.ESCAPE && ApplicationClient.uiManager.findById("base.ui.right_panel").isVisible()) {
//            setVisible(!isVisible());
//            return true;
//        }
        return false;
    }

    @BindLuaAction
    public void onResume(View view) {
    }

    @BindLuaAction
    public void onSave(View view) {
        ApplicationClient.uiManager.findById("base.ui.menu_pause").setVisible(false);
//        ApplicationClient.uiManager.findById("base.ui.menu_save").setVisible(true);

        Game game = Application.gameManager.getGame();
        Application.gameSaveManager.saveGame(game, game.getInfo(), GameInfo.Type.FAST);
    }

    @BindLuaAction
    public void onLoad(View view) {
        ApplicationClient.uiManager.findById("base.ui.menu_pause").setVisible(false);
        ApplicationClient.uiManager.findById("base.ui.menu_load").setVisible(true);
    }

    @BindLuaAction
    public void onSettings(View view) {
        ApplicationClient.uiManager.findById("base.ui.menu_pause").setVisible(false);
        ApplicationClient.uiManager.findById("base.ui.menu_settings").setVisible(true);
    }

    @BindLuaAction
    public void onQuit(View view) {
//        ApplicationClient.uiManager.findById("base.ui.menu_pause").setVisible(false);
//        ApplicationClient.uiManager.findById("base.ui.menu_main").setVisible(true);
//        Application.gameManager.stopGame();
    }

    @Override
    protected void onNewGameUpdate(Game game) {

    }
}
