package org.smallbox.faraway.core.ui;

import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.renderer.GDXRenderer;
import org.smallbox.faraway.ui.engine.view.FrameLayout;
import org.smallbox.faraway.ui.engine.view.View;

/**
 * Created by Alex on 04/06/2015.
 */
public class GDXFrameLayout extends FrameLayout {
    private boolean _needResetPos = true;

    public GDXFrameLayout(int width, int height) {
        super(width, height);
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        _needResetPos = true;
    }

    @Override
    public void draw(GDXRenderer renderer, int x, int y) {
        super.draw(renderer, x, y);

        if (_isVisible) {
            if (_needResetPos) {
                _finalX = x;
                _finalY = y;
                View view = this;
                while (view != null) {
                    _finalX += view.getPosX();
                    _finalY += view.getPosY();
                    view = view.getParent();
                }
            }

            if (_views != null) {
                for (View view : _views) {
                    view.draw(renderer, x, y);
                }
            }
        }
    }

    @Override
    public void refresh() {
        _views.forEach(View::refresh);
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
