package alone.in.DeepSpace.UserInterface;
import java.io.File;
import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import alone.in.DeepSpace.Models.BaseItem;
import alone.in.DeepSpace.Utils.Constant;
import alone.in.DeepSpace.Utils.Log;
import alone.in.DeepSpace.World.WorldMap;


public class UserInterfaceDebug {

	private RenderWindow	_app;
	private Font			_font;
	private int				_index;

	public UserInterfaceDebug(RenderWindow app) throws IOException {
		_app = app;

		_font = new Font();
		_font.loadFromFile((new File("res/fonts/xolonium_regular.otf")).toPath());
	}

	void  addDebug(final String key, String value) {
	  int y = _index * 32;

	  {
		Text text = new Text();
		text.setFont(_font);
		text.setCharacterSize(20);
		text.setStyle(Text.REGULAR);
		text.setString(key);
		text.setPosition(Constant.WINDOW_WIDTH - 320 + Constant.UI_PADDING, Constant.UI_PADDING + y);
		_app.draw(text);
	  }

	  {
		Text text = new Text();
		text.setFont(_font);
		text.setCharacterSize(20);
		text.setStyle(Text.REGULAR);
		text.setString(value);
		text.setPosition(Constant.WINDOW_WIDTH - 320 + Constant.UI_PADDING + 160, Constant.UI_PADDING + y);
		_app.draw(text);
	  }

	  _index++;
	}

	void	refresh(int frame, int x, int y) {
	  _index = 0;

	  // Background
	  RectangleShape shape = new RectangleShape();
	  shape.setSize(new Vector2f(400, Constant.WINDOW_HEIGHT));
	  shape.setFillColor(new Color(200, 200, 200, 200));
	  shape.setPosition(new Vector2f(Constant.WINDOW_WIDTH - 320, 0));
	  _app.draw(shape);

	  BaseItem item = WorldMap.getInstance().getItem(x, y);

	  Log.debug("pos: " + x + " x " + y);
	  Log.debug("item: " + item);

	  if (item != null) {
		addDebug("type", String.valueOf(item.getType()));
		addDebug("pos", item.getX() + " x " + item.getY());
		addDebug("zone req.", String.valueOf(item.getZoneIdRequired()));
		addDebug("zone", String.valueOf(item.getZoneId()));
		addDebug("room", String.valueOf(item.getRoomId()));
	  }
	}

}
