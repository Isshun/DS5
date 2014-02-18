package alone.in.deepspace.Managers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;

import alone.in.deepspace.SpriteResource;
import alone.in.deepspace.Models.BaseItem;
import alone.in.deepspace.Models.ItemInfo;
import alone.in.deepspace.Models.Profession;
import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.Utils.Log;
import alone.in.deepspace.Utils.ObjectPool;
import alone.in.deepspace.World.StructureItem;
import alone.in.deepspace.World.WorldRessource;

public class SpriteManager {
	SpriteResource[] spritesRes = {
	  new SpriteResource(BaseItem.Type.STRUCTURE_HULL,					2, 8, 0),
	  new SpriteResource(BaseItem.Type.STRUCTURE_WALL,					0, 4, 5),
	  // new SpriteResource(BaseItem.Type.STRUCTURE_WALL_BELLOW,				1, 4, 5),
	  new SpriteResource(BaseItem.Type.STRUCTURE_FLOOR,					7, 7, 0),
	  new SpriteResource(BaseItem.Type.STRUCTURE_DOOR,					2, 2, 0),
	  new SpriteResource(BaseItem.Type.STRUCTURE_WINDOW,					2, 3, 0),
	  new SpriteResource(BaseItem.Type.TRANSPORTATION_TRANSPORTER_SYSTEMS,1, 8, 0),
	  new SpriteResource(BaseItem.Type.ARBORETUM_TREE_1,					2, 0, 3),
	  new SpriteResource(BaseItem.Type.ARBORETUM_TREE_2,					3, 0, 3),
	  new SpriteResource(BaseItem.Type.ARBORETUM_TREE_3,					4, 0, 3),
	  new SpriteResource(BaseItem.Type.ARBORETUM_TREE_4,					5, 0, 3),
	  new SpriteResource(BaseItem.Type.ARBORETUM_TREE_5,					0, 0, 3),
	  new SpriteResource(BaseItem.Type.ARBORETUM_TREE_6,					0, 1, 3),
	  new SpriteResource(BaseItem.Type.ARBORETUM_TREE_7,					1, 0, 3),
	  new SpriteResource(BaseItem.Type.ARBORETUM_TREE_8,					1, 1, 3),
	  new SpriteResource(BaseItem.Type.BAR_PUB,							0, 8, 3),
	  new SpriteResource(BaseItem.Type.SICKBAY_BIOBED,					11, 4, 1),
	  // new SpriteResource(BaseItem.Type.ARBORETUM_TREE_9,					0, 1, 3),
	  new SpriteResource(BaseItem.Type.ENGINE_REACTION_CHAMBER,			12, 12, 1),
	  new SpriteResource(BaseItem.Type.ENVIRONMENT_O2_RECYCLER,			11, 16, 1),
	  new SpriteResource(BaseItem.Type.HOLODECK_GRID,						5, 6, 0),
	  new SpriteResource(BaseItem.Type.ENGINE_CONTROL_CENTER,				8, 10, 1),
	  new SpriteResource(BaseItem.Type.QUARTER_BED,						10, 7, 2),
	  new SpriteResource(BaseItem.Type.QUARTER_CHAIR,						8, 6, 2),
	  new SpriteResource(BaseItem.Type.SPECIAL_ROBOT_MAKER,					12, 7, 2),
	  new SpriteResource(BaseItem.Type.SPECIAL_ZYGOTE,						12, 9, 2),
	  new SpriteResource(BaseItem.Type.TACTICAL_PHASER,						12, 6, 2),
	  new SpriteResource(BaseItem.Type.RES_1,								8, 1, 4),
	  new SpriteResource(BaseItem.Type.NONE,								0, 0, 0),
	};
	
	private Map<Integer, Sprite>	_spritesCharacters;

	Sprite		_buf;
	Texture[]		_texture;
	//Constant.NB_TEMPLATES
	Sprite       _spriteBattery;
	Sprite[]       _spriteFloor;
	//[Constant.NB_SPRITES_ROOM]
	Font			_font;

	private Texture[] _textureCharacters;

	private Map<Long, Sprite> _sprites;

	private int[] _random = {
			0, 0, 1, 3, 2, 3, 1, 3, 0, 1,
			0, 2, 1, 3, 1, 0, 1, 3, 0, 1,
			0, 1, 2, 1, 3, 0, 1, 3, 0, 1,
			0, 0, 1, 3, 2, 2, 1, 3, 2, 1,
			3, 1, 1, 0, 3, 2, 0, 1, 0, 1};

	private static SpriteManager _self;

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
		_textureCharacters[2].loadFromFile((new File("res/Characters/Spacecharas.png")).toPath());
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

	public Sprite	getItem(BaseItem item) {
		if (item != null) {
			SpriteResource res = null;
			for (int i = 0; spritesRes[i].type != BaseItem.Type.NONE; i++) {
				if (item.isType(spritesRes[i].type)) {
					res = spritesRes[i];
				}
			}
			
			if (res != null) {
				return getSprite(res.textureIndex,
						res.posX * Constant.TILE_SIZE,
						res.posY * Constant.TILE_SIZE,
						item.getWidth() * Constant.TILE_SIZE,
						item.getHeight() * Constant.TILE_SIZE);
			}
//		   		  int alpha = 75 + 180 / item.getMatter() * item.getMatterSupply();
//		   		  sprite.setColor(new Color(255, 255, 255, alpha));
		}

		return null;
	}

	public Sprite		getIcon(BaseItem.Type type) {
//		switch (type) {
//
//		case STRUCTURE_FLOOR:
//			return _spriteFloor[0];
//
//		case IC_BATTERY:
//			return _spriteBattery;
//		}

		SpriteResource res = null;
		for (int i = 0; spritesRes[i].type != BaseItem.Type.NONE; i++) {
			if (spritesRes[i].type == type) {
				res = spritesRes[i];
			}
		}
		
		if (res != null) {
			if (type == BaseItem.Type.RES_1) {
				ItemInfo info = BaseItem.getItemInfo(type);
				return getSprite(res.textureIndex,
						res.posX * Constant.TILE_SIZE,
						res.posY * Constant.TILE_SIZE,
						info.width * Constant.TILE_SIZE,
						info.height * Constant.TILE_SIZE);
			} else {
				ItemInfo info = BaseItem.getItemInfo(type);
//				int size = Math.max(info.width, info.height);
//				if (size == 2)
//					sprite.setScale(0.75f, 0.75f);
//				if (size == 3)
//					sprite.setScale(0.5f, 0.5f);

				int texture = res.textureIndex;
				int x = res.posX * Constant.TILE_SIZE;
				int y = res.posY * Constant.TILE_SIZE;
				int width = info.width * Constant.TILE_SIZE;
				int height = info.height * Constant.TILE_SIZE;
				return getSprite(texture, x, y, width, height);
			}
		}
		
		return null;
	}

	public Sprite getExterior(int index) {
		int texture = 4;
		int offset = _random[index % 50];
		int x = (int) (offset * Constant.TILE_SIZE);
		int y = (int) (7 * (Constant.TILE_SIZE + 2) + 1);
		
		return getSprite(texture, x, y, Constant.TILE_SIZE, Constant.TILE_SIZE);
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

	public Sprite getRessource(WorldRessource item) {
		if (item.getMatterSupply() == 0) {
			return getExterior(item.getWidth() + item.getHeight() * 42);
		} else {
			int value = Math.min(item.getMatterSupply(), 7);
			return getSprite(4,
					value * Constant.TILE_SIZE,
					9 * (Constant.TILE_SIZE + 2) + 1,
					Constant.TILE_SIZE + 1,
					Constant.TILE_SIZE);
		}
	}

	public Sprite				getFloor(StructureItem item, int zone, int room) {
		int choice = 1;
		int texture = 4;
		int x = (room % choice) * Constant.TILE_SIZE;
		int y = zone * (Constant.TILE_SIZE + 2) + 1;
		int alpha = 75 + 180 / item.getMatter() * item._matterSupply;
		return getSprite(texture, x, y, Constant.TILE_SIZE, Constant.TILE_SIZE, alpha);
	}

	public Sprite				getNoOxygen() {
		int texture = 4;
		int x = 0;
		int y = 8 * (Constant.TILE_SIZE + 2) + 1;
		return getSprite(texture, x, y, Constant.TILE_SIZE, Constant.TILE_SIZE);
	}

	public Sprite		getWall(BaseItem item, int special, int index, int zone) {
		int WALL_HEIGHT = 48;
		int WALL_WIDTH = 32;

		// Door
		if (item.isType(BaseItem.Type.STRUCTURE_DOOR)) {
			int alpha = 75 + 180 / item.getMatter() * item.getMatterSupply();
			return getSprite(6,
					WALL_WIDTH * special,
					WALL_HEIGHT * 7,
					WALL_WIDTH,
					WALL_HEIGHT,
					alpha);
	  }

	  // Wall
	  else {
		  if (item.isType(BaseItem.Type.STRUCTURE_WALL)) {
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

			  // Bellow
			  if (special == 1) {
				  x = WALL_WIDTH;
				  y = WALL_HEIGHT * zone;
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
				  x = 256 + 64 * (index % 4);
				  y = WALL_HEIGHT * zone;
				  width = WALL_WIDTH * 2;
				  height = WALL_HEIGHT;
			  }

			  // Single special
			  if (special == 3) {
				  x = 128 + WALL_WIDTH * (index % 4);
				  y = WALL_HEIGHT * zone;
				  width = WALL_WIDTH;
				  height = WALL_HEIGHT;
			  }
			  
			  return getSprite(texture, x, y, width, height, alpha);
		  }
	  }
	  return null;	  
	}

	public Sprite getCharacter(Profession profession, int direction, int frame) {
		int sum = profession.getType().ordinal();
		sum = sum << 8;
		sum += direction;
		sum = sum << 8;
		sum += frame;
		
		Sprite sprite = _spritesCharacters.get(sum);
		if (sprite == null) {
			sprite = new Sprite();
			sprite.setTextureRect(new IntRect(Constant.CHAR_WIDTH * frame,
					Constant.CHAR_HEIGHT * direction,
					Constant.CHAR_WIDTH,
					Constant.CHAR_HEIGHT));

			switch (profession.getType()) {
			case ENGINEER:
				sprite.setTexture(_textureCharacters[2]);
				sprite.setTextureRect(new IntRect(0, 0, Constant.CHAR_WIDTH, 32));
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

}
