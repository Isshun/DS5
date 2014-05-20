package alone.in.deepspace.UserInterface.Panels;

import java.io.IOException;
import java.util.List;

import javax.swing.text.html.ImageView;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.Strings;
import alone.in.deepspace.UserInterface.UserSubInterface;
import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.Utils.ObjectPool;
import alone.in.deepspace.engine.ui.ButtonView;
import alone.in.deepspace.engine.ui.OnClickListener;
import alone.in.deepspace.engine.ui.FrameLayout;
import alone.in.deepspace.engine.ui.TextView;
import alone.in.deepspace.engine.ui.View;
import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.RoomManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.manager.SpriteManager;
import alone.in.deepspace.model.BaseItem;
import alone.in.deepspace.model.Room;
import alone.in.deepspace.model.StorageItem;
import alone.in.deepspace.model.StructureItem;
import alone.in.deepspace.model.UserItem;
import alone.in.deepspace.model.WorldArea;

public class PanelInfo extends UserSubInterface {

	private static final int FONT_SIZE = 20;
	private static final int LINE_HEIGHT = 28;

	public WorldArea				getArea() { return _area; }
	public BaseItem					getItem() { return _item; }

	private WorldArea				_area;
	private BaseItem				_item;
	private int						_line;
	private TextView 					_primaryName;
	private TextView 					_itemName;
	private PanelInfoItemOptions	_itemOptions;
	private TextView _itemMatter;
	private StructureItem 	_structure;
	private PanelInfoItemOptions _structureOptions;
	private TextView _lbRoom;
	private TextView _itemStorage;
	private TextView light;
	private TextView[] _lbCarry;
	private FrameLayout _itemGather;
	private TextView _itemGatherProduce;
	private FrameLayout _itemMine;
	private TextView _itemMineProduce;
	private ButtonView _itemGatherIcon;
	private ButtonView _itemMineIcon;

	private static final int 		MENU_AREA_CONTENT_FONT_SIZE = 16;

	private static final int 		MENU_PADDING_TOP = 34;
	private static final int 		MENU_PADDING_LEFT = 16;

	private static final int 		FRAME_WIDTH = Constant.PANEL_WIDTH;
	private static final int 		FRAME_HEIGHT = Constant.WINDOW_HEIGHT;

	public PanelInfo(RenderWindow app) throws IOException {
		super(app, 0, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 0), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));

		setBackgroundColor(new Color(0, 0, 0, 150));

		_primaryName = new TextView(null);
		_primaryName.setPosition(10, 40);
		_primaryName.setCharacterSize(22);
		addView(_primaryName);

		_lbRoom = new TextView(null);
		_lbRoom.setPosition(200, 40);
		_lbRoom.setCharacterSize(22);
		addView(_lbRoom);

		int itemOffset = 200;

		createGatherView();
		createMiningView();
		
		_itemName = new TextView(null);
		_itemName.setPosition(10, itemOffset + 40);
		_itemName.setCharacterSize(22);
		addView(_itemName);

		_itemMatter = new TextView(null);
		_itemMatter.setPosition(10, itemOffset + 60);
		_itemMatter.setCharacterSize(14);
		addView(_itemMatter);

		_itemStorage = new TextView(null);
		_itemStorage.setPosition(10, itemOffset + 200);
		_itemStorage.setCharacterSize(14);
		addView(_itemStorage);

		_lbCarry = new TextView[42];
		for (int i = 0; i < 42; i++) {
			_lbCarry[i] = new TextView(new Vector2f(FRAME_WIDTH, LINE_HEIGHT));
			_lbCarry[i].setCharacterSize(12);
			_lbCarry[i].setColor(Color.WHITE);
			_lbCarry[i].setPosition(new Vector2f(20, 20 + 464 + i * 28));
			addView(_lbCarry[i]);
		}

		TextView sep = new TextView(null);
		sep.setPosition(0, 200);
		sep.setCharacterSize(22);
		sep.setString("----------------------------------");
		addView(sep);

		light = new TextView(null);
		light.setPosition(0, 180);
		light.setCharacterSize(22);
		light.setString("----------------------------------");
		addView(light);
	}

	private void createGatherView() {
		_itemGather = new FrameLayout(new Vector2f(120, 200)) {
			@Override
			public void onRefresh(RenderWindow app) {

			}
		};
		_itemGather.setPosition(20, 400);
		addView(_itemGather);
		
		TextView lbTitle = new TextView(new Vector2f(10, 10));
		lbTitle.setString(Strings.PRODUCT_WHEN_GATHERED);
		lbTitle.setCharacterSize(MENU_AREA_CONTENT_FONT_SIZE);
		_itemGather.addView(lbTitle);
		
		_itemGatherProduce = new TextView(new Vector2f(10, 10));
		_itemGatherProduce.setPosition(32, 28);
		_itemGatherProduce.setCharacterSize(MENU_AREA_CONTENT_FONT_SIZE);
		_itemGather.addView(_itemGatherProduce);
		
		_itemGatherIcon = new ButtonView(new Vector2f(32, 32));
		_itemGatherIcon.setPosition(0, 28);
		_itemGather.addView(_itemGatherIcon);
	}

	private void createMiningView() {
		_itemMine = new FrameLayout(new Vector2f(120, 200)) {
			@Override
			public void onRefresh(RenderWindow app) {

			}
		};
		_itemMine.setPosition(20, 400);
		addView(_itemMine);
		
		TextView lbTitle = new TextView(new Vector2f(10, 10));
		lbTitle.setString(Strings.PRODUCT_WHEN_MINED);
		lbTitle.setCharacterSize(MENU_AREA_CONTENT_FONT_SIZE);
		_itemMine.addView(lbTitle);
		
		_itemMineProduce = new TextView(new Vector2f(10, 10));
		_itemMineProduce.setPosition(32, 28);
		_itemMineProduce.setCharacterSize(MENU_AREA_CONTENT_FONT_SIZE);
		_itemMine.addView(_itemMineProduce);

		_itemMineIcon = new ButtonView(new Vector2f(32, 32));
		_itemMineIcon.setPosition(0, 28);
		_itemMine.addView(_itemMineIcon);
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

	public void  setArea(WorldArea area) {
		_area = area;

		light.setString("light: " + String.valueOf(area.getLight()));

		if (area != null) {
			Room room = RoomManager.getInstance().get(area.getX(), area.getY());
			_lbRoom.setString(room != null ? room.getName() : "");
			if (area.getItem() != null) {
				setItem(area.getItem());
			} else {
				setItem(area.getRessource());
			}
			if (area.getStructure() != null) {
				setStructure(area.getStructure());
			} else {
				_primaryName.setString(area.getName() + " (" + area.getX() + "x" + area.getY() + ")");
			}
		} else {
			setItem(null);
		}
	}

	private void  setStructure(final StructureItem structure) {
		_structure = structure;

		if (_structureOptions != null) {
			List<TextView> texts = _structureOptions.getOptions();
			for (TextView text: texts) {
				text.setOnClickListener(null);
				removeView(text);
			}
		}

		if (structure != null) {
			_primaryName.setString(structure.getName() + " (" + structure.getX() + "x" + structure.getY() + ")");

			// TODO
			if (structure.getName().equals("base.door")) {
				_structureOptions = new PanelInfoItemOptions(20, 100);
				addView(_structureOptions.add("Automatic opening", new OnClickListener() {
					@Override
					public void onClick(View view) {
						structure.setMode(0);
						structure.setSolid(false);
					}
				}));
				addView(_structureOptions.add("Still open", new OnClickListener() {
					@Override
					public void onClick(View view) {
						structure.setMode(2);
						structure.setSolid(false);
					}
				}));
				addView(_structureOptions.add("Locked", new OnClickListener() {
					@Override
					public void onClick(View view) {
						structure.setMode(1);
						structure.setSolid(true);
					}
				}));
			}
		}
	}

	private void setItem(final UserItem item) {
		_item = item;

		if (_itemOptions != null) {
			List<TextView> texts = _itemOptions.getOptions();
			for (TextView text: texts) {
				text.setOnClickListener(null);
				removeView(text);
			}
		}

		if (item == null) {
			_itemGather.setVisible(false);
			_itemMine.setVisible(false);
			_itemName.setString("");
			_itemMatter.setString("");
			_itemStorage.setString("");
			return;
		}

		// Configure new item
		_itemName.setString(item.getName() != null ? item.getName() : "?");
		_itemMatter.setString(String.valueOf(item.getMatterSupply()));
		_itemOptions = new PanelInfoItemOptions(20, 280);
		addView(_itemOptions.add("Remove", new OnClickListener() {
			@Override
			public void onClick(View view) {
				JobManager.getInstance().storeItem(item);
			}
		}));
		addView(_itemOptions.add("Destroy", new OnClickListener() {
			@Override
			public void onClick(View view) {
				JobManager.getInstance().destroyItem(item);
			}
		}));
		addView(_itemOptions.add("Add character", new OnClickListener() {
			@Override
			public void onClick(View view) {
				ServiceManager.getCharacterManager().add(item.getX(), item.getY());
			}
		}));
		addView(_itemOptions.add("kill everyone", new OnClickListener() {
			@Override
			public void onClick(View view) {
				ServiceManager.getCharacterManager().clear();
			}
		}));
		
		// Gatherable item
		if (item.getInfo().onGather != null) {
			_itemGather.setVisible(true);
			_itemGatherProduce.setString(item.getInfo().onGather.itemProduce.label);
			_itemGatherIcon.setIcon(SpriteManager.getInstance().getIcon(item.getInfo().onGather.itemProduce));
		} else {
			_itemGather.setVisible(false);
		}

		// Minable item
		if (item.getInfo().onMine != null) {
			_itemMine.setVisible(true);
			_itemMineProduce.setString(item.getInfo().onMine.itemProduce.label);
			_itemMineIcon.setIcon(SpriteManager.getInstance().getIcon(item.getInfo().onMine.itemProduce));
		} else {
			_itemMine.setVisible(false);
		}
	}

	@Override
	public void onRefresh(RenderWindow app) {
		BaseItem item = _area != null ? _area.getItem() : null;
		if (item == null && _area != null) {
			item = _area.getRessource();
		}

//		// TODO
//		_itemGather.refresh(app);
//		_itemMine.refresh(app);
		
		if (item != null) {

			if (item.isStorage()) {
				StorageItem storage = ((StorageItem)item);
				if (storage.getItems().size() > 0) {
					_itemStorage.setString("Storage: " + storage.getItems().size());
					for (int i = 0; i < 42; i++) {
						if (i < storage.getItems().size()) {
							_lbCarry[i].setString(storage.getItems().get(i).getName());
						} else {
							_lbCarry[i].setString("");
						}
					}
				} else {
					_itemStorage.setString("Storage: empty");
				}
			}
			
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
