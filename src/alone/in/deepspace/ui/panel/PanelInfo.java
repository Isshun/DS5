package alone.in.deepspace.ui.panel;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.Strings;
import alone.in.deepspace.engine.ui.ButtonView;
import alone.in.deepspace.engine.ui.FrameLayout;
import alone.in.deepspace.engine.ui.ImageView;
import alone.in.deepspace.engine.ui.OnClickListener;
import alone.in.deepspace.engine.ui.TextView;
import alone.in.deepspace.engine.ui.View;
import alone.in.deepspace.manager.RoomManager;
import alone.in.deepspace.manager.SpriteManager;
import alone.in.deepspace.model.BaseItem;
import alone.in.deepspace.model.ItemInfo;
import alone.in.deepspace.model.ItemInfo.ItemInfoAction;
import alone.in.deepspace.model.ItemInfo.ItemInfoEffects;
import alone.in.deepspace.model.Room;
import alone.in.deepspace.model.StorageItem;
import alone.in.deepspace.model.StructureItem;
import alone.in.deepspace.model.WorldArea;
import alone.in.deepspace.model.WorldResource;
import alone.in.deepspace.ui.UserInterface;
import alone.in.deepspace.ui.UserSubInterface;
import alone.in.deepspace.util.Constant;
import alone.in.deepspace.util.ObjectPool;
import alone.in.deepspace.util.StringUtils;

public class PanelInfo extends UserSubInterface {
	private static final int 		LINE_HEIGHT = 28;
	private static final int 		MENU_AREA_CONTENT_FONT_SIZE = 16;
	private static final int 		MENU_PADDING_TOP = 34;
	private static final int 		MENU_PADDING_LEFT = 16;
	private static final int 		FRAME_WIDTH = Constant.PANEL_WIDTH;
	private static final int 		FRAME_HEIGHT = Constant.WINDOW_HEIGHT;
	private static final int 		INVENTORY_NB_COLS = 10;
	private static final int 		INVENTORY_ITEM_SIZE = 32;
	private static final int 		INVENTORY_ITEM_SPACE = 4;

	private WorldArea				_area;
	private int						_line;
	private TextView 				_itemName;
	private PanelInfoItemOptions	_itemOptions;
	private TextView 				_itemMatter;
	private StructureItem 			_structure;
	private PanelInfoItemOptions 	_structureOptions;
	private TextView 				_lbRoom;
	private ImageView[]				_lbCarry;
	private FrameLayout 			_itemGather;
	private TextView 				_itemGatherProduce;
	private FrameLayout 			_itemMine;
	private TextView 				_itemMineProduce;
	private ButtonView 				_itemGatherIcon;
	private ButtonView 				_itemMineIcon;
	private TextView 				_itemStorage;
	private FrameLayout 			_layoutItem;
	private ImageView 				_itemIcon;
	private TextView 				_itemOwner;
	private TextView 				_itemCategory;
	private TextView 				_areaName;
	private TextView 				_areaCategory;
	private ImageView 				_areaIcon;
	private FrameLayout 			_layoutArea;
	private TextView 				_areaLight;
	private TextView 				_areaPos;
	private TextView 				_itemPower;
	private FrameLayout 			_itemAction;
	private TextView 				_itemProduceName;
	private ButtonView			 	_itemProduceIcon;
	private TextView 				_itemSlots;
	private TextView 				_itemUsed;
	private FrameLayout 			_layoutStorage;
	private FrameLayout 			_layoutEffects;
	private TextView[] 				_itemEffects;
	private TextView 				_itemAccept;
	private TextView[] 				_lbCarryCount;
	private ButtonView 				_btStorageFilter;
	private FrameLayout 			_layoutStorageAdvancedFilter;
	private BaseItem 				_item;
	private CheckBoxView 			_cbFood;
	private CheckBoxView 			_cbDrink;
	private CheckBoxView 			_cbConsomable;
	private CheckBoxView 			_cbGarbage;
	private ItemInfo				_itemInfo;
	private WorldResource _resource;
	private FrameLayout _itemActionProduce;
	private FrameLayout _layoutItemProduce;
	private FrameLayout _layoutStorageSimpleFilter;

	public PanelInfo(RenderWindow app, UserInterface ui) throws IOException {
		super(app, 0, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 32), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT - 32), ui);

		setBackgroundColor(new Color(0, 0, 0, 150));

		_lbRoom = new TextView(null);
		_lbRoom.setPosition(200, 40);
		_lbRoom.setCharacterSize(22);
		addView(_lbRoom);

		createAreaInfoView(20, 4);
		createItemInfoView(20, 4);
		createGatherView();
		createMiningView();
		createItemActionView();
		createEffectsView(20, 200);
		createViewStorage(20, 400);
	}

	private void createStorageFilterAdvancedView(int x, int y) {
		_layoutStorageAdvancedFilter = new FrameLayout(new Vector2f(200, 400));
		_layoutStorageAdvancedFilter.setPosition(x, y);
		_layoutStorage.addView(_layoutStorageAdvancedFilter);

		_cbFood = addStorageFilterCheckBox(0, 42, "food");
		_cbDrink = addStorageFilterCheckBox(195, 42, "drink");
		_cbConsomable = addStorageFilterCheckBox(0, 66, "consomable");
		_cbGarbage = addStorageFilterCheckBox(195, 66, "garbage");

		// Label select
		{
			TextView text = new LinkView(new Vector2f(150, 20));
			text.setString("select all");
			text.setPosition(0, 16);
			text.setCharacterSize(FONT_SIZE);
			text.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					_cbFood.setChecked(true);
					_cbDrink.setChecked(true);
					_cbConsomable.setChecked(true);
					_cbGarbage.setChecked(true);
					if (_item != null && _item.isStorage()) {
						((StorageItem)_item).setStorageFilter(true, true, true, true);
					}
				}
			});
			_layoutStorageAdvancedFilter.addView(text);
		}
		
		// Label unselect
		{
			TextView text = new LinkView(new Vector2f(150, 20));
			text.setString("unselect all");
			text.setPosition(195, 16);
			text.setCharacterSize(FONT_SIZE);
			text.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					_cbFood.setChecked(false);
					_cbDrink.setChecked(false);
					_cbConsomable.setChecked(false);
					_cbGarbage.setChecked(false);
					if (_item != null && _item.isStorage()) {
						((StorageItem)_item).setStorageFilter(false, false, false, false);
					}
				}
			});
			_layoutStorageAdvancedFilter.addView(text);
		}
	}

	private void createStorageFilterSimpleView(int x, int y) {
		_layoutStorageSimpleFilter = new FrameLayout(new Vector2f(200, 400));
		_layoutStorageSimpleFilter.setPosition(x, y);
		_layoutStorage.addView(_layoutStorageSimpleFilter);

		_itemAccept = new TextView(null);
		_itemAccept.setPosition(0, 42);
		_itemAccept.setCharacterSize(FONT_SIZE);
		_itemAccept.setColor(COLOR_TEXT);
		_layoutStorageSimpleFilter.addView(_itemAccept);
	}

	private CheckBoxView addStorageFilterCheckBox(int x, int y, String label) {
		final CheckBoxView checkBox = new CheckBoxView(new Vector2f(150, 20));
		checkBox.setString(label);
		checkBox.setPosition(x, y);
		checkBox.setCharacterSize(FONT_SIZE);
		checkBox.setColor(COLOR_TEXT);
		checkBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				checkBox.toogleChecked();
				if (_item != null && _item.isStorage()) {
					((StorageItem)_item).setStorageFilter(
						_cbFood.getChecked(),
						_cbDrink.getChecked(),
						_cbConsomable.getChecked(),
						_cbGarbage.getChecked()
					);
				}
			}
		});
		checkBox.toogleChecked();
		_layoutStorageAdvancedFilter.addView(checkBox);
		
		return checkBox;
	}

	private void createViewStorage(int x, int y) {
		_layoutStorage = new FrameLayout(new Vector2f(200, 400));
		_layoutStorage.setPosition(x, y);
		addView(_layoutStorage);

		TextView lbAccept = new TextView(null);
		lbAccept.setPosition(0, 100);
		lbAccept.setString("Items accepted");
		lbAccept.setCharacterSize(FONT_SIZE_TITLE);
		_layoutStorage.addView(lbAccept);
		
		_itemStorage = new TextView(null);
		_itemStorage.setPosition(0, 0);
		_itemStorage.setCharacterSize(FONT_SIZE_TITLE);
		_layoutStorage.addView(_itemStorage);
		
		createStorageFilterSimpleView(0, 120);
		createStorageFilterAdvancedView(0, 120);

//		_btStorageFilter = new ButtonView(new Vector2f(100, 32));
//		_btStorageFilter.setPosition(300, 10);
//		_btStorageFilter.setString("filter");
//		_btStorageFilter.setCharacterSize(16);
//		_btStorageFilter.setBackgroundColor(Color.RED);
//		_btStorageFilter.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
////				_layoutStorageFilter.setVisible(true);
////				_layoutStorage.setVisible(false);
//			}
//		});
//		_btStorageFilter.setOnFocusListener(new OnFocusListener() {
//			@Override
//			public void onExit(View view) {
//				_btStorageFilter.setBackgroundColor(Color.RED);
//			}
//			
//			@Override
//			public void onEnter(View view) {
//				_btStorageFilter.setBackgroundColor(Color.GREEN);
//			}
//		});
//		_layoutStorage.addView(_btStorageFilter);

		_lbCarry = new ImageView[42];
		_lbCarryCount = new TextView[42];
		for (int i = 0; i < 42; i++) {
			int x2 = i % INVENTORY_NB_COLS;
			int y2 = i / INVENTORY_NB_COLS;
			_lbCarry[i] = new ImageView();
			_lbCarry[i].setId(100 + i);
			_lbCarry[i].setPosition(new Vector2f(x2 * INVENTORY_ITEM_SIZE + INVENTORY_ITEM_SPACE, 32 + y2 * INVENTORY_ITEM_SIZE + INVENTORY_ITEM_SPACE));
			_layoutStorage.addView(_lbCarry[i]);
			_lbCarryCount[i] = new TextView();
			_lbCarryCount[i].setCharacterSize(10);
			_lbCarryCount[i].setColor(Color.WHITE);
			_lbCarryCount[i].setPosition(new Vector2f(x2 * INVENTORY_ITEM_SIZE + INVENTORY_ITEM_SPACE + 16, 32 + y2 * INVENTORY_ITEM_SIZE + INVENTORY_ITEM_SPACE + 16));
			_layoutStorage.addView(_lbCarryCount[i]);
		}
	}

	private void createEffectsView(int x, int y) {
		_layoutEffects = new FrameLayout(new Vector2f(FRAME_WIDTH, 80));
		_layoutEffects.setPosition(x, y);

		TextView lbEffect = new TextView(null);
		lbEffect.setPosition(x + 10, y + 10);
		lbEffect.setCharacterSize(16);
		lbEffect.setString(Strings.LB_EFFECTS);
		_layoutEffects.addView(lbEffect);

		_itemEffects = new TextView[10];
		for (int i = 0; i < 10; i++) {
			_itemEffects[i] = new TextView(null);
			_itemEffects[i].setPosition(x + 24, y + 34 + i * 20);
			_itemEffects[i].setCharacterSize(12);
			_layoutEffects.addView(_itemEffects[i]);
		}

		_layoutItem.addView(_layoutEffects);
	}

	private void createItemInfoView(int x, int y) {
		_layoutItem = new FrameLayout(new Vector2f(FRAME_WIDTH, 80));
		_layoutItem.setPosition(x, y);

		_itemName = new TextView(null);
		_itemName.setPosition(0, 0);
		_itemName.setCharacterSize(22);
		_layoutItem.addView(_itemName);

		_itemCategory = new TextView(null);
		_itemCategory.setPosition(0, 28);
		_itemCategory.setCharacterSize(14);
		_layoutItem.addView(_itemCategory);

		_itemMatter = new TextView(null);
		_itemMatter.setPosition(0, 60);
		_itemMatter.setCharacterSize(14);
		_layoutItem.addView(_itemMatter);

		_itemPower = new TextView(null);
		_itemPower.setPosition(0, 80);
		_itemPower.setCharacterSize(14);
		_layoutItem.addView(_itemPower);

		_itemOwner = new TextView(null);
		_itemOwner.setPosition(0, 100);
		_itemOwner.setCharacterSize(14);
		_layoutItem.addView(_itemOwner);

		_itemSlots = new TextView(null);
		_itemSlots.setPosition(0, 120);
		_itemSlots.setCharacterSize(14);
		_layoutItem.addView(_itemSlots);

		_itemUsed = new TextView(null);
		_itemUsed.setPosition(0, 140);
		_itemUsed.setCharacterSize(14);
		_layoutItem.addView(_itemUsed);

		_itemIcon = new ImageView();
		_layoutItem.addView(_itemIcon);

		addView(_layoutItem);
	}

	private void createAreaInfoView(int x, int y) {
		_layoutArea = new FrameLayout(new Vector2f(FRAME_WIDTH, 80));
		_layoutArea.setPosition(x, y);

		_areaName = new TextView(null);
		_areaName.setPosition(0, 0);
		_areaName.setCharacterSize(22);
		_layoutArea.addView(_areaName);

		_areaPos= new TextView(null);
		_areaPos.setPosition(FRAME_WIDTH - 100, 0);
		_areaPos.setCharacterSize(14);
		_layoutArea.addView(_areaPos);

		_areaCategory = new TextView(null);
		_areaCategory.setPosition(0, 28);
		_areaCategory.setCharacterSize(14);
		_layoutArea.addView(_areaCategory);

		_areaLight = new TextView(null);
		_areaLight.setPosition(0, 28);
		_areaLight.setCharacterSize(14);
		_layoutArea.addView(_areaLight);

		_areaIcon = new ImageView();
		_layoutArea.addView(_areaIcon);

		addView(_layoutArea);
	}

	private void createItemActionView() {
		_itemAction = new FrameLayout(new Vector2f(120, 200));
		_itemAction.setPosition(0, 200);
		_layoutItem.addView(_itemAction);

		// Item produce
		_layoutItemProduce = new FrameLayout(new Vector2f(120, 200));
		_layoutItemProduce.setPosition(0, 200);
		_layoutItem.addView(_layoutItemProduce);

		TextView lbTitle = new TextView(new Vector2f(10, 10));
		lbTitle.setString(Strings.PROVIDE);
		lbTitle.setCharacterSize(FONT_SIZE_TITLE);
		_layoutItemProduce.addView(lbTitle);

		_itemProduceName = new LinkView(new Vector2f(300, 20));
		_itemProduceName.setPosition(32, 28);
		_itemProduceName.setCharacterSize(FONT_SIZE);
		_itemProduceName.setColor(COLOR_TEXT);
		_layoutItemProduce.addView(_itemProduceName);

		_itemProduceIcon = new ButtonView(new Vector2f(32, 32));
		_itemProduceIcon.setPosition(0, 28);
		_layoutItemProduce.addView(_itemProduceIcon);
	}

	private void createGatherView() {
		_itemGather = new FrameLayout(new Vector2f(120, 200));
		_itemGather.setVisible(false);
		_itemGather.setPosition(0, 200);
		_layoutItem.addView(_itemGather);

		TextView lbTitle = new TextView(new Vector2f(10, 10));
		lbTitle.setString(Strings.PRODUCT_WHEN_GATHERED);
		lbTitle.setCharacterSize(16);
		_itemGather.addView(lbTitle);

		_itemGatherProduce = new TextView(new Vector2f(10, 10));
		_itemGatherProduce.setPosition(32, 28);
		_itemGatherProduce.setCharacterSize(14);
		_itemGather.addView(_itemGatherProduce);

		_itemGatherIcon = new ButtonView(new Vector2f(32, 32));
		_itemGatherIcon.setPosition(0, 28);
		_itemGather.addView(_itemGatherIcon);
	}

	private void createMiningView() {
		_itemMine = new FrameLayout(new Vector2f(120, 200));
		_itemMine.setPosition(0, 200);
		_itemMine.setVisible(false);
		_layoutItem.addView(_itemMine);

		TextView lbTitle = new TextView(new Vector2f(10, 10));
		lbTitle.setString(Strings.PRODUCT_WHEN_MINED);
		lbTitle.setCharacterSize(16);
		_itemMine.addView(lbTitle);

		_itemMineProduce = new TextView(new Vector2f(10, 10));
		_itemMineProduce.setPosition(32, 28);
		_itemMineProduce.setCharacterSize(14);
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

	public void  select(WorldArea area) {
		clean();
		displayArea(area);
	}
	
	public void displayArea(WorldArea area) {
		_area = area;
		if (area == null) {
			return;
		}

		if (area.getItem() != null) {
			displayItem(area.getItem());
			return;
		}

		if (area.getRessource() != null) {
			displayResource(area.getRessource());
			return;
		}

		// Display area
		_layoutArea.setVisible(true);

		_areaLight.setString("luminosity: " + (int)Math.min(area.getLight() * 100, 100));

		Room room = RoomManager.getInstance().get(area.getX(), area.getY());
		_lbRoom.setString(room != null ? room.getName() : "");
		if (area.getItem() != null) {
		} else {
		}
		if (area.getStructure() != null) {
			setStructure(area.getStructure());
		} else {
			if (area.getStructure() != null) {
				_areaName.setString(area.getStructure().getLabel());
			} else {
				_areaName.setString(Strings.LB_GROUND);
			}
			_areaPos.setString("(" + area.getX() + "x" + area.getY() + ")");
		}
	}

	private void displayResource(WorldResource resource) {
		_resource = resource;
		if (_resource == null) {
			return;
		}
		
		// Display resource
		_layoutItem.setVisible(true);

		_itemName.setString(resource.getLabel() != null ? resource.getLabel() : resource.getName());
		_itemCategory.setString("(" + resource.getLabelCategory() + ")");
		_itemMatter.setString("Matter: " + String.valueOf(resource.getMatterSupply()));
		if (resource.getOwner() != null) {
			_itemOwner.setString("Owner: " + resource.getOwner().getName());
		} else {
			_itemOwner.setString("Owner: " + Strings.LB_NONE);
		}

		Sprite icon = SpriteManager.getInstance().getIcon(resource.getInfo());
		_itemIcon.setImage(icon);
		_itemIcon.setPosition(new Vector2f(FRAME_WIDTH - 32 - icon.getTextureRect().width, 20));

		// Gatherable item
		if (resource.getInfo().onGather != null) {
			_itemGather.setVisible(true);
			_itemGatherProduce.setString(resource.getInfo().onGather.itemProduce.label);
			_itemGatherIcon.setIcon(SpriteManager.getInstance().getIcon(resource.getInfo().onGather.itemProduce));
		} else {
			_itemGather.setVisible(false);
		}

		// Minable item
		if (resource.getInfo().onMine != null) {
			_itemMine.setVisible(true);
			_itemMineProduce.setString(resource.getInfo().onMine.itemProduce.label);
			_itemMineIcon.setIcon(SpriteManager.getInstance().getIcon(resource.getInfo().onMine.itemProduce));
		} else {
			_itemMine.setVisible(false);
		}

		_itemAction.setVisible(false);
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
			_areaName.setString(structure.getLabel());
			_areaPos.setString("(" + structure.getX() + "x" + structure.getY() + ")");

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
	
	public void displayItemInfo(ItemInfo itemInfo) {
		_itemInfo = itemInfo;
		_layoutItem.setVisible(true);

		// Basic info
		_itemName.setString(itemInfo.label != null ? itemInfo.label : itemInfo.name);
		_itemCategory.setString("(" + itemInfo.category + ")");

		// Icon
		Sprite icon = SpriteManager.getInstance().getIcon(itemInfo);
		if (icon != null) {
			_itemIcon.setImage(icon);
			_itemIcon.setPosition(new Vector2f(FRAME_WIDTH - 32 - icon.getTextureRect().width, 20));
		}
		
		// Action item
		if (itemInfo.onAction != null) {
			displayItemAction(itemInfo.onAction);
		} else {
		}

	}
	
	private void displayItemAction(ItemInfoAction action) {
		_itemAction.setVisible(true);

		if (action.itemAccept != null) {
			String str = new String();
			for (ItemInfo info: action.itemAccept) {
				str += info.label + "\n";
			}
			_itemAccept.setString(str);
			_itemAccept.setVisible(true);
		}

		// Item action produce
		if (action.itemsProduce != null) {
			_layoutItemProduce.setVisible(true);

			String str = "";
			ItemInfo produce = null;
			for (ItemInfo itemProduce: action.itemsProduce) {
				str += StringUtils.getDashedString(itemProduce.label, "x" + itemProduce.craftedQuantitfy, NB_COLUMNS - 4) + "\n";
				produce = itemProduce;
			}
			final ItemInfo finalProduce = produce;
			_itemProduceName.setString(str);
			_itemProduceName.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					_ui.select(finalProduce);
				}
			});
			_itemProduceIcon.setIcon(SpriteManager.getInstance().getIcon(action.itemsProduce.get(0)));
		}

		// Item action effects
		if (action.effects != null) {
			_layoutEffects.setVisible(true);
			ItemInfoEffects effects = action.effects;
			int line = 0;
			if (effects.drink > 0) { _itemEffects[line++].setString(Strings.LB_EFFECT_DRINK + ": " + (effects.drink > 0 ? "+" : "") + effects.drink); }
			if (effects.energy > 0) { _itemEffects[line++].setString(Strings.LB_EFFECT_ENERGY + ": " + (effects.energy> 0 ? "+" : "") + effects.energy); }
			if (effects.food > 0) { _itemEffects[line++].setString(Strings.LB_EFFECT_FOOD + ": " + (effects.food > 0 ? "+" : "") + effects.food); }
			if (effects.hapiness > 0) { _itemEffects[line++].setString(Strings.LB_EFFECT_HAPINESS + ": " + (effects.hapiness > 0 ? "+" : "") + effects.hapiness); }
			if (effects.health > 0) { _itemEffects[line++].setString(Strings.LB_EFFECT_HEALTH + ": " + (effects.health > 0 ? "+" : "") + effects.health); }
			if (effects.relation > 0) { _itemEffects[line++].setString(Strings.LB_EFFECT_RELATION + ": " + (effects.relation > 0 ? "+" : "") + effects.relation); }
			for (int i = line; i < 10; i++) {
				_itemEffects[line++].setString("");
			}
		}
	}

	public void displayItem(final BaseItem item) {
		_item = item;
		if (_item == null) {
			return;
		}
		
		_layoutItem.setVisible(true);

		displayItemInfo(item.getInfo());

//		if (_itemOptions != null) {
//			List<TextView> texts = _itemOptions.getOptions();
//			for (TextView text: texts) {
//				text.setOnClickListener(null);
//				removeView(text);
//			}
//		}

		// Configure new item
		_itemMatter.setString("Matter: " + String.valueOf(item.getMatterSupply()));
		_itemPower.setString("Power: " + String.valueOf(item.getPower()));
		_itemOwner.setString("Owner: " + (item.getOwner() != null ? item.getOwner().getName() : Strings.LB_NONE));

		// Storage
		if (item.isStorage()) {
			setItemStorage((StorageItem)item);
		}

		_itemGather.setVisible(false);
		_itemMine.setVisible(false);
	}

	private void setItemStorage(StorageItem storage) {
		_layoutStorage.setVisible(true);

		_layoutStorageAdvancedFilter.setVisible(storage.isDispenser() ? false : true);
		_layoutStorageSimpleFilter.setVisible(storage.isDispenser() ? true : false);
		
		_cbFood.setChecked(storage.acceptFood());
		_cbDrink.setChecked(storage.acceptDrink());
		_cbConsomable.setChecked(storage.acceptConsomable());
		_cbGarbage.setChecked(storage.acceptGarbage());
	}

	@Override
	public void onRefresh(int frame) {
		BaseItem item = _area != null ? _area.getItem() : null;
		if (item == null && _area != null) {
			item = _area.getRessource();
		}

		//		// TODO
		//		_itemGather.refresh(app);
		//		_itemMine.refresh(app);

		if (item != null) {

			_itemSlots.setString("Free slots: " + item.getNbFreeSlots());
			_itemUsed.setString("Used: " + item.getTotalUse());

			if (item.isStorage()) {
				StorageItem storage = ((StorageItem)item);
				_itemStorage.setString(StringUtils.getDashedString("Stored", String.valueOf(storage.getNbItems()), NB_COLUMNS_TITLE));
				Map<ItemInfo, Integer> inventoryInfo = new HashMap<ItemInfo, Integer>();
				for (BaseItem storredItem: storage.getItems()) {
					ItemInfo storedInfo = storredItem.getInfo();
					if (inventoryInfo.containsKey(storedInfo)) {
						inventoryInfo.put(storedInfo, inventoryInfo.get(storedInfo) + 1);
					} else {
						inventoryInfo.put(storedInfo, 1);
					}
				}
				// Hide old entries
				for (int i = 0; i < 42; i++) {
					_lbCarry[i].setVisible(false);
					_lbCarryCount[i].setVisible(false);
				}
				// Set new entries
				int i = 0;
				for (ItemInfo storedItemInfo: inventoryInfo.keySet()) {
					int count = inventoryInfo.get(storedItemInfo);
					
					_lbCarry[i].setVisible(true);
					_lbCarry[i].setImage(SpriteManager.getInstance().getIcon(storedItemInfo));
//					_lbCarry[i].setOnClickListener(new OnClickListener() {
//						@Override
//						public void onClick(View view) {
//							setItem(storedItemInfo);
//						}
//					});
					_lbCarryCount[i].setVisible(true);
					_lbCarryCount[i].setString("x"+count);
					i++;
				}
			}
		}
	}

	public void select(ItemInfo itemInfo) {
		clean();
		displayItemInfo(itemInfo);
	}

	private void clean() {
		_area = null;
		_item = null;
		_resource = null;
		_itemInfo = null;
		_layoutItem.setVisible(true);
		_layoutStorage.setVisible(false);
		_layoutStorageAdvancedFilter.setVisible(false);
		_layoutEffects.setVisible(false);
		_itemAccept.setVisible(false);
		_itemAction.setVisible(false);
		_layoutItemProduce.setVisible(false);
	}

}
