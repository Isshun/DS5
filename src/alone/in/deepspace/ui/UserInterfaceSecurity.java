package alone.in.deepspace.ui;

import java.io.File;
import java.io.IOException;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2f;

public class UserInterfaceSecurity extends UserSubInterface {
	UserInterfaceSecurity(RenderWindow app, int tileIndex) throws IOException {
		super(app, tileIndex, new Vector2f(0, 0), new Vector2f(200, 200), null);
	  
		_textureTile = new Texture();
		_textureTile.loadFromFile((new File("res/bg_tile_security.png")).toPath());
		_texturePanel = new Texture();
		_texturePanel.loadFromFile((new File("res/bg_panel_security.png")).toPath());
	}
}
