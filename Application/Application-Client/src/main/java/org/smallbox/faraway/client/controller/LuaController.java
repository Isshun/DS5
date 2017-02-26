package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.GameClientObserver;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.game.Game;

/**
 * Created by Alex on 25/04/2016.
 */
public abstract class LuaController implements GameClientObserver {
    private View _rootView;

    public void setRootView(View rootView) {
        if (rootView == null) {
//            Log.error("LuaController: Unable to find root view for controller: %s", getClass().getName());
        }

        _rootView = rootView;
    }

    public void setVisible(boolean visible) {
        if (getRootView() != null) {
            getRootView().setVisible(visible);
        }
    }

    @Override
    public final void onGameUpdate(Game game) {
        if (isVisible()) {
            onNewGameUpdate(game);
        }
    }

    protected abstract void onNewGameUpdate(Game game);

//    public final void gameCreate(Game game) { onGameCreate(game); }
//    public final void gameStart(Game game) { onGameStart(game); }
//    public final void gameUpdate(Game game) { onGameUpdate(game); }
//
//    public void onGameCreate(Game game) {}
//    public void onGameUpdate(Game game) {}

    public View getRootView() { return _rootView; }
    public boolean isVisible() { return getRootView() != null && getRootView().isVisible(); }
}
