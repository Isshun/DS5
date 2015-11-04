package org.smallbox.faraway.core;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.NetworkObjectModel;
import org.smallbox.faraway.core.game.module.world.model.StructureModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.game.module.world.model.resource.ResourceModel;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.core.util.FileUtils;
import org.smallbox.faraway.core.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 04/06/2015.
 */
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

    private static int                      _count;
    private static SpriteManager            _self;
    private Map<Integer, SpriteModel>       _spritesCharacters;
    private Map<Long, SpriteModel>          _sprites;
    private Texture[]                       _textureCharacters;
    private SpriteModel[]                   _selectors;
    private Map<String, SpriteModel>        _icons;
    private ItemInfo                        _groundItemInfo;
    private Map<String, Texture>            _textureCache = new HashMap<>();

    private int[] _random = {
            0, 0, 1, 3, 2, 3, 1, 3, 0, 1,
            0, 2, 1, 3, 1, 0, 1, 3, 0, 1,
            0, 1, 2, 1, 3, 0, 1, 3, 0, 1,
            0, 0, 1, 3, 2, 2, 1, 3, 2, 1,
            3, 1, 1, 0, 3, 2, 0, 1, 0, 1};

    public static SpriteManager getInstance() {
        return _self;
    }

    public SpriteManager() throws IOException {
        _self = this;
        _icons = new HashMap<>();

        _sprites = new HashMap<>();
        _spritesCharacters = new HashMap<>();

        _textureCharacters = new Texture[4];

//        //you have an instance of Image
//        Image image = createInstanceOfImage();
//
////These lines will resize your sprite to half of screen's width while keeping the ratio
//        image.width = .5f * Gdx.graphics.getWidth();
//        image.setScaling(Scaling.fillX);
//
////To resize in height's scale
//        image.width = .5f * Gdx.graphics.getHeight();
//        image.setScaling(Scaling.fillY);

        FileUtils.listRecursively("data/res/").stream().filter(file -> file.getName().endsWith(".png")).forEach(file ->
                _textureCache.put(file.getPath().replace("\\", "/"), new Texture(new FileHandle(file))));

        _textureCharacters[0] = new Texture("data/res/Characters/scientifique.png");
        _textureCharacters[1] = new Texture("data/res/Characters/soldat3.png");
        _textureCharacters[2] = new Texture("data/res/Characters/gallery_84826_3_2787.png");
        _textureCharacters[3] = new Texture("data/res/Characters/NuChara01.png");

        Texture itemSelector = new Texture("data/res/item_selector.png");
        _selectors = new SpriteModel[NB_SELECTOR_TILE];
        _selectors[0] = new SpriteModel(itemSelector, 0, 0, 8, 8);
        _selectors[1] = new SpriteModel(itemSelector, 8, 0, 8, 8);
        _selectors[2] = new SpriteModel(itemSelector, 0, 8, 8, 8);
        _selectors[3] = new SpriteModel(itemSelector, 8, 8, 8, 8);

//        _textureItemSelector = new Texture("data/res/Tilesets/item_selector.png");
//        _itemSelectors = new SpriteModel[NB_ITEM_SELECTOR_TILE];
//        for (int i = 0; i < NB_ITEM_SELECTOR_TILE; i++) {
//            _itemSelectors[i] = new SpriteModel(_textureItemSelector, i * 32, 0, 32, 32);
//        }
    }

    public SpriteModel getIcon(String path) {
        if (!_icons.containsKey(path)) {
            File file = getFile(path);
            Texture texture = new Texture(new FileHandle(file));
            SpriteModel sprite = new SpriteModel(texture, 0, 0, texture.getWidth(), texture.getHeight());
            _icons.put(path, sprite);
        }
        return _icons.get(path);
    }

//    // TODO
//    public SpriteModel getItem(MapObjectModel item, int tile) {
//        if (item == null) {
//            return null;
//        }
//
//        if (!item.isStructure()) {
//            if (item.isResource()) {
//                return getResource((ResourceModel)item);
//            }
//
////            int alpha = Math.min(item.getScience() == 0 ? 255 : 75 + 180 / item.getScience() * (int)item.getProgress(), 255);
//            int alpha = 255;
//
//            return getSprite(item.getInfo(), item.getGraphic(), tile, 0, alpha, false);
//        }
//
//        return getSprite(item.getInfo(), item.getGraphic(), tile, 0, 255, false);
//    }

//    public SpriteModel getItem(ItemInfo info, int tile) {
//        if (info == null) {
//            return null;
//        }
//
//        if (!info.isStructure) {
//            if (info.isResource) {
//                return null;
////                return getResource(info);
//            }
//
////            int alpha = Math.min(item.getScience() == 0 ? 255 : 75 + 180 / item.getScience() * (int)item.getProgress(), 255);
//            int alpha = 255;
//
//            return getSprite(info, info.graphics != null ? info.graphics.get(0) : null, 0, 0, alpha, false);
//        }
//
//        return getSprite(info, info.graphics != null ? info.graphics.get(0) : null, 0, 0, 255, false);
//    }

    public SpriteModel getItem(ItemInfo info) { return getSprite(info, info.graphics != null ? info.graphics.get(0) : null, 0, 0, 255, false); }
    public SpriteModel getItem(StructureModel structure) { return getSprite(structure.getInfo(), structure.getGraphic(), structure.isComplete() ? 1 : 0, 0, 255, false); }
    public SpriteModel getItem(NetworkObjectModel networkObject) { return getSprite(networkObject.getGraphic(), networkObject.isComplete() ? 1 : 0, 0, 255, false, 1, 1); }
    public SpriteModel getItem(ItemModel item) { return getSprite(item.getInfo(), item.getGraphic(), item.isComplete() ? item.getInfo().height : 0, 0, 255, false); }
    public SpriteModel getItem(ItemModel item, int currentFrame) { return getSprite(item.getInfo(), item.getGraphic(), item.isComplete() ? 1 : 0, 0, 255, false); }

    public SpriteModel getItem(ResourceModel resource, int offsetX, int offsetY) {
        return getSprite(resource.getInfo(), resource.getGraphic(), offsetX, offsetY, 255, false);
    }

    public SpriteModel getItem(ConsumableModel consumable) { return getSprite(consumable.getInfo(), consumable.getGraphic(), 0, 0, 255, false); }
    public SpriteModel getItem(ConsumableModel consumable, int currentFrame) { return getSprite(consumable.getInfo(), consumable.getGraphic(), 0, 0, 255, false); }

    public SpriteModel getIcon(ItemInfo info) {
        return info.graphics != null ? getSprite(info, info.graphics.get(0), 0, 0, 255, true) : null;
    }

    private SpriteModel getSprite(ItemInfo itemInfo, GraphicInfo graphicInfo, int tile, int state, int alpha, boolean isIcon) {
        return getSprite(graphicInfo, tile, state, alpha, isIcon, itemInfo.width, itemInfo.height);
    }

    private SpriteModel getSprite(GraphicInfo graphicInfo, int tile, int state, int alpha, boolean isIcon, int width, int height) {
        if (graphicInfo == null) {
            return null;
        }

        if (graphicInfo.spriteId == 0) {
            graphicInfo.spriteId = ++_count;
        }

        int offsetX = (state * Constant.TILE_WIDTH) + (graphicInfo.x);
        int offsetY = (tile * Constant.TILE_HEIGHT) + (graphicInfo.y);

        long sum = graphicInfo.type == GraphicInfo.Type.TERRAIN ?
                getSum(graphicInfo.spriteId, tile, 0, isIcon ? 1 : 0) :
                getSum(graphicInfo.spriteId, offsetX, offsetY, isIcon ? 1 : 0);

        SpriteModel sprite = _sprites.get(sum);
        if (sprite == null) {
            File imgFile = getFile(graphicInfo);
            if (imgFile.exists()) {
                Texture texture = new Texture(new FileHandle(imgFile));

                if (graphicInfo.type == GraphicInfo.Type.TERRAIN) {
                    Pixmap pixmap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
                    Pixmap.setBlending(Pixmap.Blending.None);

                    texture.getTextureData().prepare();
                    Pixmap texturePixmap = texture.getTextureData().consumePixmap();

                    // Top left
                    if ((tile & TOP_LEFT) > 0 && (tile & TOP) > 0 && (tile & LEFT) > 0) {
                        pixmap.drawPixmap(texturePixmap, 0, 0, 64, 0, 16, 16);
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
                        pixmap.drawPixmap(texturePixmap, 16, 0, 80, 0, 16, 16);
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
                        pixmap.drawPixmap(texturePixmap, 0, 16, 64, 16, 16, 16);
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
                        pixmap.drawPixmap(texturePixmap, 16, 16, 80, 16, 16, 16);
                    } else if ((tile & BOTTOM) > 0 && (tile & RIGHT) > 0) {
                        pixmap.drawPixmap(texturePixmap, 16, 16, 80, 48, 16, 16);
                    } else if ((tile & RIGHT) > 0) {
                        pixmap.drawPixmap(texturePixmap, 16, 16, 16, 48, 16, 16);
                    } else if ((tile & BOTTOM) > 0) {
                        pixmap.drawPixmap(texturePixmap, 16, 16, 48, 16, 16, 16);
                    } else {
                        pixmap.drawPixmap(texturePixmap, 16, 16, 48, 48, 16, 16);
                    }

                    texture.getTextureData().disposePixmap();

                    sprite = new SpriteModel(new Texture(pixmap), 0, 0, 64, 64);
                    sprite.getData().setColor(new Color(255, 255, 255, alpha));
                    _sprites.put(sum, sprite);

//                    sprite = new SpriteModel(texture, (tile % 3) * 32, (tile / 3) * 32, 32, 32);
//                    sprite.getData().setColor(new Color(255, 255, 255, alpha));
//                    _sprites.put(sum, sprite);
                }

                else {
                    sprite = new SpriteModel(texture,
                            offsetX,
                            offsetY,
                            width * Constant.TILE_WIDTH,
                            height * Constant.TILE_HEIGHT);
                    sprite.getData().setColor(new Color(255, 255, 255, alpha));
                    if (isIcon) {
                        switch (Math.max(width, height)) {
                            case 2: sprite.getData().setScale(0.85f, 0.85f); break;
                            case 3: sprite.getData().setScale(0.55f, 0.55f); break;
                            case 4: sprite.getData().setScale(0.35f, 0.35f); break;
                            case 5: sprite.getData().setScale(0.32f, 0.32f); break;
                            case 6: sprite.getData().setScale(0.3f, 0.3f); break;
                            case 7: sprite.getData().setScale(0.25f, 0.25f); break;
                            case 8: sprite.getData().setScale(0.2f, 0.2f); break;
                        }

                    }
                    _sprites.put(sum, sprite);
                }
            }
        }

        if (sprite != null && graphicInfo.textureRect == null) {
            getTextureRect(graphicInfo, sprite.getData());
        }

        return sprite;
    }

    private File getFile(GraphicInfo graphicInfo) {
        if ("base".equals(graphicInfo.packageName)) {
            return new File("data", graphicInfo.path);
        } else {
            return new File("mods/" + graphicInfo.packageName + "/items/" + graphicInfo.path + ".png");
        }
    }

    private File getFile(String path) {
        String packageName = "base";
        if (path.startsWith("[")) {
            packageName = path.substring(1, path.indexOf(']'));
            path = path.substring(path.indexOf(']') + 1, path.length());
        }

        if ("base".equals(packageName)) {
            return new File("data", path);
        } else {
            return new File("mods/" + packageName + "/items/" + path + ".png");
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

//        System.out.println("Item: " + item.name + ", " + startX + ", " + startY + ", " + endX + ", " + endY);

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
            Log.error("SpriteManager.getSum -> out of bounds values");
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

    private long getSum(int texture, int x, int y, int extra1, int extra2, int alpha) {
        if (texture > 64 || x > 4096 || y > 4096 || extra1 > 1024 || extra2 > 1024 || alpha > 255) {
            //throw new Exception("SpriteManager.getSum -> out of bounds values");
            Log.error("SpriteManager.getSum -> out of bounds values");
        }

        long sum = texture;
        sum = sum << 6;        // 6
        sum += x;
        sum = sum << 12;    // 6 + 12 = 18
        sum += y;
        sum = sum << 12;    // 18 + 12 = 30
        sum += extra1;
        sum = sum << 10;    // 30 + 10 = 40
        sum += extra2;
        sum = sum << 10;    // 40 + 10 = 50
        sum += alpha;
        sum = sum << 8;        // 50 + 8 = 58
        return sum;
    }

//    public SpriteModel getResource(ResourceModel resource) {
//        ItemInfo info = resource.getInfo();
//
//        if ("base.rock".equals(info.name)) {
//            return getSprite(info, resource.getGraphic(), resource.getTile(), 0, 255, false);
//        }
//
//        else if ("base.grass".equals(info.name)) {
//            return getSprite(info, resource.getGraphic(), resource.getTile(), 0, 255, false);
//        }
//
//        else if (info.actions != null && !info.actions.isEmpty() && "gather".equals(info.actions.get(0).type)) {
//            int state = (int)(Math.min(resource.getQuantity(), info.plant.mature) + 1);
//            return getSprite(info, resource.getGraphic(), state, 0, 255, false);
//        }
//
//        return getSprite(info, resource.getGraphic(), 0, 1, 255, false);
//    }

    public SpriteModel getGround(int type) {
        if (_groundItemInfo == null) {
            _groundItemInfo = Data.getData().getItemInfo("base.ground");
        }
        int offsetX = (int) (Math.random() * 2);
        int offsetY = 1;

        GraphicInfo graphicInfo = _groundItemInfo.graphics != null ? _groundItemInfo.graphics.get(0) : null;
        long sum = getSum(graphicInfo.spriteId, offsetX, offsetY, 0);

        SpriteModel sprite = _sprites.get(sum);
        if (sprite == null) {
            File imgFile = getFile(graphicInfo);
            if (imgFile.exists()) {
                Texture texture = new Texture(new FileHandle(imgFile));

                sprite = new SpriteModel(texture,
                        0,
                        32,
                        32,
                        32);
                sprite.getData().setColor(new Color(255, 255, 255, 255));
                _sprites.put(sum, sprite);
            }
        }

        return sprite;
//        return getSprite(_groundItemInfo, _groundItemInfo.graphics != null ? _groundItemInfo.graphics.get(0) : null, type, (int) (Math.random() * 2), 255, false, 32, 32);
    }

    public SpriteModel getAnimal(String path) {
        if (!_icons.containsKey(path)) {
            Texture texture = new Texture(path);
            SpriteModel sprite = new SpriteModel(texture, 0, 0, texture.getWidth(), texture.getHeight());
            _icons.put(path, sprite);
        }
        return _icons.get(path);
    }

    public SpriteModel getCharacter(CharacterModel c, int direction, int frame) {
        int extra = c.getType().index;
        int sum = 0;
        sum = sum << 8;
        sum += direction;
        sum = sum << 8;
        sum += frame;
        sum = sum << 8;
        sum += extra;

        SpriteModel sprite = _spritesCharacters.get(sum);
        if (sprite == null) {
            Texture texture = new Texture("data/characters/" + c.getType().name + ".png");

            sprite = new SpriteModel(texture,
                    0,
                    0,
                    Constant.CHAR_WIDTH,
                    Constant.CHAR_HEIGHT);

//            sprite = new SpriteModel(texture,
//                    Constant.CHAR_WIDTH * frame + (extra * 128),
//                    Constant.CHAR_HEIGHT * direction,
//                    Constant.CHAR_WIDTH,
//                    Constant.CHAR_HEIGHT);
//
            _spritesCharacters.put(sum, sprite);
        }

        return sprite;
    }

    public SpriteModel getSelector(int tile) {
        return _selectors[tile % NB_SELECTOR_TILE];
    }

    public SpriteModel getSelectorCorner(int corner) {
        return _selectors[corner];
    }

    public Texture getTexture(String path) {
        return _textureCache.get(path);
    }
}