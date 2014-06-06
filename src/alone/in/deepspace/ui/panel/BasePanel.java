package alone.in.deepspace.ui.panel;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.Mouse;

import alone.in.deepspace.engine.Viewport;
import alone.in.deepspace.engine.ui.ButtonView;
import alone.in.deepspace.engine.ui.ColorView;
import alone.in.deepspace.engine.ui.Colors;
import alone.in.deepspace.engine.ui.FrameLayout;
import alone.in.deepspace.engine.ui.OnClickListener;
import alone.in.deepspace.engine.ui.TextView;
import alone.in.deepspace.engine.ui.View;
import alone.in.deepspace.ui.UserInterface;
import alone.in.deepspace.ui.UserInterface.Mode;
import alone.in.deepspace.util.Constant;

public abstract class BasePanel extends FrameLayout {
	
	protected static final int 	LINE_HEIGHT = 20;
	protected static final int 	FONT_SIZE_TITLE = 22;
	protected static final int 	FONT_SIZE = 14;
	protected static final int 	NB_COLUMNS = Constant.NB_COLUMNS;
	protected static final int 	NB_COLUMNS_TITLE = 29;

	protected UserInterface 	_ui;
	protected RenderWindow 		_app;
	private Mode 				_mode;
	protected Viewport 			_viewport;
	private boolean 			_alwaysVisible;
	private boolean _isRightPane;
	private Key _shortcut;
		  
	public void			toogle() { _isVisible = !_isVisible; }
	public void			open() { _isVisible = true; }
	public void			close() { _isVisible = false; }
	public boolean		isOpen() { return _isVisible; }

	public BasePanel(Mode mode, Key shortcut, Vector2f pos, Vector2f size, boolean isRightPane) {
		super(size);
		
		_shortcut = shortcut;
		_isRightPane = isRightPane;
		if (isRightPane && mode != Mode.NONE) {
			ButtonView btBack = new ButtonView(new Vector2f(100, 32));
			btBack.setTextPadding(1, 6);
			btBack.setCharacterSize(FONT_SIZE_TITLE);
			btBack.setString("[    ]");
			btBack.setPosition(20, 10);
			btBack.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					_ui.back();
				}
			});
			super.addView(btBack);
			
			TextView lbBack = new TextView();
			lbBack.setString("Back");
			lbBack.setCharacterSize(FONT_SIZE_TITLE);
			lbBack.setColor(Colors.LINK_ACTIVE);
			lbBack.setPosition(20, 10);
			lbBack.setPadding(1, 20);
			super.addView(lbBack);
			
			ColorView rectangleUnderline = new ColorView(new Vector2f((int)(4 * 12.5), 1));
			rectangleUnderline.setPosition(40, 36);
			rectangleUnderline.setBackgroundColor(Colors.LINK_ACTIVE);
			super.addView(rectangleUnderline);
		}
		
		setBackgroundColor(Colors.BACKGROUND);

		View border = new ColorView(new Vector2f(4, size.y));
		border.setBackgroundColor(Colors.BORDER);
		super.addView(border);

		setPosition(pos);
		
		_mode = mode;
		_isVisible = false;
	}

	@Override
	public void addView(View view) {
		if (_isRightPane && _mode != Mode.NONE) {
			view.setPosition(view.getPosX(), view.getPosY() + 42);
		}
		super.addView(view);
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
