package org.smallbox.faraway.core.game.module.character.controller;

import org.smallbox.faraway.ui.engine.views.widgets.View;

/**
 * Created by Alex on 25/04/2016.
 */
public abstract class LuaController {
    private View _rootView;

    public void setRootView(View rootView) {
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
