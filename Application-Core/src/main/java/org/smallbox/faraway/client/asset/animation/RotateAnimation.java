package org.smallbox.faraway.client.asset.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.client.renderer.BaseRenderer;

public class RotateAnimation {
    private float   _rotation;
    private final int     _duration;

    public RotateAnimation(int duration) {
        _duration = duration;
    }

    public void draw(BaseRenderer renderer, Sprite sprite, int x, int y) {
        _rotation += (360f * 1000 * (Gdx.graphics.getDeltaTime() / _duration));
        sprite.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        sprite.setRotation(_rotation);
        renderer.drawSprite(sprite, x, y);
    }
}
