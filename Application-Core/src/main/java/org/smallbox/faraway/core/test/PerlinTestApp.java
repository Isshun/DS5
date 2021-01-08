package org.smallbox.faraway.core.test;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.smallbox.faraway.modules.world.factory.old.MapFactoryConfig;
import org.smallbox.faraway.modules.world.factory.old.PerlingGenerator;

import java.util.List;
import java.util.Random;

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
            _sprite = new Sprite[8];

            MapFactoryConfig config = MapFactoryConfig.createMountains2();
            int size = 3000;

            float[][] image = PerlingGenerator.GenerateWhiteNoise(size, size);
            float[][] perlinNoise = PerlingGenerator.GeneratePerlinNoise(image, config.perlinOctave);
            for (MapFactoryConfig.AdjustmentValue adjustment : config.adjustments) {
                perlinNoise = PerlingGenerator.AdjustLevels(perlinNoise, adjustment.min, adjustment.max);
            }

            float[][] perlinNoise2 = PerlingGenerator.GeneratePerlinNoise(image, 10);
            for (MapFactoryConfig.AdjustmentValue adjustment :  List.of(
                    new MapFactoryConfig.AdjustmentValue(0.2f, 0.68f),
                    new MapFactoryConfig.AdjustmentValue(0.2f, 0.68f),
                    new MapFactoryConfig.AdjustmentValue(0.2f, 0.68f),
                    new MapFactoryConfig.AdjustmentValue(0.2f, 0.68f)
            )) {
                perlinNoise2 = PerlingGenerator.AdjustLevels(perlinNoise2, adjustment.min, adjustment.max);
            }

            Pixmap p1 = createPixmap("data/graphics/texture/sand.png");
            Pixmap p2 = createPixmap("data/graphics/texture/sand2.png");
            Pixmap p3 = createPixmap("data/graphics/texture/water.png");

            for (int i = 0; i < 1; i++) {
                Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
                for (int x = 0; x < size; x++) {
                    for (int y = 0; y < size; y++) {
                        {
                            int alpha = (0xffffff << 8) + (int) (perlinNoise[x][y] * 255);
                            int color = p1.getPixel(x % 512, y % 512) & alpha;
                            pixmap.drawPixel(x, y, color);
                        }
                        {
                            int alpha = (0xffffff << 8) + (int) ((1 - perlinNoise[x][y]) * 255);
                            int color = p2.getPixel(x % 512, y % 512) & alpha;
                            pixmap.drawPixel(x, y, color);
                        }
                        {
                            int alpha = (0xffffff << 8) + (int) ((1 - perlinNoise2[x][y]) * 255);
                            int color = p3.getPixel(x % 512, y % 512) & alpha;
                            pixmap.drawPixel(x, y, color);
                        }
                    }
                }

                _sprite[i] = new Sprite(new Texture(pixmap));
            }
        }

        _batch.begin();

        Random r = new Random(42);

        for (int x = 0; x < 1; x++) {
            for (int y = 0; y < 1; y++) {
                int i = r.nextInt(8);
                _sprite[0].setPosition(0, 0);
                _sprite[0].draw(_batch);
            }
        }

        _batch.end();
    }

    private Pixmap createPixmap(String internalPath) {
        Texture texture = new Texture(internalPath);
        texture.getTextureData().prepare();
        return texture.getTextureData().consumePixmap();
    }
}
