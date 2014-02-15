package alone.in.deepspace.UserInterface;
import java.io.File;
import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Mouse;

import alone.in.deepspace.BaseItem;
import alone.in.deepspace.Constant;
import alone.in.deepspace.Log;
import alone.in.deepspace.ResourceManager;
import alone.in.deepspace.SpriteManager;
import alone.in.deepspace.UserSubInterface;
import alone.in.deepspace.BaseItem.Type;


public class UserInterfaceEngineering extends UserSubInterface {

	private static int FONT_SIZE		= 16;
	private static int LINE_HEIGHT		= 24;
	private static int TITLE_SIZE		= FONT_SIZE + 8;

	private static int MENU_TILE_OPEN_WIDTH	 = 300;
	private static int MENU_TILE_OPEN_HEIGHT = 160;
	
	Mode				_panelMode;
	Mode				_panelModeHover;
	int				_itemHover;

	public enum Mode {
		MODE_NONE,
		MODE_STRUCTURE,
		MODE_ITEM
	};

	private UserInteraction 	_interaction;

	UserInterfaceEngineering(RenderWindow app, int tileIndex, UserInteraction interaction) throws IOException {
	  super(app, tileIndex);
	  
	  _panelMode = Mode.MODE_NONE;
	  _panelModeHover = Mode.MODE_NONE;
	  _itemHover = -1;
	  _interaction = interaction;

	  _textureTile = new Texture();
	  _textureTile.loadFromFile((new File("res/bg_tile_engineering.png")).toPath());
	  _texturePanel = new Texture();
	  _texturePanel.loadFromFile((new File("res/bg_panel_engineering.png")).toPath());
	}

	void	draw(int frame) {
	  if (isOpen()) {
		drawPanel();
	  }

	  drawTile();
	}

	protected void	drawPanel() {
	  super.drawPanel();

	  Text text = new Text();
	  text.setFont(SpriteManager.getInstance().getFont());

	  // Header structure
	  text.setString("Structure");
	  text.setCharacterSize(TITLE_SIZE);
	  text.setPosition(_posX + Constant.UI_PADDING, _posY + Constant.UI_PADDING);
	  if (_panelModeHover == Mode.MODE_STRUCTURE) {
		text.setStyle(Text.UNDERLINED);
		text.setColor(Color.YELLOW);
		_app.draw(text);
	  }
	  text.setColor(Color.WHITE);
	  text.setStyle(Text.REGULAR);
	  _app.draw(text);
	  text.setString(_panelMode == Mode.MODE_STRUCTURE ? "Structure" : "S");
	  text.setStyle(Text.UNDERLINED);
	  text.setColor(Color.YELLOW);
	  _app.draw(text);

	  // Header item
	  text.setString("Items");
	  text.setCharacterSize(TITLE_SIZE);
	  text.setPosition(_posX + 200 + Constant.UI_PADDING, _posY + Constant.UI_PADDING);
	  if (_panelModeHover == Mode.MODE_ITEM) {
		text.setStyle(Text.UNDERLINED);
		text.setColor(Color.YELLOW);
		_app.draw(text);
	  }
	  text.setColor(Color.WHITE);
	  text.setStyle(Text.REGULAR);
	  _app.draw(text);
	  text.setString(_panelMode == Mode.MODE_ITEM ? "Items" : "I");
	  text.setStyle(Text.UNDERLINED);
	  text.setColor(Color.YELLOW);
	  _app.draw(text);

	  try {
		  if (_panelMode == Mode.MODE_STRUCTURE) {
			for (int index = 0, i = BaseItem.Type.STRUCTURE_START.ordinal() + 1; i < BaseItem.Type.STRUCTURE_STOP.ordinal(); index++, i++) {
				drawIcon(index, i);
			}
		  } else if (_panelMode == Mode.MODE_ITEM) {
			for (int index = 0, i = BaseItem.Type.ITEM_START.ordinal() + 1; i < BaseItem.Type.ITEM_STOP.ordinal(); index++, i++) {
			  drawIcon(index, i);
			}
		  }
	  } catch (IOException e) {
		  // TODO Auto-generated catch block
		  e.printStackTrace();
	  }
	}

	void	drawIcon(int index, int type) throws IOException {
	  int posX = _posX + (index % 9) * 80;
	  int posY = _posY + (int)(index / 9) * 100;

	  // Background
	  RectangleShape shape = new RectangleShape();
	  shape.setSize(new Vector2f(62, 80));
	  shape.setFillColor(_itemHover == type ? Color.WHITE : new Color(236, 201, 37));
	  shape.setPosition(posX + 20, posY + 60);
	  _app.draw(shape);
	  // shape.setSize(Vector2f(54, 54));
	  // shape.setFillColor(Color(0, 80, 140));

	  Texture texture = new Texture();
	  texture.loadFromFile((new File("../res/bg_none.png")).toPath());
	  texture.setRepeated(true);
	  Sprite sprite = new Sprite();
	  sprite.setTexture(texture);
	  sprite.setTextureRect(new IntRect(0, 0, 56, 56));
	  sprite.setPosition(posX + 23, posY + 63);
	  _app.draw(sprite);
		  
	  // Icon
	  Sprite icon = new Sprite();
	  SpriteManager.getInstance().getSprite(type, icon);
	  icon.setPosition(posX + 23, posY + 63);
	  _app.draw(icon);
		  
	  // Name
	  Text text = new Text();
	  text.setString(BaseItem.getItemName(BaseItem.getTypeIndex(type)));
	  text.setFont(SpriteManager.getInstance().getFont());
	  text.setCharacterSize(12);
	  text.setColor(Color.BLACK);
	  text.setStyle(Text.REGULAR);
	  text.setPosition(posX + 26, posY + 117);
	  _app.draw(text);
	}

	void	drawTile() {
	  super.drawTile(new Color(249, 195, 63));
	 
	  Text text = new Text();
	  text.setFont(SpriteManager.getInstance().getFont());
	  text.setCharacterSize(FONT_SIZE);

	  {
		int matter = ResourceManager.getInstance().getMatter();

		text.setString("Matter: " + matter);

		if (matter == 0)
		  text.setColor(Color.RED);
		else if (matter < 20)
		  text.setColor(Color.YELLOW);
	    text.setPosition(_posTileX + Constant.UI_PADDING, _posTileY + TITLE_SIZE + Constant.UI_PADDING);
	    _app.draw(text);
		text.setColor(Color.WHITE);
	  }

	  text.setString("Engineering");
	  text.setCharacterSize(TITLE_SIZE);
	  text.setPosition(_posTileX + Constant.UI_PADDING, _posTileY + Constant.UI_PADDING);
	  _app.draw(text);
	  text.setString("E");
	  text.setStyle(Text.UNDERLINED);
	  text.setColor(Color.YELLOW);
	  _app.draw(text);
	}

	protected boolean	checkKey(Keyboard.Key key) {
	  super.checkKey(key);

	  if (isOpen()) {
		switch (key) {
		case S:
		  _panelMode = Mode.MODE_STRUCTURE;
		  return true;
		case I:
		  _panelMode = Mode.MODE_ITEM;
		  return true;
		case E:
		  close();
		  return true;
		  default:
			  return false;
		}
	  }

	  return false;
	}

	public boolean	onMouseMove(int x, int y) {
	  _isTileActive = false;
	  _panelModeHover = Mode.MODE_NONE;

	  if (isOpen()) {
		_itemHover = -1;

		if (x > _posX && x < _posX + 800 && y > _posY && y < _posY + 600) {

		  // categories
		  if (y < _posY + 50) {
			_panelModeHover = x < _posX + 200 ? Mode.MODE_STRUCTURE : Mode.MODE_ITEM;
		  }

		  // items
		  else {
			int row = (y - _posY - 50) / 100;
			int col = (x - _posX - 10) / 80;
			int index = row * 9 + col;

			if (_panelMode == Mode.MODE_STRUCTURE) {
			  if (index + BaseItem.Type.STRUCTURE_START.ordinal() + 1 < BaseItem.Type.STRUCTURE_STOP.ordinal()) {
				_itemHover = index + BaseItem.Type.STRUCTURE_START.ordinal() + 1;
			  }
			} else if (_panelMode == Mode.MODE_ITEM) {
			  if (index + BaseItem.Type.ITEM_START.ordinal() + 1 < BaseItem.Type.ITEM_STOP.ordinal()) {
				_itemHover = index + BaseItem.Type.ITEM_START.ordinal() + 1;
			  }
			}
		  }
		  return true;
		}
	  }

	  else if (isOnTile(x, y)) {
		_isTileActive = true;
		return true;
	  }

	  return false;
	}

	public boolean	mousePress(Mouse.Button button, int x, int y) {
	  if (_isOpen) {
		return true;
	  }
	  else if (isOnTile(x, y)) {
		return true;
	  }
	  return false;
	}

	public boolean	mouseRelease(Mouse.Button button, int x, int y) {

	  // Panel open
	  if (_isOpen && x > _posX && x < _posX + 800 && y > _posY && y < _posY + 600) {
		Log.info("UI Engineering: select item #" + _itemHover);

		if (y < _posY + 50) {
		  _panelMode = _panelModeHover;
		}

		if (_itemHover != -1) {
		  _interaction.selectBuildItem(BaseItem.getTypeIndex(_itemHover));
		  _isOpen = false;
		  onMouseMove(x, y);
		}

		return true;
	  }

	  // On tile
	  else if (isOnTile(x, y)) {
		_isOpen = !_isOpen;
		_isTileActive = true;
		return true;
	  }

	  return false;
	}


}
