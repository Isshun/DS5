package org.smallbox.faraway.client.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.modules.world.factory.old.MapFactoryConfig;

import static com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888;

@ApplicationObject
public class PerlinGenerator {
    private Sprite sprite;
    private Pixmap pixmap;

    public Pixmap render() {
        if (pixmap == null) {
            MapFactoryConfig config = MapFactoryConfig.createMountains2();
            int size = 128;

            Pixmap mask1 = createPixmap("data/graphics/texture/blend_mask_3.png", RGBA8888);
            Pixmap mask2 = createPixmap("data/graphics/texture/blend_mask_3.png", RGBA8888);
            Pixmap p1 = createPixmap("data/graphics/texture/g2.png", RGBA8888);
            Pixmap p2 = createPixmap("data/graphics/texture/g2.png", RGBA8888);

            pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);

            for (int i = 0; i < 1; i++) {
                for (int x = 0; x < size; x++) {
                    for (int y = 0; y < size; y++) {
                        {
//                            int alpha = (0xffffff << 8) + (255 - mask.getPixel(x, y));
                            int alpha = (0xffffff << 8) + 255;
                            int color = p2.getPixel(x % 512, y % 512) & alpha;
//                            pixmap.drawPixel(x, y, color);
                        }
                        {
//                            int alpha = (0xffffff << 8) + Math.min((x > 128 ? mask1 : mask2).getPixel(x % 128, y), 255);
                            int alpha = (0xffffff << 8) + (mask1.getPixel(x % 128, y) & 0x000000ff);
//                            int alpha = (0xffffff << 8) + Math.min(y, 255);
                            int color = p1.getPixel(x % 512, y % 512) & alpha;
//                            if (y > 60 && y < 65 && x > 60 && x < 65) {
                                pixmap.drawPixel(x, y, color);
//                            }
                        }
                    }
                }

//                sprite = new Sprite(new Texture(pixmap));
//                sprite.setPosition(1200, 300);
            }
        }

        return pixmap;
    }

    private Pixmap createPixmap(String internalPath, Pixmap.Format format) {
        Texture texture = new Texture(Gdx.files.internal(internalPath), format, true);
        texture.getTextureData().prepare();
        return texture.getTextureData().consumePixmap();
    }

}
