package alone.in.deepspace.ui;

import java.io.IOException;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Clock;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.Mouse.Button;
import org.jsfml.window.event.Event;
import org.jsfml.window.event.MouseButtonEvent;

import alone.in.deepspace.Game;
import alone.in.deepspace.Main;
import alone.in.deepspace.engine.Viewport;
import alone.in.deepspace.engine.renderer.MainRenderer;
import alone.in.deepspace.engine.ui.UIEventManager;
import alone.in.deepspace.engine.ui.UIMessage;
import alone.in.deepspace.manager.CharacterManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.ToolTips.ToolTip;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.item.ItemBase;
import alone.in.deepspace.model.item.ItemInfo;
import alone.in.deepspace.model.item.WorldArea;
import alone.in.deepspace.model.item.WorldResource;
import alone.in.deepspace.model.room.Room;
import alone.in.deepspace.model.room.Room.Type;
import alone.in.deepspace.ui.panel.BasePanel;
import alone.in.deepspace.ui.panel.PanelBuild;
import alone.in.deepspace.ui.panel.PanelCharacter;
import alone.in.deepspace.ui.panel.PanelCrew;
import alone.in.deepspace.ui.panel.PanelDebug;
import alone.in.deepspace.ui.panel.PanelInfo;
import alone.in.deepspace.ui.panel.PanelJobs;
import alone.in.deepspace.ui.panel.PanelPlan;
import alone.in.deepspace.ui.panel.PanelPlan.PanelMode;
import alone.in.deepspace.ui.panel.PanelResource;
import alone.in.deepspace.ui.panel.PanelRoom;
import alone.in.deepspace.ui.panel.PanelRoom.PanelRoomListener;
import alone.in.deepspace.ui.panel.PanelScience;
import alone.in.deepspace.ui.panel.PanelSecurity;
import alone.in.deepspace.ui.panel.PanelShortcut;
import alone.in.deepspace.ui.panel.PanelStats;
import alone.in.deepspace.ui.panel.PanelSystem;
import alone.in.deepspace.ui.panel.PanelTooltip;
import alone.in.deepspace.util.Constant;
import alone.in.deepspace.util.Settings;

public class UserInterface implements PanelRoomListener {
	enum Action {
		REMOVE_ITEM, REMOVE_STRUCTURE, BUILD_ITEM, BUILD_ROOM
	}

	private static UserInterface		_self;
	private RenderWindow				_app;
	private Viewport					_viewport;
	private boolean						_keyLeftPressed;
	private boolean						_keyRightPressed;
	private int							_mouseRightPressX;
	private int							_mouseRightPressY;
	private int							_keyPressPosX;
	private int							_keyPressPosY;
	private int							_keyMovePosX;
	private int							_keyMovePosY;
	private UserInteraction				_interaction;
	private CharacterManager        	_characteres;
	private UIMessage 					_message;
	private Mode 						_mode;
	private ContextualMenu 				_menu;
	private Game 						_game;
	private boolean 					_mouseOnMap;
	private BasePanel 					_currentPanel;
	private	Action						_action;

	private long 						_lastLeftClick;
	private int 						_lastInput;

	private	BasePanel[]			_panels = new BasePanel[] {
			new PanelCharacter(Mode.CHARACTER),
			new PanelInfo(Mode.INFO),
			new PanelDebug(Mode.DEBUG),
			new PanelPlan(Mode.PLAN),
			new PanelRoom(Mode.ROOM),
			new PanelTooltip(Mode.TOOLTIP),
			new PanelBuild(Mode.BUILD),
			new PanelScience(Mode.SCIENCE),
			new PanelSecurity(Mode.SECURITY),
			new PanelCrew(Mode.CREW),
			new PanelJobs(Mode.JOBS),
			new PanelStats(Mode.STATS),
			new PanelShortcut(Mode.NONE),
			new PanelSystem(),
			new PanelResource()
	};

	private Character 		_selectedCharacter;
	private ItemBase 		_selectedItem;
	private WorldResource	_selectedResource;
	private ToolTip 		_selectedTooltip;
	private ItemInfo 		_selectedItemInfo;
	private WorldArea 		_selectedArea;
	private PanelMode 		_selectedPlan;
	private Type 			_selectedRoomType;
	private Room _selectedRoom;
	private int _update;
	private UserInterfaceCursor	_cursor;
	
	public enum Mode {
		INFO,
		DEBUG,
		BUILD,
		CREW,
		JOBS,
		CHARACTER,
		SCIENCE,
		SECURITY,
		ROOM,
		PLAN,
		DEBUGITEMS,
		NONE, TOOLTIP, STATS
	}

	public void onCreate(Game game, RenderWindow app, Viewport viewport) throws IOException {
		_game = game;
		_viewport = viewport;
		_app = app;
		_characteres = Game.getCharacterManager();
		_keyLeftPressed = false;
		_keyRightPressed = false;
		_cursor = new UserInterfaceCursor();

		for (BasePanel panel: _panels) {
			panel.init(app, this, viewport);
		}
		
		_interaction = new UserInteraction(app, viewport);

		setMode(Mode.NONE);
	}

	public void	onMouseMove(int x, int y) {
		_keyMovePosX = getRelativePosX(x);
		_keyMovePosY = getRelativePosY(y);

		// TODO
		_mouseOnMap = x < 1500;

		// right button pressed
		if (_keyRightPressed) {
			_viewport.update(x, y);
			if (_menu != null && _menu.isVisible()) {
				//_menu.move(_viewport.getPosX(), _viewport.getPosY());
				_menu.setViewPortPosition(_viewport.getPosX(), _viewport.getPosY());
			}
		}
	}

	public void	onLeftPress(int x, int y) {
		if (UIEventManager.getInstance().has(x, y)) {
			return;
		}

		for (BasePanel panel: _panels) {
			if (panel.catchClick(x, y)) {
				_keyLeftPressed = false;
				return;
			}
		}

		_keyLeftPressed = true;
		_keyMovePosX = _keyPressPosX = getRelativePosX(x);
		_keyMovePosY = _keyPressPosY = getRelativePosY(y);
	}

	public void	onRightPress(int x, int y) {
		if (UIEventManager.getInstance().has(x, y)) {
			return;
		}

		_keyRightPressed = true;
		_mouseRightPressX = x;
		_mouseRightPressY = y;
		_viewport.startMove(x, y);
	}

	public int 				getRelativePosX(int x) { return (int) ((x - Constant.UI_WIDTH - _viewport.getPosX()) / _viewport.getScale() / Constant.TILE_WIDTH); }
	public int 				getRelativePosY(int y) { return (int) ((y - Constant.UI_HEIGHT - _viewport.getPosY()) / _viewport.getScale() / Constant.TILE_HEIGHT); }
	public int 				getRelativePosXMax(int x) { return (int) ((x - Constant.UI_WIDTH - _viewport.getPosX()) / _viewport.getMinScale() / Constant.TILE_WIDTH); }
	public int 				getRelativePosYMax(int y) { return (int) ((y - Constant.UI_HEIGHT - _viewport.getPosY()) / _viewport.getMinScale() / Constant.TILE_HEIGHT); }
	public int 				getRelativePosXMin(int x) { return (int) ((x - Constant.UI_WIDTH - _viewport.getPosX()) / _viewport.getMaxScale() / Constant.TILE_WIDTH); }
	public int 				getRelativePosYMin(int y) { return (int) ((y - Constant.UI_HEIGHT - _viewport.getPosY()) / _viewport.getMaxScale() / Constant.TILE_HEIGHT); }
	public Character 		getSelectedCharacter() { return _selectedCharacter; }
	public WorldArea		getSelectedArea() { return _selectedArea; }
	public ItemBase 		getSelectedItem() { return _selectedItem; }
	public WorldResource	getSelectedResource() { return _selectedResource; }
	public ItemInfo			getSelectedItemInfo() { return _selectedItemInfo; }
	public ToolTip			getSelectedTooltip() { return _selectedTooltip; }
	
	public void toogleMode(Mode mode) {
		setMode(_mode != mode ? mode : Mode.NONE);
	}

	private void setMode(Mode mode) {
		MainRenderer.getInstance().setMode(mode);

		_mode = mode;
		_menu = null;
		
		if (mode == Mode.NONE) {
			cleanSelect();
		}

		for (BasePanel panel: _panels) {
			if (_mode.equals(panel.getMode())) {
				_currentPanel = panel;
				panel.setVisible(true);
			} else if (panel.isAlwaysVisible() == false) {
				panel.setVisible(false);
			}
		}

		if (_currentPanel != null) {
			_currentPanel.setVisible(true);
		}
	}

	public void	onMouseWheel(int delta, int x, int y) {
		_viewport.setScale(delta);

		_keyMovePosX = getRelativePosX(x);
		_keyMovePosY = getRelativePosY(y);
	}

	public void onRefresh(int update) {
		_update = update;
		for (BasePanel panel: _panels) {
			panel.refresh(update);
		}
	}

	public void onDraw(int update, int renderTime) {
		for (BasePanel panel: _panels) {
			panel.draw(_app, null);
		}

		if (_mouseOnMap) {
			if (_selectedRoomType != null || _selectedPlan != null) {
				if (_keyLeftPressed) {
					_cursor.draw(_app, _viewport.getRender(), Math.min(_keyPressPosX, _keyMovePosX),
							Math.min(_keyPressPosY, _keyMovePosY),
							Math.max(_keyPressPosX, _keyMovePosX),
							Math.max(_keyPressPosY, _keyMovePosY));
				} else {
					_cursor.draw(_app, _viewport.getRender(), Math.min(_keyMovePosX, _keyMovePosX),
							Math.min(_keyMovePosY, _keyMovePosY),
							Math.max(_keyMovePosX, _keyMovePosX),
							Math.max(_keyMovePosY, _keyMovePosY));
				}
			}
		}

		if (_message != null) {
			_app.draw(_message.border);
			_app.draw(_message.shape);
			_app.draw(_message.text);
			if (--_message.frame < 0) {
				_message = null;
			}

		}

		if (_menu != null) {
			_menu.draw(_app, null);
		}
	}

	public boolean checkKeyboard(Key key, int lastInput) {

		for (BasePanel panel: _panels) {
			if (panel.checkKey(key)) {
				return true;
			}
		}
		
		switch (key) {

		case ADD:
			if (Main.getUpdateInterval() - 40 > 0) {
				Main.setUpdateInterval(Main.getUpdateInterval() - 40);
			} else {
				Main.setUpdateInterval(0);
			}
			return true;

		case SUBTRACT:
			Main.setUpdateInterval(Main.getUpdateInterval() + 40);
			return true;
			
		case ESCAPE:
			setMode(Mode.NONE);
			return true;

		case BACKSPACE:
			return true;

		case TAB:
			if (_selectedCharacter != null) {
				_selectedCharacter = _characteres.getNext(_selectedCharacter);
			}
			return true;

		case PAGEUP:
			ServiceManager.getWorldMap().upFloor();
			return true;

		case PAGEDOWN:
			ServiceManager.getWorldMap().downFloor();
			return true;

		case SPACE:
			_game.setRunning(!_game.isRunning());
			return true;

		case D:
			Settings.getInstance().setDebug(!Settings.getInstance().isDebug());
			if (Settings.getInstance().isDebug()) {
				toogleMode(Mode.DEBUG);
			}
			return true;

		case C:
			toogleMode(Mode.CREW);
			return true;

		case B:
			toogleMode(Mode.BUILD);
			return true;

		case R:
			toogleMode(Mode.ROOM);
			return true;

		case S:
			toogleMode(Mode.STATS);
			return true;

		case P:
			toogleMode(Mode.PLAN);
			return true;

		case O:
			toogleMode(Mode.JOBS);
			return true;
			
		default:
			return false;
		}
	}

	public static UserInterface getInstance() {
		if (_self == null) {
			_self = new UserInterface();
		}
		return _self;
	}

	public void displayMessage(String msg) {
		//		_message = new UIMessage(msg, _mouseRealPosX, _mouseRealPosY);
		_message = new UIMessage(msg, 10, 30);
	}

	public void displayMessage(String msg, int x, int y) {
		_message = new UIMessage(msg, _viewport.getRealPosX(x) + 20, _viewport.getRealPosY(y) + 12);
	}

	public void onDoubleClick(int x, int y) {
		_keyLeftPressed = false;

		WorldArea area = ServiceManager.getWorldMap().getArea(getRelativePosX(x), getRelativePosY(y));
		if (area != null) {
			ItemBase item = area.getItem();
			ItemBase structure = area.getStructure();

			if (item != null) {
				item.nextMode();
				MainRenderer.getInstance().invalidate(item.getX(), item.getY());
			}
			else if (structure != null) {
				structure.nextMode();
				MainRenderer.getInstance().invalidate(structure.getX(), structure.getY());
			}
		}
	}

	public boolean onLeftClick(int x, int y) {
		if (_keyLeftPressed == false) {
			return false;
		}
		_keyLeftPressed = false;

		// Set plan
		if (_selectedPlan != null) {
			_interaction.plan(_selectedPlan,
					Math.min(_keyPressPosX, _keyMovePosX),
					Math.min(_keyPressPosY, _keyMovePosY),
					Math.max(_keyPressPosX, _keyMovePosX),
					Math.max(_keyPressPosY, _keyMovePosY));
			return true;
		}

		// Set room
		if (_mode == Mode.ROOM) {
			if (_keyPressPosX == _keyMovePosX && _keyPressPosY == _keyMovePosY) {
				final Room room = Game.getRoomManager().get(getRelativePosX(x), getRelativePosY(y));
				select(room);
				return true;
			}

			if (_selectedRoomType != null) {
				_interaction.roomType(_selectedRoomType,
						_keyPressPosX, _keyPressPosY,
						Math.min(_keyPressPosX, _keyMovePosX),
						Math.min(_keyPressPosY, _keyMovePosY),
						Math.max(_keyPressPosX, _keyMovePosX),
						Math.max(_keyPressPosY, _keyMovePosY));
			}
			return true;
		}
		
		// Remove item
		if (_action == Action.REMOVE_ITEM) {
			_interaction.removeItem(
					Math.min(_keyPressPosX, _keyMovePosX),
					Math.min(_keyPressPosY, _keyMovePosY),
					Math.max(_keyPressPosX, _keyMovePosX),
					Math.max(_keyPressPosY, _keyMovePosY));
			return true;
		}

		// Remove structure
		if (_action == Action.REMOVE_STRUCTURE) {
			_interaction.removeStructure(
					Math.min(_keyPressPosX, _keyMovePosX),
					Math.min(_keyPressPosY, _keyMovePosY),
					Math.max(_keyPressPosX, _keyMovePosX),
					Math.max(_keyPressPosY, _keyMovePosY));
			return true;
		}

		// Build item
		if (_action == Action.BUILD_ITEM) {
			_interaction.planBuild(_selectedItemInfo,
					Math.min(_keyPressPosX, _keyMovePosX),
					Math.min(_keyPressPosY, _keyMovePosY),
					Math.max(_keyPressPosX, _keyMovePosX),
					Math.max(_keyPressPosY, _keyMovePosY));
			return true;
		}

		// Click is catch by panel
		if (_currentPanel != null && _currentPanel.catchClick(x, y)) {
			return true;
		}

		// Select character
		if (_interaction.getMode() == UserInteraction.Mode.NONE) {// && _menu.getCode() == UserInterfaceMenu.CODE_MAIN) {
			Character c = _characteres.getCharacterAtPos(getRelativePosX(x), getRelativePosY(y));
			if (c != null && c != _selectedCharacter) {
				select(c);
			}
			else  {
				if (_selectedCharacter != null) {
					_selectedCharacter.setSelected(false);
					_selectedCharacter = null;
				}

				WorldArea a = ServiceManager.getWorldMap().getArea(getRelativePosX(x), getRelativePosY(y));
				if (a != null) {
					if (a.getRessource() != null) { select(a.getRessource()); }
					else if (a.getItem() != null) { select(a.getItem()); }
					else if (a.getStructure() != null) { select(a.getStructure()); }
					else { select(a); }
				}
			}
		}

		return false;
	}

	public void onRightClick(int x, int y) {

		// Move viewport
		if (_keyRightPressed && Math.abs(_mouseRightPressX - x) > 5 || Math.abs(_mouseRightPressY - y) > 5) {
			_viewport.update(x, y);
		}

		else if (_selectedRoomType != null) {
			cleanSelect();
		}
		
		else if (_mode == Mode.ROOM && _selectedRoomType == Room.Type.NONE) {
			final Room room = Game.getRoomManager().get(getRelativePosX(x), getRelativePosY(y));
			if (room != null) {
				_menu = new RoomContextualMenu(_app, 0, new Vector2f(x, y), new Vector2f(100, 120), _viewport, room);
			} else {
				_menu = null;
			}
		}

		// Cancel selected items 
		else {
			if (_mode == Mode.CHARACTER) {
				setMode(Mode.CREW);
				return;
			}
			//			if (_panelCharacter.getCharacter() != null) {
			//				JobManager.getInstance().addMoveJob(_panelCharacter.getCharacter(), getRelativePosX(x), getRelativePosY(y));
			//				_keyRightPressed = false;
			//				return;
			//			}

			_selectedPlan = PanelPlan.PanelMode.NONE;
			toogleMode(Mode.NONE);
		}

		_keyRightPressed = false;
	}

	public Mode getMode() {
		return _mode;
	}

	// TODO
	private void cleanSelect() {
		_action = null;
		_selectedPlan = null;
		_selectedRoom = null;
		_selectedRoomType = null;
		_selectedArea = null;
		if (_selectedCharacter != null) {
			_selectedCharacter.setSelected(false);
		}
		_selectedCharacter = null;
		if (_selectedItem != null) {
			_selectedItem.setSelected(false);
		}
		_selectedItem = null;
		_selectedResource = null;
		_selectedItemInfo = null;
		_selectedTooltip = null;
	}

	public void select(ItemInfo itemInfo) {
		cleanSelect();
		setMode(Mode.INFO);
		_selectedItemInfo = itemInfo;
	}

	public void select(ItemInfo info, Mode mode) {
		if (mode == Mode.BUILD) {
			cleanSelect();
			setMode(Mode.BUILD);
			_action = Action.BUILD_ITEM;
			_selectedItemInfo = info;
		}
	}

	public void select(Character character) {
		cleanSelect();
		setMode(Mode.CHARACTER);
		_selectedCharacter = character;
		if (_selectedCharacter != null) {
			_selectedCharacter.setSelected(true);
		}
	}
	
	public void select(Room room) {
		cleanSelect();
		setMode(Mode.ROOM);
		_selectedRoom = room;
	}

	public void select(WorldResource resource) {
		cleanSelect();
		setMode(Mode.INFO);
		_selectedResource = resource;
	}

	public void select(ItemBase item) {
		cleanSelect();
		setMode(Mode.INFO);
		_selectedItem = item;
	}

	public void select(WorldArea area) {
		cleanSelect();
		setMode(Mode.INFO);
		_selectedArea = area;
	}

	public void select(ToolTip tooltip) {
		cleanSelect();
		setMode(Mode.TOOLTIP);
		_selectedTooltip = tooltip;
	}
	
	public void select(PanelPlan.PanelMode plan) {
		cleanSelect();
		setMode(Mode.PLAN);
		_selectedPlan = plan;
	}

	public void select(Room.Type roomType) {
		_selectedRoomType = roomType;
	}

	public Game getGame() {
		return _game;
	}

	public void back() {
		setMode(Mode.NONE);
	}

	public Room getSelectedRoom() {
		return _selectedRoom;
	}

	public Type getSelectedRoomType() {
		return _selectedRoomType;
	}

	public void onEvent(Event event, Clock timer) {
		if (event.type == Event.Type.MOUSE_MOVED) {
			onMouseMove(event.asMouseEvent().position.x, event.asMouseEvent().position.y);
			UIEventManager.getInstance().onMouseMove(event.asMouseEvent().position.x, event.asMouseEvent().position.y);
		}

		if (event.type == Event.Type.MOUSE_BUTTON_PRESSED || event.type == Event.Type.MOUSE_BUTTON_RELEASED) {
			MouseButtonEvent mouseButtonEvent = event.asMouseButtonEvent();
			if (mouseButtonEvent.button == Button.LEFT) {
				if (event.type == Event.Type.MOUSE_BUTTON_PRESSED) {
					onLeftPress(mouseButtonEvent.position.x, mouseButtonEvent.position.y);
				} else {
					// Is consume by EventManager
					if (UIEventManager.getInstance().leftClick(mouseButtonEvent.position.x, mouseButtonEvent.position.y)) {
						// Nothing to do !
					}
					// Is double click
					else if (_lastLeftClick + 200 > timer.getElapsedTime().asMilliseconds()) {
						onDoubleClick(mouseButtonEvent.position.x, mouseButtonEvent.position.y);
					}
					// Is simple click
					else {
						boolean use = onLeftClick(mouseButtonEvent.position.x, mouseButtonEvent.position.y);
						if (use) {
							onRefresh(_update);
						}
					}
					_lastLeftClick = timer.getElapsedTime().asMilliseconds();
				}
			} else if (mouseButtonEvent.button == Button.RIGHT) {
				if (event.type == Event.Type.MOUSE_BUTTON_PRESSED) {
					onRightPress(mouseButtonEvent.position.x, mouseButtonEvent.position.y);
				} else {
					onRightClick(mouseButtonEvent.position.x, mouseButtonEvent.position.y);
				}
			}
			//_ui.mouseRelease(event.asMouseButtonEvent().button, event.asMouseButtonEvent().position.x, event.asMouseButtonEvent().position.y);
		}

		if (event.type == Event.Type.MOUSE_WHEEL_MOVED) {
			onMouseWheel(event.asMouseWheelEvent().delta, event.asMouseWheelEvent().position.x, event.asMouseWheelEvent().position.y);
		}

		// Check key code
		if (event.type == Event.Type.KEY_RELEASED) {
			if (checkKeyboard(event.asKeyEvent().key, _lastInput)) {
				return;
			}
		}
	}

}
