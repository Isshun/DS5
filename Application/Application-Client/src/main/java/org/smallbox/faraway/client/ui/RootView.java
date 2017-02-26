package org.smallbox.faraway.client.ui;

import org.smallbox.faraway.client.renderer.GDXRenderer;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.engine.module.ModuleBase;

/**
 * Created by Alex on 26/02/2017.
 */
public class RootView {

    private View _view;

    public void setView(View view) {
        _view = view;
    }

    public boolean isVisible() {
        return true;
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
}
