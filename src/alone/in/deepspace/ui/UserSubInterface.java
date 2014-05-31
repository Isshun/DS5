package alone.in.deepspace.ui;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Mouse;

import alone.in.deepspace.engine.ui.ColorView;
import alone.in.deepspace.engine.ui.FrameLayout;
import alone.in.deepspace.engine.ui.View;
import alone.in.deepspace.util.Constant;

public abstract class UserSubInterface extends FrameLayout {
	
	protected static final Color COLOR_TEXT = new Color(120, 255, 255);
	protected static final Color COLOR_LABEL = Color.WHITE;
	protected static final Color COLOR_ACTIVE = new Color(176, 205, 53);
	protected static final int 	LINE_HEIGHT = 20;
	protected static final int 	FONT_SIZE_TITLE = 22;
	protected static final int 	FONT_SIZE = 14;
	protected static final int 	NB_COLUMNS = 47;
	protected static final int 	NB_COLUMNS_TITLE = 29;

	protected Texture	_texturePanel;
	protected Texture	_textureTile;
	protected Sprite	_bgPanel;
	protected Sprite	_bgTile;
	protected int		_posTileX;
	protected int		_posTileY;
	protected boolean	_isTileActive;
	protected int		_tileIndex;
	public UserInterface _ui;
		  
	public void			openTile() { _isTileActive = true; }
	public void			closeTile() { _isTileActive = false; }
	public void			toogleTile() { _isTileActive = !_isTileActive; }
	public void			toogle() { _isVisible = !_isVisible; }
	public void			open() { _isVisible = true; }
	public void			close() { _isVisible = false; }
	public boolean		isOpen() { return _isVisible; }
	public boolean		isTileActive() { return _isTileActive; }
	public boolean		isOnTile(int x, int y) { return x > _posTileX && x < _posTileX + 240 && y > _posTileY && y < _posTileY + 120; }

	public UserSubInterface(RenderWindow app, int tileIndex, Vector2f pos, Vector2f size, UserInterface ui) {
		super(size);
		
		setBackgroundColor(new Color(18, 28, 30));

		View border = new ColorView(new Vector2f(4, size.y));
		border.setBackgroundColor(new Color(37, 70, 72));
		addView(border);

		setPosition(pos);
		
		_ui = ui;
		_posTileX = (Constant.MENU_TILE_WIDTH + Constant.UI_PADDING + Constant.UI_PADDING) * tileIndex + Constant.UI_PADDING;
		_posTileY = Constant.WINDOW_HEIGHT - 180 - Constant.UI_PADDING;
		_tileIndex = tileIndex;
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

	protected boolean	checkKey(Keyboard.Key key) {
		if (_isVisible) {
			if (key == Keyboard.Key.ESCAPE) {
				_isVisible = false;
				return true;
			}
		}
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

		else if (x > _posTileX && x < _posTileX + 240 && y > _posTileY && y < _posTileY + 120) {
			_isTileActive = true;
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

		// On tile
		if (x > _posTileX && x < _posTileX + 240 && y > _posTileY && y < _posTileY + 120) {
			_isVisible = !_isVisible;
			_isTileActive = true;
			return true;
		}

		// Panel open
		else if (_isVisible) {
			return true;
		}

		return false;
	}
	
	public void refresh(int update) {
		if (_isVisible) {
			onRefresh(update);
		}
	}
	
	protected void onRefresh(int update) {
		
	}
	
}
