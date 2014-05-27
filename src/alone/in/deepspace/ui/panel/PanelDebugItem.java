package alone.in.deepspace.ui.panel;

import java.io.IOException;
import java.util.List;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.engine.ui.FrameLayout;
import alone.in.deepspace.engine.ui.OnClickListener;
import alone.in.deepspace.engine.ui.TextView;
import alone.in.deepspace.engine.ui.View;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.ItemInfo;
import alone.in.deepspace.ui.UserSubInterface;
import alone.in.deepspace.util.Constant;

public class PanelDebugItem extends UserSubInterface {

	private static final int 	FRAME_WIDTH = Constant.WINDOW_WIDTH;
	private static final int	FRAME_HEIGHT = Constant.WINDOW_HEIGHT - 30;
	private static final int ROW_HEIGHT = 60;
	private static final int COL_WIDTH = 160;
	private static final int SPACING = 20;
	protected ItemInfo _item;
	private FrameLayout _grid;
	private FrameLayout _itemView;
	
	public PanelDebugItem(RenderWindow app) throws IOException {
		super(app, 0, new Vector2f(0, 32), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));
		
		setBackgroundColor(new Color(0, 0, 0));

		_itemView = new FrameLayout(new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));
		_itemView.setPosition(0, 0);
		addView(_itemView);
		
		initGrid();
	}

	private void initGrid() {
		_grid = new FrameLayout(new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));
		_grid.setPosition(0, 0);
		
		int i = 0;
		List<ItemInfo> items = ServiceManager.getData().items;
		for (ItemInfo item: items) {
			_grid.addView(createGridItem(item, i % 10, i / 10));
			i++;
		}

		addView(_grid);
	}

	private View createGridItem(final ItemInfo item, int row, int col) {
		FrameLayout layout = new FrameLayout(new Vector2f(COL_WIDTH, ROW_HEIGHT));
		layout.setBackgroundColor(new Color(255, 255, 255, 50));
		layout.setPosition(new Vector2f(SPACING + col * (COL_WIDTH + SPACING), SPACING + row * (ROW_HEIGHT + SPACING)));
		layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				initItem(item);
			}
		});
		
		TextView lbName = new TextView();
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
			TextView text = new TextView();
			text.setString(item.name);
			text.setPosition(x, y);
			text.setCharacterSize(32);
			_itemView.addView(text);
			y += 40;
		}
		
		{
			TextView text = new TextView();
			text.setString("Label: " + item.label);
			text.setPosition(x, y);
			text.setCharacterSize(16);
			_itemView.addView(text);
			y += 22;
		}

		{
			TextView text = new TextView();
			text.setString("Is structure: " + item.isStructure);
			text.setPosition(x, y);
			text.setCharacterSize(16);
			_itemView.addView(text);
			y += 22;
		}
		
		{
			TextView text = new TextView();
			text.setString("Is ressource: " + item.isResource);
			text.setPosition(x, y);
			text.setCharacterSize(16);
			_itemView.addView(text);
			y += 22;
		}
		
		{
			TextView text = new TextView();
			text.setString("Is consomable: " + item.isConsomable);
			text.setPosition(x, y);
			text.setCharacterSize(16);
			_itemView.addView(text);
			y += 22;
		}
		
		{
			TextView text = new TextView();
			text.setString("Is user item: " + item.isUserItem);
			text.setPosition(x, y);
			text.setCharacterSize(16);
			_itemView.addView(text);
			y += 22;
		}
		
		if (item.onAction != null) {

			{
				TextView text = new TextView();
				text.setString("Action:");
				text.setPosition(x, y);
				text.setCharacterSize(16);
				_itemView.addView(text);
				y += 22;
				x += 20;
			}
			
			// Action duration
			{
				TextView text = new TextView();
				text.setString("duration: " + item.onAction.duration);
				text.setPosition(x, y);
				text.setCharacterSize(16);
				_itemView.addView(text);
				y += 22;
			}

			// Produce
			if (item.onAction.produce != null) {
				TextView text = new TextView(new Vector2f(100, 20));
				String str = "produce: ";
				for (String itemProduceName: item.onAction.produce) {
					str += itemProduceName + "\n";
				}
				text.setString(str);
				text.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						initItem(item.onAction.itemsProduce.get(0));
					}
				});
				text.setPosition(x, y);
				text.setCharacterSize(16);
				_itemView.addView(text);
				y += 22;
			}

			// Effects
			if (item.onAction.effects != null) {
				TextView text = new TextView();
				String str = "effects: ";
				str += "food("+item.onAction.effects.food+"), ";
				str += "drink("+item.onAction.effects.drink+"), ";
				str += "energy("+item.onAction.effects.energy+"), ";
				str += "hapiness("+item.onAction.effects.hapiness+")";
				text.setString("Action effects " + str);
				text.setPosition(x, y);
				text.setCharacterSize(16);
				_itemView.addView(text);
				y += 22;
			}
		}
		
		if (item.craftedFromItems != null) {
			TextView text = new TextView();
			String str = "Crafted from: ";
			int i = 0;
			for (String name: item.craftedFrom) {
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

}
