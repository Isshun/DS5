package alone.in.deepspace.ui;

import java.io.File;
import java.io.IOException;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;

public class UserInterfaceScience extends UserSubInterface {
	
	UserInterfaceScience(RenderWindow app, int tileIndex) throws IOException {
		super(app, tileIndex, new Vector2f(0, 0), new Vector2f(200, 200));
  
		_textureTile = new Texture();
		_textureTile.loadFromFile((new File("res/bg_tile_science.png")).toPath());
		_texturePanel = new Texture();
		_texturePanel.loadFromFile((new File("res/bg_panel_science.png")).toPath());
	}

	protected boolean	checkKey(Keyboard.Key key) {
		super.checkKey(key);
	
	//  if (key == Keyboard.Key.R) {
	//	toogle();
	//	return true;
	//  }

		return false;
	}

}
