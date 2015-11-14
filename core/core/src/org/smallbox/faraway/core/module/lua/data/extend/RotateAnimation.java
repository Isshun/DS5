package org.smallbox.faraway.core.module.lua.data.extend;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;

/**
 * Created by Alex on 14/11/2015.
 */
public class RotateAnimation {
    private float   _rotation;
    private int     _duration;

    public RotateAnimation(int duration) {
        _duration = duration;
    }

    public void draw(GDXRenderer renderer, Sprite sprite, int x, int y) {
        _rotation += (360f * 1000 * (Gdx.graphics.getDeltaTime() / _duration));
        sprite.setRotation(_rotation);
        renderer.draw(sprite, x, y);
    }
}
