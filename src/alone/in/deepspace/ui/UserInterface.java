package alone.in.deepspace.ui;

import java.io.IOException;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;
import org.jsfml.window.event.Event;

import alone.in.deepspace.Game;
import alone.in.deepspace.Main;
import alone.in.deepspace.engine.Viewport;
import alone.in.deepspace.engine.ui.UIMessage;
import alone.in.deepspace.manager.CharacterManager;
import alone.in.deepspace.manager.RoomManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.manager.UIEventManager;
import alone.in.deepspace.model.BaseItem;
import alone.in.deepspace.model.ItemInfo;
import alone.in.deepspace.model.Room;
import alone.in.deepspace.model.ToolTips.ToolTip;
import alone.in.deepspace.model.WorldArea;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.ui.panel.BasePanel;
import alone.in.deepspace.ui.panel.PanelBuild;
import alone.in.deepspace.ui.panel.PanelCharacter;
import alone.in.deepspace.ui.panel.PanelCrew;
import alone.in.deepspace.ui.panel.PanelDebug;
import alone.in.deepspace.ui.panel.PanelDebugItem;
import alone.in.deepspace.ui.panel.PanelInfo;
import alone.in.deepspace.ui.panel.PanelJobs;
import alone.in.deepspace.ui.panel.PanelPlan;
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
	private PanelSystem 				_panelSystem;
	private PanelShortcut 				_panelShortcut;
	private PanelResource 				_panelResource;
	private PanelRoom 					_panelRoom;
	private UIMessage 					_message;
	private PanelDebugItem 				_panelDebugItems;
	private Mode 						_mode;
	private ContextualMenu 				_menu;
	private Game 						_game;
	private boolean 					_mouseOnMap;
	private PanelTooltip 				_panelTooltip;
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
			new PanelShortcut(Mode.NONE)
	};

	private PanelBuild _panelBuild;
	private PanelPlan _panelPlan;
	private PanelCharacter _panelCharacter;
	private PanelInfo _panelInfo;
	
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
		
		_panelSystem = new PanelSystem(app);
		_panelSystem.setVisible(true);
		_panelResource = new PanelResource(app);
		_panelResource.setVisible(true);
		
		// TODO
		for (BasePanel panel: _panels) {
			if (Mode.BUILD.equals(panel.getMode())) {
				_panelBuild = (PanelBuild)panel;
			}
			else if (Mode.CREW.equals(panel.getMode())) {
			}
			else if (Mode.PLAN.equals(panel.getMode())) {
				_panelPlan = (PanelPlan)panel;
			}
			else if (Mode.CHARACTER.equals(panel.getMode())) {
				_panelCharacter = (PanelCharacter)panel;
			}
			else if (Mode.INFO.equals(panel.getMode())) {
				_panelInfo = (PanelInfo)panel;
			}
			else if (Mode.ROOM.equals(panel.getMode())) {
				_panelRoom = (PanelRoom)panel;
			}
			else if (Mode.TOOLTIP.equals(panel.getMode())) {
				_panelTooltip = (PanelTooltip)panel;
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
			panel.catchClick(x, y);
			_keyLeftPressed = false;
			return;
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

	public int getRelativePosX(int x) { return (int) ((x - Constant.UI_WIDTH - _viewport.getPosX()) / _viewport.getScale() / Constant.TILE_WIDTH); }
	public int getRelativePosY(int y) { return (int) ((y - Constant.UI_HEIGHT - _viewport.getPosY()) / _viewport.getScale() / Constant.TILE_HEIGHT); }
	public int getRelativePosXMax(int x) { return (int) ((x - Constant.UI_WIDTH - _viewport.getPosX()) / _viewport.getMinScale() / Constant.TILE_WIDTH); }
	public int getRelativePosYMax(int y) { return (int) ((y - Constant.UI_HEIGHT - _viewport.getPosY()) / _viewport.getMinScale() / Constant.TILE_HEIGHT); }
	public int getRelativePosXMin(int x) { return (int) ((x - Constant.UI_WIDTH - _viewport.getPosX()) / _viewport.getMaxScale() / Constant.TILE_WIDTH); }
	public int getRelativePosYMin(int y) { return (int) ((y - Constant.UI_HEIGHT - _viewport.getPosY()) / _viewport.getMaxScale() / Constant.TILE_HEIGHT); }

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
			} else {
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
			if (_panelBuild.getPanelMode() != PanelBuild.PanelMode.NONE || _panelPlan.getPanelMode() != PanelPlan.PanelMode.NONE) {
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

	public boolean checkKeyboard(Event	event, int frame, int lastInput) {

		if (event.asKeyEvent().key == Keyboard.Key.ADD) {
			if (Main.getUpdateInterval() - 40 > 0) {
				Main.setUpdateInterval(Main.getUpdateInterval() - 40);
			} else {
				Main.setUpdateInterval(0);
			}
		}

		if (event.asKeyEvent().key == Keyboard.Key.SUBTRACT) {
			Main.setUpdateInterval(Main.getUpdateInterval() + 40);
		}

		if (event.asKeyEvent().key == Keyboard.Key.ESCAPE) {
			setMode(Mode.NONE);
		}

		if (event.asKeyEvent().key == Keyboard.Key.BACKSPACE) {

		}


		if (_interaction.getMode() != UserInteraction.Mode.NONE) {
			if (event.type == Event.Type.KEY_RELEASED && event.asKeyEvent().key == Keyboard.Key.ESCAPE) {
				//		  _interaction.cancel();
				return true;
			}
		}


		if (event.asKeyEvent().key == Keyboard.Key.ESCAPE || event.asKeyEvent().key == Keyboard.Key.BACKSPACE) {
			setMode(Mode.NONE);
			//			if (_mode != Mode.NONE) {
			//				setMode(Mode.NONE);
			//			} else {
			//				//_game.setRunning(!_game.isRunning());
			//			}
			return true;
		}

		if (event.asKeyEvent().key == Keyboard.Key.TAB) {
			if ((event.type == Event.Type.KEY_RELEASED)) {
				if (_panelCharacter.getCharacter() != null) {
					_panelCharacter.select(_characteres.getNext(_panelCharacter.getCharacter()));
				}
			}
			return true;
		}

		if (event.asKeyEvent().key == Keyboard.Key.PAGEUP) {
			if ((event.type == Event.Type.KEY_RELEASED)) {
				ServiceManager.getWorldMap().upFloor();
			}
			return true;
		}

		if (event.asKeyEvent().key == Keyboard.Key.SPACE) {
			if ((event.type == Event.Type.KEY_RELEASED)) {
				_game.setRunning(_game.isRunning());
			}
			return true;
		}

		if (event.asKeyEvent().key == Keyboard.Key.PAGEDOWN) {
			if ((event.type == Event.Type.KEY_RELEASED)) {
				ServiceManager.getWorldMap().downFloor();
			}
			return true;
		}

		if (event.asKeyEvent().key == Keyboard.Key.D) {
			Settings.getInstance().setDebug(!Settings.getInstance().isDebug());
			if (Settings.getInstance().isDebug()) {
				toogleMode(Mode.DEBUG);
			} else {
				toogleMode(_panelCharacter.getCharacter() != null ? Mode.CHARACTER : Mode.INFO);
			}
			// 	ServiceManager.getWorldMap().dump();
		}	
		else if (event.asKeyEvent().key == Keyboard.Key.C) {
			toogleMode(Mode.CREW);
		}
		else if (event.asKeyEvent().key == Keyboard.Key.I) {
			_panelDebugItems.toogle();
		}
		else if (event.asKeyEvent().key == Keyboard.Key.BACKSPACE) {
			_panelDebugItems.reset();
		}
		else if (event.asKeyEvent().key == Keyboard.Key.E || event.asKeyEvent().key == Keyboard.Key.B) {
			toogleMode(Mode.BUILD);
		}
		else if (event.asKeyEvent().key == Keyboard.Key.R) {
			toogleMode(Mode.ROOM);
		}
		else if (event.asKeyEvent().key == Keyboard.Key.S) {
			toogleMode(Mode.STATS);
		}
		else if (event.asKeyEvent().key == Keyboard.Key.P) {
			toogleMode(Mode.PLAN);
		}
		else if (event.asKeyEvent().key == Keyboard.Key.J) {
			toogleMode(Mode.JOBS);
		}
		//	  else if (event.asKeyEvent().key == Keyboard.Key.I) {
		//		ServiceManager.getWorldMap().dumpItems();
		//	  }
		else if (event.asKeyEvent().key == Keyboard.Key.UP) {
			if (frame > lastInput + Constant.KEY_REPEAT_INTERVAL && (event.type == Event.Type.KEY_PRESSED)) {
				_viewport.update(0, Constant.MOVE_VIEW_OFFSET);
				lastInput = frame;
				// _cursor._y--;
			}
		}
		else if (event.asKeyEvent().key == Keyboard.Key.DOWN) {
			if (frame > lastInput + Constant.KEY_REPEAT_INTERVAL && (event.type == Event.Type.KEY_PRESSED)) {
				_viewport.update(0, -Constant.MOVE_VIEW_OFFSET);
				lastInput = frame;
				// _cursor._y++;
			}
		}
		else if (event.asKeyEvent().key == Keyboard.Key.RIGHT) {
			if (frame > lastInput + Constant.KEY_REPEAT_INTERVAL && (event.type == Event.Type.KEY_PRESSED)) {
				_viewport.update(-Constant.MOVE_VIEW_OFFSET, 0);
				lastInput = frame;
				// _cursor._x++;
			}
		}
		else if (event.asKeyEvent().key == Keyboard.Key.LEFT) {
			if (frame > lastInput + Constant.KEY_REPEAT_INTERVAL && (event.type == Event.Type.KEY_PRESSED)) {
				_viewport.update(Constant.MOVE_VIEW_OFFSET, 0);
				lastInput = frame;
				// _cursor._x--;
			}
		}

		return false;
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
			BaseItem item = area.getItem();
			BaseItem structure = area.getStructure();

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
		if (_panelPlan.getPanelMode() == PanelPlan.PanelMode.GATHER) {
			_interaction.planGather(
					Math.min(_keyPressPosX, _keyMovePosX),
					Math.min(_keyPressPosY, _keyMovePosY),
					Math.max(_keyPressPosX, _keyMovePosX),
					Math.max(_keyPressPosY, _keyMovePosY));
			return;
		}

		// Plan mining
		if (_panelPlan.getPanelMode() == PanelPlan.PanelMode.MINING) {
			_interaction.planMining(
					Math.min(_keyPressPosX, _keyMovePosX),
					Math.min(_keyPressPosY, _keyMovePosY),
					Math.max(_keyPressPosX, _keyMovePosX),
					Math.max(_keyPressPosY, _keyMovePosY));
			return;
		}

		// Plan dump
		if (_panelPlan.getPanelMode() == PanelPlan.PanelMode.DUMP) {
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
			if (c != null && c != _panelCharacter.getCharacter()) {
				select(c);
			}
			else  {
				_panelCharacter.select(null);

				WorldArea a = ServiceManager.getWorldMap().getArea(getRelativePosX(x), getRelativePosY(y));
				if (a != null) {
					_panelInfo.select(a);
					//				if (_panelInfo.getArea() == a && _panelInfo.getItem() == null && a.getItem() != null) {
					//				  _panelInfo.setItem(a.getItem());
					//				} else {
					//				  _panelInfo.setItem(null);
					//				}
					toogleMode(Mode.INFO);
				}
			}
		}

	}

	public void setCharacter(Character c) {
		_panelCharacter.select(c);
		toogleMode(Mode.CHARACTER);
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
			_panelPlan.setMode(PanelPlan.PanelMode.NONE);
			toogleMode(Mode.NONE);
		}

		_keyRightPressed = false;
	}

	public Mode getMode() {
		return _mode;
	}

	public void select(ItemInfo itemInfo) {
		setMode(Mode.INFO);
		_panelInfo.select(itemInfo);
	}

	public void select(Character character) {
		setMode(Mode.CHARACTER);
		_panelCharacter.select(character);
	}

	public void select(BaseItem item) {
		setMode(Mode.INFO);
		_panelInfo.select(item);
	}

	public void select(ToolTip toolTip) {
		_panelTooltip.select(toolTip);
		setMode(Mode.TOOLTIP);
	}

	public Game getGame() {
		return _game;
	}
}
