package org.smallbox.farpoint.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import org.smallbox.faraway.Color;
import org.smallbox.faraway.GFXRenderer;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.engine.ui.View;
import org.smallbox.farpoint.GDXRenderer;

/**
 * Created by Alex on 04/06/2015.
 */
public class GDXLabel extends TextView {
    private boolean     _needResetPos = true;
    private int         _finalX;
    private int         _finalY;
    private String      _string;
    private int         _textSize;
    private com.badlogic.gdx.graphics.Color _gdxColor;
    private com.badlogic.gdx.graphics.Color _gdxBackgroundColor;

    @Override
    public void setStringValue(String string) {
        _string = string;
    }

    @Override
    public void setCharacterSize(int size) {
        _textSize = size;
    }

    @Override
    public void setStyle(int style) {

    }

    @Override
    public void setColor(Color color) {
        if (color != null) {
            _gdxColor = new com.badlogic.gdx.graphics.Color(color.r / 255f, color.g / 255f, color.b / 255f, color.a / 255f);
        }
    }

    @Override
    public void setBackgroundColor(Color color) {
        if (color != null) {
            _gdxBackgroundColor = new com.badlogic.gdx.graphics.Color(color.r / 255f, color.g / 255f, color.b / 255f, color.a / 255f);
        }
    }

    @Override
    public Color getColor() {
        return null;
    }

    @Override
    public void setDashedString(String label, String value, int nbColumns) {

    }

    @Override
    public String getString() {
        return null;
    }

    @Override
    protected void onDraw(GFXRenderer renderer, RenderEffect effect) {

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

//        ((GDXRenderer) renderer).draw(com.badlogic.gdx.graphics.Color.RED, _finalX, _finalY, _width, _height);
        ((GDXRenderer)renderer).draw(_string, _textSize, _finalX, _finalY, _gdxColor);
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