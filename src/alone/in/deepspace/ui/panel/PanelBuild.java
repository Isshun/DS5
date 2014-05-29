package alone.in.deepspace.ui.panel;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Mouse;

import alone.in.deepspace.engine.ui.ButtonView;
import alone.in.deepspace.engine.ui.ColorView;
import alone.in.deepspace.engine.ui.OnClickListener;
import alone.in.deepspace.engine.ui.OnFocusListener;
import alone.in.deepspace.engine.ui.TextView;
import alone.in.deepspace.engine.ui.View;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.manager.SpriteManager;
import alone.in.deepspace.model.CategoryInfo;
import alone.in.deepspace.model.ItemInfo;
import alone.in.deepspace.ui.UserInteraction;
import alone.in.deepspace.ui.UserSubInterface;
import alone.in.deepspace.util.Constant;
import alone.in.deepspace.util.Log;

public class PanelBuild extends UserSubInterface {

	private static final Color 	COLOR_YELLOW = new Color(70, 100, 100);
	private static int 			FRAME_WIDTH = Constant.PANEL_WIDTH;
	private static int 			FRAME_HEIGHT = Constant.PANEL_HEIGHT;

	public enum Mode {
		NONE,
		BUILD_STRUCTURE,
		BUILD_ITEM,
		REMOVE_STRUCTURE,
		REMOVE_ITEM
	};

	private Mode						_panelMode;
	private Mode						_panelModeHover;
	private ItemInfo					_itemHover;
	private Map<ItemInfo, ButtonView> 	_icons;
	protected ItemInfo 					_currentSelected;
	protected Mode 						_mode;
	private int							_startY;

	public PanelBuild(RenderWindow app, int tileIndex, UserInteraction interaction) throws IOException {
		super(app, tileIndex, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 32), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT - 32));

		setBackgroundColor(new Color(22, 35, 35));

		_icons = new HashMap<ItemInfo, ButtonView>();
		_panelMode = Mode.BUILD_STRUCTURE;
		_panelModeHover = Mode.BUILD_STRUCTURE;
		_mode = Mode.NONE;

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

	protected void	drawPanel() {
		clearAllViews();
		_icons.clear();

		TextView lbUp = new TextView(new Vector2f(140, 32));
		lbUp.setString("UP");
		lbUp.setCharacterSize(20);
		lbUp.setPosition(new Vector2f(20, 20));
		lbUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				_startY += 400;
				drawPanel();
			}
		});
		addView(lbUp);

		TextView lbDown = new TextView(new Vector2f(140, 32));
		lbDown.setString("DOWN");
		lbDown.setCharacterSize(20);
		lbDown.setPosition(new Vector2f(200, 20));
		lbDown.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				_startY -= 400;
				drawPanel();
			}
		});
		addView(lbDown);

		try {

			// TODO
			int posY = _startY + 64;
			List<CategoryInfo> categories = ServiceManager.getData().categories;
			for (CategoryInfo category: categories) {
				if (posY > 42) {
					TextView lbQuarter = new TextView(new Vector2f(140, 32));
					lbQuarter.setString(category.label);
					lbQuarter.setCharacterSize(20);
					lbQuarter.setPosition(new Vector2f(20, posY + 8));
					addView(lbQuarter);
				}
				posY += 44;

				int i = -1;
				for (ItemInfo info: category.items) {
					if (info.isUserItem || info.isStructure) {
						drawIcon(posY, ++i, info, posY > 42);
					}
				}
				posY += ((int)(i / 4) + 1) * 100;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		View layer = new ColorView(new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));
//		layer.setBackgroundColor(new Color(58, 215, 248, 100));
//		addView(layer);
	}

	void	drawIcon(int offset, int index, final ItemInfo info, boolean visible) throws IOException {
		ButtonView icon = _icons.get(info);
		if (icon == null) {

			// TODO
			//			if (type < 0) {
			//				icon = new ButtonView(new Vector2f(62, 80), "remove");
			//				icon.setIcon(SpriteManager.getInstance().getBullet(3));
			//			} else {
			icon = new ButtonView(new Vector2f(62, 80));
			icon.setString(info.label.length() > 7 ? info.label.substring(0, 7) : info.label);
			icon.setTextPadding(60, 0);
			icon.setIcon(SpriteManager.getInstance().getIcon(info));
			icon.setPadding(4, 4, 4, 4);
			icon.setPosition(20 + (index % 4) * 80, offset + (int)(index / 4) * 100);

			icon.setBackgroundColor(new Color(29, 85, 96, 100));
			//icon.setBorderColor(new Color(161, 255, 255));
			
			icon.setBorderSize(2);
			icon.setOnFocusListener(new OnFocusListener() {
				@Override
				public void onEnter(View view) {
					view.setBackgroundColor(new Color(29, 85, 96, 180));
				}

				@Override
				public void onExit(View view) {
					view.setBackgroundColor(info.equals(_currentSelected) ? new Color(29, 85, 96) : new Color(29, 85, 96, 100));
				}
			});
			icon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					for (ButtonView icon: _icons.values()) {
						icon.setBackgroundColor(new Color(29, 85, 96, 100));
						icon.setBorderColor(null);
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

					view.setBackgroundColor(new Color(29, 85, 96));
					view.setBorderColor(new Color(161, 255, 255));
				}
			});
			addView(icon);

			_icons.put(info, icon);
		}
		icon.resetPos();
		icon.setVisible(visible);
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
