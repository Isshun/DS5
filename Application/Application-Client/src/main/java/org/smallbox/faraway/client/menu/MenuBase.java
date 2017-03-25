package org.smallbox.faraway.client.menu;

import org.smallbox.faraway.client.render.layer.GDXRenderer;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.engine.ColorUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class MenuBase {
    private int         _x;
    private int         _y;
    private final int   _width;
    private final int   _height;
    private List<View>  _views;
    private boolean     _isVisible;
    private ColorUtils _backgroundColor;

    public MenuBase(int width, int height) {
        _views = new ArrayList<>();
        _width = width;
        _height = height;
    }

    public void onKeyDown() {
    }

    public void onKeyUp() {
    }

    public void onKeyEnter() {
    }

    public boolean checkKey(int key) {
        return false;
    }

    protected void addView(View view) {
        _views.add(view);
    }

    protected void setBackgroundColor(ColorUtils color) {
        _backgroundColor = color;
    }

    public void setVisible(boolean isVisible) {
        _isVisible = isVisible;
    }

    public void draw(GDXRenderer renderer, Viewport viewport) {
        // TODO background

        for (View view: _views) {
            view.draw(renderer, 0, 0);
        }

        onDraw(renderer, viewport);
    }

    public abstract void onDraw(GDXRenderer renderer, Viewport viewport);

    public boolean isVisible() {
        return _isVisible;
    }

    public void setPosition(int x, int y) {
        _x = x;
        _y = y;
    }

}
