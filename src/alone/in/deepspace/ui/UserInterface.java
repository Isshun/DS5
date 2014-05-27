package alone.in.deepspace.ui;

import java.io.File;
import java.io.IOException;

import org.jsfml.graphics.Font;
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
import alone.in.deepspace.model.Character;
import alone.in.deepspace.model.Room;
import alone.in.deepspace.model.WorldArea;
import alone.in.deepspace.ui.panel.PanelBase;
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
import alone.in.deepspace.ui.panel.PanelShortcut;
import alone.in.deepspace.ui.panel.PanelSystem;
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
	private UserInterfaceScience		_uiScience;
	private UserInterfaceSecurity		_uiSecurity;
	private CharacterManager        	_characteres;
	private PanelCharacter  			_panelCharacter;
	private PanelPlan					_panelPlan;
	private PanelInfo					_panelInfo;
	private PanelCrew					_panelCrew;
	private PanelBuild					_panelBuild;
	private PanelDebug					_panelDebug;
	private UserInterfaceMenuOperation	_uiBase;
	private Font 						_font;
	private PanelBase 					_panelBase;
	private PanelSystem 				_panelSystem;
	private PanelShortcut 				_panelShortcut;
	private PanelJobs					_panelJobs;
	private PanelResource 				_panelResource;
	private UserInterfaceMessage 		_panelMessage;
	private PanelRoom 					_panelRoom;
	private UIMessage 					_message;
	private PanelDebugItem 				_panelDebugItems;
	private Mode 						_mode;
	private ContextualMenu 				_menu;
	private Game _game;
	private int _used;

	public enum Mode {
		BASE,
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
		NONE
	}

	public void onCreate(Game game, RenderWindow app, Viewport viewport) throws IOException {
		_game = game;
		_viewport = viewport;
		_app = app;
		_characteres = ServiceManager.getCharacterManager();
		_keyLeftPressed = false;
		_keyRightPressed = false;
		_font = new Font();
		_font.loadFromFile((new File("res/fonts/xolonium_regular.otf")).toPath());

		_panelBase = new PanelBase(app);
		_panelCharacter = new PanelCharacter(app);
		_panelInfo = new PanelInfo(app);
		_panelDebug = new PanelDebug(app);
		_panelDebug.setUI(this);
		_panelDebugItems = new PanelDebugItem(app);
		_panelPlan = new PanelPlan(app);
		_panelSystem = new PanelSystem(app);
		_panelSystem.setVisible(true);
		_panelShortcut = new PanelShortcut(app, this);
		_panelShortcut.setVisible(true);
		_panelResource = new PanelResource(app);
		_panelResource.setVisible(true);
		_panelMessage = new UserInterfaceMessage(app);
		_panelMessage.setVisible(true);
		_panelRoom = new PanelRoom(app);

		_interaction = new UserInteraction(app, viewport);
		_panelBuild = new PanelBuild(app, 3, _interaction);
		_uiScience = new UserInterfaceScience(app, 2);
		_uiSecurity = new UserInterfaceSecurity(app, 4);
		_panelCrew = new PanelCrew(app, 0);
		_panelCrew.setUI(this);
		_uiBase = new UserInterfaceMenuOperation(app, 1);
		_panelJobs = new PanelJobs(app);
		_panelMessage.setStart(0);

		setMode(Mode.NONE);
	}

	public void	onMouseMove(int x, int y) {
		_keyMovePosX = getRelativePosX(x);
		_keyMovePosY = getRelativePosY(y);

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

		if (_panelBuild.catchClick(x, y)) {
			_keyLeftPressed = false;
			return;
		}

		if (_panelCrew.catchClick(x, y)) {
			_keyLeftPressed = false;
			return;
		}

		if (_uiScience.catchClick(x, y)) {
			_keyLeftPressed = false;
			return;
		}

		if (_uiSecurity.catchClick(x, y)) {
			_keyLeftPressed = false;
			return;
		}

		if (_uiBase.catchClick(x, y)) {
			_keyLeftPressed = false;
			return;
		}

		//		if (_panelPlan.catchClick(x, y)) {
		//			_keyLeftPressed = false;
		//			return;
		//		}

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

	public void setMode(Mode mode) {
		_mode = _mode != mode ? mode : Mode.NONE;
		_menu = null;

		if (_mode != Mode.CHARACTER) 	_panelCharacter.setVisible(false);
		if (_mode != Mode.INFO) 		_panelInfo.setVisible(false);
		if (_mode != Mode.PLAN) 		_panelPlan.setVisible(false);
		if (_mode != Mode.DEBUG) 		_panelDebug.setVisible(false);
		if (_mode != Mode.DEBUGITEMS)	_panelDebugItems.setVisible(false);
		if (_mode != Mode.BASE) 		_panelBase.setVisible(false);
		if (_mode != Mode.BUILD) 		_panelBuild.setVisible(false);
		if (_mode != Mode.CREW) 		_panelCrew.setVisible(false);
		if (_mode != Mode.BASE) 		_uiBase.setVisible(false);
		if (_mode != Mode.SCIENCE) 		_uiScience.setVisible(false);
		if (_mode != Mode.SECURITY)		_uiSecurity.setVisible(false);
		if (_mode != Mode.JOBS)			_panelJobs.setVisible(false);
		if (_mode != Mode.ROOM)			_panelRoom.setVisible(false);

		switch (_mode) {
		case BUILD: 		_panelBuild.toogle(); break;
		case INFO: 			_panelInfo.toogle(); break;
		case DEBUG: 		_panelDebug.toogle(); break;
		case DEBUGITEMS:	_panelDebugItems.toogle(); break;
		case PLAN: 			_panelPlan.toogle(); break;
		case CHARACTER: 	_panelCharacter.toogle(); break;
		case BASE: 			_panelBase.toogle(); break;
		case JOBS: 			_panelJobs.toogle(); break;
		case CREW: 			_panelCrew.toogle(); break;
		case ROOM: 			_panelRoom.toogle(); break;
		case SCIENCE:		_uiScience.toogle(); break;
		case SECURITY:		_uiSecurity.toogle(); break;
		case NONE: break;
		}
	}

	public void	onMouseWheel(int delta, int x, int y) {
		_viewport.setScale(delta);

		_keyMovePosX = getRelativePosX(x);
		_keyMovePosY = getRelativePosY(y);
	}

	public void onDraw(int frame, int update, int renderTime) {
		_panelCharacter.draw(_app, null);
		_panelBase.draw(_app, null);
		_panelInfo.draw(_app, null);
		_panelPlan.draw(_app, null);
		_panelDebug.draw(_app, null);
		_panelDebugItems.draw(_app, null);
		_panelSystem.draw(_app, null);
		_panelShortcut.draw(_app, null);
		_panelResource.draw(_app, null);
		_panelRoom.draw(_app, null);
		_panelMessage.setFrame(frame);
		_panelCrew.draw(_app, null);
		_uiScience.draw(_app, null);
		_uiSecurity.draw(_app, null);
		_uiBase.draw(_app, null);
		_panelBuild.draw(_app, null);
		_panelJobs.draw(_app, null);
		
//		int mb = 1024 * 1024;
//        Runtime runtime = Runtime.getRuntime();
//        int used = (int) ((runtime.totalMemory() - runtime.freeMemory()) / mb);
//        int total = (int) (runtime.totalMemory() / mb);
//        _used = (_used * 7 + used) / 8;
//        System.out.println("Heap: " + String.valueOf(_used) + " / " + String.valueOf(total) + " Mo");

		if (_panelBuild.getMode() != PanelBuild.Mode.NONE || _panelPlan.getMode() != PanelPlan.Mode.NONE) {
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

		if (_panelBuild.checkKey(event.asKeyEvent().key)) {
			return true;
		}

		if (_panelCrew.checkKey(event.asKeyEvent().key)) {
			return true;
		}

		if (_uiBase.checkKey(event.asKeyEvent().key)) {
			return true;
		}

		if (_uiSecurity.checkKey(event.asKeyEvent().key)) {
			return true;
		}

		if (_uiScience.checkKey(event.asKeyEvent().key)) {
			return true;
		}

		if (_interaction.getMode() != UserInteraction.Mode.NONE) {
			if (event.type == Event.Type.KEY_RELEASED && event.asKeyEvent().key == Keyboard.Key.ESCAPE) {
				//		  _interaction.cancel();
				return true;
			}
		}

		
		if (event.asKeyEvent().key == Keyboard.Key.ESCAPE || event.asKeyEvent().key == Keyboard.Key.BACKSPACE) {
			if (_mode != Mode.NONE) {
				setMode(Mode.NONE);
			} else {
				_game.setRunning(!_game.isRunning());
			}
			return true;
		}
		
		if (event.asKeyEvent().key == Keyboard.Key.TAB) {
			if ((event.type == Event.Type.KEY_RELEASED)) {
				if (_panelCharacter.getCharacter() != null) {
					_panelCharacter.setCharacter(_characteres.getNext(_panelCharacter.getCharacter()));
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
				setMode(Mode.DEBUG);
			} else {
				setMode(_panelCharacter.getCharacter() != null ? Mode.CHARACTER : Mode.INFO);
			}
			// 	ServiceManager.getWorldMap().dump();
		}	
		else if (event.asKeyEvent().key == Keyboard.Key.C) {
			setMode(Mode.CREW);
		}
		else if (event.asKeyEvent().key == Keyboard.Key.I) {
			_panelDebugItems.toogle();
		}
		else if (event.asKeyEvent().key == Keyboard.Key.BACKSPACE) {
			_panelDebugItems.reset();
		}
		else if (event.asKeyEvent().key == Keyboard.Key.E || event.asKeyEvent().key == Keyboard.Key.B) {
			setMode(Mode.BUILD);
		}
		else if (event.asKeyEvent().key == Keyboard.Key.O) {
			_uiBase.toogleTile();
		}
		else if (event.asKeyEvent().key == Keyboard.Key.R) {
			setMode(Mode.ROOM);
		}
		else if (event.asKeyEvent().key == Keyboard.Key.P) {
			setMode(Mode.PLAN);
		}
		else if (event.asKeyEvent().key == Keyboard.Key.J) {
			setMode(Mode.JOBS);
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
		if (_panelPlan.getMode() == PanelPlan.Mode.GATHER) {
			_interaction.planGather(
					Math.min(_keyPressPosX, _keyMovePosX),
					Math.min(_keyPressPosY, _keyMovePosY),
					Math.max(_keyPressPosX, _keyMovePosX),
					Math.max(_keyPressPosY, _keyMovePosY));
			return;
		}

		// Plan mining
		if (_panelPlan.getMode() == PanelPlan.Mode.MINING) {
			_interaction.planMining(
					Math.min(_keyPressPosX, _keyMovePosX),
					Math.min(_keyPressPosY, _keyMovePosY),
					Math.max(_keyPressPosX, _keyMovePosX),
					Math.max(_keyPressPosY, _keyMovePosY));
			return;
		}

		// Plan dump
		if (_panelPlan.getMode() == PanelPlan.Mode.DUMP) {
			_interaction.planDump(
					Math.min(_keyPressPosX, _keyMovePosX),
					Math.min(_keyPressPosY, _keyMovePosY),
					Math.max(_keyPressPosX, _keyMovePosX),
					Math.max(_keyPressPosY, _keyMovePosY));
			return;
		}

		// Remove item
		if (_panelBuild.getMode() == PanelBuild.Mode.REMOVE_ITEM) {
			_interaction.removeItem(
					Math.min(_keyPressPosX, _keyMovePosX),
					Math.min(_keyPressPosY, _keyMovePosY),
					Math.max(_keyPressPosX, _keyMovePosX),
					Math.max(_keyPressPosY, _keyMovePosY));
			return;
		}

		// Remove structure
		if (_panelBuild.getMode() == PanelBuild.Mode.REMOVE_STRUCTURE) {
			_interaction.removeStructure(
					Math.min(_keyPressPosX, _keyMovePosX),
					Math.min(_keyPressPosY, _keyMovePosY),
					Math.max(_keyPressPosX, _keyMovePosX),
					Math.max(_keyPressPosY, _keyMovePosY));
			return;
		}

		// Build item
		if (_panelBuild.getSelectedItem() != null) {
			_interaction.build(_panelBuild.getSelectedItem(),
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


		_panelCharacter.setCharacter(null);
		setMode(Mode.BASE);

		// Select character
		if (_interaction.getMode() == UserInteraction.Mode.NONE) {// && _menu.getCode() == UserInterfaceMenu.CODE_MAIN) {
//			Character c = _characteres.getCharacterAtPos(getRelativePosX(x), getRelativePosY(y));
			Character c = null;
			if (c != null) {
				setCharacter(c);
			} else {
				WorldArea a = ServiceManager.getWorldMap().getArea(getRelativePosX(x), getRelativePosY(y));
				if (a != null) {
					_panelInfo.setArea(a);
					//				if (_panelInfo.getArea() == a && _panelInfo.getItem() == null && a.getItem() != null) {
					//				  _panelInfo.setItem(a.getItem());
					//				} else {
					//				  _panelInfo.setItem(null);
					//				}
					setMode(Mode.INFO);
				}
			}
		}

	}

	public void setCharacter(Character c) {
		_panelCharacter.setCharacter(c);
		setMode(Mode.CHARACTER);
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
			_panelBuild.setSelectedItem(null);
			_panelRoom.setSelected(null);
			_panelPlan.setMode(PanelPlan.Mode.NONE);
			setMode(Mode.NONE);
		}

		_keyRightPressed = false;
	}

	public Mode getMode() {
		return _mode;
	}
}
