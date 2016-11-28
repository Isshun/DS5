package org.smallbox.faraway.core.game.module.character.controller;

import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;

/**
 * Created by Alex on 25/04/2016.
 */
public abstract class LuaController implements GameObserver {
    private View _rootView;

    public void setRootView(View rootView) {
        if (rootView == null) {
            Log.error("LuaController: Unable to find root view for controller: %s", getClass().getName());
        }

        _rootView = rootView;
    }

    public void setVisible(boolean visible) {
        if (getRootView() != null) {
            getRootView().setVisible(visible);
        }
    }

    public final void gameCreate(Game game) { onGameCreate(game); }
    public final void gameStart(Game game) { onGameStart(game); }
    public final void gameUpdate(Game game) { onGameUpdate(game); }

    protected void onGameCreate(Game game) {}
    protected void onGameUpdate(Game game) {}

    public View getRootView() { return _rootView; }
    public boolean isVisible() { return getRootView() != null && getRootView().isVisible(); }
}
