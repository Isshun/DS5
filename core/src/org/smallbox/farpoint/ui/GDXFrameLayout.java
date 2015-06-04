package org.smallbox.farpoint.ui;

import org.smallbox.faraway.Color;
import org.smallbox.faraway.GFXRenderer;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.engine.ui.FrameLayout;
import org.smallbox.faraway.engine.ui.View;
import org.smallbox.farpoint.GDXRenderEffect;
import org.smallbox.farpoint.GDXRenderer;

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
    protected void createRender() {
        int posX = _x;
        int posY = _y;

        View parent = _parent;
        while (parent != null) {
            posX += parent._x;
            posY += parent._y;
            parent = parent._parent;
        }

        _renderEffect = new GDXRenderEffect();
        _renderEffect.setTranslate(posX + _offsetX, posY + _offsetY);
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
    public void draw(GFXRenderer renderer, RenderEffect effect) {
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

        if (_gdxBackgroundColor != null) {
            ((GDXRenderer) renderer).draw(_gdxBackgroundColor, _finalX, _finalY, _width, _height);
        }

        super.draw(renderer, effect);
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
