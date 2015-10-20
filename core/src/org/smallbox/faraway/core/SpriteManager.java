package org.smallbox.faraway.core;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import org.smallbox.faraway.core.engine.SpriteModel;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.model.character.base.CharacterModel;
import org.smallbox.faraway.core.game.model.item.*;
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

    private static int                         _count;
    private static SpriteManager            _self;
    private Map<Integer, GDXSpriteModel>    _spritesCharacters;
    private Map<Long, GDXSpriteModel>         _sprites;
    private Texture[]                         _textureCharacters;
    private GDXSpriteModel[]                _selectors;
    private Map<String, SpriteModel>        _icons;
    private ItemInfo _groundItemInfo;

    private int[] _random = {
            0, 0, 1, 3, 2, 3, 1, 3, 0, 1,
            0, 2, 1, 3, 1, 0, 1, 3, 0, 1,
            0, 1, 2, 1, 3, 0, 1, 3, 0, 1,
            0, 0, 1, 3, 2, 2, 1, 3, 2, 1,
            3, 1, 1, 0, 3, 2, 0, 1, 0, 1};

    private Map<String, Texture>            _textureCache = new HashMap<>();

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
        _selectors = new GDXSpriteModel[NB_SELECTOR_TILE];
        _selectors[0] = new GDXSpriteModel(itemSelector, 0, 0, 8, 8);
        _selectors[1] = new GDXSpriteModel(itemSelector, 8, 0, 8, 8);
        _selectors[2] = new GDXSpriteModel(itemSelector, 0, 8, 8, 8);
        _selectors[3] = new GDXSpriteModel(itemSelector, 8, 8, 8, 8);

//        _textureItemSelector = new Texture("data/res/Tilesets/item_selector.png");
//        _itemSelectors = new GDXSpriteModel[NB_ITEM_SELECTOR_TILE];
//        for (int i = 0; i < NB_ITEM_SELECTOR_TILE; i++) {
//            _itemSelectors[i] = new GDXSpriteModel(_textureItemSelector, i * 32, 0, 32, 32);
//        }
    }

    public SpriteModel getIcon(String path) {
        if (!_icons.containsKey(path)) {
            Texture texture = new Texture(path);
            GDXSpriteModel sprite = new GDXSpriteModel(texture, 0, 0, texture.getWidth(), texture.getHeight());
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
    public SpriteModel getItem(ItemModel item) { return getSprite(item.getInfo(), item.getGraphic(), item.isComplete() ? 1 : 0, 0, 255, false); }
    public SpriteModel getItem(ItemModel item, int currentFrame) { return getSprite(item.getInfo(), item.getGraphic(), item.isComplete() ? 1 : 0, 0, 255, false); }
    public SpriteModel getItem(ResourceModel resource) { return getSprite(resource.getInfo(), resource.getGraphic(), 0, 0, 255, false); }
    public SpriteModel getItem(ConsumableModel consumable) { return getSprite(consumable.getInfo(), consumable.getGraphic(), 0, 0, 255, false); }
    public SpriteModel getItem(ConsumableModel consumable, int currentFrame) { return getSprite(consumable.getInfo(), consumable.getGraphic(), 0, 0, 255, false); }

    public SpriteModel getIcon(ItemInfo info) {
        return info.graphics != null ? getSprite(info, info.graphics.get(0), 0, 0, 255, true) : null;
    }

    private SpriteModel getSprite(ItemInfo itemInfo, GraphicInfo graphicInfo, int tile, int state, int alpha, boolean isIcon) {
        return getSprite(itemInfo, graphicInfo, tile, state, alpha, isIcon, Constant.TILE_WIDTH, Constant.TILE_HEIGHT);
    }

    private SpriteModel getSprite(ItemInfo itemInfo, GraphicInfo graphicInfo, int tile, int state, int alpha, boolean isIcon, int width, int height) {
        if (graphicInfo == null) {
            return null;
        }

        if (graphicInfo.spriteId == 0) {
            graphicInfo.spriteId = ++_count;
        }

        long sum = getSum(graphicInfo.spriteId, (state * width) + graphicInfo.x, (tile * height) + graphicInfo.y, isIcon ? 1 : 0);

        GDXSpriteModel sprite = _sprites.get(sum);
        if (sprite == null) {
            int tileX = (state * width);
            int tileY = (tile * height);
            int offsetX = (state * width) + (graphicInfo.x);
            int offsetY = (tile * height) + (graphicInfo.y);

            File imgFile;
            if ("base".equals(graphicInfo.packageName)) {
                imgFile = new File("data", graphicInfo.path);
//                imgFile = foundImageFile(item.fileName + ".png");
            } else {
                imgFile = new File("mods/" + graphicInfo.packageName + "/items/" + graphicInfo.path + ".png");
            }
            if (imgFile != null && imgFile.exists()) {
                sprite = new GDXSpriteModel(new Texture(new FileHandle(imgFile)),
                        offsetX,
                        offsetY,
                        itemInfo.width * width,
                        itemInfo.height * height);
                sprite.getData().setColor(new Color(255, 255, 255, alpha));
                if (isIcon) {
                    switch (Math.max(itemInfo.width, itemInfo.height)) {
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

        if (sprite != null && graphicInfo.textureRect == null) {
            getTextureRect(graphicInfo, sprite.getData());
        }

        return sprite;
    }

    private void getTextureRect(GraphicInfo item, Sprite sprite) {
        TextureData textureData = sprite.getTexture().getTextureData();
        textureData.prepare();
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
            _groundItemInfo = GameData.getData().getItemInfo("base.ground");
        }
        return getSprite(_groundItemInfo, _groundItemInfo.graphics != null ? _groundItemInfo.graphics.get(0) : null, type, (int) (Math.random() * 2), 255, false, 32, 32);
    }

    public SpriteModel getAnimal(String path) {
        if (!_icons.containsKey(path)) {
            Texture texture = new Texture(path);
            GDXSpriteModel sprite = new GDXSpriteModel(texture, 0, 0, texture.getWidth(), texture.getHeight());
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

        GDXSpriteModel sprite = _spritesCharacters.get(sum);
        if (sprite == null) {
            Texture texture = new Texture("data/characters/" + c.getType().name + ".png");

            sprite = new GDXSpriteModel(texture,
                    0,
                    0,
                    Constant.CHAR_WIDTH,
                    Constant.CHAR_HEIGHT);

//            sprite = new GDXSpriteModel(texture,
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
