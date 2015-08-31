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
    private int     _finalX;
    private int     _finalY;
    private com.badlogic.gdx.graphics.Color _gdxBackgroundColor;

    public GDXFrameLayout(int width, int height) {
        super(width, height);
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        _needResetPos = true;
    }

    @Override
    public void setBackgroundColor(Color color) {
        if (color != null) {
            _gdxBackgroundColor = new com.badlogic.gdx.graphics.Color(color.r / 255f, color.g / 255f, color.b / 255f, color.a / 255f);
        }
    }

    @Override
    public void draw(GDXRenderer renderer, Viewport viewport) {
        if (_needResetPos) {
            _finalX = 0;
            _finalY = 0;
            View view = this;
            while (view != null) {
                _finalX += view.getPosX();
                _finalY += view.getPosY();
                view = view.getParent();
            }
        }

        if (_isVisible && _gdxBackgroundColor != null) {
            renderer.draw(_gdxBackgroundColor, _finalX, _finalY, _width, _height);
        }

        super.draw(renderer, viewport);
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
