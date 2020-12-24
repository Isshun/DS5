package org.smallbox.faraway.client.drawable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.util.Constant;

/**
 * Created by Alex on 15/07/2015.
 */
public class AnimDrawable extends GDXDrawable {
    private final int       _interval;
    private TextureRegion[] _textureRegion;
    private int             _nbTile;
    private int             _count;

    public AnimDrawable(String path, int nbTile) {
        _interval = 1;
        _nbTile = nbTile;
        _textureRegion = new TextureRegion[nbTile];
        for (int i = 0; i < nbTile; i++) {
            _textureRegion[i] = new TextureRegion(DependencyInjector.getInstance().getDependency(SpriteManager.class).getTexture(path), i * Constant.TILE_WIDTH, 0, Constant.TILE_WIDTH, Constant.TILE_HEIGHT);
            _textureRegion[i].flip(false, true);
        }
    }

    public AnimDrawable(String path, int x, int y, int width, int height, int nbTile, int interval) {
        _interval = interval;
        _nbTile = nbTile;
        _textureRegion = new TextureRegion[nbTile];
        for (int i = 0; i < nbTile; i++) {
            _textureRegion[i] = new TextureRegion(DependencyInjector.getInstance().getDependency(SpriteManager.class).getTexture(path), x + i * width, y, width, height);
            _textureRegion[i].flip(false, true);
        }
    }

    @Override
    public void draw(SpriteBatch batch, int x, int y) {
        batch.draw(_textureRegion[_count++ / _interval % _nbTile], x, y);
    }
}
