package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.Color;
import org.smallbox.faraway.Game;
import org.smallbox.faraway.GameEventListener;
import org.smallbox.faraway.engine.ui.*;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.model.item.ItemInfo;
import org.smallbox.faraway.ui.UserInterface.Mode;

import java.util.List;

public class PanelDebugItem extends BasePanel {

	private static final int 	FRAME_WIDTH = Constant.WINDOW_WIDTH;
	private static final int	FRAME_HEIGHT = Constant.WINDOW_HEIGHT - 30;
	private static final int ROW_HEIGHT = 60;
	private static final int COL_WIDTH = 160;
	private static final int SPACING = 20;
	protected ItemInfo _item;
	private FrameLayout _grid;
	private FrameLayout _itemView;
	
	public PanelDebugItem(Mode mode, GameEventListener.Key shortcut) {
		super(mode, shortcut, 0, 32, FRAME_WIDTH, FRAME_HEIGHT, null);
		
		setBackgroundColor(new Color(0, 0, 0));

		_itemView = ViewFactory.getInstance().createFrameLayout(FRAME_WIDTH, FRAME_HEIGHT);
		_itemView.setPosition(0, 0);
		addView(_itemView);
		
		initGrid();
	}

	private void initGrid() {
		_grid = ViewFactory.getInstance().createFrameLayout(FRAME_WIDTH, FRAME_HEIGHT);
		_grid.setPosition(0, 0);
		
		int i = 0;
		List<ItemInfo> items = Game.getData().items;
		for (ItemInfo item: items) {
			_grid.addView(createGridItem(item, i % 10, i / 10));
			i++;
		}

		addView(_grid);
	}

	private View createGridItem(final ItemInfo item, int row, int col) {
		FrameLayout layout = ViewFactory.getInstance().createFrameLayout(COL_WIDTH, ROW_HEIGHT);
		layout.setBackgroundColor(new Color(255, 255, 255, 50));
		layout.setPosition(SPACING + col * (COL_WIDTH + SPACING), SPACING + row * (ROW_HEIGHT + SPACING));
		layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				initItem(item);
			}
		});
		
		TextView lbName = ViewFactory.getInstance().createTextView();
		lbName.setString(item.name);
		lbName.setPosition(6, 6);
		lbName.setCharacterSize(12);
		layout.addView(lbName);
		
		return layout;
	}

	protected void initItem(final ItemInfo item) {
		_grid.setVisible(false);
		_itemView.clearAllViews();
		_itemView.setVisible(true);

		int y = 6;
		int x = 6;
		
		{
			TextView text = ViewFactory.getInstance().createTextView();
			text.setString(item.name);
			text.setPosition(x, y);
			text.setCharacterSize(32);
			_itemView.addView(text);
			y += 40;
		}
		
		{
			TextView text = ViewFactory.getInstance().createTextView();
			text.setString("Label: " + item.label);
			text.setPosition(x, y);
			text.setCharacterSize(16);
			_itemView.addView(text);
			y += 22;
		}

		{
			TextView text = ViewFactory.getInstance().createTextView();
			text.setString("Is structure: " + item.isStructure);
			text.setPosition(x, y);
			text.setCharacterSize(16);
			_itemView.addView(text);
			y += 22;
		}
		
		{
			TextView text = ViewFactory.getInstance().createTextView();
			text.setString("Is ressource: " + item.isResource);
			text.setPosition(x, y);
			text.setCharacterSize(16);
			_itemView.addView(text);
			y += 22;
		}
		
		{
			TextView text = ViewFactory.getInstance().createTextView();
			text.setString("Is consomable: " + item.isConsomable);
			text.setPosition(x, y);
			text.setCharacterSize(16);
			_itemView.addView(text);
			y += 22;
		}
		
		{
			TextView text = ViewFactory.getInstance().createTextView();
			text.setString("Is user item: " + item.isUserItem);
			text.setPosition(x, y);
			text.setCharacterSize(16);
			_itemView.addView(text);
			y += 22;
		}
		
		if (item.actions != null) {

			{
				TextView text = ViewFactory.getInstance().createTextView();
				text.setString("Action:");
				text.setPosition(x, y);
				text.setCharacterSize(16);
				_itemView.addView(text);
				y += 22;
				x += 20;
			}
			
			// Action duration
			{
				TextView text = ViewFactory.getInstance().createTextView();
				//text.setString("duration: " + item.actions.duration);
				text.setString("duration: TODO");
				text.setPosition(x, y);
				text.setCharacterSize(16);
				_itemView.addView(text);
				y += 22;
			}
//
//			// Produce
//			if (item.actions.produce != null) {
//				TextView text = ViewFactory.getInstance().createTextView();
//				text.setSize(100, 20);
//				String str = "products: ";
//				for (String itemProduceName: item.actions.produce) {
//					str += itemProduceName + "\n";
//				}
//				text.setString(str);
//				text.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View view) {
//						initItem(item.actions.itemsProduce.get(0));
//					}
//				});
//				text.setPosition(x, y);
//				text.setCharacterSize(16);
//				_itemView.addView(text);
//				y += 22;
//			}
//
//			// Effects
//			if (item.actions.effects != null) {
//				TextView text = ViewFactory.getInstance().createTextView();
//				String str = "effects: ";
//				str += "food("+item.actions.effects.food+"), ";
//				str += "drink("+item.actions.effects.drink+"), ";
//				str += "energy("+item.actions.effects.energy+"), ";
//				str += "happiness("+item.actions.effects.happiness+")";
//				text.setString("Action effects " + str);
//				text.setPosition(x, y);
//				text.setCharacterSize(16);
//				_itemView.addView(text);
//				y += 22;
//			}
		}
		
		if (item.craftedFromItems != null) {
			TextView text = ViewFactory.getInstance().createTextView();
			String str = "Crafted from: ";
			int i = 0;
			for (String name: item.receipts) {
				if (i++ > 0) {
					str += " / ";
				}
				str += name;
			}
			text.setString(str);
			text.setPosition(x, y);
			text.setCharacterSize(16);
			_itemView.addView(text);
			y += 22;
		}
		

	}

	public void reset() {
		_grid.setVisible(true);
		_itemView.setVisible(false);
	}

	@Override
	protected void onCreate(ViewFactory factory) {
		// TODO Auto-generated method stub
		
	}

}
