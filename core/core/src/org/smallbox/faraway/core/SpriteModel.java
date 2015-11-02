package org.smallbox.faraway.core;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Alex on 04/06/2015.
 */
public class SpriteModel {
    protected Sprite _data;

    public SpriteModel(Texture texture, int x, int y, int width, int height) {
        _data = new Sprite(texture, x, y, width, height);
        _data.flip(false, true);
    }

    public int getWidth() {
        return 0;
    }

    public int getHeight() {
        return 0;
    }

    public Sprite getData() {
        return _data;
    }
}
