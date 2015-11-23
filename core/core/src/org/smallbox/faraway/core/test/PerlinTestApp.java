package org.smallbox.faraway.core.test;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.smallbox.faraway.core.game.module.world.factory.old.MapFactoryConfig;
import org.smallbox.faraway.core.game.module.world.factory.old.PerlingGenerator;

import java.util.Random;

/**
 * Created by Alex on 07/07/2015.
 */
public class PerlinTestApp extends ApplicationAdapter {
    private Sprite[]        _sprite;
    private SpriteBatch     _batch;

    @Override
    public void create () {
        _batch = new SpriteBatch();
    }

    @Override
    public void render () {

        if (_sprite == null) {
            _sprite = new Sprite[4];

            MapFactoryConfig config = MapFactoryConfig.createMountains();
            int size = 512;

            float[][] image = PerlingGenerator.GenerateWhiteNoise(size, size);
            float[][] perlinNoise = PerlingGenerator.GeneratePerlinNoise(image, config.perlinOctave);
            for (MapFactoryConfig.AdjustmentValue adjustment : config.adjustments) {
                perlinNoise = PerlingGenerator.AdjustLevels(perlinNoise, adjustment.min, adjustment.max);
            }

            Texture t1 = new Texture("data/res/g1.png");
            t1.getTextureData().prepare();
            Texture t2 = new Texture("data/res/g2.png");
            t2.getTextureData().prepare();

            Pixmap p1 = t1.getTextureData().consumePixmap();
            Pixmap p2 = t2.getTextureData().consumePixmap();

            for (int i = 0; i < 4; i++) {
                Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
                for (int x = 0; x < size; x++) {
                    for (int y = 0; y < size; y++) {
                        int relX = i == 0 || i == 1 ? x : size - x - 1;
                        int relY = i == 0 || i == 2 ? y : size - y - 1;

                        {
                            int alpha = (0xffffff << 8) + (int) (perlinNoise[x][y] * 255);
                            int color = p1.getPixel(x, y) & alpha;
                            pixmap.drawPixel(relX, relY, color);
                        }

                        {
                            int alpha = (0xffffff << 8) + (int) ((1 - perlinNoise[x][y]) * 255);
                            int color = p2.getPixel(x, y) & alpha;
                            pixmap.drawPixel(relX, relY, color);
                        }

//                    {
//                        int alpha = (0xffffff << 8) + (int) (perlinNoise[x][y] * 255);
//                        pixmap.drawPixel(x, y, alpha);
//                    }
                    }
                }

                _sprite[i] = new Sprite(new Texture(pixmap));
            }
        }

        _batch.begin();

        Random r = new Random(42);

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                int i = 0;//r.nextInt(4);
                _sprite[i].setPosition(512 * x, 512 * y);
                _sprite[i].draw(_batch);
            }
        }

        _batch.end();
    }
}
