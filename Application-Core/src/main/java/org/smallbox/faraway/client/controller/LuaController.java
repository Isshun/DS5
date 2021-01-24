package org.smallbox.faraway.client.controller;

import com.badlogic.gdx.Gdx;
import org.smallbox.faraway.client.input.GameClientObserver;
import org.smallbox.faraway.client.ui.widgets.CompositeView;

public abstract class LuaController implements GameClientObserver {

    private CompositeView _rootView;
    private long _lastUpdate;
//    private String fileName;

    public void setRootView(CompositeView rootView) {
        _rootView = rootView;
    }

    public void setVisible(boolean visible) {
        if (getRootView() != null) {
            getRootView().setVisible(visible);
        }

        controllerUpdate();
    }

    public void controllerUpdate() {
        if (isVisible()) {
            Gdx.app.postRunnable(this::onControllerUpdate);
        }
    }

    protected void onControllerUpdate() {}

    public CompositeView getRootView() { return _rootView; }
    public String getCanonicalName() { return getClass().getCanonicalName(); }
    public boolean isVisible() { return getRootView() != null && getRootView().isVisible(); }

//    public String getFileName() {
//        return fileName;
//    }
//
//    public void setFileName(String fileName) {
//        this.fileName = fileName;
//    }
}
