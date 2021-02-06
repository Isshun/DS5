package org.smallbox.faraway.client.asset;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.modelInfo.GraphicInfo;

@ApplicationObject
public class IconManager {
    @Inject private AssetManager assetManager;
    @Inject private SpriteManager spriteManager;

    public Texture getTexture(String path) {
        return assetManager.lazyLoad(path, Texture.class);
    }

    public Sprite getOrCreateIcon(GraphicInfo graphicInfo, int width, int height) {
        Sprite sprite = spriteManager.getOrCreateSprite(graphicInfo, 0, width + "-" + height);
        sprite.setSize(width, height);
        return sprite;
    }

    public Sprite getOrCreateIcon(String absolutePath) {
        return spriteManager.getOrCreateSprite(absolutePath, 0, 0, 0, 0, 0, null);
    }

}