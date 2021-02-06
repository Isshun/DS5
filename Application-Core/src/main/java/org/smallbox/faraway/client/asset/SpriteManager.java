package org.smallbox.faraway.client.asset;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import org.apache.commons.collections4.CollectionUtils;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.modelInfo.GraphicInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationObject
public class SpriteManager {
    @Inject private DataManager dataManager;
    @Inject private AssetManager assetManager;

    protected final Map<String, Sprite> _sprites = new ConcurrentHashMap<>();

    public void init() {
        dataManager.getItems().stream()
                .map(itemInfo -> itemInfo.graphics)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .forEach(graphicInfo -> assetManager.load(graphicInfo.absolutePath, Texture.class));
    }

    public void setTexturesFilter() {
        assetManager.getAll(Texture.class, new Array<>()).forEach(texture -> texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear));
    }

    public Texture getTexture(String path) {
        return assetManager.lazyLoad(path, Texture.class);
    }

    public Sprite getOrCreateSprite(ItemInfo itemInfo) {
        return itemInfo != null && CollectionUtils.isNotEmpty(itemInfo.graphics) ? getOrCreateSprite(itemInfo.graphics.get(0)) : null;
    }

    public Sprite getOrCreateSprite(GraphicInfo graphicInfo) {
        return getOrCreateSprite(graphicInfo, 0, "");
    }

    public Sprite getOrCreateSprite(GraphicInfo graphicInfo, int tile, String keyExtra) {
        return getOrCreateSprite(
                graphicInfo.absolutePath,
                graphicInfo.x,
                graphicInfo.y,
                (graphicInfo.width != 0 ? graphicInfo.width : graphicInfo.tileWidth),
                (graphicInfo.height != 0 ? graphicInfo.height : graphicInfo.tileHeight),
                tile,
                keyExtra);
    }

    public Sprite getOrCreateSprite(String absolutePath, int srcX, int srcY, int width, int height, int tile, String keyExtra) {
        String key = absolutePath + "-" + tile + "-" + (keyExtra != null ? keyExtra : "");

        if (!_sprites.containsKey(key)) {
            Texture texture = assetManager.lazyLoad(absolutePath, Texture.class);

            // Create sprite from texture
            if (texture != null) {
                Sprite sprite = new Sprite(texture,
                        (srcX + tile) * width,
                        srcY * height,
                        width != 0 ? width : texture.getWidth(),
                        height != 0 ? height : texture.getHeight());

                sprite.setFlip(false, true);
                sprite.setColor(new Color(255, 255, 255, 1));

                _sprites.put(key, sprite);
            }

        }

        return _sprites.get(key);
    }

}