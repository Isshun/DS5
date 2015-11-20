package org.smallbox.faraway.ui.engine.views.widgets;

import org.smallbox.faraway.core.engine.renderer.GDXRenderer;

/**
 * Created by Alex on 26/09/2015.
 */
public class UIList extends View {
    public UIList(int width, int height) {
        super(width, height);
    }

    @Override
    public void addView(View view) {
        view.setDeep(_deep + 1);

        _views.add(view);

        view.setParent(this);

        int offset = 0;
        for (View subView : _views) {
            offset += subView.getHeight() + subView.getMarginTop() + subView.getMarginBottom();
        }

        if (_fixedWidth == -1 || _fixedHeight == -1) {
            setSize(_width, offset);
        }
    }

    @Override
    public void draw(GDXRenderer renderer, int x, int y) {
        super.draw(renderer, x, y);

        if (_isVisible) {
            int offset = 0;
            for (View view : _views) {
                if (view.isVisible()) {
                    view.draw(renderer, _x + x + _marginLeft, offset + _y + y + _marginTop);
                    offset += view.getHeight() + view.getMarginTop() + view.getMarginBottom();
                }
            }
        }
    }

    @Override
    public int getContentWidth() {
        return _width;
    }

    @Override
    public int getContentHeight() {
        return _height;
    }
}
