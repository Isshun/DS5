package alone.in.deepspace.UserInterface;
import java.io.File;
import java.io.IOException;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;


public class UserInterfaceSecurity extends UserSubInterface {
	UserInterfaceSecurity(RenderWindow app, int tileIndex) throws IOException {
		super(app, tileIndex, new Vector2f(0, 0), new Vector2f(200, 200));
	  
		_textureTile = new Texture();
		_textureTile.loadFromFile((new File("res/bg_tile_security.png")).toPath());
		_texturePanel = new Texture();
		_texturePanel.loadFromFile((new File("res/bg_panel_security.png")).toPath());
	}

	void	draw(int frame) {
	  // if (_isOpen) {
	  // 	drawPanel(frame);
	  // }
	  drawTile();
	}

	void	drawTile() {
//	  super.drawTile(TILE_ACTIVE_COLOR);
//
//	  Text text = new Text();
//	  text.setFont(SpriteManager.getInstance().getFont());
//	  text.setCharacterSize(FONT_SIZE);
//
//	  {
//		int matter = ResourceManager.getInstance().getMatter();
//		text.setString("Matter: " + matter);
//
//		if (matter == 0)
//		  text.setColor(Color.RED);
//		else if (matter < 20)
//		  text.setColor(Color.YELLOW);
//	    text.setPosition(_posTileX + Constant.UI_PADDING,
//						 _posTileY + TITLE_SIZE + Constant.UI_PADDING);
//	    _app.draw(text);
//		text.setColor(Color.WHITE);
//	  }
//
//	  {
//	    text.setString("Power: " + ResourceManager.getInstance().getPower());
//	    text.setPosition(_posTileX + Constant.UI_PADDING,
//						 _posTileY + TITLE_SIZE + Constant.UI_PADDING + LINE_HEIGHT);
//	    _app.draw(text);
//	  }
//
//	  {
//	    text.setString("O2: " + ResourceManager.getInstance().getO2());
//	    text.setPosition(_posTileX + Constant.UI_PADDING,
//						 _posTileY + TITLE_SIZE + Constant.UI_PADDING + LINE_HEIGHT * 2);
//	    _app.draw(text);
//	  }
//
//	  text.setString("Security");
//	  text.setCharacterSize(TITLE_SIZE);
//	  text.setPosition(_posTileX + Constant.UI_PADDING, _posTileY + Constant.UI_PADDING);
//	  _app.draw(text);
//	  text.setString("S");
//	  text.setStyle(Text.UNDERLINED);
//	  text.setColor(Color.YELLOW);
//	  _app.draw(text);
	}

	protected boolean	checkKey(Keyboard.Key key) {
	  super.checkKey(key);

	  if (key == Keyboard.Key.S) {
		toogle();
		return true;
	  }

	  return false;
	}


}
