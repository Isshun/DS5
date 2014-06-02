package alone.in.deepspace.ui.panel;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
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

	protected Texture	_texturePanel;
	protected Texture	_textureTile;
	protected Sprite	_bgPanel;
	protected Sprite	_bgTile;
	protected boolean	_isTileActive;
	protected int		_tileIndex;
	protected UserInterface _ui;
	protected RenderWindow _app;
	private Mode 		_mode;
	protected Viewport _viewport;
		  
	public void			openTile() { _isTileActive = true; }
	public void			closeTile() { _isTileActive = false; }
	public void			toogleTile() { _isTileActive = !_isTileActive; }
	public void			toogle() { _isVisible = !_isVisible; }
	public void			open() { _isVisible = true; }
	public void			close() { _isVisible = false; }
	public boolean		isOpen() { return _isVisible; }
	public boolean		isTileActive() { return _isTileActive; }

	public BasePanel(Mode mode, Vector2f pos, Vector2f size) {
		super(size);
		
		setBackgroundColor(new Color(18, 28, 30));

		View border = new ColorView(new Vector2f(4, size.y));
		border.setBackgroundColor(new Color(37, 70, 72));
		addView(border);

		setPosition(pos);
		
		_mode = mode;
		_isTileActive = false;
		_isVisible = false;

//		_textureTile = new Texture();
//		_textureTile.loadFromFile((new File("res/bg_tile_base.png")).toPath());
//		_bgTile = new Sprite();
//		_bgTile.setTexture(_textureTile);
//		_bgTile.setTextureRect(new IntRect(0, 0, 240, 120));
//
//		_texturePanel = new Texture();
//		_texturePanel.loadFromFile((new File("res/bg_panel_base.png")).toPath());
//		_bgPanel = new Sprite();
//		_bgPanel.setTexture(_texturePanel);
//		_bgPanel.setTextureRect(new IntRect(0, 0, 800, 600));
	}
	
	public void init(RenderWindow app, UserInterface ui, Viewport viewport) {
		_app = app;
		_ui = ui;
		_viewport = viewport;;
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
		_isTileActive = false;
		
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
	
}
