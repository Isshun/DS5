package org.smallbox.faraway.core.ui;

import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.core.renderer.GDXRenderer;
import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.ui.engine.view.UILabel;
import org.smallbox.faraway.ui.engine.view.View;
import org.smallbox.faraway.util.StringUtils;

/**
 * Created by Alex on 04/06/2015.
 */
public class GDXLabel extends UILabel {
    private boolean                             _needResetPos = true;
    private String                              _string;
    private int                                 _textSize;
    private com.badlogic.gdx.graphics.Color     _gdxColor;
    private com.badlogic.gdx.graphics.Color     _gdxBackgroundColor;
    private Color                               _color;

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
        if (color != null && _color != color) {
            _gdxColor = new com.badlogic.gdx.graphics.Color(color.r / 255f, color.g / 255f, color.b / 255f, color.a / 255f);
        }
        _color = color;
    }

    @Override
    public void setBackgroundColor(Color color) {
        if (color != null) {
            _gdxBackgroundColor = new com.badlogic.gdx.graphics.Color(color.r / 255f, color.g / 255f, color.b / 255f, color.a / 255f);
        }
    }

    public void setBackgroundColor(com.badlogic.gdx.graphics.Color color) {
        if (color != null) {
            _gdxBackgroundColor = color;
        }
    }

    @Override
    public Color getColor() {
        return _color;
    }

    @Override
    public void setDashedString(String label, String value, int nbColumns) {
        _string = StringUtils.getDashedString(label, value, nbColumns);
    }

    @Override
    public String getString() {
        return _string;
    }

    @Override
    protected void onDraw(GDXRenderer renderer, Viewport viewport) {
    }

    @Override
    public void draw(GDXRenderer renderer, Viewport viewport) {
        draw(renderer, 0, 0);
    }

    @Override
    public void draw(GDXRenderer renderer, int x, int y) {
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

                if (_align == Align.CENTER) {
                    _offsetX = (_width - getContentWidth()) / 2;
                    _offsetY = (_height - getContentHeight()) / 2;
                }

                if (_align == Align.CENTER_VERTICAL) {
                    _offsetY = (_height - getContentHeight()) / 2;
                }
            }

            if (_gdxBackgroundColor != null) {
                ((GDXRenderer) renderer).draw(_gdxBackgroundColor, _finalX, _finalY, _width, _height);
            }

//        ((GDXRenderer) renderer).draw(com.badlogic.gdx.graphics.Color.RED, _finalX, _finalY, _width, _height);
            ((GDXRenderer) renderer).draw(_string, _textSize, _finalX + _offsetX + _paddingLeft, _finalY + _offsetY + _paddingTop, _gdxColor);
        }
    }

    @Override
    public void refresh() {

    }

    @Override
    public int getContentWidth() {
        if (_string != null) {
            return (int) GDXRenderer.getInstance().getFont(_textSize).getBounds(_string).width;
        }
        return 0;
    }

    @Override
    public int getContentHeight() {
        if (_string != null) {
            return (int) GDXRenderer.getInstance().getFont(_textSize).getBounds(_string).height;
        }
        return 0;
    }
}
