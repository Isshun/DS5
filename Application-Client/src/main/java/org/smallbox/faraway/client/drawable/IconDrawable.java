package org.smallbox.faraway.client.drawable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.smallbox.faraway.client.ApplicationClient;

/**
 * Created by Alex on 15/07/2015.
 */
public class IconDrawable extends GDXDrawable {
    private final TextureRegion _textureRegion;

    public IconDrawable(String path, int x, int y, int width, int height) {
        _textureRegion = new TextureRegion(ApplicationClient.spriteManager.getTexture(path), x, y, width, height);
        _textureRegion.flip(false, true);
    }

    @Override
    public void draw(SpriteBatch batch, int x, int y) {
        batch.draw(_textureRegion, x, y);
    }
}