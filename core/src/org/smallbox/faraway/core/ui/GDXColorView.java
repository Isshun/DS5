package org.smallbox.faraway.core.ui;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.renderer.GDXRenderer;
import org.smallbox.faraway.ui.engine.view.ColorView;
import org.smallbox.faraway.ui.engine.view.View;

/**
 * Created by Alex on 04/06/2015.
 */
public class GDXColorView extends ColorView {
    private boolean                         _needResetPos;
    private int                             _finalX;
    private int                             _finalY;

    @Override
    public void addView(View view) {

    }

    public GDXColorView(int width, int height) {
        super(width, height);
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        _needResetPos = true;
    }

    @Override
    public void draw(GDXRenderer renderer, int x, int y) {
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

        if (_backgroundColor != null) {
            ((GDXRenderer)renderer).draw(_backgroundColor, _finalX + x, _finalY + y, _width, _height);
        }
    }

    @Override
    public void setBackgroundColor(Color color) {
        if (color != null) {
            _backgroundColor = color;
        }
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
