package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.core.game.Game;

/**
 * Created by Alex on 26/04/2016.
 */
public class PauseController extends LuaController {

    @Override
    protected void onNewGameUpdate(Game game) {
        setVisible(false);
    }

    @Override
    public void onGamePaused() {
        setVisible(true);
    }

    @Override
    public void onGameResume() {
        setVisible(false);
    }

}
