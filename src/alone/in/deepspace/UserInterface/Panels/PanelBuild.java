package alone.in.deepspace.UserInterface.Panels;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Mouse;

import alone.in.deepspace.Character.ServiceManager;
import alone.in.deepspace.Engine.ui.OnClickListener;
import alone.in.deepspace.Engine.ui.ButtonView;
import alone.in.deepspace.Engine.ui.TextView;
import alone.in.deepspace.Engine.ui.View;
import alone.in.deepspace.Managers.SpriteManager;
import alone.in.deepspace.UserInterface.OnFocusListener;
import alone.in.deepspace.UserInterface.UserInteraction;
import alone.in.deepspace.UserInterface.UserSubInterface;
import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.Utils.Log;
import alone.in.deepspace.World.BaseItem;
import alone.in.deepspace.World.ItemInfo;

public class PanelBuild extends UserSubInterface {

	private static final Color 	COLOR_YELLOW = new Color(236, 201, 37);
	private static int 			FRAME_WIDTH = Constant.PANEL_WIDTH;
	private static int 			FRAME_HEIGHT = Constant.PANEL_HEIGHT;

	Mode				_panelMode;
	Mode				_panelModeHover;
	ItemInfo			_itemHover;

	public enum Mode {
		NONE,
		BUILD_STRUCTURE,
		BUILD_ITEM,
		REMOVE_STRUCTURE,
		REMOVE_ITEM
	};

	private Map<ItemInfo, ButtonView> 	_icons;
	private TextView 					_lbStructure;
	protected ItemInfo 					_currentSelected;
	protected Mode 						_mode;

	public PanelBuild(RenderWindow app, int tileIndex, UserInteraction interaction) throws IOException {
		super(app, tileIndex, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 0), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));

		setBackgroundColor(new Color(200, 200, 50, 140));

		_icons = new HashMap<ItemInfo, ButtonView>();
		_panelMode = Mode.BUILD_STRUCTURE;
		_panelModeHover = Mode.BUILD_STRUCTURE;
		_mode = Mode.NONE;

//		_lbStructure = new TextView(new Vector2f(140, 32));
//		_lbStructure.setString("Structure");
//		_lbStructure.setCharacterSize(20);
//		_lbStructure.setPosition(new Vector2f(20, 20));
//		addView(_lbStructure);
//
//		TextView lbQuarter = new TextView(new Vector2f(140, 32));
//		lbQuarter.setString("Quarter");
//		lbQuarter.setCharacterSize(20);
//		lbQuarter.setPosition(new Vector2f(20, 270));
//		addView(lbQuarter);
//
//		TextView lbEngineering = new TextView(new Vector2f(140, 32));
//		lbEngineering.setString("Engineering");
//		lbEngineering.setCharacterSize(20);
//		lbEngineering.setPosition(new Vector2f(20, 520));
//		addView(lbEngineering);
//
//		TextView lbSickbay = new TextView(new Vector2f(140, 32));
//		lbSickbay.setString("Sickbay");
//		lbSickbay.setCharacterSize(20);
//		lbSickbay.setPosition(new Vector2f(20, 670));
//		addView(lbSickbay);
//
//		TextView lbCommon = new TextView(new Vector2f(140, 32));
//		lbCommon.setString("Common");
//		lbCommon.setCharacterSize(20);
//		lbCommon.setPosition(new Vector2f(20, 920));
//		addView(lbCommon);

		drawPanel();
	}

	@Override
	public void onRefresh(RenderWindow app) {
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

			// TODO
			int i = 0;
			for (ItemInfo info: ServiceManager.getData().items) {
				drawIcon(0, ++i, info);
			}
			
//			// Structure
//			drawIcon(0, 0, -2);
//			drawIcon(0, 1, BaseItem.Type.STRUCTURE_ROOM.ordinal());
//			drawIcon(0, 2, BaseItem.Type.STRUCTURE_DOOR.ordinal());
//			drawIcon(0, 3, BaseItem.Type.STRUCTURE_FLOOR.ordinal());
//			drawIcon(0, 4, BaseItem.Type.STRUCTURE_HULL.ordinal());
//			drawIcon(0, 5, BaseItem.Type.STRUCTURE_WALL.ordinal());
//			drawIcon(0, 6, BaseItem.Type.STRUCTURE_WINDOW.ordinal());
//			drawIcon(0, 7, BaseItem.Type.STRUCTURE_GREENHOUSE.ordinal());
//
//			// Quarter
//			drawIcon(250, 0, -3);
//			drawIcon(250, 1, BaseItem.Type.QUARTER_BED.ordinal());
//			drawIcon(250, 2, BaseItem.Type.QUARTER_BEDSIDE_TABLE.ordinal());
//			drawIcon(250, 3, BaseItem.Type.QUARTER_CHAIR.ordinal());
//			drawIcon(250, 4, BaseItem.Type.QUARTER_CHEST.ordinal());
//			drawIcon(250, 5, BaseItem.Type.QUARTER_DESK.ordinal());
//			drawIcon(250, 6, BaseItem.Type.QUARTER_WARDROBE.ordinal());
//
//			// Engineering
//			drawIcon(500, 0, BaseItem.Type.ENGINE_CONTROL_CENTER.ordinal());
//			drawIcon(500, 1, BaseItem.Type.ENGINE_REACTION_CHAMBER.ordinal());
//			drawIcon(500, 2, BaseItem.Type.ENVIRONMENT_O2_RECYCLER.ordinal());
//			drawIcon(500, 3, BaseItem.Type.ENVIRONMENT_TEMPERATURE_REGULATION.ordinal());
//
//			// Special
//			drawIcon(650, 0, BaseItem.Type.SCIENCE_ZYGOTE.ordinal());
//			drawIcon(650, 1, BaseItem.Type.SCIENCE_ROBOT_MAKER.ordinal());
//
//			// Tactical
//			drawIcon(750, 0, BaseItem.Type.TACTICAL_PHASER.ordinal());
//			drawIcon(750, 1, BaseItem.Type.TACTICAL_SHIELD_GRID.ordinal());
//
//			// Common
//			drawIcon(900, 0, BaseItem.Type.BAR_PUB.ordinal());
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

	void	drawIcon(int offset, int index, final ItemInfo info) throws IOException {
		ButtonView icon = _icons.get(info);
		if (icon == null) {
			
			// TODO
//			if (type < 0) {
//				icon = new ButtonView(new Vector2f(62, 80), "remove");
//				icon.setIcon(SpriteManager.getInstance().getBullet(3));
//			} else {
				icon = new ButtonView(new Vector2f(62, 80), info.label);
				icon.setIcon(SpriteManager.getInstance().getIcon(info));
//			}
			icon.setPosition(20 + (index % 4) * 80, 60 + offset + (int)(index / 4) * 100);
			icon.setBackgroundColor(info.equals(_itemHover) ? Color.WHITE : COLOR_YELLOW);
			icon.setOnFocusListener(new OnFocusListener() {
				@Override
				public void onEnter(View view) {
					view.setBackgroundColor(Color.CYAN);
				}

				@Override
				public void onExit(View view) {
					view.setBackgroundColor(info.equals(_currentSelected) ? Color.RED : COLOR_YELLOW);
				}
			});
			icon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					for (ButtonView icon: _icons.values()) {
						icon.setBackgroundColor(COLOR_YELLOW);
					}
// TODO
					//					if (type == -2) {
//						_mode = Mode.REMOVE_STRUCTURE;
//					} else if (type == -3) {
//						_mode = Mode.REMOVE_ITEM;
//					} else {
//						_mode = Mode.BUILD_ITEM;
//						_currentSelected = BaseItem.getTypeIndex(type);
//					}
					setSelectedItem(info);

					((ButtonView) view).setBackgroundColor(Color.RED);
				}
			});
			addView(icon);

			_icons.put(info, icon);
		}
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

	public boolean	checkKey(Keyboard.Key key) {
		super.checkKey(key);

		if (isOpen()) {
			switch (key) {
			case S:
				_panelMode = Mode.BUILD_STRUCTURE;
				return true;
			case I:
				_panelMode = Mode.BUILD_ITEM;
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
		_panelModeHover = Mode.NONE;

		if (isOpen()) {
			_itemHover = null;

			if (x > _posX && x < _posX + 800 && y > _posY && y < _posY + 600) {

				// categories
				if (y < _posY + 50) {
					_panelModeHover = x < _posX + 200 ? Mode.BUILD_STRUCTURE : Mode.BUILD_ITEM;
				}

				// items
				else {
					int row = (y - _posY - 50) / 100;
					int col = (x - _posX - 10) / 80;
					int index = row * 9 + col;

					// TODO
					_itemHover = ServiceManager.getData().items.get(0);
//					if (_panelMode == Mode.BUILD_STRUCTURE) {
//						if (index + BaseItem.Type.STRUCTURE_START.ordinal() + 1 < BaseItem.Type.STRUCTURE_STOP.ordinal()) {
//							_itemHover = index + BaseItem.Type.STRUCTURE_START.ordinal() + 1;
//						}
//					} else if (_panelMode == Mode.BUILD_ITEM) {
//						if (index + BaseItem.Type.ITEM_START.ordinal() + 1 < BaseItem.Type.ITEM_STOP.ordinal()) {
//							_itemHover = index + BaseItem.Type.ITEM_START.ordinal() + 1;
//						}
//					}
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

	public boolean	catchClick(int x, int y) {
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

			if (_itemHover != null) {
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

	public ItemInfo getSelectedItem() {
		return _currentSelected;
	}

	public void setSelectedItem(ItemInfo info) {
		_mode = info == null ? Mode.NONE : Mode.BUILD_ITEM;
		_currentSelected = info;
	}

	public Mode getMode() {
		return _mode;
	}


}