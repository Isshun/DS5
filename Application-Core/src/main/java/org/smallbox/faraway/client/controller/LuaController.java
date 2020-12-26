package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.GameClientObserver;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;

public abstract class LuaController implements GameClientObserver {

    private View _rootView;
    private long _lastUpdate;

    public void setRootView(View rootView) {
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
    public String getCanonicalName() { return getClass().getCanonicalName(); }
    public boolean isVisible() { return getRootView() != null && getRootView().isVisible(); }
}
