package alone.in.DeepSpace.UserInterface;
import java.io.File;
import java.io.IOException;

import org.jsfml.graphics.Font;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.Transform;

import alone.in.DeepSpace.Models.BaseItem;
import alone.in.DeepSpace.Utils.Constant;
import alone.in.DeepSpace.World.WorldArea;


public class UserInterfaceMenuInfo extends UserSubInterface {

	void  setArea(WorldArea area) { _area = area; }
	  void  setItem(BaseItem item) { _item = item; }
	  WorldArea	getArea() { return _area; }
	  BaseItem		getItem() { return _item; }

	  RenderWindow     _app;
	  Font				_font;
	  Sprite			_background;
	  Texture			_backgroundTexture;
	  WorldArea			_area;
	  BaseItem				_item;
	  int					_line;

	  private static final int MENU_AREA_FONT_SIZE = 20;
	  private static final int MENU_AREA_CONTENT_FONT_SIZE = 16;
	  private static final int MENU_AREA_MESSAGE_FONT_SIZE = 16;

	  private static final int MENU_PADDING_TOP = 34;
	  private static final int MENU_PADDING_LEFT = 16;

	  UserInterfaceMenuInfo(RenderWindow app) throws IOException {
		  super(app, 0);
		  
		  _app = app;
		  _area = null;
		  _item = null;
	  }

	  void	init() throws IOException {
		  _backgroundTexture = new Texture();
		  _backgroundTexture.loadFromFile((new File("res/menu1.png")).toPath());
		  _background = new Sprite();
		  _background.setTexture(_backgroundTexture);
		  _background.setTextureRect(new IntRect(0, 0, 380, 420));

		  _font = new Font();
		  _font.loadFromFile((new File("res/fonts/xolonium_regular.otf")).toPath());
	  }

	  void	addLine(RenderStates render, final String label, final String value) {
	    addLine(render, label + ": " + value);
	  }

	  void	addLine(RenderStates render, final String label, int value) {
	    addLine(render, label + ": " + value);
	  }

	  void	addLine(RenderStates render, final String str) {
	    Text text = new Text();
	    text.setString(str);
	    text.setFont(_font);
	    text.setCharacterSize(MENU_AREA_CONTENT_FONT_SIZE);
	    text.setStyle(Text.REGULAR);
	    text.setPosition(MENU_PADDING_LEFT + 0, MENU_PADDING_TOP + 32 + (_line++ * 24));
	    _app.draw(text, render);
	  }

	  void	refresh(int frame) {

	    BaseItem item = _item != null ? _item : _area;

	    Transform			transform = new Transform();
	    transform = Transform.translate(transform, Constant.WINDOW_WIDTH - 380 - 64, 250);
	    RenderStates		render = new RenderStates(transform);

	    // Background
	    _app.draw(_background, render);

	    if (item != null) {

	      // Name
	  	final String name = item.getName();
	  	if (name != null) {
	  	  Text text = new Text();
	  	  // oss + item.getType();
	  	  text.setString(name);
	  	  text.setFont(_font);
	  	  text.setCharacterSize(MENU_AREA_FONT_SIZE);
	  	  text.setStyle(Text.REGULAR);
	  	  text.setPosition(MENU_PADDING_LEFT + 0, MENU_PADDING_TOP);
	  	  _app.draw(text, render);
	  	}

	  	_line = 0;
	  	addLine(render, "Pos: " + item.getX() + " x " + item.getY());
	  	addLine(render, "Oxygen", _area.getOxygen());
	  	addLine(render, "Owner", item.getOwner() != null ? item.getOwner().getName() : "null");
	  	// addLine(render, "ItemInfo", item.getItemInfo());
	  	addLine(render, "Width", item.getWidth());
	  	addLine(render, "Height", item.getHeight());
	  	addLine(render, "Type", item.getType().ordinal());
	  	addLine(render, "ZoneId", item.getZoneId());
	  	addLine(render, "ZoneIdRequired", item.getZoneIdRequired());
	  	addLine(render, "RoomId", item.getRoomId());
	  	addLine(render, "Id", item.getId());
	  	addLine(render, "Matter", item.getMatter() + " (supply: " + item.getMatterSupply() + ")");
	  	addLine(render, "Power", item.power + " (supply: " + item.powerSupply + ")");
	  	addLine(render, "Solid", item.isSolid ? "True" : "False");
	  	addLine(render, "Free", item.isFree() ? "True" : "False");
	  	addLine(render, "SleepingItem", item.isSleepingItem() ? "True" : "False");
	  	addLine(render, "Structure", item.isStructure() ? "True" : "False");
	    }
	  }
}
