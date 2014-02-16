package alone.in.DeepSpace.UserInterface;
import java.io.File;
import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Mouse;

import alone.in.DeepSpace.UserInterface.Utils.UIFrame;
import alone.in.DeepSpace.Utils.Constant;


public class UserSubInterface extends UIFrame {
	  protected boolean	isOnTile(int x, int y) {
			return x > _posTileX && x < _posTileX + 240 && y > _posTileY && y < _posTileY + 120;
		  }

		  protected RenderWindow 		_app;
		  protected Texture			_texturePanel;
		  protected Texture			_textureTile;
		  Sprite			_bgPanel;
		  Sprite			_bgTile;
		  protected int				_posX;
		  protected int				_posY;
		  protected int				_posTileX;
		  protected int				_posTileY;
		  protected boolean			_isTileActive;
		  protected boolean			_isOpen;
		  int				_tileIndex;
		  
		  void	openTile() { _isTileActive = true; }
		  void	closeTile() { _isTileActive = false; }
		  public void	toogleTile() { _isTileActive = !_isTileActive; }
		  protected void	toogle() { _isOpen = !_isOpen; }
		  public void	open() { _isOpen = true; }
		  public void	close() { _isOpen = false; }
		  protected boolean	isOpen() { return _isOpen; }
		  boolean	isTileActive() { return _isTileActive; }

	public UserSubInterface(RenderWindow app, int tileIndex, Vector2f pos, Vector2f size) throws IOException {
		super(size);
		
		setPosition(pos);
		
		_app = app;

		_posX = 200;
		_posY = 200;

		_posTileX = (Constant.MENU_TILE_WIDTH + Constant.UI_PADDING + Constant.UI_PADDING) * tileIndex + Constant.UI_PADDING;
		_posTileY = Constant.WINDOW_HEIGHT - 180 - Constant.UI_PADDING;
		_tileIndex = tileIndex;
		_isTileActive = false;
		_isOpen = false;

		_textureTile = new Texture();
		_textureTile.loadFromFile((new File("res/bg_tile_base.png")).toPath());
		_bgTile = new Sprite();
		_bgTile.setTexture(_textureTile);
		_bgTile.setTextureRect(new IntRect(0, 0, 240, 120));

		_texturePanel = new Texture();
		_texturePanel.loadFromFile((new File("res/bg_panel_base.png")).toPath());
		_bgPanel = new Sprite();
		_bgPanel.setTexture(_texturePanel);
		_bgPanel.setTextureRect(new IntRect(0, 0, 800, 600));
	}

	protected boolean	checkKey(Keyboard.Key key) {
		if (_isOpen) {
			if (key == Keyboard.Key.ESCAPE) {
				_isOpen = false;
				return true;
			}
		}
		return false;
	}

	public boolean	onMouseMove(int x, int y) {
		_isTileActive = false;
		
		if (_isOpen && x > _posX && x < _posX + 800 && y > _posY && y < _posY + 600) {
			return true;
		}

		else if (x > _posTileX && x < _posTileX + 240 && y > _posTileY && y < _posTileY + 120) {
			_isTileActive = true;
			return true;
		}

		return false;
	}

	public boolean	mousePress(Mouse.Button button, int x, int y) {
		if (_isOpen) {
			return true;
		}
		return false;
	}

	public boolean	mouseRelease(Mouse.Button button, int x, int y) {

		// On tile
		if (x > _posTileX && x < _posTileX + 240 && y > _posTileY && y < _posTileY + 120) {
			_isOpen = !_isOpen;
			_isTileActive = true;
			return true;
		}

		// Panel open
		else if (_isOpen) {
			return true;
		}

		return false;
	}

	protected void	drawPanel() {
		_bgPanel.setPosition(_posX, _posY);
		_app.draw(_bgPanel);
	}

	protected void	drawTile(Color color) {
		_bgTile.setPosition(_posTileX, _posTileY);
		_bgTile.setColor(isTileActive() || isOpen() ? color : Color.WHITE);
		_app.draw(_bgTile);
	}
}
