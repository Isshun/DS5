package alone.in.deepspace.UserInterface;

import java.io.File;
import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;
import org.jsfml.window.event.Event;

import alone.in.deepspace.Main;
import alone.in.deepspace.UserInterface.Panels.PanelBase;
import alone.in.deepspace.UserInterface.Panels.PanelBuild;
import alone.in.deepspace.UserInterface.Panels.PanelCharacter;
import alone.in.deepspace.UserInterface.Panels.PanelCrew;
import alone.in.deepspace.UserInterface.Panels.PanelDebug;
import alone.in.deepspace.UserInterface.Panels.PanelDebugItem;
import alone.in.deepspace.UserInterface.Panels.PanelInfo;
import alone.in.deepspace.UserInterface.Panels.PanelJobs;
import alone.in.deepspace.UserInterface.Panels.PanelPlan;
import alone.in.deepspace.UserInterface.Panels.PanelResource;
import alone.in.deepspace.UserInterface.Panels.PanelRoom;
import alone.in.deepspace.UserInterface.Panels.PanelShortcut;
import alone.in.deepspace.UserInterface.Panels.PanelSystem;
import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.Utils.Settings;
import alone.in.deepspace.engine.Viewport;
import alone.in.deepspace.engine.ui.FrameLayout;
import alone.in.deepspace.engine.ui.UIMessage;
import alone.in.deepspace.manager.CharacterManager;
import alone.in.deepspace.manager.RoomManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.manager.UIEventManager;
import alone.in.deepspace.model.BaseItem;
import alone.in.deepspace.model.Character;
import alone.in.deepspace.model.Room;
import alone.in.deepspace.model.WorldArea;

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
	private boolean						_crewViewOpen;
	private UserInteraction				_interaction;
	private UserInterfaceScience		_uiScience;
	private UserInterfaceSecurity		_uiSecurity;
	private CharacterManager        	_characteres;
	private PanelCharacter  			_panelCharacter;
	private PanelPlan					_panelPlan;
	private PanelInfo					_panelInfo;
	private PanelCrew					_uiCharacter;
	private PanelBuild					_panelBuild;
	private PanelDebug					_panelDebug;
	private UserInterfaceMenuOperation	_uiBase;
	private Font 						_font;
	private PanelBase 					_panelBase;
	private PanelSystem 				_panelSystem;
	private PanelShortcut 				_panelShortcut;
	private PanelJobs					_uiJobs;
	private PanelResource 				_panelResource;
	private UserInterfaceMessage 		_panelMessage;
	private PanelRoom 					_panelRoom;
	private UIMessage 					_message;
	private int 						_mouseRealPosX;
	private int 						_mouseRealPosY;
	private PanelDebugItem 				_panelDebugItems;

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
		PLAN, DEBUGITEMS
	}

	public void	onMouseMove(int x, int y) {
		_mouseRealPosX = x;
		_mouseRealPosY = y;
		_keyMovePosX = getRelativePosX(x);
		_keyMovePosY = getRelativePosY(y);

		// right button pressed
		if (_keyRightPressed) {
			_viewport.update(x, y);
		}

		// no buttons pressed
		else {
			// _cursor.setMousePos(x  _viewport.getScale() - UI_WIDTH - _viewport.getPosX() - 1,
			//                      y  _viewport.getScale() - UI_HEIGHT - _viewport.getPosY() - 1);
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

		if (_uiCharacter.catchClick(x, y)) {
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

	public void setMode(Mode info) {
		if (info != Mode.CHARACTER) _panelCharacter.setVisible(false);
		if (info != Mode.INFO) 		_panelInfo.setVisible(false);
		if (info != Mode.PLAN) 		_panelPlan.setVisible(false);
		if (info != Mode.DEBUG) 	_panelDebug.setVisible(false);
		if (info != Mode.DEBUGITEMS)_panelDebug.setVisible(false);
		if (info != Mode.BASE) 		_panelBase.setVisible(false);
		if (info != Mode.BUILD) 	_panelBuild.setVisible(false);
		if (info != Mode.CREW) 		_uiCharacter.setVisible(false);
		if (info != Mode.BASE) 		_uiBase.setVisible(false);
		if (info != Mode.SCIENCE) 	_uiScience.setVisible(false);
		if (info != Mode.SECURITY)	_uiSecurity.setVisible(false);
		if (info != Mode.JOBS)		_uiJobs.setVisible(false);
		if (info != Mode.ROOM)		_panelRoom.setVisible(false);

		switch (info) {
		case BUILD: 	_panelBuild.toogle(); break;
		case INFO: 		_panelInfo.toogle(); break;
		case DEBUG: 	_panelDebug.toogle(); break;
		case DEBUGITEMS:_panelDebugItems.toogle(); break;
		case PLAN: 		_panelPlan.toogle(); break;
		case CHARACTER: _panelCharacter.toogle(); break;
		case BASE: 		_panelBase.toogle(); break;
		case JOBS: 		_uiJobs.toogle(); break;
		case CREW: 		_uiCharacter.toogle(); break;
		case ROOM: 		_panelRoom.toogle(); break;
		case SCIENCE:	_uiScience.toogle(); break;
		case SECURITY:	_uiSecurity.toogle(); break;
		}
	}

	public void	onMouseWheel(int delta, int x, int y) {
		_viewport.setScale(delta);

		_keyMovePosX = getRelativePosX(x);
		_keyMovePosY = getRelativePosY(y);
	}

	public void refresh(int frame, int update, int renderTime) {
		_panelCharacter.refresh(_app, null);
		_panelBase.refresh(_app, null);
		_panelInfo.refresh(_app, null);
		//	  	_panelDebug.refresh(, _interaction.getCursor().getX(), _interaction.getCursor().getY());
		_panelPlan.refresh(_app, null);
		_panelDebug.refresh(_app, null);
		_panelDebugItems.refresh(_app, null);
		_panelSystem.refresh(_app, null);
		_panelShortcut.refresh(_app, null);
		_panelResource.refresh(_app, null);
		_panelRoom.refresh(_app, null);

		_panelMessage.setFrame(frame);
		//	  	_panelMessage.refresh(_app);

		//	  	_interaction.refreshCursor();

		_uiCharacter.refresh(_app, null);
		_uiScience.refresh(_app, null);
		_uiSecurity.refresh(_app, null);
		_uiBase.refresh(_app, null);
		_panelBuild.refresh(_app, null);
		_uiJobs.refresh(_app, null);

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
			//			RoomManager.getInstance().putRoom(fromX, fromY, toX, toY, roomType);
		}

		if (_message != null) {
			_app.draw(_message.border);
			_app.draw(_message.shape);
			_app.draw(_message.text);
			if (--_message.frame < 0) {
				_message = null;
			}

		}

	}

	public boolean checkKeyboard(Event	event, int frame, int lastInput) {

		if (event.asKeyEvent().key == Keyboard.Key.ADD) {
			if (Main.getUpdateInterval() - 40 > 0) {
				Main.setUpdateInterval(Main.getUpdateInterval() - 40);
			}
		}

		if (event.asKeyEvent().key == Keyboard.Key.SUBTRACT) {
			Main.setUpdateInterval(Main.getUpdateInterval() + 40);
		}

		if (_panelBuild.checkKey(event.asKeyEvent().key)) {
			return true;
		}

		if (_uiCharacter.checkKey(event.asKeyEvent().key)) {
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

		if (_interaction.getMode() != UserInteraction.Mode.MODE_NONE) {
			if (event.type == Event.Type.KEY_RELEASED && event.asKeyEvent().key == Keyboard.Key.ESCAPE) {
				//		  _interaction.cancel();
				return true;
			}
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
				Main.pause();
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
			_crewViewOpen = !_crewViewOpen;
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
			_panelRoom.toogle();
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

	public void init(RenderWindow app, Viewport viewport) throws IOException {
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

		_interaction = new UserInteraction(_viewport);
		_panelBuild = new PanelBuild(app, 3, _interaction);
		_uiScience = new UserInterfaceScience(app, 2);
		_uiSecurity = new UserInterfaceSecurity(app, 4);
		_crewViewOpen = false;
		_uiCharacter = new PanelCrew(app, 0);
		_uiCharacter.setUI(this);
		_uiBase = new UserInterfaceMenuOperation(app, 1);
		_uiJobs = new PanelJobs(app);
		_panelMessage.setStart(0);

		setMode(Mode.BASE);
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
			int fromX = _keyLeftPressed ? Math.min(_keyPressPosX, _keyMovePosX) : _keyMovePosX;
			int fromY = _keyLeftPressed ? Math.min(_keyPressPosY, _keyMovePosY) : _keyMovePosY;
			int toX = _keyLeftPressed ? Math.max(_keyPressPosX, _keyMovePosX) : _keyMovePosX;
			int toY = _keyLeftPressed ? Math.max(_keyPressPosY, _keyMovePosY) : _keyMovePosY;
			if (roomType == Room.Type.NONE) {
				RoomManager.getInstance().removeRoom(fromX, fromY, toX, toY, roomType);
			} else {
				RoomManager.getInstance().putRoom(fromX, fromY, toX, toY, roomType, 0);
			}
		}

		_panelCharacter.setCharacter(null);
		setMode(Mode.BASE);

		// Select character
		if (_interaction.getMode() == UserInteraction.Mode.MODE_NONE) {// && _menu.getCode() == UserInterfaceMenu.CODE_MAIN) {
			Character c = _characteres.getCharacterAtPos(getRelativePosX(x), getRelativePosY(y));
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

		// Cancel selected items 
		if (_mouseRightPressX >= x-1 && _mouseRightPressX <= x+1 && _mouseRightPressY >= y-1 && _mouseRightPressY <= y+1) {
			_panelBuild.setSelectedItem(null);
			_panelRoom.setSelected(null);
		}

		// Move viewport
		if (_keyRightPressed && Math.abs(_mouseRightPressX - x) > 5 || Math.abs(_mouseRightPressY - y) > 5) {
			_viewport.update(x, y);
		}

		_keyRightPressed = false;
	}
}
