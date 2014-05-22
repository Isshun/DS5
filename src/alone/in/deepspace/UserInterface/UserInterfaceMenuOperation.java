package alone.in.deepspace.UserInterface;

import java.io.File;
import java.io.IOException;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;

public class UserInterfaceMenuOperation extends UserSubInterface {
	
	public void toogleJobs() { _isJobsOpen = !_isJobsOpen; }

	boolean			_isJobsOpen;
	
	UserInterfaceMenuOperation(RenderWindow app, int tileIndex) throws IOException {
		super(app, tileIndex, new Vector2f(0, 0), new Vector2f(200, 200));

		_textureTile = new Texture();
		_textureTile.loadFromFile((new File("res/bg_tile_operation.png")).toPath());
		_texturePanel = new Texture();
		_texturePanel.loadFromFile((new File("res/bg_panel_operation.png")).toPath());
	}

	protected boolean	checkKey(Keyboard.Key key) {
		super.checkKey(key);
		if (key == Keyboard.Key.O) {
			toogle();
			return true;
		}
		return false;
	}
}
