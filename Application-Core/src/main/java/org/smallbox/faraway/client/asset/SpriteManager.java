package org.smallbox.faraway.client.asset;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import org.apache.commons.collections4.CollectionUtils;
import org.smallbox.faraway.util.GameException;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.modelInfo.GraphicInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.log.Log;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

@ApplicationObject
public class SpriteManager {
    private static final int NB_SELECTOR_TILE = 4;

    private static final int TOP_LEFT = 0b10000000;
    private static final int TOP = 0b01000000;
    private static final int TOP_RIGHT = 0b00100000;
    private static final int LEFT = 0b00010000;
    private static final int RIGHT = 0b00001000;
    private static final int BOTTOM_LEFT = 0b00000100;
    private static final int BOTTOM = 0b00000010;
    private static final int BOTTOM_RIGHT = 0b00000001;

    private final Map<Integer, Sprite> _spritesCharacters;
    protected final Map<Long, Sprite> _sprites;
    private Sprite[] _selectors;
    private final Map<String, Sprite> _icons;
    private final Set<String> paths = new ConcurrentSkipListSet<>();

    protected int _spriteCount;
    @Inject private DataManager dataManager;
    @Inject private AssetManager assetManager;

    public SpriteManager() {
        _icons = new HashMap<>();

        _sprites = new HashMap<>();
        _spritesCharacters = new HashMap<>();
    }

    public void init() {
        Texture itemSelector = assetManager.lazyLoad("data/res/item_selector.png", Texture.class);
        _selectors = new Sprite[NB_SELECTOR_TILE];
        _selectors[0] = new Sprite(itemSelector, 0, 0, 8, 8);
        _selectors[0].flip(false, true);
        _selectors[1] = new Sprite(itemSelector, 8, 0, 8, 8);
        _selectors[1].flip(false, true);
        _selectors[2] = new Sprite(itemSelector, 0, 8, 8, 8);
        _selectors[2].flip(false, true);
        _selectors[3] = new Sprite(itemSelector, 8, 8, 8, 8);
        _selectors[3].flip(false, true);

        dataManager.getItems().stream()
                .map(itemInfo -> itemInfo.graphics)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .forEach(graphicInfo -> assetManager.load("data" + graphicInfo.path, Texture.class));
    }

    public void setTexturesFilter() {
        assetManager.getAll(Texture.class, new Array<>()).forEach(texture -> texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear));
    }

    public Sprite getIcon(String path) {
        if (!_icons.containsKey(path)) {
            String realPath = "data" + path.replace("[base]", "");
            Texture texture = getTexture(realPath);
            Sprite sprite = new Sprite(texture, 0, 0, texture.getWidth(), texture.getHeight());
            sprite.flip(false, true);
            _icons.put(path, sprite);
        }
        return _icons.get(path);
    }

    /** TODO
     * Depending of the available resources:
     * - Use graphic info with type 'icon'
     * - Use regular texture scaled to fit in a single tile
     */
    public Sprite getIcon(ItemInfo info) {
//        if (info.icon == null) {
//            Sprite sprite = getNewSprite(info.graphics.get(0));
//            switch ((int) Math.max(sprite.getWidth() / Constant.TILE_SIZE, sprite.getHeight() / Constant.TILE_SIZE)) {
//                case 2: sprite.setScale(0.85f, 0.85f); break;
//                case 3: sprite.setScale(0.55f, 0.55f); break;
//                case 4: sprite.setScale(0.35f, 0.35f); break;
//                case 5: sprite.setScale(0.32f, 0.32f); break;
//                case 6: sprite.setScale(0.3f, 0.3f); break;
//                case 7: sprite.setScale(0.25f, 0.25f); break;
//                case 8: sprite.setScale(0.2f, 0.2f); break;
//            }
//        }
//
//        return getNewSprite(info.icon);
        return info.graphics != null ? getSprite(info, info.graphics.get(0), 0, 0, 255, true) : null;
    }

    public Sprite getSprite(ItemInfo itemInfo, GraphicInfo graphicInfo, int parcelTile, int state, int alpha, boolean isIcon) {
        assert graphicInfo != null;

        int width = graphicInfo.width;
        int height = graphicInfo.height;

        if (graphicInfo.spriteId == -1) {
            graphicInfo.spriteId = ++_spriteCount;
        }

        long sum = graphicInfo.type == GraphicInfo.Type.WALL || graphicInfo.type == GraphicInfo.Type.DOOR ?
                getSum(graphicInfo.spriteId, parcelTile, 0, isIcon ? 1 : 0) :
                getSum(graphicInfo.spriteId, 0, 0, isIcon ? 1 : 0);

        Sprite sprite = _sprites.get(sum);
        if (sprite == null) {
            Texture texture = getTexture(graphicInfo.packageName + graphicInfo.path);

            if (texture != null) {
                if (graphicInfo.type == GraphicInfo.Type.DOOR) {

                    if ((parcelTile & RIGHT) > 0 && (parcelTile & LEFT) > 0) {
                        sprite = new Sprite(texture, 0, Constant.TILE_SIZE, width, height);
                        sprite.setFlip(false, true);
                        _sprites.put(sum, sprite);
                    } else if ((parcelTile & TOP) > 0 && (parcelTile & BOTTOM) > 0) {
                        sprite = new Sprite(texture, Constant.TILE_SIZE, Constant.TILE_SIZE, width, height);
                        sprite.setFlip(false, true);
                        _sprites.put(sum, sprite);
                    } else {
                        sprite = new Sprite(texture, 0, Constant.TILE_SIZE, width, height);
                        sprite.setFlip(false, true);
                        _sprites.put(sum, sprite);
                    }
                } else {
                    sprite = new Sprite(texture, 0, 0, width, height);
                    sprite.setFlip(false, true);
                    sprite.setColor(new Color(255, 255, 255, alpha));
                    if (isIcon) {
                        switch (Math.max(width / Constant.TILE_SIZE, height / Constant.TILE_SIZE)) {
                            case 2:
                                sprite.setScale(0.85f, 0.85f);
                                break;
                            case 3:
                                sprite.setScale(0.55f, 0.55f);
                                break;
                            case 4:
                                sprite.setScale(0.35f, 0.35f);
                                break;
                            case 5:
                                sprite.setScale(0.32f, 0.32f);
                                break;
                            case 6:
                                sprite.setScale(0.3f, 0.3f);
                                break;
                            case 7:
                                sprite.setScale(0.25f, 0.25f);
                                break;
                            case 8:
                                sprite.setScale(0.2f, 0.2f);
                                break;
                        }

                    }
                    _sprites.put(sum, sprite);
                }
            }
        }

        if (sprite != null && graphicInfo.textureRect == null) {
            getTextureRect(graphicInfo, sprite);
        }

        return sprite;
    }

    private void getTextureRect(GraphicInfo item, Sprite sprite) {
        TextureData textureData = sprite.getTexture().getTextureData();
        if (!textureData.isPrepared()) {
            textureData.prepare();
        }
        Pixmap pixmap = textureData.consumePixmap();

        int startX = -1;
        for (int x = 0; x < sprite.getWidth(); x++) {
            for (int y = 0; y < sprite.getHeight(); y++) {
                if (startX == -1 && (pixmap.getPixel(x, y) & 0x000000ff) != 0) startX = x;
            }
        }
        int endX = -1;
        for (int x = (int) (sprite.getWidth() - 1); x >= 0; x--) {
            for (int y = 0; y < sprite.getHeight(); y++) {
                if (endX == -1 && (pixmap.getPixel(x, y) & 0x000000ff) != 0) endX = x + 1;
            }
        }
        int startY = -1;
        for (int y = 0; y < sprite.getHeight(); y++) {
            for (int x = 0; x < sprite.getWidth(); x++) {
                if (startY == -1 && (pixmap.getPixel(x, y) & 0x000000ff) != 0) startY = y;
            }
        }
        int endY = -1;
        for (int y = (int) (sprite.getHeight() - 1); y >= 0; y--) {
            for (int x = 0; x < sprite.getWidth(); x++) {
                if (endY == -1 && (pixmap.getPixel(x, y) & 0x000000ff) != 0) endY = y + 1;
            }
        }

//        Log.info("Item: " + item.name + ", " + startX + ", " + startY + ", " + endX + ", " + endY);

        item.textureRect = new Rectangle(startX, startY, endX, endY);

        pixmap.dispose();
        textureData.disposePixmap();
    }

    protected long getSum(int spriteId, int tile, int state, int extra) {
        if (spriteId > 4096 || tile > 4096 || extra > 4096 || state > 4096) {
            throw new GameException(SpriteManager.class, "SpriteManager.getSum -> out of bounds values");
        }

        long sum = spriteId;
        sum = sum << 12;
        sum += tile;
        sum = sum << 12;
        sum += extra;
        sum = sum << 12;
        sum += state;
        sum = sum << 12;

        return sum;
    }

    public Sprite getCharacter(CharacterModel c, int direction, int frame) {
        int extra = c.getType().index;
        int sum = 0;
        sum = (sum << 8) + direction;
        sum = (sum << 8) + frame;
        sum = (sum << 8) + extra;

        Sprite sprite = _spritesCharacters.get(sum);
        if (sprite == null) {
            Texture texture = assetManager.lazyLoad(c.getType().path, Texture.class);

            sprite = new Sprite(texture, 0, 0, Constant.CHARACTER_WIDTH, Constant.CHARACTER_HEIGHT);
            sprite.setFlip(false, true);

//            sprite = new Sprite(texture,
//                    Constant.CHAR_WIDTH * frame + (extra * 128),
//                    Constant.CHAR_HEIGHT * direction,
//                    Constant.CHAR_WIDTH,
//                    Constant.CHAR_HEIGHT);
//
            _spritesCharacters.put(sum, sprite);
        }

        return sprite;
    }

    public Sprite getSelector(int tile) {
        return _selectors[tile % NB_SELECTOR_TILE];
    }

    public Sprite getSelectorCorner(int corner) {
        return _selectors[corner];
    }

    public Texture getTexture(String path) {
        return lazyLoad(path);
    }

    private Texture lazyLoad(String path) {
        if (!assetManager.contains(path) && !paths.contains(path)) {
            try {
                paths.add(path);
                assetManager.load(path, Texture.class);
                assetManager.finishLoading();
                assetManager.get(path, Texture.class).setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                Log.warning(SpriteManager.class, "Lazy load " + path);
            } catch (GdxRuntimeException e) {
                Log.error(SpriteManager.class, "Unable to find " + path);
            }
        }

        if (!assetManager.contains(path)) {
            Log.error("Unable to find " + path);
            return assetManager.get("data/graphics/missing.png");
        }

        return assetManager.get(path);
    }

    public Texture getTexture(GraphicInfo graphicInfo) {
        return getTexture(graphicInfo.packageName + graphicInfo.path);
    }

    public Sprite getNewSprite(ItemInfo itemInfo) {
        return itemInfo != null && CollectionUtils.isNotEmpty(itemInfo.graphics) ? getNewSprite(itemInfo.graphics.get(0), 0) : null;
    }

    public Sprite getNewSprite(GraphicInfo graphicInfo) {
        return getNewSprite(graphicInfo, 0);
    }

    public Sprite getNewSprite(GraphicInfo graphicInfo, int tile) {
        //assetManager.finishLoading();

        assert graphicInfo != null;

        if (graphicInfo.spriteId == -1) {
            graphicInfo.spriteId = ++_spriteCount;
        }

        long sum = getSum(graphicInfo.spriteId, graphicInfo.x, graphicInfo.y, tile);

//        long sum = graphicInfo.type == GraphicInfo.Type.WALL || graphicInfo.type == GraphicInfo.Type.DOOR ?
//                getSum(graphicInfo.spriteId, 0, 0, 0) :
//                getSum(graphicInfo.spriteId, 0, 0, 0);
//
        Sprite sprite = _sprites.get(sum);
        if (sprite == null) {
//            Texture texture = _textures.get(graphicInfo.packageName + graphicInfo.path);
            Texture texture = lazyLoad("data" + graphicInfo.path);
            if (texture != null) {
                texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

                if (graphicInfo.type == GraphicInfo.Type.ICON) {
                    sprite = new Sprite(texture, 0, 0, graphicInfo.width, graphicInfo.height);
                } else if (graphicInfo.width != 0 && graphicInfo.height != 0) {
                    sprite = new Sprite(texture, 0, 0, graphicInfo.width, graphicInfo.height);
                } else {
                    sprite = new Sprite(texture,
                            (graphicInfo.x + tile) * graphicInfo.tileWidth,
                            graphicInfo.y * graphicInfo.tileHeight,
                            graphicInfo.tileWidth,
                            graphicInfo.tileHeight);
                }
                sprite.setFlip(false, true);
                sprite.setColor(new Color(255, 255, 255, 1));

                _sprites.put(sum, sprite);
            }
        }

        return _sprites.get(sum);
    }

}