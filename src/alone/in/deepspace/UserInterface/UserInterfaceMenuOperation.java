package alone.in.DeepSpace.UserInterface;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.crypto.spec.PSource;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Text;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;

import alone.in.DeepSpace.JobManager;
import alone.in.DeepSpace.ResourceManager;
import alone.in.DeepSpace.SpriteManager;
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

	void	draw(int frame) {
	  if (_isOpen) {
		drawJobs();
	  }
	  drawTile();
	}

	void	drawTile() {
	  super.drawTile(COLOR_TILE_ACTIVE);

	  Text text = new Text();
	  text.setFont(SpriteManager.getInstance().getFont());
	  text.setCharacterSize(FONT_SIZE);

	  {
	    text.setString("Power: " + ResourceManager.getInstance().getPower());
	    text.setPosition(_posTileX + Constant.UI_PADDING, _posTileY + TITLE_SIZE + Constant.UI_PADDING + LINE_HEIGHT * 0);
	    _app.draw(text);
	  }

	  {
	    text.setString("O2: " + ResourceManager.getInstance().getO2());
	    text.setPosition(_posTileX + Constant.UI_PADDING, _posTileY + TITLE_SIZE + Constant.UI_PADDING + LINE_HEIGHT * 1);
	    _app.draw(text);
	  }

	  text.setString("Operation");
	  text.setCharacterSize(TITLE_SIZE);
	  text.setPosition(_posTileX + Constant.UI_PADDING, _posTileY + Constant.UI_PADDING);
	  _app.draw(text);
	  text.setString("O");
	  text.setStyle(Text.UNDERLINED);
	  text.setColor(Color.YELLOW);
	  _app.draw(text);
	}

	void	drawJobs() {
	  int posX = 20;
	  int posY = 20;

	  // _background.setPosition(posX, UI_PADDING);
	  // _background.setColor(MENU_COLOR);
	  // _app.draw(_background);

	  // Background
	  RectangleShape shape = new RectangleShape();
	  shape.setSize(new Vector2f(300, 800));
	  shape.setFillColor(new Color(0, 0, 100, 100));
	  shape.setPosition(posX, posY);
	  _app.draw(shape);

	  Text text = new Text();
	  text.setFont(SpriteManager.getInstance().getFont());
	  text.setCharacterSize(12);

	  // Display jobs
	  List<Job> jobs = JobManager.getInstance().getJobs();

	  text.setColor(Color.WHITE);
	  text.setString("Operation");
	  text.setCharacterSize(28);
	  text.setPosition(posX, posY);
	  _app.draw(text);
	  text.setColor(Color.YELLOW);
	  text.setStyle(Text.UNDERLINED);
	  text.setString("O");
	  _app.draw(text);

	  text.setStyle(Text.REGULAR);
	  text.setColor(Color.WHITE);
	  text.setCharacterSize(16);
	  text.setString("jobs: " + jobs.size());
	  text.setPosition(posX, posY + 38);
	  _app.draw(text);
	  
	  text.setCharacterSize(12);
	  int i = 0;
	  for (Job job: jobs) {
		if (i < 50) {
		  String oss = "Job # " + job.getId()
			  + ": " + JobManager.getActionName(job.getAction())
			  + " " + BaseItem.getItemName(job.getItemType());
		  if (job.getCharacter() != null) {
			text.setColor(Color.WHITE);
			oss += " (" + job.getCharacter().getName() + ")";
		  } else {
			text.setColor(Color.YELLOW);
			oss += " (on queue)";
		  }
		  text.setString(oss);
		  text.setPosition(posX + Constant.UI_PADDING, posY + 52 + Constant.UI_PADDING + (14 * i++));
		  _app.draw(text);
		}
	  }

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
