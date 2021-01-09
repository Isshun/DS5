package org.smallbox.faraway.client.drawable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.util.Constant;

public class AnimDrawable extends GDXDrawable {
    private final int       _interval;
    private final TextureRegion[] _textureRegion;
    private final int             _nbTile;
    private int             _count;

    public AnimDrawable(String path, int nbTile) {
        _interval = 1;
        _nbTile = nbTile;
        _textureRegion = new TextureRegion[nbTile];
        for (int i = 0; i < nbTile; i++) {
            _textureRegion[i] = new TextureRegion(DependencyManager.getInstance().getDependency(SpriteManager.class).getTexture(path), i * Constant.TILE_SIZE, 0, Constant.TILE_SIZE, Constant.TILE_SIZE);
            _textureRegion[i].flip(false, true);
        }
    }

    public AnimDrawable(String path, int x, int y, int width, int height, int nbTile, int interval) {
        _interval = interval;
        _nbTile = nbTile;
        _textureRegion = new TextureRegion[nbTile];
        for (int i = 0; i < nbTile; i++) {
            _textureRegion[i] = new TextureRegion(DependencyManager.getInstance().getDependency(SpriteManager.class).getTexture(path), x + i * width, y, width, height);
            _textureRegion[i].flip(false, true);
        }
    }

    @Override
    public void draw(SpriteBatch batch, int x, int y) {
        batch.draw(_textureRegion[_count++ / _interval % _nbTile], x, y);
    }
}
