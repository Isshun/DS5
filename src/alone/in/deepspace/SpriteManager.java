package alone.in.DeepSpace;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;

import alone.in.DeepSpace.Models.BaseItem;
import alone.in.DeepSpace.Models.Profession;
import alone.in.DeepSpace.Utils.Constant;
import alone.in.DeepSpace.Utils.Log;
import alone.in.DeepSpace.Utils.ObjectPool;
import alone.in.DeepSpace.World.StructureItem;
import alone.in.DeepSpace.World.WorldRessource;

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

	private int random;

	private Texture[] _textureCharacters;

	private Map<Integer, Sprite> _sprites;

	private static SpriteManager _self;
	
	private Sprite _temp;

	private SpriteManager() throws IOException {
		
		_temp = new Sprite();
		
		_sprites = new HashMap<Integer, Sprite>();
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

	  // IC battery
	  {
	    Texture texture = new Texture();
	    texture.loadFromFile((new File("res/battery.png").toPath()));

	    _spriteBattery = _temp;
	    _spriteBattery.setTexture(texture);
	    _spriteBattery.setTextureRect(ObjectPool.getIntRect(0, 0, 24, 24));
	  }
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

	public Sprite	getSprite(BaseItem item) {
		Sprite sprite = _temp;
	  // if (item != NULL) {
	  // 	for (int i = 0; spritesRes[i].type != BaseItem.Type.NONE; i++) {
	  // 	  if (item.isType(spritesRes[i].type)) {

	  // 		// Floor
	  // 		if (item.isType(BaseItem.Type.STRUCTURE_FLOOR)) {
	  // 		  // int choice = 1;

	  // 		  // if (item.getZoneId() == UserInterfaceMenu::CODE_ZONE_HOLODECK) {
	  // 		  // 	choice = 3;
	  // 		  // }

	  // 		  // sprite.setTexture(*_texture[4]);
	  // 		  // sprite.setTextureRect(IntRect((item.getRoomId() % choice) * TILE_SIZE,
	  // 		  // 									 item.getZoneId() * TILE_SIZE,
	  // 		  // 									 TILE_SIZE,
	  // 		  // 									 TILE_SIZE));
	  // 		}

	  // 		// Ressource
	  // 		else if (item.isRessource()) {
	  // 		  sprite.setTexture(*_texture[spritesRes[i].textureIndex]);
	  // 		  sprite.setTextureRect(IntRect(spritesRes[i].posX * TILE_SIZE,
	  // 											 spritesRes[i].posY * TILE_SIZE,
	  // 											 item.getWidth() * TILE_SIZE,
	  // 											 item.getHeight() * TILE_SIZE));
	  // 		}

	  // 		// Else
	  // 		else {
	  // 		  sprite.setScale(0.8f, 0.8f);
	  // 		  sprite.setTexture(*_texture[spritesRes[i].textureIndex]);
	  // 		  sprite.setTextureRect(IntRect(spritesRes[i].posX * TILE_SIZE,
	  // 											 spritesRes[i].posY * TILE_SIZE,
	  // 											 item.getWidth() * TILE_SIZE,
	  // 											 item.getHeight() * TILE_SIZE));
	  // 		  int alpha = 75 + 180 / item.matter * item._matterSupply;
	  // 		  sprite.setColor(Color(255,255,255,alpha));
	  // 		}

	  // 		return;
	  // 	  }
	  // 	}
	  // }

	  // sprite.setTexture(*_texture[0]);
	  // sprite.setTextureRect(IntRect(0, 0, TILE_SIZE, TILE_SIZE));
		
		return sprite;
	}

	public Sprite		getSprite(int type) {
		Sprite sprite = _temp;

	  // switch (type) {

	  // case BaseItem.Type.STRUCTURE_FLOOR:
	  //   sprite = _spriteFloor[0];
	  // 	return;

	  // case IC_BATTERY:
	  //   sprite = _spriteBattery;
	  // 	return;
	  // }

	  // for (int i = 0; spritesRes[i].type != BaseItem.Type.NONE; i++) {
	  // 	if (spritesRes[i].type == type) {
	  // 	  if (type == BaseItem.Type.RES_1) {
	  // 		// sprite.setTexture(*_texture[spritesRes[i].textureIndex]);
	  // 		// sprite.setTextureRect(IntRect(spritesRes[i].posX * TILE_SIZE,
	  // 		// 								   spritesRes[i].posY * TILE_SIZE,
	  // 		// 								   info.width * TILE_SIZE,
	  // 		// 								   info.height * TILE_SIZE));
	  // 	  } else {
	  // 		ItemInfo info = BaseItem.Type.getItemInfo(type);
	  // 		int size = max(info.width, info.height);
	  // 		if (size == 2)
	  // 		  sprite.setScale(0.75f, 0.75f);
	  // 		if (size == 3)
	  // 		  sprite.setScale(0.5f, 0.5f);

	  // 		sprite.setTexture(*_texture[spritesRes[i].textureIndex]);
	  // 		sprite.setTextureRect(IntRect(spritesRes[i].posX * TILE_SIZE,
	  // 										   spritesRes[i].posY * TILE_SIZE,
	  // 										   info.width * TILE_SIZE,
	  // 										   info.height * TILE_SIZE));
	  // 	  }
	  // 	  return;
	  // 	}
	  // }
		return sprite;
	}

	public Sprite getExterior() {
		int texture = 4;
		random = 0;
		int x = (int) (random % 8 * Constant.TILE_SIZE);
		int y = (int) (7 * (Constant.TILE_SIZE + 2) + 1);
		int sum = getSum(texture, x, y, 0, 0);
		
		Sprite sprite = _sprites.get(sum);
		if (sprite == null) {
			sprite = new Sprite();
			sprite.setColor(Color.WHITE);
			sprite.setTexture(_texture[texture]);
			sprite.setTextureRect(ObjectPool.getIntRect(x, y,
					Constant.TILE_SIZE,
					Constant.TILE_SIZE));
			_sprites.put(sum, sprite);
		}
		return sprite;
	}

	private int getSum(int texture, int x, int y, int extra1, int extra2) {
		if (texture > 255 || x > 255 || y > 255 || extra1 > 255 || extra2 > 255) {
			//throw new Exception("SpriteManager.getSum -> out of bounds values");
			Log.error("SpriteManager.getSum -> out of bounds values");
		}
		
		int sum = texture;
		sum = sum << 8;
		sum += x;
		sum = sum << 8;
		sum += y;
		sum = sum << 8;
		sum += extra1;
		sum = sum << 8;
		sum += extra2;
		return sum;
	}

	public Sprite getRessource(WorldRessource item) {
		if (item.getMatterSupply() == 0) {
			return getExterior();
		} else {
			int value = Math.min(item.getMatterSupply(), 7);
			Sprite sprite = _temp;
			sprite.setTexture(_texture[4]);
			sprite.setTextureRect(ObjectPool.getIntRect(value * Constant.TILE_SIZE,
					9 * (Constant.TILE_SIZE + 2) + 1,
					Constant.TILE_SIZE + 1,
					Constant.TILE_SIZE));
			return sprite;
		}
	}

	public Sprite				getFloor(StructureItem item, int zone, int room) {
		int choice = 1;
		
		int texture = 4;
		int x = (room % choice) * Constant.TILE_SIZE;
		int y = zone * (Constant.TILE_SIZE + 2) + 1;
		int sum = getSum(texture, x, y, 0, 0);
		
		Sprite sprite = _sprites.get(sum);
		if (sprite == null) {
			sprite = new Sprite();
			sprite.setTexture(_texture[texture]);
			sprite.setTextureRect(ObjectPool.getIntRect(x, y,
					Constant.TILE_SIZE,
					Constant.TILE_SIZE));
			
			_sprites.put(sum, sprite);
		}

		int alpha = 75 + 180 / item.getMatter() * item._matterSupply;
		sprite.setColor(new Color(255,255,255,alpha));

		return sprite;
// TODO
	  //	  if (zone == UserInterfaceMenu::CODE_ZONE_HOLODECK) {
//	  	choice = 3;
//	  }

	}

	public Sprite				getNoOxygen() {
		Sprite sprite = _temp;
		
		sprite.setTexture(_texture[4]);
		sprite.setTextureRect(ObjectPool.getIntRect(0,
				8 * (Constant.TILE_SIZE + 2) + 1,
				Constant.TILE_SIZE,
				Constant.TILE_SIZE));

		return sprite;
	}

	public Sprite		getWall(BaseItem item, int special, int index, int zone) {
		int WALL_HEIGHT = 48;
		int WALL_WIDTH = 32;

		// Door
		if (item.isType(BaseItem.Type.STRUCTURE_DOOR)) {
			int alpha = 75 + 180 / item.getMatter() * item.getMatterSupply();
			Sprite sprite = _temp;
			sprite.setColor(new Color(255,255,255,alpha));
			sprite.setTexture(_texture[6]);
			sprite.setTextureRect(ObjectPool.getIntRect(WALL_WIDTH * special,
											   WALL_HEIGHT * 7,
											   WALL_WIDTH,
											   WALL_HEIGHT));
			return sprite;
	  }

	  // Wall
	  else {
		  if (item.isType(BaseItem.Type.STRUCTURE_WALL)) {
			  int alpha = 75 + 180 / item.getMatter() * item.getMatterSupply();

			  int texture = 6;
			  int sum = getSum(texture, 255, special, index, zone);
			
			  Sprite sprite = _sprites.get(sum);
			  if (sprite == null) {
				  sprite = new Sprite();
				  sprite.setTexture(_texture[6]);
				  // Normal
				  if (special == 0) {
					  sprite.setTextureRect(ObjectPool.getIntRect(0,
							  WALL_HEIGHT * zone,
							  WALL_WIDTH,
							  WALL_HEIGHT));
				  }

				  // Bellow
				  if (special == 1) {
					  sprite.setTextureRect(ObjectPool.getIntRect(WALL_WIDTH,
							  WALL_HEIGHT * zone,
							  WALL_WIDTH,
							  WALL_HEIGHT));
				  }

				  // Double normal
				  if (special == 4) {
					  sprite.setTextureRect(ObjectPool.getIntRect(64,
							  WALL_HEIGHT * zone,
							  WALL_WIDTH * 2,
							  WALL_HEIGHT));
				  }

				  // Double special
				  if (special == 2) {
					  sprite.setTextureRect(ObjectPool.getIntRect(256 + 64 * (index % 4),
							  WALL_HEIGHT * zone,
							  WALL_WIDTH * 2,
							  WALL_HEIGHT));
				  }

				  // Single special
				  if (special == 3) {
					  sprite.setTextureRect(ObjectPool.getIntRect(128 + WALL_WIDTH * (index % 4),
							  WALL_HEIGHT * zone,
							  WALL_WIDTH,
							  WALL_HEIGHT));
				  }
				  sprite.setColor(new Color(255,255,255,alpha));
				  _sprites.put(sum, sprite);
			  }
			  return sprite;
		  }
	  }
	  return null;	  
	}

	public Font getFont() {
		return _font;
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
			sprite.setTextureRect(new IntRect(Constant.CHAR_WIDTH * frame, Constant.CHAR_HEIGHT * direction, Constant.CHAR_WIDTH, Constant.CHAR_HEIGHT));

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

}
