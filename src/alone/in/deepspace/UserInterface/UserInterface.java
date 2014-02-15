package alone.in.DeepSpace.UserInterface;
import java.io.File;
import java.io.IOException;

import org.jsfml.graphics.Font;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Text;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Mouse;
import org.jsfml.window.event.Event;

import alone.in.DeepSpace.CharacterManager;
import alone.in.DeepSpace.Viewport;
import alone.in.DeepSpace.Models.Character;
import alone.in.DeepSpace.Utils.Constant;
import alone.in.DeepSpace.Utils.Log;
import alone.in.DeepSpace.Utils.Settings;
import alone.in.DeepSpace.World.WorldArea;
import alone.in.DeepSpace.World.WorldMap;

public class UserInterface {

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
	UserInterfaceMenuCharacter  _menuCharacter;
	UserInterfaceMenuInfo		_menuInfo;
	UserInterfaceCrew			_uiCharacter;
	UserInterfaceEngineering	_uiEngeneering;
	UserInterfaceDebug			_uiDebug;
	UserInterfaceMenuOperation	_uiBase;
	private Font 				_font;
	
	public UserInterface(RenderWindow app, Viewport viewport) throws IOException {
	  _app = app;
	  _viewport = viewport;
	  _characteres = CharacterManager.getInstance();
	  _keyLeftPressed = false;
	  _keyRightPressed = false;
	  _zoom = 1.0f;
	  _font = new Font();
	  _font.loadFromFile((new File("res/fonts/xolonium_regular.otf")).toPath());
	
	  // _menu = new UserInterfaceMenu(app);
	  // _menu.init();
	
	  _menuCharacter = new UserInterfaceMenuCharacter(app);
	  _menuCharacter.init();
	
	  _menuInfo = new UserInterfaceMenuInfo(app);
	  _menuInfo.init();
	
	  _interaction = new UserInteraction(_viewport);
	
	  _uiEngeneering = new UserInterfaceEngineering(app, 3, _interaction);
	  _uiScience = new UserInterfaceScience(app, 2);
	  _uiSecurity = new UserInterfaceSecurity(app, 4);
	  _crewViewOpen = false;
	  _uiCharacter = new UserInterfaceCrew(app, 0);
	  _uiDebug = new UserInterfaceDebug(app);
	  _uiBase = new UserInterfaceMenuOperation(app, 1);
	}
	
	public void	mouseMoved(int x, int y) {
	  // if (x <= UI_WIDTH || y <= UI_HEIGHT)
	  // 	return;
	
	  if (_uiEngeneering.onMouseMove(x, y)) {
		return;
	  }
	
	  if (_uiCharacter.onMouseMove(x, y)) {
		return;
	  }
	
	  if (_uiSecurity.onMouseMove(x, y)) {
		return;
	  }
	
	  if (_uiScience.onMouseMove(x, y)) {
		return;
	  }
	
	  if (_uiBase.onMouseMove(x, y)) {
		return;
	  }
	
	  _keyMovePosX = getRelativePosX(x);
	  _keyMovePosY = getRelativePosY(y);
	  _interaction.mouseMove(_keyMovePosX, _keyMovePosY);
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
	  if (_uiEngeneering.mousePress(button, x, y)) {
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
	
	  _interaction.mousePress(button, getRelativePosX(x), getRelativePosY(y));
	  _interaction.mouseMove(getRelativePosX(x), getRelativePosY(y));
	
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
	
	int getRelativePosX(int x) {
		return (int) ((x - Constant.UI_WIDTH - _viewport.getPosX()) / _viewport.getScale() / Constant.TILE_SIZE);
	}
	int getRelativePosY(int y) {
		return (int) ((y - Constant.UI_HEIGHT - _viewport.getPosY()) / _viewport.getScale() / Constant.TILE_SIZE);
	}

	public void	mouseRelease(Mouse.Button button, int x, int y) {
	  if (_uiEngeneering.mouseRelease(button, x, y)) {
		_uiSecurity.close();
		_uiScience.close();
		_uiCharacter.close();
		_uiBase.close();
		return;
	  }
	
	  if (_uiCharacter.mouseRelease(button, x, y)) {
		_uiSecurity.close();
		_uiScience.close();
		_uiEngeneering.close();
		_uiBase.close();
		return;
	  }
	
	  if (_uiBase.mouseRelease(button, x, y)) {
		_uiSecurity.close();
		_uiScience.close();
		_uiCharacter.close();
		_uiEngeneering.close();
		return;
	  }
	
	  if (_uiScience.mouseRelease(button, x, y)) {
		_uiSecurity.close();
		_uiCharacter.close();
		_uiEngeneering.close();
		_uiBase.close();
		return;
	  }
	
	  if (_uiSecurity.mouseRelease(button, x, y)) {
		_uiScience.close();
		_uiCharacter.close();
		_uiEngeneering.close();
		_uiBase.close();
		return;
	  }
	
	  if (_interaction.mouseRelease(button, x, y)) {
		return;
	  }
	
	  _interaction.cancel();
	
	  if (button == Mouse.Button.LEFT) {
	    if (true) {
	
	      _menuCharacter.setCharacter(null);
	
	      // Select character
	      if (_interaction.getMode() == UserInteraction.Mode.MODE_NONE) {// && _menu.getCode() == UserInterfaceMenu.CODE_MAIN) {
			Log.info("select character");
	        Character c = _characteres.getCharacterAtPos(getRelativePosX(x), getRelativePosY(y));
			if (c != null) {
			  _menuCharacter.setCharacter(c);
			} else {
			  WorldArea a = WorldMap.getInstance().getArea(getRelativePosX(x), getRelativePosY(y));
			  if (a != null) {
				if (_menuInfo.getArea() == a && _menuInfo.getItem() == null && a.getItem() != null) {
				  _menuInfo.setItem(a.getItem());
				} else {
				  _menuInfo.setArea(a);
				  _menuInfo.setItem(null);
				}
			  }
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
			_interaction.cancel();
		  }
	
		}
	  }
	}
	
	public void	mouseWheel(int delta, int x, int y) {
	  _viewport.setScale(delta);
	
	  _keyMovePosX = getRelativePosX(x);
	  _keyMovePosY = getRelativePosY(y);
	}
	
	public void refresh(int frame, int update, int renderTime) {
	  // Display character frame
	  if (_menuCharacter.getCharacter() != null) {
	    _menuCharacter.refresh(frame);
	  }
	
	  // Display info frame
	  else if (_menuInfo.getArea() != null || _menuInfo.getItem() != null) {
	    _menuInfo.refresh(frame);
	  }
	
	  // Display debug view
	  if (Settings.getInstance().isDebug()) {
	  	_uiDebug.refresh(frame, _interaction.getCursor().getX(), _interaction.getCursor().getY());
	  	//drawCursor(_keyMovePosX, _keyMovePosY, _keyMovePosX, _keyMovePosY);
	  }
	
	  _interaction.refreshCursor();
	  // _uiResource.refreshResources(frame, interval);
	
	  _uiCharacter.draw(frame);
	  _uiScience.draw(frame);
	  _uiSecurity.draw(frame);
	  _uiBase.draw(frame);
	  _uiEngeneering.draw(frame);
	  
		Text text = new Text();
		text.setFont(_font);
		text.setCharacterSize(20);
		text.setStyle(Text.REGULAR);
		text.setString("#" + frame + " (" + renderTime + "ms)");
		text.setPosition(Constant.WINDOW_WIDTH - 200 + Constant.UI_PADDING, Constant.UI_PADDING);
		_app.draw(text);

	}
	
	public boolean checkKeyboard(Event	event, int frame, int lastInput) {
	
	  if (_uiEngeneering.checkKey(event.asKeyEvent().key)) {
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
		  _interaction.cancel();
		  return true;
		}
	  }
	
	  if (event.asKeyEvent().key == Keyboard.Key.TAB) {
		if ((event.type == Event.Type.KEY_RELEASED)) {
		  if (_menuCharacter.getCharacter() != null) {
			_menuCharacter.setCharacter(_characteres.getNext(_menuCharacter.getCharacter()));
		  }
		}
		return true;
	  }
	
	  if (event.asKeyEvent().key == Keyboard.Key.D) {
		Settings.getInstance().setDebug(!Settings.getInstance().isDebug());
		// 	WorldMap.getInstance().dump();
	  }	
	  else if (event.asKeyEvent().key == Keyboard.Key.C) {
		_crewViewOpen = !_crewViewOpen;
	  }
	  else if (event.asKeyEvent().key == Keyboard.Key.E) {
		_uiEngeneering.open();
	  }	
	  else if (event.asKeyEvent().key == Keyboard.Key.O) {
		_uiBase.toogleTile();
	  }
	  else if (event.asKeyEvent().key == Keyboard.Key.J) {
		_uiBase.toogleJobs();
	  }
	  else if (event.asKeyEvent().key == Keyboard.Key.I) {
		WorldMap.getInstance().dumpItems();
	  }
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
}
