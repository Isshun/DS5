package alone.in.DeepSpace.UserInterface;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Text;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;

import alone.in.DeepSpace.Managers.JobManager;
import alone.in.DeepSpace.Managers.ResourceManager;
import alone.in.DeepSpace.Managers.SpriteManager;
import alone.in.DeepSpace.Models.BaseItem;
import alone.in.DeepSpace.Models.Job;
import alone.in.DeepSpace.Utils.Constant;


public class UserInterfaceMenuOperation extends UserSubInterface {
	
	private static int FONT_SIZE		= 16;
	private static int LINE_HEIGHT		= 24;
	private static int TITLE_SIZE		= FONT_SIZE + 8;

	private static Color COLOR_TILE_ACTIVE	= new Color(255, 50, 100);
	
	public void toogleJobs() { _isJobsOpen = !_isJobsOpen; }

	boolean			_isJobsOpen;
	
	
	UserInterfaceMenuOperation(RenderWindow app, int tileIndex) throws IOException {
		  super(app, tileIndex, new Vector2f(0, 0), new Vector2f(200, 200));

		_textureTile = new Texture();
		_textureTile.loadFromFile((new File("res/bg_tile_operation.png")).toPath());
		_texturePanel = new Texture();
		_texturePanel.loadFromFile((new File("res/bg_panel_operation.png")).toPath());
	}

	@Override
	public void onRefresh() {
	}

	void	drawTile() {
//	  super.drawTile(COLOR_TILE_ACTIVE);
//
//	  Text text = new Text();
//	  text.setFont(SpriteManager.getInstance().getFont());
//	  text.setCharacterSize(FONT_SIZE);
//
//	  {
//	    text.setString("Power: " + ResourceManager.getInstance().getPower());
//	    text.setPosition(_posTileX + Constant.UI_PADDING, _posTileY + TITLE_SIZE + Constant.UI_PADDING + LINE_HEIGHT * 0);
//	    _app.draw(text);
//	  }
//
//	  {
//	    text.setString("O2: " + ResourceManager.getInstance().getO2());
//	    text.setPosition(_posTileX + Constant.UI_PADDING, _posTileY + TITLE_SIZE + Constant.UI_PADDING + LINE_HEIGHT * 1);
//	    _app.draw(text);
//	  }
//
//	  text.setString("Operation");
//	  text.setCharacterSize(TITLE_SIZE);
//	  text.setPosition(_posTileX + Constant.UI_PADDING, _posTileY + Constant.UI_PADDING);
//	  _app.draw(text);
//	  text.setString("O");
//	  text.setStyle(Text.UNDERLINED);
//	  text.setColor(Color.YELLOW);
//	  _app.draw(text);
	}

	void	drawJobs() {

	  // for (int i = 0; i < 20; i++) {
	  //   std.ostringstream oss;
	  //   oss + "Job # " + i + ": Build item";
	  //   text.setString(oss.str());
	  //   text.setPosition(posX + UI_PADDING, posY + UI_PADDING + (32  i));
	  //   _app.draw(text);
	  // }
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
