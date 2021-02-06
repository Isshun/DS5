package org.smallbox.faraway.client.asset;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;

@ApplicationObject
public class PixmapManager {
    @Inject private AssetManager assetManager;

    public Texture createOverlay(Texture textureIn, String key) {
        return assetManager.createTextureFromTexturePixmap(key, textureIn, (pixmapIn, pixmapOut) -> {
            for (int x = 0; x < pixmapIn.getWidth(); x++) {
                for (int y = 0; y < pixmapIn.getHeight(); y++) {
                    int colorInt = pixmapIn.getPixel(x, y);
                    int r = (int) Math.min(((colorInt >> 24) & 0x000000ff) * 1.4 + 32, 255);
                    int g = (int) Math.min(((colorInt >> 16) & 0x000000ff) * 1.4 + 32, 255);
                    int b = (int) Math.min(((colorInt >> 8) & 0x000000ff) * 1.4 + 32, 255);
                    pixmapOut.drawPixel(x, y, (r << 24) + (g << 16) + (b << 8) + (colorInt & 0x000000ff));
                }
            }
        });
    }

    public Texture createOverlay(TextureRegion textureRegionIn, String key) {
        return assetManager.createTextureFromTexturePixmap(key, textureRegionIn.getTexture(), (pixmapIn, pixmapOut) -> {
            for (int x = 0; x < textureRegionIn.getRegionWidth(); x++) {
                for (int y = 0; y < textureRegionIn.getRegionHeight(); y++) {
                    int colorInt = pixmapIn.getPixel(textureRegionIn.getRegionX() + x, textureRegionIn.getRegionY() + y);
                    int r = (int) Math.min(((colorInt >> 24) & 0x000000ff) * 1.4 + 32, 255);
                    int g = (int) Math.min(((colorInt >> 16) & 0x000000ff) * 1.4 + 32, 255);
                    int b = (int) Math.min(((colorInt >> 8) & 0x000000ff) * 1.4 + 32, 255);
                    pixmapOut.drawPixel(x, y, (r << 24) + (g << 16) + (b << 8) + (colorInt & 0x000000ff));
                }
            }
        });
    }

}