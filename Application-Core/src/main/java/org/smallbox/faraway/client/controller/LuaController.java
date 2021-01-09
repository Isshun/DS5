package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.GameClientObserver;
import org.smallbox.faraway.client.ui.engine.views.CompositeView;

public abstract class LuaController implements GameClientObserver {

    private CompositeView _rootView;
    private long _lastUpdate;

    public void setRootView(CompositeView rootView) {
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

    public CompositeView getRootView() { return _rootView; }
    public String getCanonicalName() { return getClass().getCanonicalName(); }
    public boolean isVisible() { return getRootView() != null && getRootView().isVisible(); }
}