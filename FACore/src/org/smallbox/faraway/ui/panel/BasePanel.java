package org.smallbox.faraway.ui.panel;

import org.jsfml.window.Keyboard;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.Mouse;

import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.engine.Viewport;
import org.smallbox.faraway.engine.ui.FrameLayout;
import org.smallbox.faraway.ui.UserInteraction;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.UserInterface.Mode;
import org.smallbox.faraway.engine.util.Constant;

public abstract class BasePanel extends FrameLayout {
	
	protected static final int 	LINE_HEIGHT = 20;
	protected static final int 	FONT_SIZE_TITLE = 22;
	protected static final int 	FONT_SIZE = 14;
	protected static final int 	NB_COLUMNS = Constant.NB_COLUMNS;
	protected static final int 	NB_COLUMNS_TITLE = Constant.NB_COLUMNS_TITLE;

	protected UserInterface 	_ui;
	protected Mode 				_mode;
	protected RenderEffect 		_effect;
	private boolean 			_alwaysVisible;
	private Key 				_shortcut;
	protected UserInteraction 	_interaction;
		  
	public void			toogle() { _isVisible = !_isVisible; }
	public void			open() { _isVisible = true; }
	public void			close() { _isVisible = false; }
	public boolean		isOpen() { return _isVisible; }

	public BasePanel(Mode mode, Key shortcut, int x, int y, int width, int height) {
		super(width, height);
		setPosition(x, y);
		_shortcut = shortcut;
		_mode = mode;
		_isVisible = false;
	}

	protected void setAlwaysVisible(boolean alwaysVisible) {
		_alwaysVisible = alwaysVisible;
		_isVisible = alwaysVisible;
	}

	public void init(UserInterface ui, UserInteraction interaction, RenderEffect effect) {
		_ui = ui;
		_interaction = interaction;
		_effect = effect;
		onCreate();
	}
	
	protected abstract void onCreate();
	
	public boolean	checkKey(Keyboard.Key key) {
		if (_isVisible) {
			return onKey(key);
		}
		return false;
	}

	protected boolean onKey(Key key) {
		return false;
	}
	
	public void setUI(UserInterface ui) {
		_ui = ui;
	}

	public boolean	onMouseMove(int x, int y) {
		if (_isVisible && x > _posX && x < _posX + 800 && y > _posY && y < _posY + 600) {
			return true;
		}

		return false;
	}

	public boolean	catchClick(int x, int y) {
		if (_isVisible && x > _posX && x < _posX + _size.x && y > _posY && y < _posY + _size.y) {
			return true;
		}
		return false;
	}

	public boolean	mouseRelease(Mouse.Button button, int x, int y) {
		return _isVisible;
	}
	
	public void refresh(int update) {
		if (_isVisible) {
			onRefresh(update);
		}
	}
	
	protected void onRefresh(int update) {
		
	}
	public Mode getMode() {
		return _mode;
	}

	public boolean isAlwaysVisible() {
		return _alwaysVisible;
	}

	public boolean drawCursor() {
		return false;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			onOpen();
		} else {
			onClose();
		}
	}
	protected void onClose() {
	}

	protected void onOpen() {
	}
	public Key getShortcut() {
		return _shortcut;
	}
}
