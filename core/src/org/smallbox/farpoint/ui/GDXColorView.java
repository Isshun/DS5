package org.smallbox.farpoint.ui;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.GFXRenderer;
import org.smallbox.faraway.engine.RenderEffect;
import org.smallbox.faraway.ui.engine.ColorView;
import org.smallbox.faraway.ui.engine.View;
import org.smallbox.farpoint.GDXRenderer;

/**
 * Created by Alex on 04/06/2015.
 */
public class GDXColorView extends ColorView {
    private com.badlogic.gdx.graphics.Color _gdxBackgroundColor;
    private boolean                         _needResetPos;
    private int                             _finalX;
    private int                             _finalY;

    public GDXColorView(int width, int height) {
        super(width, height);
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        _needResetPos = true;
    }

    @Override
    public void draw(GFXRenderer renderer, RenderEffect effect) {
        draw(renderer, 0, 0);
    }

    @Override
    public void draw(GFXRenderer renderer, int x, int y) {
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
            ((GDXRenderer)renderer).draw(_gdxBackgroundColor, _finalX + x, _finalY + y, _width, _height);
        }
    }

    @Override
    public void setBackgroundColor(Color color) {
        if (color != null) {
            _gdxBackgroundColor = new com.badlogic.gdx.graphics.Color(color.r / 255f, color.g / 255f, color.b / 255f, color.a / 255f);
        }
    }

    @Override
    public void refresh() {

    }

    @Override
    public int getContentWidth() {
        return 0;
    }

    @Override
    public int getContentHeight() {
        return 0;
    }
}
