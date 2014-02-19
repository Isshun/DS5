package alone.in.deepspace.UserInterface;
import java.io.File;
import java.io.IOException;

import org.jsfml.graphics.Font;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Mouse;
import org.jsfml.window.Mouse.Button;
import org.jsfml.window.event.Event;
import org.omg.stub.java.rmi._Remote_Stub;

import alone.in.deepspace.Viewport;
import alone.in.deepspace.Managers.CharacterManager;
import alone.in.deepspace.Models.BaseItem;
import alone.in.deepspace.Models.Character;
import alone.in.deepspace.Models.Room;
import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.Utils.Log;
import alone.in.deepspace.Utils.Settings;
import alone.in.deepspace.World.WorldArea;
import alone.in.deepspace.World.WorldMap;

public class UserInterface {

	private static UserInterface _self;
	RenderWindow				_app;
	Viewport					_viewport;
	boolean						_keyLeftPressed;
	boolean						_keyRightPressed;
	int							_mouseRightPressX;
	int							_mouseRightPressY;
	int							_keyPressPosX;
	int							_keyPressPosY;
	int							_keyMovePosX;
	int							_keyMovePosY;
	float						_zoom;
	boolean						_crewViewOpen;
	UserInteraction				_interaction;
	UserInterfaceScience		_uiScience;
	UserInterfaceSecurity		_uiSecurity;
	CharacterManager        	_characteres;
	PanelCharacter  			_panelCharacter;
	PanelInfo					_panelInfo;
	UserInterfaceCrew			_uiCharacter;
	PanelBuild	_panelBuild;
	PanelDebug					_panelDebug;
	UserInterfaceMenuOperation	_uiBase;
	private Font 				_font;
	private PanelBase 			_panelBase;
	private PanelSystem 		_panelSystem;
	private PanelShortcut 		_panelShortcut;
	private PanelJobs			_uiJobs;
	private PanelResource 		_panelResource;
	private UserInterfaceMessage _panelMessage;
	private PanelRoom 			_panelRoom;
	private UIMessage 			_message;
	private int 				_mouseRealPosX;
	private int 				_mouseRealPosY;
	
	public enum Mode {
		BASE,
		INFO,
		DEBUG,
		BUILD,
		CREW,
		JOBS,
		CHARACTER, SCIENCE, SECURITY, ROOM
	}
	
	public void	mouseMoved(int x, int y) {
//		_viewport.setMousePos(x, y);
		// if (x <= UI_WIDTH || y <= UI_HEIGHT)
		// 	return;
	
//	  if (_uiEngeneering.onMouseMove(x, y)) {
//		return;
//	  }
//	
//	  if (_uiCharacter.onMouseMove(x, y)) {
//		return;
//	  }
//	
//	  if (_uiSecurity.onMouseMove(x, y)) {
//		return;
//	  }
//	
//	  if (_uiScience.onMouseMove(x, y)) {
//		return;
//	  }
//	
//	  if (_uiBase.onMouseMove(x, y)) {
//		return;
//	  }
	
		_mouseRealPosX = x;
		_mouseRealPosY = y;
	  _keyMovePosX = getRelativePosX(x);
	  _keyMovePosY = getRelativePosY(y);
//	  _interaction.mouseMove(_keyMovePosX, _keyMovePosY);
	  // _cursor.setPos(_keyMovePosX, _keyMovePosY);
	
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
	
	public void	mousePress(Mouse.Button button, int x, int y) {
		if (EventManager.getInstance().has(x, y)) {
			return;
		}
		
	  if (_panelBuild.mousePress(button, x, y)) {
		_keyLeftPressed = false;
		return;
	  }
	
	  if (_uiCharacter.mousePress(button, x, y)) {
		_keyLeftPressed = false;
		return;
	  }
	
	  if (_uiScience.mousePress(button, x, y)) {
		_keyLeftPressed = false;
		return;
	  }
	
	  if (_uiSecurity.mousePress(button, x, y)) {
		_keyLeftPressed = false;
		return;
	  }
	
	  if (_uiBase.mousePress(button, x, y)) {
		_keyLeftPressed = false;
		return;
	  }
//	
//	  _interaction.mousePress(button, getRelativePosX(x), getRelativePosY(y));
//	  _interaction.mouseMove(getRelativePosX(x), getRelativePosY(y));
//	
	  if (button == Mouse.Button.LEFT) {
		_keyLeftPressed = true;
		_keyMovePosX = _keyPressPosX = getRelativePosX(x);
		_keyMovePosY = _keyPressPosY = getRelativePosY(y);
	  }
	  else if (button == Mouse.Button.RIGHT) {
		_keyRightPressed = true;
		_mouseRightPressX = x;
		_mouseRightPressY = y;
	    _viewport.startMove(x, y);
	  }
	}
	
	public int getRelativePosX(int x) {
		return (int) ((x - Constant.UI_WIDTH - _viewport.getPosX()) / _viewport.getScale() / Constant.TILE_SIZE);
	}
	
	public int getRelativePosY(int y) {
		return (int) ((y - Constant.UI_HEIGHT - _viewport.getPosY()) / _viewport.getScale() / Constant.TILE_SIZE);
	}

	public int getRelativePosXMax(int x) {
		return (int) ((x - Constant.UI_WIDTH - _viewport.getPosX()) / _viewport.getMinScale() / Constant.TILE_SIZE);
	}
	
	public int getRelativePosYMax(int y) {
		return (int) ((y - Constant.UI_HEIGHT - _viewport.getPosY()) / _viewport.getMinScale() / Constant.TILE_SIZE);
	}

	public int getRelativePosXMin(int x) {
		return (int) ((x - Constant.UI_WIDTH - _viewport.getPosX()) / _viewport.getMaxScale() / Constant.TILE_SIZE);
	}
	
	public int getRelativePosYMin(int y) {
		return (int) ((y - Constant.UI_HEIGHT - _viewport.getPosY()) / _viewport.getMaxScale() / Constant.TILE_SIZE);
	}

	public void	mouseRelease(Mouse.Button button, int x, int y) {
		
		if (button == Button.LEFT && EventManager.getInstance().rightClick(x, y)) {
			return;
		}
		
//	  if (_uiEngeneering.mouseRelease(button, x, y)) {
//		  setMode(Mode.BUILD);
//		_uiSecurity.close();
//		_uiScience.close();
//		_uiCharacter.close();
//		_uiBase.close();
//		return;
//	  }
//	
//	  if (_uiCharacter.mouseRelease(button, x, y)) {
//		_uiSecurity.close();
//		_uiScience.close();
//		_uiEngeneering.close();
//		_uiBase.close();
//		return;
//	  }
//	
//	  if (_uiBase.mouseRelease(button, x, y)) {
//		_uiSecurity.close();
//		_uiScience.close();
//		_uiCharacter.close();
//		_uiEngeneering.close();
//		return;
//	  }
//	
//	  if (_uiScience.mouseRelease(button, x, y)) {
//		_uiSecurity.close();
//		_uiCharacter.close();
//		_uiEngeneering.close();
//		_uiBase.close();
//		return;
//	  }
//	
//	  if (_uiSecurity.mouseRelease(button, x, y)) {
//		_uiScience.close();
//		_uiCharacter.close();
//		_uiEngeneering.close();
//		_uiBase.close();
//		return;
//	  }
		
		if (button == Button.LEFT && _panelBuild.getSelectedItem() != null) {
			_interaction.build(_panelBuild.getSelectedItem(),
					Math.min(_keyPressPosX, _keyMovePosX),
					Math.min(_keyPressPosY, _keyMovePosY),
					Math.max(_keyPressPosX, _keyMovePosX),
					Math.max(_keyPressPosY, _keyMovePosY));
		}
		
		Room.Type roomType = _panelRoom.getSelectedRoom();
		if (button == Button.LEFT && roomType != null) {
			int fromX = _keyLeftPressed ? Math.min(_keyPressPosX, _keyMovePosX) : _keyMovePosX;
			int fromY = _keyLeftPressed ? Math.min(_keyPressPosY, _keyMovePosY) : _keyMovePosY;
			int toX = _keyLeftPressed ? Math.max(_keyPressPosX, _keyMovePosX) : _keyMovePosX;
			int toY = _keyLeftPressed ? Math.max(_keyPressPosY, _keyMovePosY) : _keyMovePosY;
			RoomManager.getInstance().putRoom(fromX, fromY, toX, toY, roomType, 0);
		}

		if (button == Button.RIGHT && _mouseRightPressX >= x-1 && _mouseRightPressX <= x+1 && _mouseRightPressY >= y-1 && _mouseRightPressY <= y+1) {
			_panelBuild.setSelectedItem(null);
			_panelRoom.setSelected(null);
		}
	
//	  if (_interaction.mouseRelease(button, x, y)) {
//		return;
//	  }
	
//	  _interaction.cancel();
	
	  if (button == Mouse.Button.LEFT) {
	    if (true) {
	
	      _panelCharacter.setCharacter(null);
	      setMode(Mode.BASE);
	
	      // Select character
	      if (_interaction.getMode() == UserInteraction.Mode.MODE_NONE) {// && _menu.getCode() == UserInterfaceMenu.CODE_MAIN) {
			Log.info("select character");
	        Character c = _characteres.getCharacterAtPos(getRelativePosX(x), getRelativePosY(y));
			if (c != null) {
			  _panelCharacter.setCharacter(c);
			  setMode(Mode.CHARACTER);
			} else {
			  WorldArea a = WorldMap.getInstance().getArea(getRelativePosX(x), getRelativePosY(y));
			  _panelInfo.setArea(a);
			  if (a != null) {
//				if (_panelInfo.getArea() == a && _panelInfo.getItem() == null && a.getItem() != null) {
//				  _panelInfo.setItem(a.getItem());
//				} else {
//				  _panelInfo.setItem(null);
//				}
			  }
		      setMode(Mode.INFO);
			}
	      }
	
	      _keyLeftPressed = false;
	    }
	  }
	  else if (button == Mouse.Button.RIGHT) {
		if (_keyRightPressed) {
		  _keyRightPressed = false;
	
		  if (Math.abs(_mouseRightPressX - x) > 5 || Math.abs(_mouseRightPressY - y) > 5) {
			_viewport.update(x, y);
			// _viewport.update(_mouseRightPress.x - x, _mouseRightPress.y - y);
		  } else {
//			_interaction.cancel();
		  }
	
		}
	  }
	}
	
	public void setMode(Mode info) {
		if (info != Mode.CHARACTER) _panelCharacter.setVisible(false);
		if (info != Mode.INFO) 		_panelInfo.setVisible(false);
		if (info != Mode.DEBUG) 	_panelDebug.setVisible(false);
		if (info != Mode.BASE) 		_panelBase.setVisible(false);
		if (info != Mode.BUILD) 	_panelBuild.setVisible(false);
		if (info != Mode.CREW) 		_uiCharacter.setVisible(false);
		if (info != Mode.BASE) 		_uiBase.setVisible(false);
		if (info != Mode.SCIENCE) 	_uiScience.setVisible(false);
		if (info != Mode.SECURITY)	_uiSecurity.setVisible(false);
		if (info != Mode.JOBS)		_uiJobs.setVisible(false);
		if (info != Mode.ROOM)		_panelRoom.setVisible(false);
		
		switch (info) {
		case BUILD: _panelBuild.toogle(); break;
		case INFO: _panelInfo.toogle(); break;
		case DEBUG: _panelDebug.toogle(); break;
		case CHARACTER: _panelCharacter.toogle(); break;
		case BASE: _panelBase.toogle(); break;
		case JOBS: _uiJobs.toogle(); break;
		case CREW: _uiCharacter.toogle(); break;
		case ROOM: _panelRoom.toogle(); break;
		}
	}

	public void	mouseWheel(int delta, int x, int y) {
	  _viewport.setScale(delta);
	
	  _keyMovePosX = getRelativePosX(x);
	  _keyMovePosY = getRelativePosY(y);
	}
	
	public void refresh(int frame, int update, int renderTime) {
		_panelCharacter.refresh(_app);
		_panelBase.refresh(_app);
	    _panelInfo.refresh(_app);
//	  	_panelDebug.refresh(, _interaction.getCursor().getX(), _interaction.getCursor().getY());
	  	_panelDebug.refresh(_app);
	  	_panelSystem.refresh(_app);
	  	_panelShortcut.refresh(_app);
	  	_panelResource.refresh(_app);
	  	_panelRoom.refresh(_app);
	  	
	  	_panelMessage.setFrame(frame);
//	  	_panelMessage.refresh(_app);
	  	
//	  	_interaction.refreshCursor();
	
	  	_uiCharacter.refresh(_app);
	  	_uiScience.refresh(_app);
	  	_uiSecurity.refresh(_app);
	  	_uiBase.refresh(_app);
	  	_panelBuild.refresh(_app);
	  	_uiJobs.refresh(_app);
	  	
		BaseItem.Type type = _panelBuild.getSelectedItem();
		if (type != null) {
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
	
	  if (event.asKeyEvent().key == Keyboard.Key.D) {
		Settings.getInstance().setDebug(!Settings.getInstance().isDebug());
		if (Settings.getInstance().isDebug()) {
			setMode(Mode.DEBUG);
		} else {
			setMode(_panelCharacter.getCharacter() != null ? Mode.CHARACTER : Mode.INFO);
		}
		// 	WorldMap.getInstance().dump();
	  }	
	  else if (event.asKeyEvent().key == Keyboard.Key.C) {
		_crewViewOpen = !_crewViewOpen;
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
//		WorldMap.getInstance().dumpItems();
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
		_characteres = CharacterManager.getInstance();
		_keyLeftPressed = false;
		_keyRightPressed = false;
		_zoom = 1.0f;
		_font = new Font();
		_font.loadFromFile((new File("res/fonts/xolonium_regular.otf")).toPath());
	
		_panelBase = new PanelBase(app);
		_panelCharacter = new PanelCharacter(app);
		_panelInfo = new PanelInfo(app);
		_panelDebug = new PanelDebug(app);
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
		_uiCharacter = new UserInterfaceCrew(app, 0);
		_uiBase = new UserInterfaceMenuOperation(app, 1);
		_uiJobs = new PanelJobs(app);
		_panelMessage.setStart(0);

		setMode(Mode.BASE);
	}

	public void displayMessage(String msg) {
		_message = new UIMessage(msg, _mouseRealPosX, _mouseRealPosY);
	}
}
