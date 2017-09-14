package org.smallbox.faraway.client.ui.engine.views;

import org.smallbox.faraway.client.render.layer.GDXRenderer;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;

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
}
