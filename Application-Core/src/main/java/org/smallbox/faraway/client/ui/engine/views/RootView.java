package org.smallbox.faraway.client.ui.engine.views;

import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.engine.module.ModuleBase;

public class RootView {

    private View _view;

    public void setView(View view) {
        _view = view;
    }

    public boolean isVisible() {
        return _view.isVisible();
    }

    public boolean inGame() {
        return true;
    }

    public ModuleBase getModule() {
        return null;
    }

    public void draw(GDXRenderer renderer, int x, int y) {
        _view.draw(renderer, x, y);
    }

    public View getView() {
        return _view;
    }

    public String toString() {
        return _view.getName();
    }

    public String getName() {
        return _view.getName();
    }

    public void setVisible(boolean visible) { _view.setVisible(visible); }
}
