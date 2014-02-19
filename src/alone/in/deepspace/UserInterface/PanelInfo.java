package alone.in.deepspace.UserInterface;
import java.io.IOException;
import java.util.List;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.Managers.CharacterManager;
import alone.in.deepspace.Managers.JobManager;
import alone.in.deepspace.Managers.SpriteManager;
import alone.in.deepspace.Models.BaseItem;
import alone.in.deepspace.UserInterface.Utils.OnClickListener;
import alone.in.deepspace.UserInterface.Utils.UIText;
import alone.in.deepspace.UserInterface.Utils.UIView;
import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.Utils.ObjectPool;
import alone.in.deepspace.World.UserItem;
import alone.in.deepspace.World.WorldArea;


public class PanelInfo extends UserSubInterface {

//	  void  setItem(BaseItem item) { _item = item; }
	  WorldArea	getArea() { return _area; }
	  BaseItem		getItem() { return _item; }

	  WorldArea			_area;
	  BaseItem			_item;
	  int				_line;
	private UIText _primaryName;
	private UIText _itemName;
	private PanelInfoItemOptions _itemOptions;

	  private static final int MENU_AREA_FONT_SIZE = 20;
	  private static final int MENU_AREA_CONTENT_FONT_SIZE = 16;

	  private static final int MENU_PADDING_TOP = 34;
	  private static final int MENU_PADDING_LEFT = 16;
	  
	  private static final int FRAME_WIDTH = 380;
	  private static final int FRAME_HEIGHT = Constant.WINDOW_HEIGHT;
	  
	  PanelInfo(RenderWindow app) throws IOException {
		  super(app, 0, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 0), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));
		  
		  setBackgroundColor(new Color(0, 0, 0, 150));
		  
		  _primaryName = new UIText(null);
		  _primaryName.setPosition(10, 40);
		  _primaryName.setCharacterSize(22);
		  addView(_primaryName);
		  
		  int itemOffset = 200;
		  
		  _itemName = new UIText(null);
		  _itemName.setPosition(10, itemOffset + 40);
		  _itemName.setCharacterSize(22);
		  addView(_itemName);
		  
		  UIText sep = new UIText(null);
		  sep.setPosition(0, 200);
		  sep.setCharacterSize(22);
		  sep.setString("----------------------------------");
		  addView(sep);

	  }

	  void	addLine(final RenderWindow app, final String label, final String value) {
		  addLine(app, label + ": " + value);
	  }

	  void	addLine(final RenderWindow app, final String label, int value) {
	    addLine(app, label + ": " + value);
	  }

	  void	addLine(final RenderWindow app, final String str) {
	    Text text = ObjectPool.getText();
	    text.setString(str);
	    text.setFont(SpriteManager.getInstance().getFont());
	    text.setCharacterSize(MENU_AREA_CONTENT_FONT_SIZE);
	    text.setStyle(Text.REGULAR);
	    text.setPosition(MENU_PADDING_LEFT + 0, MENU_PADDING_TOP + 32 + (_line++ * 24));
	    app.draw(text, _render);
	    ObjectPool.release(text);
	  }

	  void  setArea(WorldArea area) {
		  _area = area;
		  
		  if (area != null) {
			  setItem(area.getItem());
			  _primaryName.setString(area.getName());
		  } else {
			  setItem(null);
		  }
	  }
	  
	  private void setItem(final UserItem item) {
		  _item = item;
		  
		  if (_itemOptions != null) {
			  List<UIText> texts = _itemOptions.getOptions();
			  for (UIText text: texts) {
				  text.setOnClickListener(null);
				  removeView(text);
			  }
		  }
		  
		  if (item == null) {
			  _itemName.setString("");
			  return;
		  }

		  // Configure new item
		  _itemName.setString(item.getName() != null ? item.getName() : "?");
		  _itemOptions = new PanelInfoItemOptions(20, 280);
		  addView(_itemOptions.add("Remove", new OnClickListener() {
			  @Override
			  public void onClick(UIView view) {
				  JobManager.getInstance().storeItem(item);
			  }
		  }));
		  addView(_itemOptions.add("Destroy", new OnClickListener() {
			  @Override
			  public void onClick(UIView view) {
				  JobManager.getInstance().destroyItem(item);
			  }
		  }));
		  addView(_itemOptions.add("Add character", new OnClickListener() {
			  @Override
			  public void onClick(UIView view) {
				  CharacterManager.getInstance().add(item.getX(), item.getY());
			  }
		  }));
		  addView(_itemOptions.add("kill everyone", new OnClickListener() {
			  @Override
			  public void onClick(UIView view) {
				  CharacterManager.getInstance().clear();
			  }
		  }));
	  }
	  
	@Override
	  public void onRefresh(RenderWindow app) {
		  BaseItem item = _area != null ? _area.getItem() : null;

		  if (item != null) {

//			  // Name
//			  final String name = item.getName();
//	  	if (name != null) {
//	  	  Text text = new Text();
//	  	  // oss + item.getType();
//	  	  text.setString(name);
//	  	  text.setFont(SpriteManager.getInstance().getFont());
//	  	  text.setCharacterSize(MENU_AREA_FONT_SIZE);
//	  	  text.setStyle(Text.REGULAR);
//	  	  text.setPosition(MENU_PADDING_LEFT + 0, MENU_PADDING_TOP);
//	  	app.draw(text, _render);
//	  	}

//	  	_line = 0;
//	  	addLine(app, "Pos: " + item.getX() + " x " + item.getY());
//	  	addLine(app, "Oxygen", _area.getOxygen());
//	  	addLine(app, "Owner", item.getOwner() != null ? item.getOwner().getName() : "null");
//	  	// addLine(render, "ItemInfo", item.getItemInfo());
//	  	addLine(app, "Width", item.getWidth());
//	  	addLine(app, "Height", item.getHeight());
//	  	addLine(app, "Type", item.getType().ordinal());
//	  	addLine(app, "ZoneId", item.getZoneId());
//	  	addLine(app, "ZoneIdRequired", item.getZoneIdRequired());
//	  	addLine(app, "RoomId", item.getRoomId());
//	  	addLine(app, "Id", item.getId());
//	  	addLine(app, "Matter", item.getMatter() + " (supply: " + item.getMatterSupply() + ")");
//	  	addLine(app, "Power", item.power + " (supply: " + item.powerSupply + ")");
//	  	addLine(app, "Solid", item.isSolid ? "True" : "False");
//	  	addLine(app, "Free", item.isFree() ? "True" : "False");
//	  	addLine(app, "SleepingItem", item.isSleepingItem() ? "True" : "False");
//	  	addLine(app, "Structure", item.isStructure() ? "True" : "False");
	    }
	  }
}
