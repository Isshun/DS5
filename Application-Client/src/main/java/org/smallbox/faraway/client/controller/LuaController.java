package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.GameClientObserver;
import org.smallbox.faraway.client.SelectionManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.dependencyInjector.Inject;

/**
 * Created by Alex on 25/04/2016.
 */
public abstract class LuaController implements GameClientObserver {

    private View _rootView;
    private long _lastUpdate;

    public void setRootView(View rootView) {
        if (rootView == null) {
//            throw new GameException("LuaController: Unable to find root view for controller: %s", getClass().getName());
        }

        _rootView = rootView;
    }

    public void setVisible(boolean visible) {
        if (getRootView() != null) {

            if (!getRootView().isVisible() && visible) {
                onControllerUpdate();
            }

            getRootView().setVisible(visible);
        }
    }

    public void controllerUpdate() {
        if (isVisible()) {
            onControllerUpdate();
        }
    }

    protected void onControllerUpdate() {}

    public View getRootView() { return _rootView; }
    public boolean isVisible() { return getRootView() != null && getRootView().isVisible(); }
}
