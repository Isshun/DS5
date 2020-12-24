package org.smallbox.faraway.client.drawable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;

public class IconDrawable extends GDXDrawable {
    private final TextureRegion _textureRegion;

    public IconDrawable(String path, int x, int y, int width, int height) {
        _textureRegion = new TextureRegion(DependencyInjector.getInstance().getDependency(SpriteManager.class).getTexture(path), x, y, width, height);
        _textureRegion.flip(false, true);
    }

    @Override
    public void draw(SpriteBatch batch, int x, int y) {
        batch.draw(_textureRegion, x, y);
    }
}
