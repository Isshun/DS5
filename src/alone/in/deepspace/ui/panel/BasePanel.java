package alone.in.deepspace.ui.panel;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.Mouse;

import alone.in.deepspace.engine.Viewport;
import alone.in.deepspace.engine.ui.ColorView;
import alone.in.deepspace.engine.ui.FrameLayout;
import alone.in.deepspace.engine.ui.View;
import alone.in.deepspace.ui.UserInterface;
import alone.in.deepspace.ui.UserInterface.Mode;

public abstract class BasePanel extends FrameLayout {
	
	protected static final int 	LINE_HEIGHT = 20;
	protected static final int 	FONT_SIZE_TITLE = 22;
	protected static final int 	FONT_SIZE = 14;
	protected static final int 	NB_COLUMNS = 47;
	protected static final int 	NB_COLUMNS_TITLE = 29;

	protected UserInterface 	_ui;
	protected RenderWindow 		_app;
	private Mode 				_mode;
	protected Viewport 			_viewport;
	private boolean 			_alwaysVisible;
		  
	public void			toogle() { _isVisible = !_isVisible; }
	public void			open() { _isVisible = true; }
	public void			close() { _isVisible = false; }
	public boolean		isOpen() { return _isVisible; }

	public BasePanel(Mode mode, Vector2f pos, Vector2f size) {
		super(size);
		
		setBackgroundColor(new Color(18, 28, 30));

		View border = new ColorView(new Vector2f(4, size.y));
		border.setBackgroundColor(new Color(37, 70, 72));
		addView(border);

		setPosition(pos);
		
		_mode = mode;
		_isVisible = false;
	}

	protected void setAlwaysVisible(boolean alwaysVisible) {
		_alwaysVisible = alwaysVisible;
		_isVisible = alwaysVisible;
	}

	public void init(RenderWindow app, UserInterface ui, Viewport viewport) {
		_app = app;
		_ui = ui;
		_viewport = viewport;
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
	
}
