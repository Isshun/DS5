package alone.in.DeepSpace.UserInterface;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Mouse;

import alone.in.DeepSpace.Models.BaseItem;
import alone.in.DeepSpace.Models.BaseItem.Type;
import alone.in.DeepSpace.UserInterface.Utils.OnClickListener;
import alone.in.DeepSpace.UserInterface.Utils.UIIcon;
import alone.in.DeepSpace.UserInterface.Utils.UIText;
import alone.in.DeepSpace.UserInterface.Utils.UIView;
import alone.in.DeepSpace.Utils.Constant;
import alone.in.DeepSpace.Utils.Log;

public class UserInterfaceEngineering extends UserSubInterface {

	private static final Color COLOR_YELLOW = new Color(236, 201, 37);
	private static int 	FONT_SIZE		= 16;
	private static int 	LINE_HEIGHT		= 24;
	private static int 	TITLE_SIZE		= FONT_SIZE + 8;

	private static int 	FRAME_WIDTH = Constant.PANEL_WIDTH;
	private static int 	FRAME_HEIGHT = Constant.PANEL_HEIGHT;
	
	Mode				_panelMode;
	Mode				_panelModeHover;
	int					_itemHover;

	public enum Mode {
		MODE_NONE,
		MODE_STRUCTURE,
		MODE_ITEM
	};

	private UserInteraction 	_interaction;
	private Map<Integer, UIIcon> _icons;
	private UIText _lbStructure;
	protected Type _currentSelected;

	UserInterfaceEngineering(RenderWindow app, int tileIndex, UserInteraction interaction) throws IOException {
		super(app, tileIndex, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 0), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));
		
		setBackgroundColor(new Color(255, 255, 0, 40));
		  
		_icons = new HashMap<Integer, UIIcon>();
		_panelMode = Mode.MODE_STRUCTURE;
		_panelModeHover = Mode.MODE_STRUCTURE;
		_itemHover = -1;
		_interaction = interaction;
		
		_lbStructure = new UIText(new Vector2f(140, 32));
		_lbStructure.setString("Structure");
		_lbStructure.setCharacterSize(20);
		_lbStructure.setPosition(new Vector2f(20, 20));
		addView(_lbStructure);

		UIText lbQuarter = new UIText(new Vector2f(140, 32));
		lbQuarter.setString("Quarter");
		lbQuarter.setCharacterSize(20);
		lbQuarter.setPosition(new Vector2f(20, 270));
		addView(lbQuarter);

		UIText lbEngineering = new UIText(new Vector2f(140, 32));
		lbEngineering.setString("Engineering");
		lbEngineering.setCharacterSize(20);
		lbEngineering.setPosition(new Vector2f(20, 520));
		addView(lbEngineering);

		drawPanel();
	}

	@Override
	public void onRefresh() {
	}

	protected void	drawPanel() {

//	  Text text = new Text();
//	  text.setFont(SpriteManager.getInstance().getFont());
//
//	  // Header structure
//	  text.setString("Structure");
//	  text.setCharacterSize(TITLE_SIZE);
//	  text.setPosition(_posX + Constant.UI_PADDING, _posY + Constant.UI_PADDING);
//	  if (_panelModeHover == Mode.MODE_STRUCTURE) {
//		text.setStyle(Text.UNDERLINED);
//		text.setColor(Color.YELLOW);
//		_app.draw(text);
//	  }
//	  text.setColor(Color.WHITE);
//	  text.setStyle(Text.REGULAR);
//	  _app.draw(text);
//	  text.setString(_panelMode == Mode.MODE_STRUCTURE ? "Structure" : "S");
//	  text.setStyle(Text.UNDERLINED);
//	  text.setColor(Color.YELLOW);
//	  _app.draw(text);
//
//	  // Header item
//	  text.setString("Items");
//	  text.setCharacterSize(TITLE_SIZE);
//	  text.setPosition(_posX + 200 + Constant.UI_PADDING, _posY + Constant.UI_PADDING);
//	  if (_panelModeHover == Mode.MODE_ITEM) {
//		text.setStyle(Text.UNDERLINED);
//		text.setColor(Color.YELLOW);
//		_app.draw(text);
//	  }
//	  text.setColor(Color.WHITE);
//	  text.setStyle(Text.REGULAR);
//	  _app.draw(text);
//	  text.setString(_panelMode == Mode.MODE_ITEM ? "Items" : "I");
//	  text.setStyle(Text.UNDERLINED);
//	  text.setColor(Color.YELLOW);
//	  _app.draw(text);

		try {
			drawIcon(0, 0, BaseItem.Type.STRUCTURE_ROOM.ordinal());
			drawIcon(0, 1, BaseItem.Type.STRUCTURE_DOOR.ordinal());
			drawIcon(0, 2, BaseItem.Type.STRUCTURE_FLOOR.ordinal());
			drawIcon(0, 3, BaseItem.Type.STRUCTURE_HULL.ordinal());
			drawIcon(0, 4, BaseItem.Type.STRUCTURE_WALL.ordinal());
			drawIcon(0, 5, BaseItem.Type.STRUCTURE_WINDOW.ordinal());

			drawIcon(250, 0, BaseItem.Type.QUARTER_BED.ordinal());
			drawIcon(250, 1, BaseItem.Type.QUARTER_BEDSIDE_TABLE.ordinal());
			drawIcon(250, 2, BaseItem.Type.QUARTER_CHAIR.ordinal());
			drawIcon(250, 3, BaseItem.Type.QUARTER_CHEST.ordinal());
			drawIcon(250, 4, BaseItem.Type.QUARTER_DESK.ordinal());
			drawIcon(250, 5, BaseItem.Type.QUARTER_WARDROBE.ordinal());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
//	  try {
//		  if (_panelMode == Mode.MODE_STRUCTURE) {
//			for (int index = 0, i = BaseItem.Type.STRUCTURE_START.ordinal() + 1; i < BaseItem.Type.STRUCTURE_STOP.ordinal(); index++, i++) {
//				drawIcon(index, i);
//			}
//		  } else if (_panelMode == Mode.MODE_ITEM) {
//			for (int index = 0, i = BaseItem.Type.ITEM_START.ordinal() + 1; i < BaseItem.Type.ITEM_STOP.ordinal(); index++, i++) {
//			  drawIcon(index, i);
//			}
//		  }
//	  } catch (IOException e) {
//		  // TODO Auto-generated catch block
//		  e.printStackTrace();
//	  }
	}

	void	drawIcon(int offset, int index, final int type) throws IOException {
		
		UIIcon icon = _icons.get(type);
		if (icon == null) {
			icon = new UIIcon(new Vector2f(62, 80), type);
			icon.setPosition(20 + (index % 4) * 80, 60 + offset + (int)(index / 4) * 100);
			icon.setBackground(_itemHover == type ? Color.WHITE : COLOR_YELLOW);
			icon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(UIView view) {
					if (_currentSelected != null) {
						UIIcon current = _icons.get(_currentSelected.ordinal());
						current.setBackground(COLOR_YELLOW);
					}
					_currentSelected = BaseItem.getTypeIndex(type);
					((UIIcon) view).setBackground(Color.RED);
				}
			});
			addView(icon);

//			  shape.setPosition(posX + 20, posY + 60);
//			  _app.draw(shape);
			  // shape.setSize(Vector2f(54, 54));
			  // shape.setFillColor(Color(0, 80, 140));

//			  Texture texture = new Texture();
//			  texture.loadFromFile((new File("res/bg_none.png")).toPath());
//			  texture.setRepeated(true);
//			  Sprite sprite = new Sprite();
//			  sprite.setTexture(texture);
//			  sprite.setTextureRect(new IntRect(0, 0, 56, 56));
//			  sprite.setPosition(posX + 23, posY + 63);
//			  _app.draw(sprite);
//				  
//			  // Icon
//				  
			  
			 _icons.put(type, icon);
		}
		
		//icon.refresh(_app);
	}

	void	drawTile() {
//	  super.drawTile(new Color(249, 195, 63));
//	 
//	  Text text = new Text();
//	  text.setFont(SpriteManager.getInstance().getFont());
//	  text.setCharacterSize(FONT_SIZE);
//
//	  {
//		int matter = ResourceManager.getInstance().getMatter();
//
//		text.setString("Matter: " + matter);
//
//		if (matter == 0)
//		  text.setColor(Color.RED);
//		else if (matter < 20)
//		  text.setColor(Color.YELLOW);
//	    text.setPosition(_posTileX + Constant.UI_PADDING, _posTileY + TITLE_SIZE + Constant.UI_PADDING);
//	    _app.draw(text);
//		text.setColor(Color.WHITE);
//	  }
//
//	  text.setString("Engineering");
//	  text.setCharacterSize(TITLE_SIZE);
//	  text.setPosition(_posTileX + Constant.UI_PADDING, _posTileY + Constant.UI_PADDING);
//	  _app.draw(text);
//	  text.setString("E");
//	  text.setStyle(Text.UNDERLINED);
//	  text.setColor(Color.YELLOW);
//	  _app.draw(text);
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
		  setVisible(false);
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
	  if (_isVisible && x > _posX) {
		return true;
	  }
	  else if (isOnTile(x, y)) {
		return true;
	  }
	  return false;
	}

	public boolean	mouseRelease(Mouse.Button button, int x, int y) {

	  // Panel open
	  if (_isVisible && x > _posX && x < _posX + 800 && y > _posY && y < _posY + 600) {
		Log.info("UI Engineering: select item #" + _itemHover);

		if (y < _posY + 50) {
		  _panelMode = _panelModeHover;
		}

		if (_itemHover != -1) {
		  //_interaction.selectBuildItem(BaseItem.getTypeIndex(_itemHover));
//		  _isOpen = false;
		  onMouseMove(x, y);
		}

		return true;
	  }

	  // On tile
	  else if (isOnTile(x, y)) {
		_isVisible = !_isVisible;
		_isTileActive = true;
		return true;
	  }

	  return false;
	}

	public Type getSelectedItem() {
		return _currentSelected;
	}


}
