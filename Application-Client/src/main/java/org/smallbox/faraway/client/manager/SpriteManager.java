package org.smallbox.faraway.client.manager;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import org.apache.commons.lang3.NotImplementedException;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.modelInfo.GraphicInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.NetworkItem;
import org.smallbox.faraway.core.module.world.model.StructureItem;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.util.CollectionUtils;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.FileUtils;
import org.smallbox.faraway.util.Log;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Alex on 04/06/2015.
 */
@ApplicationObject
public class SpriteManager {
    private static final int                NB_SELECTOR_TILE = 4;

    private static final int                TOP_LEFT = 0b10000000;
    private static final int                TOP = 0b01000000;
    private static final int                TOP_RIGHT = 0b00100000;
    private static final int                LEFT = 0b00010000;
    private static final int                RIGHT = 0b00001000;
    private static final int                BOTTOM_LEFT = 0b00000100;
    private static final int                BOTTOM = 0b00000010;
    private static final int                BOTTOM_RIGHT = 0b00000001;

    private Map<Integer, Sprite>            _spritesCharacters;
    private Map<Long, Sprite>               _sprites;
    private Sprite[]                        _selectors;
    private Map<String, Sprite>             _icons;
    private Map<String, Texture>            _textures;

    private int _spriteCount;
    private AssetManager _manager;
    
    @Inject
    private Data data;

    public SpriteManager() {
        _icons = new HashMap<>();
        _manager = new AssetManager();

        _sprites = new HashMap<>();
        _spritesCharacters = new HashMap<>();
    }

    public void reload() {
        _sprites.clear();
        _spriteCount = 0;
    }

    public void init() {

        Texture itemSelector = new Texture(FileUtils.getFileHandle("data/res/item_selector.png"));
        _selectors = new Sprite[NB_SELECTOR_TILE];
        _selectors[0] = new Sprite(itemSelector, 0, 0, 8, 8);
        _selectors[0].flip(false, true);
        _selectors[1] = new Sprite(itemSelector, 8, 0, 8, 8);
        _selectors[1].flip(false, true);
        _selectors[2] = new Sprite(itemSelector, 0, 8, 8, 8);
        _selectors[2].flip(false, true);
        _selectors[3] = new Sprite(itemSelector, 8, 8, 8, 8);
        _selectors[3].flip(false, true);

        _textures = new ConcurrentHashMap<>();

        _textures.put("data/res/bg_area.png", new Texture(FileUtils.getFileHandle("data/res/bg_area.png")));
//        _textures.put("data/graphics/jobs/ic_build.png", new Texture(FileUtils.getFileHandle("data/graphics/jobs/ic_build.png")));
//        _textures.put("data/graphics/jobs/ic_craft.png", new Texture(FileUtils.getFileHandle("data/graphics/jobs/ic_craft.png")));
//        _textures.put("data/graphics/jobs/ic_dump.png", new Texture(FileUtils.getFileHandle("data/graphics/jobs/ic_dump.png")));
//        _textures.put("data/graphics/jobs/ic_gather.png", new Texture(FileUtils.getFileHandle("data/graphics/jobs/ic_gather.png")));
//        _textures.put("data/graphics/jobs/ic_haul.png", new Texture(FileUtils.getFileHandle("data/graphics/jobs/ic_haul.png")));
//        _textures.put("data/graphics/jobs/ic_mining.png", new Texture(FileUtils.getFileHandle("data/graphics/jobs/ic_mining.png")));
//        _textures.put("data/graphics/jobs/ic_store.png", new Texture(FileUtils.getFileHandle("data/graphics/jobs/ic_store.png")));

//        _textures = FileUtils.listRecursively("data/res").stream()
//                .filter(file -> file.getName().endsWith(".png"))
//                .collect(Collectors.toMap(file -> file.getPath().replace("\\", "/"), file -> new Texture(new FileHandle(file))));

        if (CollectionUtils.isNotEmpty(data.getItems())) {
            data.getItems().forEach(itemInfo -> {
                if (itemInfo.graphics != null && !itemInfo.graphics.isEmpty()) {
                    itemInfo.graphics.forEach(graphicInfo -> {
                        _manager.load("data" + graphicInfo.path, Texture.class);
                        File file = getFile(itemInfo, graphicInfo);
                        if (file.exists()) {
                            _textures.put(graphicInfo.packageName + graphicInfo.path, new Texture(new FileHandle(file)));
                        } else {
                            _textures.put(graphicInfo.packageName + graphicInfo.path, new Texture(FileUtils.getFileHandle("data/graphics/missing.png")));
                            Log.warning("Impossible de trouver la texture de l'item: " + itemInfo.name);
                        }
                    });
                }
            });
        } else {
            throw new GameException(SpriteManager.class, "Data collection should not be empty");
        }
    }

    public Sprite getIcon(String path) {
        if (!_icons.containsKey(path)) {
            File file = getFile(path);
            Texture texture = new Texture(new FileHandle(file));
            Sprite sprite = new Sprite(texture, 0, 0, texture.getWidth(), texture.getHeight());
            sprite.flip(false, true);
            _icons.put(path, sprite);
        }
        return _icons.get(path);
    }

    public Sprite getIcon(String path, int width, int height) {
        if (!_icons.containsKey(path)) {
            File file = getFile(path);
            Texture texture = new Texture(new FileHandle(file));
            Sprite sprite = new Sprite(texture, 0, 0, width, height);
            sprite.flip(false, true);
            _icons.put(path, sprite);
        }
        return _icons.get(path);
    }

    public Sprite getItem(ItemInfo info) { return getSprite(info, info.graphics != null ? info.graphics.get(0) : null, 0, 0, 255, false); }
    public Sprite getItem(StructureItem structure) { return structure.isComplete() ? getSprite(structure.getInfo(), structure.getGraphic(), structure.getParcel().getTile(), 0, 255, false) : getBluePrint(); }

    private Sprite getBluePrint() {
        long sum = getSum(-1, 0, 0, 0);
        Sprite sprite = _sprites.get(sum);
        if (sprite == null) {
            Texture texture = new Texture(new FileHandle(new File("data", "graphics/items/structures/blueprint.png")));
            sprite = new Sprite(texture, 0, 0, Constant.TILE_WIDTH, Constant.TILE_HEIGHT);
            sprite.setFlip(false, true);
            _sprites.put(sum, sprite);
        }
        return sprite;
    }

    public Sprite getItem(NetworkItem networkObject) { return getSprite(networkObject.getGraphic(), networkObject.isComplete() ? 1 : 0, 0, 255, false, 32, 32); }
//    public Sprite getItem(UsableItem item) { return getSprite(item.getInfo(), item.getGraphic(), item.isComplete() ? item.getInfo().height : 0, 0, 255, false); }
//    public Sprite getItem(UsableItem item, int currentFrame) { return getSprite(item.getInfo(), item.getGraphic(), item.isComplete() ? 1 : 0, 0, 255, false); }

    public Sprite getItem(GraphicInfo graphicInfo, int parcelTile, int itemTile) {
        if (graphicInfo.type == GraphicInfo.Type.TERRAIN) {
            return getRock(graphicInfo, parcelTile);
        }
        return getSprite(graphicInfo, parcelTile, itemTile, 255, false);
    }

    public Sprite getItem(ConsumableItem consumable) { return getSprite(consumable.getInfo(), consumable.getGraphic(), 0, 0, 255, false); }
    public Sprite getItem(ConsumableItem consumable, int currentFrame) { return getSprite(consumable.getInfo(), consumable.getGraphic(), 0, 0, 255, false); }

    public Sprite getIcon(ItemInfo info) {
        return info.graphics != null ? getSprite(info, info.graphics.get(0), 0, 0, 255, true) : null;
    }

    public Sprite getSprite(ItemInfo itemInfo, GraphicInfo graphicInfo, int tile, int state, int alpha, boolean isIcon) {
        return getSprite(graphicInfo, tile, state, alpha, isIcon, graphicInfo.width, graphicInfo.height);
    }

    private Sprite getSprite(GraphicInfo graphicInfo, int tile, int state, int alpha, boolean isIcon) {
        return getSprite(graphicInfo, tile, state, alpha, isIcon, graphicInfo.width, graphicInfo.height);
    }

    public Sprite getRock(GraphicInfo graphicInfo, int tile) {
        assert graphicInfo != null && graphicInfo.type == GraphicInfo.Type.TERRAIN;

        if (graphicInfo.spriteId == -1) {
            graphicInfo.spriteId = ++_spriteCount;
        }

        Sprite sprite = _sprites.get(getSum(graphicInfo.spriteId, tile, 0, 0));
        if (sprite == null) {
            Texture txTerrain = _manager.get(graphicInfo.path);
            if (txTerrain != null) {
                Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);

                txTerrain.getTextureData().prepare();
                Pixmap texturePixmap = txTerrain.getTextureData().consumePixmap();

                // Top left
                if ((tile & TOP_LEFT) > 0 && (tile & TOP) > 0 && (tile & LEFT) > 0) {
                    pixmap.drawPixmap(texturePixmap, 0, 0, 16, 16, 16, 16);
                } else if ((tile & TOP) > 0 && (tile & LEFT) > 0) {
                    pixmap.drawPixmap(texturePixmap, 0, 0, 64, 32, 16, 16);
                } else if ((tile & LEFT) > 0) {
                    pixmap.drawPixmap(texturePixmap, 0, 0, 32, 0, 16, 16);
                } else if ((tile & TOP) > 0) {
                    pixmap.drawPixmap(texturePixmap, 0, 0, 0, 32, 16, 16);
                } else {
                    pixmap.drawPixmap(texturePixmap, 0, 0, 0, 0, 16, 16);
                }

                // Top right
                if ((tile & TOP_RIGHT) > 0 && (tile & TOP) > 0 && (tile & RIGHT) > 0) {
                    pixmap.drawPixmap(texturePixmap, 16, 0, 16, 16, 16, 16);
                } else if ((tile & TOP) > 0 && (tile & RIGHT) > 0) {
                    pixmap.drawPixmap(texturePixmap, 16, 0, 80, 32, 16, 16);
                } else if ((tile & RIGHT) > 0) {
                    pixmap.drawPixmap(texturePixmap, 16, 0, 16, 0, 16, 16);
                } else if ((tile & TOP) > 0) {
                    pixmap.drawPixmap(texturePixmap, 16, 0, 48, 32, 16, 16);
                } else {
                    pixmap.drawPixmap(texturePixmap, 16, 0, 48, 0, 16, 16);
                }

                // Bottom left
                if ((tile & BOTTOM_LEFT) > 0 && (tile & BOTTOM) > 0 && (tile & LEFT) > 0) {
                    pixmap.drawPixmap(texturePixmap, 0, 16, 16, 16, 16, 16);
                } else if ((tile & BOTTOM) > 0 && (tile & LEFT) > 0) {
                    pixmap.drawPixmap(texturePixmap, 0, 16, 64, 48, 16, 16);
                } else if ((tile & LEFT) > 0) {
                    pixmap.drawPixmap(texturePixmap, 0, 16, 32, 48, 16, 16);
                } else if ((tile & BOTTOM) > 0) {
                    pixmap.drawPixmap(texturePixmap, 0, 16, 0, 16, 16, 16);
                } else {
                    pixmap.drawPixmap(texturePixmap, 0, 16, 0, 48, 16, 16);
                }

                // Bottom right
                if ((tile & BOTTOM_RIGHT) > 0 && (tile & BOTTOM) > 0 && (tile & RIGHT) > 0) {
                    pixmap.drawPixmap(texturePixmap, 16, 16, 16, 16, 16, 16);
                } else if ((tile & BOTTOM) > 0 && (tile & RIGHT) > 0) {
                    pixmap.drawPixmap(texturePixmap, 16, 16, 80, 48, 16, 16);
                } else if ((tile & RIGHT) > 0) {
                    pixmap.drawPixmap(texturePixmap, 16, 16, 16, 48, 16, 16);
                } else if ((tile & BOTTOM) > 0) {
                    pixmap.drawPixmap(texturePixmap, 16, 16, 48, 16, 16, 16);
                } else {
                    pixmap.drawPixmap(texturePixmap, 16, 16, 48, 48, 16, 16);
                }

                txTerrain.getTextureData().disposePixmap();

                sprite = new Sprite(new Texture(pixmap));
                sprite.setFlip(false, true);
                _sprites.put(getSum(graphicInfo.spriteId, tile, 0, 0), sprite);
            }
        }

        return sprite;
    }

    private Sprite getSprite(GraphicInfo graphicInfo, int parcelTile, int itemTile, int alpha, boolean isIcon, int width, int height) {
        assert graphicInfo != null;

        if (graphicInfo.spriteId == -1) {
            graphicInfo.spriteId = ++_spriteCount;
        }

        long sum = graphicInfo.type == GraphicInfo.Type.WALL || graphicInfo.type == GraphicInfo.Type.DOOR ?
                getSum(graphicInfo.spriteId, parcelTile, 0, isIcon ? 1 : 0) :
                getSum(graphicInfo.spriteId, 0, 0, isIcon ? 1 : 0);

        Sprite sprite = _sprites.get(sum);
        if (sprite == null) {
            Texture texture = _textures.get(graphicInfo.packageName + graphicInfo.path);
            if (texture != null) {
                if (graphicInfo.type == GraphicInfo.Type.DOOR) {

                    if ((parcelTile & RIGHT) > 0 && (parcelTile & LEFT) > 0) {
                        sprite = new Sprite(texture, 0, 32, width, height);
                        sprite.setFlip(false, true);
                        _sprites.put(sum, sprite);
                    }

                    else if ((parcelTile & TOP) > 0 && (parcelTile & BOTTOM) > 0) {
                        sprite = new Sprite(texture, 32, 32, width, height);
                        sprite.setFlip(false, true);
                        _sprites.put(sum, sprite);
                    }

                    else {
                        sprite = new Sprite(texture, 0, 32, width, height);
                        sprite.setFlip(false, true);
                        _sprites.put(sum, sprite);
                    }
                }

                else if (graphicInfo.type == GraphicInfo.Type.WALL) {
                    Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);

                    texture.getTextureData().prepare();
                    Pixmap texturePixmap = texture.getTextureData().consumePixmap();

                    // Top left
                    if ((parcelTile & TOP_LEFT) > 0 && (parcelTile & TOP) > 0 && (parcelTile & LEFT) > 0) {
                        pixmap.drawPixmap(texturePixmap, 0, 0, 32, 32, 16, 16);
                    } else if ((parcelTile & TOP) > 0 && (parcelTile & LEFT) > 0) {
                        pixmap.drawPixmap(texturePixmap, 0, 0, 64, 64, 16, 16);
                    } else if ((parcelTile & LEFT) > 0) {
                        pixmap.drawPixmap(texturePixmap, 0, 0, 64, 0, 16, 16);
                    } else if ((parcelTile & TOP) > 0) {
                        pixmap.drawPixmap(texturePixmap, 0, 0, 64, 48, 16, 16);
                    } else {
                        pixmap.drawPixmap(texturePixmap, 0, 0, 0, 0, 16, 16);
                    }

                    // Top right
                    if ((parcelTile & TOP_RIGHT) > 0 && (parcelTile & TOP) > 0 && (parcelTile & RIGHT) > 0) {
                        pixmap.drawPixmap(texturePixmap, 16, 0, 32, 32, 16, 16);
                    } else if ((parcelTile & TOP) > 0 && (parcelTile & RIGHT) > 0) {
                        pixmap.drawPixmap(texturePixmap, 16, 0, 16, 64, 16, 16);
                    } else if ((parcelTile & RIGHT) > 0) {
                        pixmap.drawPixmap(texturePixmap, 16, 0, 16, 0, 16, 16);
                    } else if ((parcelTile & TOP) > 0) {
                        pixmap.drawPixmap(texturePixmap, 16, 0, 80, 64, 16, 16);
                    } else {
                        pixmap.drawPixmap(texturePixmap, 16, 0, 80, 0, 16, 16);
                    }

                    // Bottom left
                    if ((parcelTile & BOTTOM_LEFT) > 0 && (parcelTile & BOTTOM) > 0 && (parcelTile & LEFT) > 0) {
                        pixmap.drawPixmap(texturePixmap, 0, 16, 32, 32, 16, 16);
                    } else if ((parcelTile & BOTTOM) > 0 && (parcelTile & LEFT) > 0) {
                        pixmap.drawPixmap(texturePixmap, 0, 16, 64, 16, 16, 16);
                    } else if ((parcelTile & LEFT) > 0) {
                        pixmap.drawPixmap(texturePixmap, 0, 16, 64, 80, 16, 16);
                    } else if ((parcelTile & BOTTOM) > 0) {
                        pixmap.drawPixmap(texturePixmap, 0, 16, 64, 32, 16, 16);
                    } else {
                        pixmap.drawPixmap(texturePixmap, 0, 16, 0, 80, 16, 16);
                    }

                    // Bottom right
                    if ((parcelTile & BOTTOM_RIGHT) > 0 && (parcelTile & BOTTOM) > 0 && (parcelTile & RIGHT) > 0) {
                        pixmap.drawPixmap(texturePixmap, 16, 16, 32, 32, 16, 16);
                    } else if ((parcelTile & BOTTOM) > 0 && (parcelTile & RIGHT) > 0) {
                        pixmap.drawPixmap(texturePixmap, 16, 16, 16, 16, 16, 16);
                    } else if ((parcelTile & RIGHT) > 0) {
                        pixmap.drawPixmap(texturePixmap, 16, 16, 16, 80, 16, 16);
                    } else if ((parcelTile & BOTTOM) > 0) {
                        pixmap.drawPixmap(texturePixmap, 16, 16, 80, 16, 16, 16);
                    } else {
                        pixmap.drawPixmap(texturePixmap, 16, 16, 80, 80, 16, 16);
                    }

                    texture.getTextureData().disposePixmap();

                    sprite = new Sprite(new Texture(pixmap), 0, 0, 32, 32);
                    sprite.setColor(new Color(255, 255, 255, alpha));
                    sprite.setFlip(false, true);
                    _sprites.put(sum, sprite);

//                    sprite = new Sprite(texture, (tile % 3) * 32, (tile / 3) * 32, 32, 32);
//                    sprite.setColor(new Color(255, 255, 255, alpha));
//                    _sprites.put(sum, sprite);
                }

                else {
                    sprite = new Sprite(texture, 0, 0, width, height);
                    sprite.setFlip(false, true);
                    sprite.setColor(new Color(255, 255, 255, alpha));
                    if (isIcon) {
                        switch (Math.max(width/32, height/32)) {
                            case 2: sprite.setScale(0.85f, 0.85f); break;
                            case 3: sprite.setScale(0.55f, 0.55f); break;
                            case 4: sprite.setScale(0.35f, 0.35f); break;
                            case 5: sprite.setScale(0.32f, 0.32f); break;
                            case 6: sprite.setScale(0.3f, 0.3f); break;
                            case 7: sprite.setScale(0.25f, 0.25f); break;
                            case 8: sprite.setScale(0.2f, 0.2f); break;
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

    public static File getFile(ItemInfo itemInfo, GraphicInfo graphicInfo) {
        if ("base".equals(graphicInfo.packageName)) {
            return FileUtils.getFile("data", graphicInfo.path);
        } else {
            return Paths.get(itemInfo.dataDirectory.getAbsolutePath(), "data", graphicInfo.path).toFile();
        }
    }

    private File getFile(String path) {
        String packageName = "base";
        if (path.startsWith("[")) {
            packageName = path.substring(1, path.indexOf(']'));
            path = path.substring(path.indexOf(']') + 1, path.length());
        }

        if ("base".equals(packageName)) {
            return FileUtils.getFile("data", path);
        } else {
            throw new NotImplementedException("");
//            return new File("mods/" + packageName + "/items/" + path);
        }
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
        for (int x = (int)(sprite.getWidth() - 1); x >= 0; x--) {
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
        for (int y = (int)(sprite.getHeight() - 1); y >= 0; y--) {
            for (int x = 0; x < sprite.getWidth(); x++) {
                if (endY == -1 && (pixmap.getPixel(x, y) & 0x000000ff) != 0) endY = y + 1;
            }
        }

//        Log.info("Item: " + item.name + ", " + startX + ", " + startY + ", " + endX + ", " + endY);

        item.textureRect = new Rectangle(startX, startY, endX, endY);

        textureData.disposePixmap();
    }

//    private File foundImageFile(String fileName) {
//        // TODO: clean
//        for (File file: new File("data/items/").listFiles()) {
//            if (file.isDirectory() && !file.getName().equals("24")) {
//                for (File subFile: file.listFiles()) {
//                    if (subFile.getName().equals(fileName)) {
//                        return subFile;
//                    }
//                }
//            }
//            if (file.getName().equals(fileName)) {
//                return file;
//            }
//        }
//        return null;
//    }

    private long getSum(int spriteId, int tile, int state, int extra) {
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

    public Sprite getAnimal(String path) {
        if (!_icons.containsKey(path)) {
            Texture texture = new Texture(path);
            Sprite sprite = new Sprite(texture, 0, 0, texture.getWidth(), texture.getHeight());
            _icons.put(path, sprite);
        }
        return _icons.get(path);
    }

    public Sprite getCharacter(CharacterModel c, int direction, int frame) {
        int extra = c.getType().index;
        int sum = 0;
        sum = (sum << 8) + direction;
        sum = (sum << 8) + frame;
        sum = (sum << 8) + extra;

        Sprite sprite = _spritesCharacters.get(sum);
        if (sprite == null) {
            Texture texture = new Texture(FileUtils.getFileHandle(c.getType().path));

            sprite = new Sprite(texture, 0, 0, Constant.CHAR_WIDTH, Constant.CHAR_HEIGHT);
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
        assert _textures.containsKey(path);
        return _textures.get(path);
    }

    public Sprite getNewSprite(ItemInfo itemInfo) {
        return itemInfo != null && CollectionUtils.isNotEmpty(itemInfo.graphics) ? getNewSprite(itemInfo.graphics.get(0), 0) : null;
    }

    public Sprite getNewSprite(GraphicInfo graphicInfo) {
        return getNewSprite(graphicInfo, 0);
    }

    public Sprite getNewSprite(GraphicInfo graphicInfo, int tile) {
        _manager.finishLoading();

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
            Texture texture = _manager.get("data" + graphicInfo.path, Texture.class);
            if (texture != null) {

                sprite = new Sprite(texture,
                        (graphicInfo.x + tile) * graphicInfo.tileWidth,
                        graphicInfo.y * graphicInfo.tileHeight,
                        graphicInfo.tileWidth,
                        graphicInfo.tileHeight);
                sprite.setFlip(false, true);
                sprite.setColor(new Color(255, 255, 255, 1));
//                if (isIcon) {
//                    switch (Math.max(width/32, height/32)) {
//                        case 2: sprite.setScale(0.85f, 0.85f); break;
//                        case 3: sprite.setScale(0.55f, 0.55f); break;
//                        case 4: sprite.setScale(0.35f, 0.35f); break;
//                        case 5: sprite.setScale(0.32f, 0.32f); break;
//                        case 6: sprite.setScale(0.3f, 0.3f); break;
//                        case 7: sprite.setScale(0.25f, 0.25f); break;
//                        case 8: sprite.setScale(0.2f, 0.2f); break;
//                    }
//                }

                _sprites.put(sum, sprite);
            }
        }

        return _sprites.get(sum);
    }
}