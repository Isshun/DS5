package org.smallbox.faraway;

import org.jsfml.graphics.Sprite;

/**
 * Created by Alex on 27/05/2015.
 */
public class SpriteModel {
    private Sprite  _data;
    private int     _x;
    private int     _y;

    public SpriteModel() {
        _data = new Sprite();
    }

    public Sprite getData() {
        return _data;
    }

    public void setPosition(int x, int y) {
        _x = x;
        _y = y;

        if (_data != null) {
            _data.setPosition(_x, _y);
        }
    }

    public int getWidth() {
        return _data.getTextureRect().width;
    }

    public int getHeight() {
        return _data.getTextureRect().height;
    }
}
