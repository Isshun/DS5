package alone.in.DeepSpace.UserInterface;
import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import alone.in.DeepSpace.Managers.SpriteManager;
import alone.in.DeepSpace.Models.BaseItem;
import alone.in.DeepSpace.Utils.Constant;
import alone.in.DeepSpace.Utils.ObjectPool;
import alone.in.DeepSpace.World.WorldArea;


public class PanelInfo extends UserSubInterface {

	void  setArea(WorldArea area) { _area = area; }
	  void  setItem(BaseItem item) { _item = item; }
	  WorldArea	getArea() { return _area; }
	  BaseItem		getItem() { return _item; }

	  RenderWindow 		_app;
	  WorldArea			_area;
	  BaseItem			_item;
	  int				_line;

	  private static final int MENU_AREA_FONT_SIZE = 20;
	  private static final int MENU_AREA_CONTENT_FONT_SIZE = 16;
	  private static final int MENU_AREA_MESSAGE_FONT_SIZE = 16;

	  private static final int MENU_PADDING_TOP = 34;
	  private static final int MENU_PADDING_LEFT = 16;
	  
	  private static final int FRAME_WIDTH = 380;
	  private static final int FRAME_HEIGHT = Constant.WINDOW_HEIGHT;
	  
	  PanelInfo(RenderWindow app) throws IOException {
		  super(app, 0, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 0), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));
		  
		  setBackgroundColor(new Color(0, 0, 0, 150));
		  
		  _app = app;
		  _area = null;
		  _item = null;
	  }

	  void	addLine(final String label, final String value) {
		  addLine(label + ": " + value);
	  }

	  void	addLine(final String label, int value) {
	    addLine(label + ": " + value);
	  }

	  void	addLine(final String str) {
	    Text text = ObjectPool.getText();
	    text.setString(str);
	    text.setFont(SpriteManager.getInstance().getFont());
	    text.setCharacterSize(MENU_AREA_CONTENT_FONT_SIZE);
	    text.setStyle(Text.REGULAR);
	    text.setPosition(MENU_PADDING_LEFT + 0, MENU_PADDING_TOP + 32 + (_line++ * 24));
	    _app.draw(text, _render);
	    ObjectPool.release(text);
	  }

	  void	refresh(int frame) {
		  super.refresh();
		  
		  if (_isVisible == false) {
			  return;
		  }

		  BaseItem item = _item != null ? _item : _area;

	    if (item != null) {

	      // Name
	  	final String name = item.getName();
	  	if (name != null) {
	  	  Text text = new Text();
	  	  // oss + item.getType();
	  	  text.setString(name);
	  	  text.setFont(SpriteManager.getInstance().getFont());
	  	  text.setCharacterSize(MENU_AREA_FONT_SIZE);
	  	  text.setStyle(Text.REGULAR);
	  	  text.setPosition(MENU_PADDING_LEFT + 0, MENU_PADDING_TOP);
	  	  _app.draw(text, _render);
	  	}

	  	_line = 0;
	  	addLine("Pos: " + item.getX() + " x " + item.getY());
	  	addLine("Oxygen", _area.getOxygen());
	  	addLine("Owner", item.getOwner() != null ? item.getOwner().getName() : "null");
	  	// addLine(render, "ItemInfo", item.getItemInfo());
	  	addLine("Width", item.getWidth());
	  	addLine("Height", item.getHeight());
	  	addLine("Type", item.getType().ordinal());
	  	addLine("ZoneId", item.getZoneId());
	  	addLine("ZoneIdRequired", item.getZoneIdRequired());
	  	addLine("RoomId", item.getRoomId());
	  	addLine("Id", item.getId());
	  	addLine("Matter", item.getMatter() + " (supply: " + item.getMatterSupply() + ")");
	  	addLine("Power", item.power + " (supply: " + item.powerSupply + ")");
	  	addLine("Solid", item.isSolid ? "True" : "False");
	  	addLine("Free", item.isFree() ? "True" : "False");
	  	addLine("SleepingItem", item.isSleepingItem() ? "True" : "False");
	  	addLine("Structure", item.isStructure() ? "True" : "False");
	    }
	  }
}
