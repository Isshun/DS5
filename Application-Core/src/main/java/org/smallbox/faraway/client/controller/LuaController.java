package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.ui.widgets.CompositeView;

import java.util.Optional;

public abstract class LuaController {
    private CompositeView _rootView;

    public void setRootView(CompositeView rootView) {
        _rootView = rootView;
    }

    public void setVisible(boolean visible) {
        Optional.ofNullable(getRootView()).ifPresent(view -> view.setVisible(visible));
    }

    public CompositeView getRootView() {
        return _rootView;
    }

    public String getCanonicalName() {
        return getClass().getCanonicalName();
    }

    public boolean isVisible() {
        return getRootView() != null && getRootView().isVisible();
    }
}
