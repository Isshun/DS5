package org.smallbox.faraway.core.game.module.character.controller;

import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.ui.engine.views.widgets.View;

/**
 * Created by Alex on 25/04/2016.
 */
public abstract class LuaController implements GameObserver {
    private View _rootView;

    public void setRootView(View rootView) {
        if (rootView == null) {
            Log.error("LuaController: Unable to find view (controller: %s)", getClass().getName());
        }

        _rootView = rootView;

//        if (rootView != null) {
//            Log.info("Set view to controller (%s -> %s)", rootView.getName(), getClass().getName());
//
//            bindFields(rootView);
//            bindSubControllers(rootView);
//            bindMethods(rootView);
//
//            onCreate();
//        }
    }

    public void setVisible(boolean visible) {
        if (getRootView() != null) {
            getRootView().setVisible(visible);
        }
    }

    protected View getView() {
        return _rootView;
    }

    public void create() { onCreate(); }
    public void gameStart(Game game) { onGameStart(game); }
    public void gameUpdate(Game game) { onGameUpdate(game); }

    protected void onCreate() {}
    protected void onGameStart(Game game) {}
    protected void onGameUpdate(Game game) {}

    public View getRootView() {
        return _rootView;
    }
}
