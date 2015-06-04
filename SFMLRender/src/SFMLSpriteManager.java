import org.jsfml.graphics.Color;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.Texture;
import org.newdawn.slick.util.pathfinding.Path;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.SpriteModel;
import org.smallbox.faraway.Viewport;
import org.smallbox.faraway.engine.ui.FrameLayout;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.engine.ui.View;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.model.ProfessionModel;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.item.ItemBase;
import org.smallbox.faraway.model.item.ItemInfo;
import org.smallbox.faraway.model.item.StructureItem;
import org.smallbox.faraway.model.item.WorldResource;
import org.smallbox.faraway.manager.SpriteManager;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class SFMLSpriteManager extends SpriteManager {
	private static final int WALL_HEIGHT = 48;
	private static final int WALL_WIDTH = 32;
	private static final int NB_SELECTOR_TILE = 4;
	private static final int NB_ITEM_SELECTOR_TILE = 4;
	private static int 				_count;
	private static SFMLSpriteManager _self;
	private Map<Integer, SFMLSprite>	_spritesCharacters;
	private Map<Long, SFMLSprite> 		_sprites;
	private Texture[] 				_textureCharacters;
	private Texture[]				_texture;
	private Font					_font;

	private int[] _random = {
			0, 0, 1, 3, 2, 3, 1, 3, 0, 1,
			0, 2, 1, 3, 1, 0, 1, 3, 0, 1,
			0, 1, 2, 1, 3, 0, 1, 3, 0, 1,
			0, 0, 1, 3, 2, 2, 1, 3, 2, 1,
			3, 1, 1, 0, 3, 2, 0, 1, 0, 1};
	private Texture _textureNeedBar;
	private Texture _textureSelector;
	private SFMLSprite[] _selectors;
	private Texture _textureItemSelector;
	private SFMLSprite[] _itemSelectors;
	private Map<String, SpriteModel> _icons;

	public SFMLSpriteManager() throws IOException {
		_icons = new HashMap<>();

		_sprites = new HashMap<>();
		_spritesCharacters = new HashMap<>();

		_textureCharacters = new Texture[4];

		_textureCharacters[0] = new Texture();
		_textureCharacters[0].loadFromFile((new File("data/res/Characters/scientifique.png")).toPath());
		_textureCharacters[0].setSmooth(true);

		_textureCharacters[1] = new Texture();
		_textureCharacters[1].loadFromFile((new File("data/res/Characters/soldat3.png")).toPath());
		_textureCharacters[1].setSmooth(true);

		_textureCharacters[2] = new Texture();
		_textureCharacters[2].loadFromFile((new File("data/res/Characters/gallery_84826_3_2787.png")).toPath());
		_textureCharacters[2].setSmooth(true);

		_textureCharacters[3] = new Texture();
		_textureCharacters[3].loadFromFile((new File("data/res/Characters/NuChara01.png")).toPath());
		_textureCharacters[3].setSmooth(true);

		_textureNeedBar = new Texture();
		_textureNeedBar.loadFromFile((new File("data/res/Tilesets/needbar.png")).toPath());
		_textureNeedBar.setRepeated(true);

		_textureSelector = new Texture();
		_textureSelector.loadFromFile((new File("data/res/Tilesets/selector.png")).toPath());
		_selectors = new SFMLSprite[NB_SELECTOR_TILE];
		for (int i = 0; i < NB_SELECTOR_TILE; i++) {
			_selectors[i] = new SFMLSprite();
			_selectors[i].getData().setTexture(_textureSelector);
			_selectors[i].getData().setTextureRect(new IntRect(i * 34, 0, 34, 48));
		}

		_textureItemSelector = new Texture();
		_textureItemSelector.loadFromFile((new File("data/res/Tilesets/item_selector.png")).toPath());
		_itemSelectors = new SFMLSprite[NB_ITEM_SELECTOR_TILE];
		for (int i = 0; i < NB_ITEM_SELECTOR_TILE; i++) {
			_itemSelectors[i] = new SFMLSprite();
			_itemSelectors[i].getData().setTexture(_textureItemSelector);
			_itemSelectors[i].getData().setTextureRect(new IntRect(i * 32, 0, 32, 32));
		}

		_texture = new Texture[8];

		_texture[0] = new Texture();
		_texture[0].loadFromFile((new File("data/res/Tilesets/Futuristic_A5.png").toPath()));
		_texture[0].setSmooth(true);

		_texture[1] = new Texture();
		_texture[1].loadFromFile((new File("data/res/Tilesets/Futuristic_TileC.png").toPath()));
		_texture[1].setSmooth(true);

		_texture[2] = new Texture();
		_texture[2].loadFromFile((new File("data/res/Tilesets/Futuristic_TileB.png").toPath()));
		_texture[2].setSmooth(true);

		_texture[3] = new Texture();
		_texture[3].loadFromFile((new File("data/res/Tilesets/Futuristic_TileE.png").toPath()));
		_texture[3].setSmooth(true);

		_texture[4] = new Texture();
		_texture[4].loadFromFile((new File("data/res/Tilesets/zones.png").toPath()));
		_texture[4].setSmooth(true);

		_texture[5] = new Texture();
		_texture[5].loadFromFile((new File("data/res/Tilesets/Futuristic_A3.png").toPath()));
		_texture[5].setSmooth(true);

		_texture[6] = new Texture();
		_texture[6].loadFromFile((new File("data/res/Tilesets/walls.png").toPath()));
		_texture[6].setSmooth(true);

		_texture[7] = new Texture();
		_texture[7].loadFromFile((new File("data/res/Tilesets/icons.png").toPath()));
		_texture[7].setSmooth(true);

		// Font
		_font = new Font();
		_font.loadFromFile((new File("data/res/fonts/font.ttf")).toPath());

		//		// IC battery
		//		{
		//			Texture texture = new Texture();
		//			texture.loadFromFile((new File("res/battery.png").toPath()));
		//
		//			_spriteBattery = _temp;
		//			_spriteBattery.setTexture(texture);
		//			_spriteBattery.setTextureRect(ObjectPool.getIntRect(0, 0, 24, 24));
		//		}
	}

	@Override
	public SpriteModel getIcon(String path) {
		if (!_icons.containsKey(path)) {
			try {
				Texture texture = new Texture();
				texture.setSmooth(true);
				texture.loadFromFile(new File(path).toPath());
				SFMLSprite sprite = new SFMLSprite();
                sprite.getData().setTexture(texture);
				_icons.put(path, sprite);
			} catch (IOException e) {
				_icons.put(path, null);
				e.printStackTrace();
			}
		}
		return _icons.get(path);
	}

	public Font getFont() {
		return _font;
	}

	// TODO
	@Override
	public SpriteModel getItem(ItemBase item, int tile) {
		if (item == null) {
			return null;
		}
		
		if (item.isStructure() == false) {
			if (item.isRessource()) {
				return getResource((WorldResource) item);
			}

			int alpha = Math.min(item.getMatter() == 0 ? 255 : 75 + 180 / item.getMatter() * (int)item.getMatterSupply(), 255);
			
			return getSprite(item.getInfo(), tile, 0, alpha, false);
		}

		return getSprite(item.getInfo(), tile, 0, 255, false);
	}

    @Override
	public SpriteModel getItem(ItemBase item) {
		return getItem(item, 0);
	}

    @Override
	public SpriteModel getIcon(ItemInfo info) {
		//		switch (type) {
		//
		//		case STRUCTURE_FLOOR:
		//			return _spriteFloor[0];
		//
		//		case IC_BATTERY:
		//			return _spriteBattery;
		//		}

//		for (int i = 0; spritesRes[i].type != BaseItem.Type.NONE; i++) {
//			if (spritesRes[i].type == type) {
//				res = spritesRes[i];
//			}
//		}
		
		return getSprite(info, 0, 0, 255, true);

//		if (res != null) {
//			if (info.isRessource) {
//				return getSprite(res.textureIndex,
//						res.posX * Constant.TILE_SIZE,
//						res.posY * Constant.TILE_SIZE,
//						info.width * Constant.TILE_SIZE,
//						info.height * Constant.TILE_SIZE);
//			} else {
//				//				int size = Math.max(info.width, info.height);
//				//				if (size == 2)
//				//					sprite.setScale(0.75f, 0.75f);
//				//				if (size == 3)
//				//					sprite.setScale(0.5f, 0.5f);
//
//				int texture = res.textureIndex;
//				int x = res.posX * Constant.TILE_SIZE;
//				int y = res.posY * Constant.TILE_SIZE;
//				int width = info.width * Constant.TILE_SIZE;
//				int height = info.height * Constant.TILE_SIZE;
//				return getSprite(texture, x, y, width, height);
//			}
//		}
//
//		return null;
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
		SFMLSprite sprite = _sprites.get(sum);
		if (sprite == null) {
			sprite = new SFMLSprite();
			sprite.getData().setColor(new Color(255, 255, 255, alpha));
			sprite.getData().setTexture(_texture[texture]);
			sprite.getData().setTextureRect(ObjectPool.getIntRect(x, y, width, height));
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
		
		SFMLSprite sprite = _sprites.get(sum);
		if (sprite == null) {
			int tileX = tile % 10;
			int tileY = tile / 10;
			int offsetY = state * item.height * Constant.TILE_HEIGHT;
			
			try {
				File imgFile = null;
				if ("base".equals(item.packageName)) {
					imgFile = new File("data/items/" + item.fileName + ".png");
				} else {
					imgFile = new File("mods/" + item.packageName + "/items/" + item.fileName + ".png");
				}
				if (imgFile.exists()) {
					sprite = new SFMLSprite();
					sprite.getData().setColor(new Color(255, 255, 255, alpha));
					Texture texture = new Texture();
					texture.loadFromFile((imgFile.toPath()));
					texture.setSmooth(true);
					sprite.getData().setTexture(texture);
					sprite.getData().setTextureRect(ObjectPool.getIntRect(
							tileX * item.width * Constant.TILE_WIDTH,
							tileY * item.height * Constant.TILE_HEIGHT + offsetY,
							item.width * Constant.TILE_WIDTH,
							item.height * Constant.TILE_HEIGHT));
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
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sprite;
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
	public SpriteModel getResource(WorldResource resource) {
		ItemInfo info = resource.getInfo();

		if ("base.res_rock".equals(info.name)) {
			return getSprite(info, resource.getTile(), 0, 255, false);
		}

		else if ("base.grass".equals(info.name)) {
			return getSprite(info, resource.getTile(), 0, 255, false);
		}

		else if (!info.actions.isEmpty() && "gather".equals(info.actions.get(0).type)) {
			int state = (int)(Math.min(resource.getValue(), info.actions.get(0).mature) + 1);
			return getSprite(info, 0, state, 255, false);
		}

		return getSprite(info, 0, 1, 255, false);
	}

    @Override
	public SpriteModel getFloor(StructureItem item, int zone, int room) {
		if (item != null && item.getName().equals("base.floor")) {
			int choice = 1;
			int texture = 4;
			int x = (room % choice) * Constant.TILE_WIDTH;
			int y = zone * (Constant.TILE_HEIGHT + 2) + 1;
			int alpha = Math.min(item != null ? 75 + 180 / item.getMatter() * (int)item.getMatterSupply() : 255, 255);
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
	public SpriteModel getWall(StructureItem item, int special, int index, int zone) {
		// Door
		if (item.getName().equals("base.door")) {
			int alpha = 75 + 180 / item.getMatter() * (int)item.getMatterSupply();
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
				int alpha = 75 + 180 / item.getMatter() * (int)item.getMatterSupply();
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

		SFMLSprite sprite = _spritesCharacters.get(sum);
		if (sprite == null) {
			sprite = new SFMLSprite();
			sprite.getData().setTextureRect(new IntRect(Constant.CHAR_WIDTH * frame + (extra * 128),
					Constant.CHAR_HEIGHT * direction,
					Constant.CHAR_WIDTH,
					Constant.CHAR_HEIGHT));

			switch (profession.getType()) {
			case ENGINEER:
				sprite.getData().setTexture(_textureCharacters[2]);
				sprite.getData().setScale(0.8f, 0.8f);
//				sprite.setTextureRect(new IntRect(0, 0, Constant.CHAR_WIDTH, 32));
				break;
			case SECURITY:
				sprite.getData().setTexture(_textureCharacters[1]);
				sprite.getData().setScale(0.8f, 0.8f);
				break;
			default:
				sprite.getData().setTexture(_textureCharacters[0]);
				sprite.getData().setScale(0.8f, 0.8f);
				break;
			}
			_spritesCharacters.put(sum, sprite);
		}

		return sprite;
	}

    @Override
	public SpriteModel getFoe(Object object, int direction, int frame) {
		SFMLSprite sprite = _spritesCharacters.get(9999);
		if (sprite == null) {
			sprite = new SFMLSprite();
			sprite.getData().setTexture(_textureCharacters[3]);
			//			sprite.setScale(0.8f, 0.8f);
			sprite.getData().setTextureRect(new IntRect(Constant.CHAR_WIDTH * frame,
					Constant.CHAR_HEIGHT * direction,
					Constant.CHAR_WIDTH,
					Constant.CHAR_HEIGHT));
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
	public SpriteModel getSelector(ItemBase item, int frame) {
		return _itemSelectors[frame % NB_ITEM_SELECTOR_TILE];
	}

    @Override
    public RenderEffect createRenderEffect() {
        return new SFMLRenderEffect();
    }

    @Override
    public Viewport createViewport() {
        return new SFMLViewport(-Constant.WORLD_WIDTH * Constant.TILE_WIDTH / 2, -Constant.WORLD_HEIGHT * Constant.TILE_HEIGHT / 2);
    }
}
