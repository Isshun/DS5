package org.smallbox.faraway.client.asset;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Array;
import org.apache.commons.collections4.CollectionUtils;
import org.smallbox.faraway.client.engine.animator.Animator;
import org.smallbox.faraway.client.engine.SpriteExtra;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.modelInfo.GraphicInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.util.Constant;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationObject
public class SpriteManager {
    @Inject private DataManager dataManager;
    @Inject private AssetManager assetManager;
    @Inject private PixmapManager pixmapManager;

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
        return getOrCreateSprite(graphicInfo, 0, false, "", null);
    }

    public Sprite getOrCreateSprite(GraphicInfo graphicInfo, boolean overlay, SpriteExtra spriteExtra) {
        return getOrCreateSprite(graphicInfo, 0, overlay, "", spriteExtra);
    }

    public Sprite getOrCreateSprite(GraphicInfo graphicInfo, boolean overlay) {
        return getOrCreateSprite(graphicInfo, 0, overlay, "", null);
    }

    public Sprite getOrCreateSprite(GraphicInfo graphicInfo, int tile, boolean overlay, String keyExtra, SpriteExtra spriteExtra) {
        return getOrCreateSprite(
                graphicInfo.absolutePath,
                graphicInfo.x,
                graphicInfo.y,
                (graphicInfo.width != 0 ? graphicInfo.width : graphicInfo.tileWidth),
                (graphicInfo.height != 0 ? graphicInfo.height : graphicInfo.tileHeight),
                tile,
                overlay,
                keyExtra,
                spriteExtra);
    }

    public Sprite getOrCreateSprite(String absolutePath, int srcX, int srcY, int width, int height, int tile, boolean overlay, String keyExtra, SpriteExtra extra) {
        String key = absolutePath + "-" + tile + "-" + (overlay ? "overlay-" : "") + (keyExtra != null ? keyExtra : "");

        if (!_sprites.containsKey(key)) {
            Texture texture = assetManager.lazyLoad(absolutePath, Texture.class);

            if (overlay) {
                texture = pixmapManager.createOverlay(texture, key);
            }

            // Create sprite from texture
            if (texture != null) {
                int cols = texture.getWidth() / Constant.TILE_SIZE;
                int tileX = cols != 0 ? (tile % cols) : 0;
                int tileY = cols != 0 ? (tile / cols) : 0;
                Sprite sprite = new Sprite(texture,
                        (srcX + tileX) * width,
                        (srcY + tileY) * height,
                        width != 0 ? width : texture.getWidth(),
                        height != 0 ? height : texture.getHeight());

                sprite.setFlip(false, true);

                _sprites.put(key, sprite);
            }

        }

        Sprite sprite = _sprites.get(key);
        sprite.setAlpha(1);

        if (extra != null) {
            sprite.setFlip(extra.flip, true);
            sprite.setRotation(extra.rotate);
            sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() - 25);
            sprite.setScale(extra.scale);

            if (extra.animator != null) {
                extra.animator.update(sprite);
            }
        }

        return sprite;
    }

    public SpriteExtra buildSpriteExtraParameters(GraphicInfo graphicInfo) {
        SpriteExtra extra = new SpriteExtra();

        Optional.ofNullable(graphicInfo.randomization).ifPresent(randomization -> {
            if (randomization.offset > 0) {
                extra.offsetX = new Random().nextInt(randomization.offset) - randomization.offset / 2;
                extra.offsetY = new Random().nextInt(randomization.offset) - randomization.offset / 2;
            }

            if (randomization.rotate > 0) {
                extra.rotate = new Random().nextInt(randomization.rotate) - randomization.rotate / 2;
            }

            if (randomization.scale != 1) {
                extra.scale = 1 + new Random().nextFloat() * randomization.scale - randomization.scale / 2;
            }

            extra.flip = randomization.flip && new Random().nextBoolean();
        });

        Optional.ofNullable(graphicInfo.animation).ifPresent(animation -> {
            extra.animator = new Animator(-animation.value / 2, animation.value / 2, animation.speed, Interpolation.pow2, Sprite::setRotation);
        });

        return extra;
    }
}