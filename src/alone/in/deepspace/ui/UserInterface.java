package alone.in.deepspace.ui;

import java.io.IOException;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard.Key;

import alone.in.deepspace.Game;
import alone.in.deepspace.Main;
import alone.in.deepspace.engine.Viewport;
import alone.in.deepspace.engine.ui.UIEventManager;
import alone.in.deepspace.engine.ui.UIMessage;
import alone.in.deepspace.manager.CharacterManager;
import alone.in.deepspace.manager.RoomManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.Room;
import alone.in.deepspace.model.ToolTips.ToolTip;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.item.ItemBase;
import alone.in.deepspace.model.item.ItemInfo;
import alone.in.deepspace.model.item.WorldArea;
import alone.in.deepspace.ui.panel.BasePanel;
import alone.in.deepspace.ui.panel.PanelBuild;
import alone.in.deepspace.ui.panel.PanelCharacter;
import alone.in.deepspace.ui.panel.PanelCrew;
import alone.in.deepspace.ui.panel.PanelDebug;
import alone.in.deepspace.ui.panel.PanelDebugItem;
import alone.in.deepspace.ui.panel.PanelInfo;
import alone.in.deepspace.ui.panel.PanelJobs;
import alone.in.deepspace.ui.panel.PanelPlan;
import alone.in.deepspace.ui.panel.PanelPlan.PanelMode;
import alone.in.deepspace.ui.panel.PanelResource;
import alone.in.deepspace.ui.panel.PanelRoom;
import alone.in.deepspace.ui.panel.PanelScience;
import alone.in.deepspace.ui.panel.PanelSecurity;
import alone.in.deepspace.ui.panel.PanelShortcut;
import alone.in.deepspace.ui.panel.PanelStats;
import alone.in.deepspace.ui.panel.PanelSystem;
import alone.in.deepspace.ui.panel.PanelTooltip;
import alone.in.deepspace.util.Constant;
import alone.in.deepspace.util.Settings;

public class UserInterface {

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
	private PanelShortcut 				_panelShortcut;
	private PanelRoom 					_panelRoom;
	private UIMessage 					_message;
	private Mode 						_mode;
	private ContextualMenu 				_menu;
	private Game 						_game;
	private boolean 					_mouseOnMap;
	private BasePanel 					_currentPanel;

	private	BasePanel[]			_panels = new BasePanel[] {
			new PanelCharacter(Mode.CHARACTER),
			new PanelInfo(Mode.INFO),
			new PanelDebug(Mode.DEBUG),
			new PanelDebugItem(Mode.DEBUGITEMS),
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

	private PanelBuild _panelBuild;
	private Character 	_selectedCharacter;
	private ItemBase 	_selectedItem;
	private ToolTip 	_selectedTooltip;
	private ItemInfo 	_selectedItemInfo;
	private WorldArea 	_selectedArea;
	private PanelMode 	_selectedPlan;
	
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
		_characteres = ServiceManager.getCharacterManager();
		_keyLeftPressed = false;
		_keyRightPressed = false;

		for (BasePanel panel: _panels) {
			panel.init(app, this, viewport);
		}
		
		// TODO
		for (BasePanel panel: _panels) {
			if (Mode.BUILD.equals(panel.getMode())) {
				_panelBuild = (PanelBuild)panel;
			}
			else if (Mode.ROOM.equals(panel.getMode())) {
				_panelRoom = (PanelRoom)panel;
			}
		}

		_interaction = new UserInteraction(app, viewport);

		_currentPanel = _panelShortcut;

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

	public int 			getRelativePosX(int x) { return (int) ((x - Constant.UI_WIDTH - _viewport.getPosX()) / _viewport.getScale() / Constant.TILE_WIDTH); }
	public int 			getRelativePosY(int y) { return (int) ((y - Constant.UI_HEIGHT - _viewport.getPosY()) / _viewport.getScale() / Constant.TILE_HEIGHT); }
	public int 			getRelativePosXMax(int x) { return (int) ((x - Constant.UI_WIDTH - _viewport.getPosX()) / _viewport.getMinScale() / Constant.TILE_WIDTH); }
	public int 			getRelativePosYMax(int y) { return (int) ((y - Constant.UI_HEIGHT - _viewport.getPosY()) / _viewport.getMinScale() / Constant.TILE_HEIGHT); }
	public int 			getRelativePosXMin(int x) { return (int) ((x - Constant.UI_WIDTH - _viewport.getPosX()) / _viewport.getMaxScale() / Constant.TILE_WIDTH); }
	public int 			getRelativePosYMin(int y) { return (int) ((y - Constant.UI_HEIGHT - _viewport.getPosY()) / _viewport.getMaxScale() / Constant.TILE_HEIGHT); }
	public Character 	getSelectedCharacter() { return _selectedCharacter; }
	public WorldArea	getSelectedArea() { return _selectedArea; }
	public ItemBase 	getSelectedItem() { return _selectedItem; }
	public ItemInfo		getSelectedItemInfo() { return _selectedItemInfo; }
	public ToolTip		getSelectedTooltip() { return _selectedTooltip; }
	
	public void toogleMode(Mode mode) {
		setMode(_mode != mode ? mode : Mode.NONE);
	}

	private void setMode(Mode mode) {
		_mode = mode;
		_menu = null;

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
		for (BasePanel panel: _panels) {
			panel.refresh(update);
		}
	}

	public void onDraw(int frame, int update, int renderTime) {
		for (BasePanel panel: _panels) {
			panel.draw(_app, null);
		}

		if (_mouseOnMap) {
			if (_panelBuild.getPanelMode() != PanelBuild.PanelMode.NONE || _selectedPlan != null) {
				if (_keyLeftPressed) {
					_interaction.drawCursor(Math.min(_keyPressPosX, _keyMovePosX),
							Math.min(_keyPressPosY, _keyMovePosY),
							Math.max(_keyPressPosX, _keyMovePosX),
							Math.max(_keyPressPosY, _keyMovePosY));
				} else {
					_interaction.drawCursor(Math.min(_keyMovePosX, _keyMovePosX),
							Math.min(_keyMovePosY, _keyMovePosY),
							Math.max(_keyMovePosX, _keyMovePosX),
							Math.max(_keyMovePosY, _keyMovePosY));
				}
			}
		}

		Room.Type roomType = _panelRoom.getSelectedRoom();
		if (roomType != null) {
			int fromX = _keyLeftPressed ? Math.min(_keyPressPosX, _keyMovePosX) : _keyMovePosX;
			int fromY = _keyLeftPressed ? Math.min(_keyPressPosY, _keyMovePosY) : _keyMovePosY;
			int toX = _keyLeftPressed ? Math.max(_keyPressPosX, _keyMovePosX) : _keyMovePosX;
			int toY = _keyLeftPressed ? Math.max(_keyPressPosY, _keyMovePosY) : _keyMovePosY;
			_interaction.drawCursor(fromX, fromY, toX, toY);
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

	public boolean checkKeyboard(Key key, int frame, int lastInput) {

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

		//	  else if (event.asKeyEvent().key == Keyboard.Key.I) {
		//		ServiceManager.getWorldMap().dumpItems();
		//	  }
//		else if (key == Keyboard.Key.UP) {
//			if (frame > lastInput + Constant.KEY_REPEAT_INTERVAL && (key.type == Event.Type.KEY_PRESSED)) {
//				_viewport.update(0, Constant.MOVE_VIEW_OFFSET);
//				lastInput = frame;
//				// _cursor._y--;
//			}
//		}
//		else if (key == Keyboard.Key.DOWN) {
//			if (frame > lastInput + Constant.KEY_REPEAT_INTERVAL && (key.type == Event.Type.KEY_PRESSED)) {
//				_viewport.update(0, -Constant.MOVE_VIEW_OFFSET);
//				lastInput = frame;
//				// _cursor._y++;
//			}
//		}
//		else if (key == Keyboard.Key.RIGHT) {
//			if (frame > lastInput + Constant.KEY_REPEAT_INTERVAL && (key.type == Event.Type.KEY_PRESSED)) {
//				_viewport.update(-Constant.MOVE_VIEW_OFFSET, 0);
//				lastInput = frame;
//				// _cursor._x++;
//			}
//		}
//		else if (key == Keyboard.Key.LEFT) {
//			if (frame > lastInput + Constant.KEY_REPEAT_INTERVAL && (key.type == Event.Type.KEY_PRESSED)) {
//				_viewport.update(Constant.MOVE_VIEW_OFFSET, 0);
//				lastInput = frame;
//				// _cursor._x--;
//			}
//		}
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
				ServiceManager.getWorldRenderer().invalidate(item.getX(), item.getY());
			}
			else if (structure != null) {
				structure.nextMode();
				ServiceManager.getWorldRenderer().invalidate(structure.getX(), structure.getY());
			}
		}
	}

	public void onLeftClick(int x, int y) {
		if (_keyLeftPressed == false) {
			return;
		}
		_keyLeftPressed = false;

		// Plan gather
		if (_selectedPlan == PanelPlan.PanelMode.GATHER) {
			_interaction.planGather(
					Math.min(_keyPressPosX, _keyMovePosX),
					Math.min(_keyPressPosY, _keyMovePosY),
					Math.max(_keyPressPosX, _keyMovePosX),
					Math.max(_keyPressPosY, _keyMovePosY));
			return;
		}

		// Plan mining
		if (_selectedPlan == PanelPlan.PanelMode.MINING) {
			_interaction.planMining(
					Math.min(_keyPressPosX, _keyMovePosX),
					Math.min(_keyPressPosY, _keyMovePosY),
					Math.max(_keyPressPosX, _keyMovePosX),
					Math.max(_keyPressPosY, _keyMovePosY));
			return;
		}

		// Plan dump
		if (_selectedPlan == PanelPlan.PanelMode.DUMP) {
			_interaction.planDump(
					Math.min(_keyPressPosX, _keyMovePosX),
					Math.min(_keyPressPosY, _keyMovePosY),
					Math.max(_keyPressPosX, _keyMovePosX),
					Math.max(_keyPressPosY, _keyMovePosY));
			return;
		}

		// Remove item
		if (_panelBuild.getPanelMode() == PanelBuild.PanelMode.REMOVE_ITEM) {
			_interaction.removeItem(
					Math.min(_keyPressPosX, _keyMovePosX),
					Math.min(_keyPressPosY, _keyMovePosY),
					Math.max(_keyPressPosX, _keyMovePosX),
					Math.max(_keyPressPosY, _keyMovePosY));
			return;
		}

		// Remove structure
		if (_panelBuild.getPanelMode() == PanelBuild.PanelMode.REMOVE_STRUCTURE) {
			_interaction.removeStructure(
					Math.min(_keyPressPosX, _keyMovePosX),
					Math.min(_keyPressPosY, _keyMovePosY),
					Math.max(_keyPressPosX, _keyMovePosX),
					Math.max(_keyPressPosY, _keyMovePosY));
			return;
		}

		// Build item
		if (_panelBuild.getSelectedItem() != null) {
			_interaction.planBuild(_panelBuild.getSelectedItem(),
					Math.min(_keyPressPosX, _keyMovePosX),
					Math.min(_keyPressPosY, _keyMovePosY),
					Math.max(_keyPressPosX, _keyMovePosX),
					Math.max(_keyPressPosY, _keyMovePosY));
			return;
		}

		// Set room
		Room.Type roomType = _panelRoom.getSelectedRoom();
		if (roomType != null) {
			int fromX = Math.min(_keyPressPosX, _keyMovePosX);
			int fromY = Math.min(_keyPressPosY, _keyMovePosY);
			int toX = Math.max(_keyPressPosX, _keyMovePosX);
			int toY = Math.max(_keyPressPosY, _keyMovePosY);
			if (roomType == Room.Type.NONE) {
				RoomManager.getInstance().removeRoom(fromX, fromY, toX, toY, roomType);
			} else {
				RoomManager.getInstance().putRoom(_keyPressPosX, _keyPressPosX, fromX, fromY, toX, toY, roomType, null);
			}

			return;
		}

		if (_mode == Mode.ROOM) {
			final Room room = RoomManager.getInstance().get(getRelativePosX(x), getRelativePosY(y));
			_panelRoom.setRoom(room);
			return;
		}

		// Click is catch by panel
		if (_currentPanel != null && _currentPanel.catchClick(x, y)) {
			return;
		}

		// Select character
		if (_interaction.getMode() == UserInteraction.Mode.NONE) {// && _menu.getCode() == UserInterfaceMenu.CODE_MAIN) {
			Character c = _characteres.getCharacterAtPos(getRelativePosX(x), getRelativePosY(y));
			if (c != null && c != _selectedCharacter) {
				select(c);
			}
			else  {
				_selectedCharacter = null;

				WorldArea a = ServiceManager.getWorldMap().getArea(getRelativePosX(x), getRelativePosY(y));
				if (a != null) {
					if (a.getItem() != null) { select(a.getItem()); }
					else if (a.getStructure() != null) { select(a.getStructure()); }
					else if (a.getRessource() != null) { select(a.getRessource()); }
					else { select(a); }
				}
			}
		}

	}

	public void onRightClick(int x, int y) {

		// Move viewport
		if (_keyRightPressed && Math.abs(_mouseRightPressX - x) > 5 || Math.abs(_mouseRightPressY - y) > 5) {
			_viewport.update(x, y);
		}

		else if (_mode == Mode.ROOM && _panelRoom.getSelectedRoom() == Room.Type.NONE) {
			final Room room = RoomManager.getInstance().get(getRelativePosX(x), getRelativePosY(y));
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

			_panelBuild.select(null);
			_panelRoom.select(null);
			_selectedPlan = PanelPlan.PanelMode.NONE;
			toogleMode(Mode.NONE);
		}

		_keyRightPressed = false;
	}

	public Mode getMode() {
		return _mode;
	}

	private void cleanSelect() {
		_selectedPlan = null;
		_selectedArea = null;
		_selectedCharacter = null;
		_selectedItem = null;
		_selectedItemInfo = null;
		_selectedTooltip = null;
	}

	public void select(ItemInfo itemInfo) {
		cleanSelect();
		setMode(Mode.INFO);
		_selectedItemInfo = itemInfo;
	}

	public void select(Character character) {
		cleanSelect();
		setMode(Mode.CHARACTER);
		_selectedCharacter = character;
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

	public Game getGame() {
		return _game;
	}
}
