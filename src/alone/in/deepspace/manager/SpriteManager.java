package alone.in.deepspace.manager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;

import alone.in.deepspace.model.BaseItem;
import alone.in.deepspace.model.Character;
import alone.in.deepspace.model.ItemInfo;
import alone.in.deepspace.model.Profession;
import alone.in.deepspace.model.StructureItem;
import alone.in.deepspace.model.WorldRessource;
import alone.in.deepspace.util.Constant;
import alone.in.deepspace.util.Log;
import alone.in.deepspace.util.ObjectPool;

public class SpriteManager {
	private static int 				_count;
	private static SpriteManager 	_self;
	private Map<Integer, Sprite>	_spritesCharacters;
	private Map<Long, Sprite> 		_sprites;
	private Texture[] 				_textureCharacters;
	private Texture[]				_texture;
	private Font					_font;

	private int[] _random = {
			0, 0, 1, 3, 2, 3, 1, 3, 0, 1,
			0, 2, 1, 3, 1, 0, 1, 3, 0, 1,
			0, 1, 2, 1, 3, 0, 1, 3, 0, 1,
			0, 0, 1, 3, 2, 2, 1, 3, 2, 1,
			3, 1, 1, 0, 3, 2, 0, 1, 0, 1};

	private SpriteManager() throws IOException {
		_sprites = new HashMap<Long, Sprite>();
		_spritesCharacters = new HashMap<Integer, Sprite>();

		_textureCharacters = new Texture[4];

		_textureCharacters[0] = new Texture();
		_textureCharacters[0].loadFromFile((new File("res/Characters/scientifique.png")).toPath());
		_textureCharacters[0].setSmooth(true);

		_textureCharacters[1] = new Texture();
		_textureCharacters[1].loadFromFile((new File("res/Characters/soldat3.png")).toPath());
		_textureCharacters[1].setSmooth(true);

		_textureCharacters[2] = new Texture();
		_textureCharacters[2].loadFromFile((new File("res/Characters/gallery_84826_3_2787.png")).toPath());
		_textureCharacters[2].setSmooth(true);

		_textureCharacters[3] = new Texture();
		_textureCharacters[3].loadFromFile((new File("res/Characters/NuChara01.png")).toPath());
		_textureCharacters[3].setSmooth(true);

		_texture = new Texture[8];

		_texture[0] = new Texture();
		_texture[0].loadFromFile((new File("res/Tilesets/Futuristic_A5.png").toPath()));
		_texture[0].setSmooth(true);

		_texture[1] = new Texture();
		_texture[1].loadFromFile((new File("res/Tilesets/Futuristic_TileC.png").toPath()));
		_texture[1].setSmooth(true);

		_texture[2] = new Texture();
		_texture[2].loadFromFile((new File("res/Tilesets/Futuristic_TileB.png").toPath()));
		_texture[2].setSmooth(true);

		_texture[3] = new Texture();
		_texture[3].loadFromFile((new File("res/Tilesets/Futuristic_TileE.png").toPath()));
		_texture[3].setSmooth(true);

		_texture[4] = new Texture();
		_texture[4].loadFromFile((new File("res/Tilesets/zones.png").toPath()));
		_texture[4].setSmooth(true);

		_texture[5] = new Texture();
		_texture[5].loadFromFile((new File("res/Tilesets/Futuristic_A3.png").toPath()));
		_texture[5].setSmooth(true);

		_texture[6] = new Texture();
		_texture[6].loadFromFile((new File("res/Tilesets/walls.png").toPath()));
		_texture[6].setSmooth(true);

		_texture[7] = new Texture();
		_texture[7].loadFromFile((new File("res/Tilesets/icons.png").toPath()));
		_texture[7].setSmooth(true);

		// Font
		_font = new Font();
		_font.loadFromFile((new File("res/fonts/xolonium_regular.otf")).toPath());

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

	public static SpriteManager getInstance() {
		if (_self == null) {
			try {
				_self = new SpriteManager();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return _self;
	}

	public Font getFont() {
		return _font;
	}

	public Sprite getItem(BaseItem item, int tile) {
		if (item == null) {
			return null;
		}
		
		if (item.isStructure() == false) {
			if (item.isRessource()) {
				return getRessource((WorldRessource)item, 0);
			}

			int alpha = Math.min(item.getMatter() == 0 ? 255 : 75 + 180 / item.getMatter() * item.getMatterSupply(), 255);
			
			return getSprite(item.getInfo(), tile, alpha);
		}

		return null;
	}

	public Sprite	getItem(BaseItem item) {
		return getItem(item, 0);
	}

	public Sprite		getIcon(ItemInfo info) {
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
		
		return getSprite(info, 0, 255);

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

	public Sprite getGreenHouse(int index) {
		int texture = 4;
//		int offset = _random[index % 50];
		int x = (int) (index * (Constant.TILE_WIDTH + 2) + 1);
		int y = (int) (10 * (Constant.TILE_HEIGHT + 2) + 1);

		return getSprite(texture, x, y, Constant.TILE_WIDTH, Constant.TILE_HEIGHT);
	}

	public Sprite getExterior(int index, int floor) {
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

	private Sprite getSprite(int textureIndex, int i, int j, int k, int l) {
		return getSprite(textureIndex, i, j, k, l, 255);
	}

	private Sprite getSprite(int texture, int x, int y, int width, int height, int alpha) {
		long sum = getSum(texture, x, y, width, height, alpha);
		Sprite sprite = _sprites.get(sum);
		if (sprite == null) {
			sprite = new Sprite();
			sprite.setColor(new Color(255, 255, 255, alpha));
			sprite.setTexture(_texture[texture]);
			sprite.setTextureRect(ObjectPool.getIntRect(x, y, width, height));
			_sprites.put(sum, sprite);
		}
		return sprite;
	}

	private Sprite getSprite(ItemInfo item, int tile, int alpha) {
		if (item.spriteId == 0) {
			item.spriteId = ++_count;
		}
		
		long sum = getSum(item.spriteId, tile);
		
		Sprite sprite = _sprites.get(sum);
		if (sprite == null) {
			int tileX = tile % 10;
			int tileY = tile / 10;
			try {
				File imgFile = null;
				if ("base".equals(item.packageName)) {
					imgFile = new File("data/items/" + item.fileName + ".png");
				} else {
					imgFile = new File("mods/" + item.packageName + "/items/" + item.fileName + ".png");
				}
				if (imgFile.exists()) {
					sprite = new Sprite();
					sprite.setColor(new Color(255, 255, 255, alpha));
					Texture texture = new Texture();
					texture.loadFromFile((imgFile.toPath()));
					texture.setSmooth(true);
					sprite.setTexture(texture);
					sprite.setTextureRect(ObjectPool.getIntRect(tileX * item.width * Constant.TILE_WIDTH, tileY * item.height * Constant.TILE_HEIGHT, item.width * Constant.TILE_WIDTH, item.height * Constant.TILE_HEIGHT));
					_sprites.put(sum, sprite);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sprite;
	}

	private long getSum(int spriteId, int tile) {
		if (spriteId > 4096 || tile > 4096) {
			Log.error("SpriteManager.getSum -> out of bounds values");
		}

		long sum = spriteId;
		sum = sum << 12;
		sum += tile;
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

	public Sprite 				getRessource(WorldRessource item, int tile) {

		if ("base.rock".equals(item.getInfo().name)) {
			return getSprite(item.getInfo(), tile, 255);
		}
		
//		if (item.getMatterSupply() == 0) {
//			return null;//getExterior(item.getWidth() + item.getHeight() * 42);
//		} else {
//			int value = Math.min(item.getMatterSupply(), 7);
//			return getSprite(4,
//					value * Constant.TILE_WIDTH,
//					9 * (Constant.TILE_HEIGHT + 2) + 1,
//					Constant.TILE_WIDTH + 1,
//					Constant.TILE_HEIGHT);
		return getSprite(item.getInfo(), 0, 255);
//		}
	}

	public Sprite				getFloor(StructureItem item, int zone, int room) {
		if (item != null) {
			return getSprite(item.getInfo(), 0, 255);
		} else {
			int choice = 1;
			int texture = 4;
			int x = (room % choice) * Constant.TILE_WIDTH;
			int y = zone * (Constant.TILE_HEIGHT + 2) + 1;
			int alpha = Math.min(item != null ? 75 + 180 / item.getMatter() * item.getMatterSupply() : 255, 255);
			return getSprite(texture, x, y, Constant.TILE_WIDTH, Constant.TILE_HEIGHT, alpha);
		}
	}

	public Sprite				getNoOxygen() {
		int texture = 4;
		int x = 0;
		int y = 8 * (Constant.TILE_HEIGHT + 2) + 1;
		return getSprite(texture, x, y, Constant.TILE_WIDTH, Constant.TILE_HEIGHT);
	}

	public Sprite		getWall(BaseItem item, int special, int index, int zone) {
		int WALL_HEIGHT = 48;
		int WALL_WIDTH = 32;

		// Door
		if (item.getName().equals("base.door")) {
			int alpha = 75 + 180 / item.getMatter() * item.getMatterSupply();
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
				int alpha = 75 + 180 / item.getMatter() * item.getMatterSupply();
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
		}
		return null;	  
	}

	public Sprite getCharacter(Character c, int dirIndex, int frame) {
		return getCharacter(c.getProfession(), dirIndex, frame, c.isSleeping() ? 1 : 0);
	}

	public Sprite getCharacter(Profession profession, int direction, int frame, int extra) {
		int sum = profession.getType().ordinal();
		sum = sum << 8;
		sum += direction;
		sum = sum << 8;
		sum += frame;
		sum = sum << 8;
		sum += extra;

		Sprite sprite = _spritesCharacters.get(sum);
		if (sprite == null) {
			sprite = new Sprite();
			sprite.setTextureRect(new IntRect(Constant.CHAR_WIDTH * frame + (extra * 128),
					Constant.CHAR_HEIGHT * direction,
					Constant.CHAR_WIDTH,
					Constant.CHAR_HEIGHT));

			switch (profession.getType()) {
			case ENGINEER:
				sprite.setTexture(_textureCharacters[2]);
				sprite.setScale(0.8f, 0.8f);
//				sprite.setTextureRect(new IntRect(0, 0, Constant.CHAR_WIDTH, 32));
				break;
			case SECURITY:
				sprite.setTexture(_textureCharacters[1]);
				sprite.setScale(0.8f, 0.8f);
				break;
			default:
				sprite.setTexture(_textureCharacters[0]);
				sprite.setScale(0.8f, 0.8f);
				break;
			}
			_spritesCharacters.put(sum, sprite);
		}

		return sprite;
	}

	public Sprite getFoe(Object object, int direction, int frame) {
		Sprite sprite = _spritesCharacters.get(9999);
		if (sprite == null) {
			sprite = new Sprite();
			sprite.setTexture(_textureCharacters[3]);
			//			sprite.setScale(0.8f, 0.8f);
			sprite.setTextureRect(new IntRect(Constant.CHAR_WIDTH * frame,
					Constant.CHAR_HEIGHT * direction,
					Constant.CHAR_WIDTH,
					Constant.CHAR_HEIGHT));
			_spritesCharacters.put(9999, sprite);

		}
		return sprite;
	}

	public Sprite getBullet(int i) {
		return getSprite(1,
				i * Constant.TILE_WIDTH,
				17 * Constant.TILE_HEIGHT,
				Constant.TILE_WIDTH,
				Constant.TILE_HEIGHT);
	}

	public Sprite getIconChecked() {
		return getSprite(7, 0, 0, 16, 16, 255);
	}

	public Sprite getIconUnChecked() {
		return getSprite(7, 32, 0, 16, 16, 255);
	}

}
