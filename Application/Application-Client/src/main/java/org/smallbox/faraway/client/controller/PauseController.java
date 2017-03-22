package org.smallbox.faraway.client.controller;

/**
 * Created by Alex on 26/04/2016.
 */
public class PauseController extends LuaController {

    @Override
    protected void onControllerUpdate() {
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
