package org.smallbox.faraway.core;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.core.engine.SpriteModel;

/**
 * Created by Alex on 04/06/2015.
 */
public class GDXSpriteModel implements SpriteModel {
    protected Sprite _data;

    public GDXSpriteModel(Texture texture, int x, int y, int width, int height) {
        _data = new Sprite(texture, x, y, width, height);
        _data.flip(false, true);
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    public Sprite getData() {
        return _data;
    }
}
