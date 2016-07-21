package org.smallbox.faraway.core.game.module.character.controller;

import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.ui.engine.views.widgets.View;

/**
 * Created by Alex on 25/04/2016.
 */
public abstract class LuaController {
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

    protected View getView() {
        return _rootView;
    }

    public void create() {
        onCreate();
    }

    protected abstract void onCreate();

    public View getRootView() {
        return _rootView;
    }
}
