package alone.in.deepspace.ui.panel;

import java.io.IOException;
import java.util.List;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderStates;
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
import alone.in.deepspace.model.ItemInfo.ItemInfoEffects;
import alone.in.deepspace.model.Room;
import alone.in.deepspace.model.StorageItem;
import alone.in.deepspace.model.StructureItem;
import alone.in.deepspace.model.WorldArea;
import alone.in.deepspace.model.WorldRessource;
import alone.in.deepspace.ui.UserSubInterface;
import alone.in.deepspace.util.Constant;
import alone.in.deepspace.util.ObjectPool;

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
	private TextView 				_itemActionProduce;
	private ButtonView			 	_itemActionIcon;
	private TextView 				_itemSlots;
	private TextView 				_itemUsed;
	private FrameLayout 			_layoutStorage;
	private FrameLayout 			_layoutEffects;
	private TextView[] 				_itemEffects;
	private TextView _itemAccept;

	public PanelInfo(RenderWindow app) throws IOException {
		super(app, 0, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 32), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT - 32));

		setBackgroundColor(new Color(0, 0, 0, 150));

		_lbRoom = new TextView(null);
		_lbRoom.setPosition(200, 40);
		_lbRoom.setCharacterSize(22);
		addView(_lbRoom);

		createAreaInfoView(0, 4);
		createItemInfoView(0, 4);
		createGatherView();
		createMiningView();
		createActionView();
		createEffectsView(0, 200);

		_layoutStorage = new FrameLayout(new Vector2f(200, 400));
		_layoutStorage.setPosition(0, 400);

		_itemStorage = new TextView(null);
		_itemStorage.setPosition(10, 10);
		_itemStorage.setCharacterSize(16);
		_layoutStorage.addView(_itemStorage);

		_itemAccept = new TextView(null);
		_itemAccept.setPosition(10, 500);
		_itemAccept.setCharacterSize(16);
		addView(_itemAccept);
		
//		_lbContains = new TextView(null);
//		_lbContains.setPosition(10, 10);
//		_lbContains.setString(Strings.LB_STORAGE_CONTAINS);
//		_lbContains.setCharacterSize(22);
//		_layoutStorage.addView(_lbContains);

		_lbCarry = new ImageView[42];
		for (int i = 0; i < 42; i++) {
			int x = i % INVENTORY_NB_COLS;
			int y = i / INVENTORY_NB_COLS;
			_lbCarry[i] = new ImageView();
			_lbCarry[i].setId(100 + i);
			_lbCarry[i].setPosition(new Vector2f(Constant.UI_PADDING_H + x * INVENTORY_ITEM_SIZE + INVENTORY_ITEM_SPACE, 40 + y * INVENTORY_ITEM_SIZE + INVENTORY_ITEM_SPACE));
			_layoutStorage.addView(_lbCarry[i]);
		}
		_layoutItem.addView(_layoutStorage);
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
		_itemName.setPosition(10, 0);
		_itemName.setCharacterSize(22);
		_layoutItem.addView(_itemName);

		_itemCategory = new TextView(null);
		_itemCategory.setPosition(10, 28);
		_itemCategory.setCharacterSize(14);
		_layoutItem.addView(_itemCategory);

		_itemMatter = new TextView(null);
		_itemMatter.setPosition(10, 60);
		_itemMatter.setCharacterSize(14);
		_layoutItem.addView(_itemMatter);

		_itemPower = new TextView(null);
		_itemPower.setPosition(10, 80);
		_itemPower.setCharacterSize(14);
		_layoutItem.addView(_itemPower);

		_itemOwner = new TextView(null);
		_itemOwner.setPosition(10, 100);
		_itemOwner.setCharacterSize(14);
		_layoutItem.addView(_itemOwner);

		_itemSlots = new TextView(null);
		_itemSlots.setPosition(10, 120);
		_itemSlots.setCharacterSize(14);
		_layoutItem.addView(_itemSlots);

		_itemUsed = new TextView(null);
		_itemUsed.setPosition(10, 140);
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
		_areaName.setPosition(10, 0);
		_areaName.setCharacterSize(22);
		_layoutArea.addView(_areaName);

		_areaPos= new TextView(null);
		_areaPos.setPosition(FRAME_WIDTH - 100, 0);
		_areaPos.setCharacterSize(14);
		_layoutArea.addView(_areaPos);

		_areaCategory = new TextView(null);
		_areaCategory.setPosition(10, 28);
		_areaCategory.setCharacterSize(14);
		_layoutArea.addView(_areaCategory);

		_areaLight = new TextView(null);
		_areaLight.setPosition(10, 28);
		_areaLight.setCharacterSize(14);
		_layoutArea.addView(_areaLight);

		_areaIcon = new ImageView();
		_layoutArea.addView(_areaIcon);
		
		addView(_layoutArea);
	}

	private void createActionView() {
		_itemAction = new FrameLayout(new Vector2f(120, 200));
		_itemAction.setPosition(10, 200);
		_layoutItem.addView(_itemAction);
		
		TextView lbTitle = new TextView(new Vector2f(10, 10));
		lbTitle.setString(Strings.PROVIDE);
		lbTitle.setCharacterSize(16);
		_itemAction.addView(lbTitle);
		
		_itemActionProduce = new TextView(new Vector2f(10, 10));
		_itemActionProduce.setPosition(32, 28);
		_itemActionProduce.setCharacterSize(14);
		_itemAction.addView(_itemActionProduce);
		
		_itemActionIcon = new ButtonView(new Vector2f(32, 32));
		_itemActionIcon.setPosition(0, 28);
		_itemAction.addView(_itemActionIcon);
	}

	private void createGatherView() {
		_itemGather = new FrameLayout(new Vector2f(120, 200));
		_itemGather.setVisible(false);
		_itemGather.setPosition(10, 200);
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

	public void  setArea(WorldArea area) {
		if (area == null) {
			return;
		}
		
		_area = area;
		_layoutArea.setVisible(false);
		_layoutItem.setVisible(false);
		_layoutItem.setVisible(false);
		
		if (area.getItem() != null) {
			setItem(area.getItem());
			return;
		}

		if (area.getRessource() != null) {
			setRessource(area.getRessource());
			return;
		}

		if (area != null) {
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
	}

	private void setRessource(WorldRessource item) {
		_layoutItem.setVisible(true);

		_itemName.setString(item.getLabel() != null ? item.getLabel() : item.getName());
		_itemCategory.setString("(" + item.getLabelType() + ")");
		_itemMatter.setString("Matter: " + String.valueOf(item.getMatterSupply()));
		if (item.getOwner() != null) {
			_itemOwner.setString("Owner: " + item.getOwner().getName());
		} else {
			_itemOwner.setString("Owner: " + Strings.LB_NONE);
		}

		Sprite icon = SpriteManager.getInstance().getIcon(item.getInfo());
		_itemIcon.setImage(icon);
		_itemIcon.setPosition(new Vector2f(FRAME_WIDTH - 32 - icon.getTextureRect().width, 20));

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

	private void setItem(final BaseItem item) {
		if (_itemOptions != null) {
			List<TextView> texts = _itemOptions.getOptions();
			for (TextView text: texts) {
				text.setOnClickListener(null);
				removeView(text);
			}
		}

		_layoutItem.setVisible(true);
		_layoutEffects.setVisible(false);
		_layoutStorage.setVisible(item.isStorage());
		_itemAccept.setVisible(false);

		// Configure new item
		_itemName.setString(item.getLabel() != null ? item.getLabel() : item.getName());
		_itemCategory.setString("(" + item.getLabelType() + ")");
		_itemMatter.setString("Matter: " + String.valueOf(item.getMatterSupply()));
		_itemPower.setString("Power: " + String.valueOf(item.getPower()));
		if (item.getOwner() != null) {
			_itemOwner.setString("Owner: " + item.getOwner().getName());
		} else {
			_itemOwner.setString("Owner: " + Strings.LB_NONE);
		}

		Sprite icon = SpriteManager.getInstance().getIcon(item.getInfo());
		if (icon != null) {
			_itemIcon.setImage(icon);
			_itemIcon.setPosition(new Vector2f(FRAME_WIDTH - 32 - icon.getTextureRect().width, 20));
		}

		// Action item
		if (item.getInfo().onAction != null) {
			_itemAction.setVisible(true);
			
			if (item.getInfo().onAction.itemAccept != null) {
				String str = "Accept:\n";
				for (ItemInfo info: item.getInfo().onAction.itemAccept) {
					str += "  " + info.label + "\n";
				}
				_itemAccept.setString(str);
				_itemAccept.setVisible(true);
			}
			
			// Item action produce
			if (item.getInfo().onAction.itemProduce != null) {
				_itemActionProduce.setString(item.getInfo().onAction.itemProduce.label);
				_itemActionIcon.setIcon(SpriteManager.getInstance().getIcon(item.getInfo().onAction.itemProduce));
			}

			// Item action effects
			if (item.getInfo().onAction.effects != null) {
				_layoutEffects.setVisible(true);
				ItemInfoEffects effects = item.getInfo().onAction.effects;
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
		} else {
			_itemAction.setVisible(false);
		}
		
		_itemGather.setVisible(false);
		_itemMine.setVisible(false);

//		_itemOptions = new PanelInfoItemOptions(20, 280);
//		addView(_itemOptions.add("Remove", new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				JobManager.getInstance().storeItem(item);
//			}
//		}));
//		addView(_itemOptions.add("Destroy", new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				JobManager.getInstance().destroyItem(item);
//			}
//		}));
//		addView(_itemOptions.add("Add character", new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				ServiceManager.getCharacterManager().add(item.getX(), item.getY());
//			}
//		}));
//		addView(_itemOptions.add("kill everyone", new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				ServiceManager.getCharacterManager().clear();
//			}
//		}));
		
	}

	@Override
	public void onDraw(RenderWindow app, RenderStates render) {
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
				if (storage.getItems().size() > 0) {
					_itemStorage.setString("Storage: " + storage.getItems().size());
					for (int i = 0; i < 42; i++) {
						if (i < storage.getItems().size()) {
							final BaseItem storedItem = storage.getItems().get(i);
							_lbCarry[i].setImage(SpriteManager.getInstance().getIcon(storedItem.getInfo()));
							_lbCarry[i].setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View view) {
									setItem(storedItem);
								}
							});
						} else {
							_lbCarry[i].setImage(null);
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
