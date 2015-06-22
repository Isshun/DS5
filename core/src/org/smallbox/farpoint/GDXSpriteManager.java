package org.smallbox.farpoint;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import org.smallbox.faraway.engine.RenderEffect;
import org.smallbox.faraway.engine.SpriteModel;
import org.smallbox.faraway.engine.Viewport;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.engine.SpriteManager;
import org.smallbox.faraway.game.model.ProfessionModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.MapObjectModel;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.StructureModel;
import org.smallbox.faraway.game.model.item.ResourceModel;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 04/06/2015.
 */
public class GDXSpriteManager extends SpriteManager {
    private static final int                WALL_HEIGHT = 48;
    private static final int                WALL_WIDTH = 32;
    private static final int                NB_SELECTOR_TILE = 4;
    private static final int                NB_ITEM_SELECTOR_TILE = 4;

    private static int 				        _count;
    private Map<Integer, GDXSpriteModel>    _spritesCharacters;
    private Map<Long, GDXSpriteModel> 		_sprites;
    private Texture[] 				        _textureCharacters;
    private Texture[]				        _texture;
    private Texture                         _textureSelector;
    private GDXSpriteModel[]                _selectors;
    private Texture                         _textureItemSelector;
    private GDXSpriteModel[]                _itemSelectors;
    private Map<String, SpriteModel>        _icons;
    private ItemInfo                        _groundItemInfo;

    private int[] _random = {
            0, 0, 1, 3, 2, 3, 1, 3, 0, 1,
            0, 2, 1, 3, 1, 0, 1, 3, 0, 1,
            0, 1, 2, 1, 3, 0, 1, 3, 0, 1,
            0, 0, 1, 3, 2, 2, 1, 3, 2, 1,
            3, 1, 1, 0, 3, 2, 0, 1, 0, 1};

    public GDXSpriteManager() throws IOException {
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

        _textureCharacters[0] = new Texture("data/res/Characters/scientifique.png");
        _textureCharacters[1] = new Texture("data/res/Characters/soldat3.png");
        _textureCharacters[2] = new Texture("data/res/Characters/gallery_84826_3_2787.png");
        _textureCharacters[3] = new Texture("data/res/Characters/NuChara01.png");
        _textureSelector = new Texture("data/res/selector.png");
        _selectors = new GDXSpriteModel[NB_SELECTOR_TILE];
        for (int i = 0; i < NB_SELECTOR_TILE; i++) {
            _selectors[i] = new GDXSpriteModel(_textureSelector, i * 34, 0, 34, 48);
        }

        _textureItemSelector = new Texture("data/res/Tilesets/item_selector.png");
        _itemSelectors = new GDXSpriteModel[NB_ITEM_SELECTOR_TILE];
        for (int i = 0; i < NB_ITEM_SELECTOR_TILE; i++) {
            _itemSelectors[i] = new GDXSpriteModel(_textureItemSelector, i * 32, 0, 32, 32);
        }

        _texture = new Texture[8];

        _texture[0] = new Texture("data/res/Tilesets/Futuristic_A5.png");
        _texture[1] = new Texture("data/res/Tilesets/Futuristic_TileC.png");
        _texture[2] = new Texture("data/res/Tilesets/Futuristic_TileB.png");
        _texture[3] = new Texture("data/res/Tilesets/Futuristic_TileE.png");
        _texture[4] = new Texture("data/res/Tilesets/zones.png");
        _texture[5] = new Texture("data/res/Tilesets/Futuristic_A3.png");
        _texture[6] = new Texture("data/res/Tilesets/walls.png");
        _texture[7] = new Texture("data/res/Tilesets/icons.png");
    }

    @Override
    public SpriteModel getIcon(String path) {
        if (!_icons.containsKey(path)) {
            Texture texture = new Texture(path);
            GDXSpriteModel sprite = new GDXSpriteModel(texture, 0, 0, texture.getWidth(), texture.getHeight());
            _icons.put(path, sprite);
        }
        return _icons.get(path);
    }

    // TODO
    @Override
    public SpriteModel getItem(MapObjectModel item, int tile) {
        if (item == null) {
            return null;
        }

        if (item.isStructure() == false) {
            if (item.isResource()) {
                return getResource((ResourceModel) item);
            }

//            int alpha = Math.min(item.getScience() == 0 ? 255 : 75 + 180 / item.getScience() * (int)item.getProgress(), 255);
            int alpha = 255;

            return getSprite(item.getInfo(), tile, 0, alpha, false);
        }

        return getSprite(item.getInfo(), tile, 0, 255, false);
    }

    @Override
    public SpriteModel getItem(MapObjectModel item) {
        return getItem(item, 0);
    }

    @Override
    public SpriteModel getIcon(ItemInfo info) {
        return getSprite(info, 0, 0, 255, true);
    }

    @Override
    public SpriteModel getGreenHouse(int index) {
        int texture = 4;
//		int offset = _random[index % 50];
        int x = (int) (index * (Constant.TILE_WIDTH + 2) + 1);
        int y = (int) (10 * (Constant.TILE_HEIGHT + 2) + 1);

        return getSprite(texture, x, y, Constant.TILE_WIDTH, Constant.TILE_HEIGHT);
    }

    @Override
    public SpriteModel getExterior(int index, int floor) {
        int texture = 4;
        return getSprite(texture, Math.min(floor, 4) * Constant.TILE_WIDTH, 12 * Constant.TILE_HEIGHT, Constant.TILE_WIDTH, Constant.TILE_HEIGHT);
//		if (bottom) {
//		} else {
//			int offset = _random[index % 50];
//			int x = (int) (offset * Constant.TILE_WIDTH);
//			int y = (int) (7 * (Constant.TILE_HEIGHT + 2) + 1);
//			return getSprite(texture, x, y, Constant.TILE_WIDTH, Constant.TILE_HEIGHT);
//		}
    }

    private SpriteModel getSprite(int textureIndex, int i, int j, int k, int l) {
        return getSprite(textureIndex, i, j, k, l, 255);
    }

    private SpriteModel getSprite(int texture, int x, int y, int width, int height, int alpha) {
        long sum = getSum(texture, x, y, width, height, alpha);
        GDXSpriteModel sprite = _sprites.get(sum);
        if (sprite == null) {
            sprite = new GDXSpriteModel(_texture[texture], x, y, width, height);
            sprite.getData().setColor(new Color(255, 255, 255, alpha));
            _sprites.put(sum, sprite);
        }
        return sprite;
    }

    //	private Sprite getSprite(ItemInfo item, int tile, int alpha, boolean isIcon) {
//	}
//
    private SpriteModel getSprite(ItemInfo item, int tile, int state, int alpha, boolean isIcon) {
        if (item.spriteId == 0) {
            item.spriteId = ++_count;
        }

        long sum = getSum(item.spriteId, tile, state, isIcon ? 1 : 0);

        GDXSpriteModel sprite = _sprites.get(sum);
        if (sprite == null) {
            int tileX = item.tiles != null ? tile % item.tiles[0] : 0;
            int tileY = item.tiles != null ? tile / item.tiles[0] : 0;
            int offsetY = state * item.height * Constant.TILE_HEIGHT;

            File imgFile;
            if ("base".equals(item.packageName)) {
                imgFile = foundImageFile(item.fileName + ".png");
            } else {
                imgFile = new File("mods/" + item.packageName + "/items/" + item.fileName + ".png");
            }
            if (imgFile != null && imgFile.exists()) {
                sprite = new GDXSpriteModel(new Texture(new FileHandle(imgFile)), tileX * item.width * Constant.TILE_WIDTH,
                        tileY * item.height * Constant.TILE_HEIGHT + offsetY,
                        item.width * Constant.TILE_WIDTH,
                        item.height * Constant.TILE_HEIGHT);
                sprite.getData().setColor(new Color(255, 255, 255, alpha));
                if (isIcon) {
                    switch (Math.max(item.width, item.height)) {
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
        return sprite;
    }

    private File foundImageFile(String fileName) {
        // TODO: clean
        for (File file: new File("data/items/").listFiles()) {
            if (file.isDirectory() && !file.getName().equals("24")) {
                for (File subFile: file.listFiles()) {
                    if (subFile.getName().equals(fileName)) {
                        return subFile;
                    }
                }
            }
            if (file.getName().equals(fileName)) {
                return file;
            }
        }
        return null;
    }

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
        sum = sum << 6;		// 6
        sum += x;
        sum = sum << 12;	// 6 + 12 = 18
        sum += y;
        sum = sum << 12;	// 18 + 12 = 30
        sum += extra1;
        sum = sum << 10;	// 30 + 10 = 40
        sum += extra2;
        sum = sum << 10;	// 40 + 10 = 50
        sum += alpha;
        sum = sum << 8;		// 50 + 8 = 58
        return sum;
    }

    @Override
    public SpriteModel getResource(ResourceModel resource) {
        ItemInfo info = resource.getInfo();

        if ("base.rock".equals(info.name)) {
            return getSprite(info, resource.getTile(), 0, 255, false);
        }

        else if ("base.grass".equals(info.name)) {
            return getSprite(info, resource.getTile(), 0, 255, false);
        }

        else if (!info.actions.isEmpty() && "gather".equals(info.actions.get(0).type)) {
            int state = (int)(Math.min(resource.getQuantity(), info.actions.get(0).mature) + 1);
            return getSprite(info, 0, state, 255, false);
        }

        return getSprite(info, 0, 1, 255, false);
    }

    @Override
    public SpriteModel getGround(int type) {
        if (_groundItemInfo == null) {
            _groundItemInfo = GameData.getData().getItemInfo("base.ground");
        }
        return getSprite(_groundItemInfo, 0, 0, 255, false);
    }

    @Override
    public SpriteModel getFloor(StructureModel item, int zone, int room) {
        if (item != null && item.getName().equals("base.floor")) {
            int choice = 1;
            int texture = 4;
            int x = (room % choice) * Constant.TILE_WIDTH;
            int y = zone * (Constant.TILE_HEIGHT + 2) + 1;
            int alpha = 255;
//            int alpha = Math.min(item != null ? 75 + 180 / item.getScience() * (int)item.getMatterSupply() : 255, 255);
            return getSprite(texture, x, y, Constant.TILE_WIDTH, Constant.TILE_HEIGHT, alpha);
        } else if (item != null) {
            return getSprite(item.getInfo(), 0, 0, 255, false);
        }
        return null;
    }

    @Override
    public SpriteModel getNoOxygen() {
        int texture = 4;
        int x = 0;
        int y = 8 * (Constant.TILE_HEIGHT + 2) + 1;
        return getSprite(texture, x, y, Constant.TILE_WIDTH, Constant.TILE_HEIGHT);
    }

    @Override
    public SpriteModel getSimpleWall(int zone) {
        int texture = 6;
        int x = 0;
        int y = WALL_HEIGHT * zone;
        int width = WALL_WIDTH;
        int height = WALL_HEIGHT;

        return getSprite(texture, x, y, width, height, 255);
    }

    @Override
    public SpriteModel getWall(StructureModel item, int special, int index, int zone) {
        // Door
        if (item.getName().equals("base.door")) {
            int alpha = 255;
//            int alpha = 75 + 180 / item.getScience() * (int)item.getMatterSupply();
            return getSprite(6,
                    WALL_WIDTH * item.getMode(),
                    WALL_HEIGHT * 7,
                    WALL_WIDTH,
                    WALL_HEIGHT,
                    alpha);
        }

        // Wall
        else {
            if (item.isWall()) {
//                int alpha = 75 + 180 / item.getScience() * (int)item.getMatterSupply();
                int alpha = 255;
                int texture = 6;
                int x = 0;
                int y = 0;
                int width = 0;
                int height = 0;

                // Normal
                if (special == 0) {
                    x = 0;
                    y = WALL_HEIGHT * zone;
                    width = WALL_WIDTH;
                    height = WALL_HEIGHT;
                }

                // Wall bellow
                if (special == 1) {
                    x = (WALL_WIDTH + 2) * index + 1;
                    y = (WALL_HEIGHT + 2) * 8 + 1;
                    width = WALL_WIDTH;
                    height = WALL_HEIGHT;
                }

                // Double normal
                if (special == 4) {
                    x = 64;
                    y = WALL_HEIGHT * zone;
                    width = WALL_WIDTH * 2;
                    height = WALL_HEIGHT;
                }

                // Double special
                if (special == 2) {
                    index = _random[index % 20];
                    x = 256 + 64 * (index % 4);
                    y = WALL_HEIGHT * zone;
                    width = WALL_WIDTH * 2;
                    height = WALL_HEIGHT;
                }

                // Single special
                if (special == 3) {
                    index = _random[index % 20];
                    x = 128 + WALL_WIDTH * (index % 4);
                    y = WALL_HEIGHT * zone;
                    width = WALL_WIDTH;
                    height = WALL_HEIGHT;
                }

                // Wall above
                if (special == 5) {
                    x = (WALL_WIDTH + 2) * index + 1;
                    y = (WALL_HEIGHT + 2) * 9 + 1;
                    width = WALL_WIDTH;
                    if (index == 0) {
                        height = WALL_HEIGHT - 6;
                    } else {
                        height = WALL_HEIGHT;
                    }
                }

                return getSprite(texture, x, y, width, height, alpha);
            }

            else if (item.isHull()) {
                return getSprite(item.getInfo(), 0, 0, 255, false);
            }
        }
        return null;
    }

    @Override
    public SpriteModel getCharacter(CharacterModel c, int dirIndex, int frame) {
        return getCharacter(c.getProfession(), dirIndex, frame, c.isSleeping() ? 1 : 0);
    }

    @Override
    public SpriteModel getCharacter(ProfessionModel profession, int direction, int frame, int extra) {
        int sum = profession.getType().ordinal();
        sum = sum << 8;
        sum += direction;
        sum = sum << 8;
        sum += frame;
        sum = sum << 8;
        sum += extra;

        GDXSpriteModel sprite = _spritesCharacters.get(sum);
        if (sprite == null) {
            Texture texture;

            switch (profession.getType()) {
                case ENGINEER:
                    texture = _textureCharacters[2];
//                    sprite.getData().setScale(0.8f, 0.8f);
//				sprite.setTextureRect(new IntRect(0, 0, Constant.CHAR_WIDTH, 32));
                    break;
                case SECURITY:
                    texture = _textureCharacters[1];
//                    sprite.getData().setScale(0.8f, 0.8f);
                    break;
                default:
                    texture = _textureCharacters[0];
//                    sprite.getData().setScale(0.8f, 0.8f);
                    break;
            }

            sprite = new GDXSpriteModel(texture,
                    Constant.CHAR_WIDTH * frame + (extra * 128),
                    Constant.CHAR_HEIGHT * direction,
                    Constant.CHAR_WIDTH,
                    Constant.CHAR_HEIGHT);

            _spritesCharacters.put(sum, sprite);
        }

        return sprite;
    }

    @Override
    public SpriteModel getFoe(Object object, int direction, int frame) {
        GDXSpriteModel sprite = _spritesCharacters.get(9999);
        if (sprite == null) {
            sprite = new GDXSpriteModel(_textureCharacters[3],
                    Constant.CHAR_WIDTH * frame,
                    Constant.CHAR_HEIGHT * direction,
                    Constant.CHAR_WIDTH,
                    Constant.CHAR_HEIGHT);
            //			sprite.setScale(0.8f, 0.8f);
            _spritesCharacters.put(9999, sprite);

        }
        return sprite;
    }

    @Override
    public SpriteModel getBullet(int i) {
        return getSprite(1,
                i * Constant.TILE_WIDTH,
                17 * Constant.TILE_HEIGHT,
                Constant.TILE_WIDTH,
                Constant.TILE_HEIGHT);
    }

    @Override
    public SpriteModel getIconChecked() {
        return getSprite(7, 0, 0, 16, 16, 255);
    }

    @Override
    public SpriteModel getIconUnChecked() {
        return getSprite(7, 32, 0, 16, 16, 255);
    }

    @Override
    public SpriteModel getSelector(int tile) {
        return _selectors[tile % NB_SELECTOR_TILE];
    }

    @Override
    public SpriteModel getSelectorCorner(int corner) {
        return _itemSelectors[corner];
    }

    @Override
    public SpriteModel getSelector(MapObjectModel item, int frame) {
        return _itemSelectors[frame % NB_ITEM_SELECTOR_TILE];
    }

    @Override
    public RenderEffect createRenderEffect() {
        return new GDXRenderEffect();
    }

    @Override
    public Viewport createViewport() {
        return new GDXViewport(300, 200);
    }
}
