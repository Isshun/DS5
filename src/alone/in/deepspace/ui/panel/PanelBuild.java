package alone.in.deepspace.ui.panel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsfml.graphics.Color;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard.Key;

import alone.in.deepspace.Game;
import alone.in.deepspace.engine.ui.ButtonView;
import alone.in.deepspace.engine.ui.ColorView;
import alone.in.deepspace.engine.ui.Colors;
import alone.in.deepspace.engine.ui.FrameLayout;
import alone.in.deepspace.engine.ui.LinkView;
import alone.in.deepspace.engine.ui.OnClickListener;
import alone.in.deepspace.engine.ui.OnFocusListener;
import alone.in.deepspace.engine.ui.TextView;
import alone.in.deepspace.engine.ui.View;
import alone.in.deepspace.manager.SpriteManager;
import alone.in.deepspace.model.CategoryInfo;
import alone.in.deepspace.model.item.ItemInfo;
import alone.in.deepspace.ui.UserInteraction.Action;
import alone.in.deepspace.ui.UserInterface.Mode;
import alone.in.deepspace.util.StringUtils;

public class PanelBuild extends BaseRightPanel {
	public enum PanelMode {
		NONE,
		BUILD_STRUCTURE,
		BUILD_ITEM,
		REMOVE_STRUCTURE,
		REMOVE_ITEM
	};

	private static final Color				COLOR_INACTIVE = new Color(29, 85, 96, 100);
	private static final int 				GRID_WIDTH = 100;
	private static final int 				GRID_HEIGHT = 120;

	private Map<ItemInfo, ButtonView> 		_icons;
	private List<View>						_iconsList;
	protected ItemInfo 						_currentSelected;
	protected PanelMode 					_panelMode;
	private boolean 						_animRunning;
	private Map<CategoryInfo, FrameLayout>	_layouts;
	private CategoryInfo 					_currentCategory;
	private ButtonView[] 					_iconShortcut;
	private FrameLayout 					_mainView;

	public PanelBuild(Mode mode, Key shortcut) {
		super(mode, shortcut);
	}

	@Override
	protected void onCreate() {
		_iconShortcut = new ButtonView[10];
		_layouts = new HashMap<CategoryInfo, FrameLayout>();
		_iconsList = new ArrayList<View>();
		_icons = new HashMap<ItemInfo, ButtonView>();
		_panelMode = PanelMode.NONE;
		_mainView = new FrameLayout();
		addView(_mainView);

		drawPanel(true);
	}

	public PanelMode getPanelMode() { return _panelMode; }
	public ItemInfo getSelectedItem() { return _currentSelected; }

	// TODO: ugly
	protected void	drawPanel(boolean witchAnim) {
		System.out.println("DRAW PANEL");
		
		_animRunning = true;
		_mainView.clearAllViews();
		_icons.clear();

		// TODO
		int posY = 0;
		List<CategoryInfo> categories = Game.getData().categories;
		_iconsList.clear();
		_layouts.clear();

		for (CategoryInfo c: categories) {
			final CategoryInfo category = c;

			// Content
			final FrameLayout layout = new FrameLayout();
			layout.setPosition(new Vector2f(20, posY + 52));
			_mainView.addView(layout);
			_layouts.put(category, layout);

			// Title
			TextView lbTitle = new LinkView(new Vector2f(380, 28));
			lbTitle.setDashedString(c.labelWithoutShortcut.toUpperCase(), c.items.size() + " items", NB_COLUMNS_TITLE);
			lbTitle.setCharacterSize(FONT_SIZE_TITLE);
			lbTitle.setPosition(new Vector2f(20, posY + 8));
			lbTitle.setColor(Colors.TEXT);
			lbTitle.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					toogleCategory(category);
				}
			});
			_mainView.addView(lbTitle);
			
			System.out.println("posY: " + posY + ", label: " + c.labelWithoutShortcut);

			// Shortcut
			TextView lbShortcut = new TextView();
			lbShortcut.setString(c.shortcut.toUpperCase());
			lbShortcut.setCharacterSize(FONT_SIZE_TITLE);
			lbShortcut.setColor(Colors.LINK_ACTIVE);
			lbShortcut.setPosition(new Vector2f(c.shortcutPos * 12 + 20, posY + 8));
			_mainView.addView(lbShortcut);

			// Underline -- because at FONT_SIZE_TITLE regular underline get bold state...
			View underline = new ColorView(new Vector2f(12, 1));
			underline.setBackgroundColor(Colors.LINK_ACTIVE);
			underline.setPosition(new Vector2f(c.shortcutPos * 12 + 20, posY + 33));
			_mainView.addView(underline);

			posY += 44;

			// Items
			if (category.equals(_currentCategory)) {
				int i = 0;
				for (ItemInfo info: c.items) {
					if (info.isUserItem || info.isStructure) {
						View icon = drawIcon(layout, i, info, posY > 42);
						icon.setVisible(!witchAnim);
						_iconsList.add(icon);
						i++;
					}
				}
				posY += Math.ceil((double)i / 4) * GRID_HEIGHT;
				for (; i < 10; i++) {
					_iconShortcut[i] = null;
				}
			}
		}

		View border = new ColorView(new Vector2f(4, FRAME_HEIGHT));
		border.setBackgroundColor(new Color(37, 70, 72));
		_mainView.addView(border);
	}

	protected void toogleCategory(CategoryInfo category) {
		_currentCategory = _currentCategory != category ? category : null;

		if (_currentCategory != null) {
			openCategory(_currentCategory);
		} else {
			drawPanel(false);			
		}
	}

	protected void openCategory(CategoryInfo category) {
		boolean withAnim = _currentCategory != category;

		_currentCategory = category;
		
//		for (FrameLayout l: _layouts.values()) {
//			l.setVisible(false);
//		}
//		if (_currentCategory != null) {
//			_layouts.get(_currentCategory).setVisible(true);
//		}
		drawPanel(withAnim);
	}

	private ButtonView	drawIcon(FrameLayout layout, int index, final ItemInfo info, boolean visible) {
		ButtonView icon = _icons.get(info);
		if (icon == null) {
			int x = (index % 4) * GRID_WIDTH;
			int y = (int)(index / 4) * GRID_HEIGHT;

			icon = new ButtonView(new Vector2f(82, 100));
			String label = info.label.length() > 9 ? info.label.substring(0, 9) : info.label;
			icon.setString(label);
			icon.setTextPadding(80, 4);
			icon.setIcon(SpriteManager.getInstance().getIcon(info));
			if (info.name.equals("base.bed")) {
				icon.setId(112);
			}
			icon.setIconPadding(14, 10);
			icon.setPosition(x, y);
			icon.setColor(Colors.TEXT);
			icon.setCharacterSize(FONT_SIZE);
			icon.setBackgroundColor(COLOR_INACTIVE);
			icon.setBorderSize(2);
			icon.setData(info);
			icon.setOnFocusListener(new OnFocusListener() {
				@Override
				public void onEnter(View view) {
					view.setBackgroundColor(new Color(29, 85, 96, 180));
				}

				@Override
				public void onExit(View view) {
					view.setBackgroundColor(info.equals(_currentSelected) ? new Color(29, 85, 96) : new Color(29, 85, 96, 100));
				}
			});
			icon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					clickOnIcon(view);
					_interaction.set(Action.BUILD_ITEM, info);
				}
			});
			if (index < 10) {
				_iconShortcut[index] = icon;
			}
			layout.addView(icon);

			TextView lbIndex = new TextView();
			lbIndex.setString(String.valueOf(index+1));
			lbIndex.setColor(Colors.LINK_ACTIVE);
			lbIndex.setStyle(TextView.UNDERLINED);
			lbIndex.setCharacterSize(FONT_SIZE);
			lbIndex.setPosition(x+4, y);
			layout.addView(lbIndex);

			_icons.put(info, icon);
		}
		icon.resetPos();

		return icon;
	}

	@Override
	protected void onRefresh(int frame) {
		if (_animRunning) {
			int i = 0;
			for (View icon: _iconsList) {
				if (icon.isVisible() == false) {
					icon.setVisible(true);
					if (++i == 4) {
						return;
					}
				}
			}
		}
		_animRunning = false;
	}

	@Override
	protected boolean onKey(Key key) {
		String shortcut = StringUtils.getStringFromKey(key);
		if (shortcut != null) {
			List<CategoryInfo> categories = Game.getData().categories;
			for (CategoryInfo category: categories) {
				if (shortcut.equals(category.shortcut)) {
					openCategory(category);
					return true;
				}
			}
		}
		
		switch (key) {
		case NUM0: clickOnIcon(_iconShortcut[9]); break;
		case NUM1: clickOnIcon(_iconShortcut[0]); break;
		case NUM2: clickOnIcon(_iconShortcut[1]); break;
		case NUM3: clickOnIcon(_iconShortcut[2]); break;
		case NUM4: clickOnIcon(_iconShortcut[3]); break;
		case NUM5: clickOnIcon(_iconShortcut[4]); break;
		case NUM6: clickOnIcon(_iconShortcut[5]); break;
		case NUM7: clickOnIcon(_iconShortcut[6]); break;
		case NUM8: clickOnIcon(_iconShortcut[7]); break;
		case NUM9: clickOnIcon(_iconShortcut[8]); break;
		default: break;
		}

		return false;
	}

	private void clickOnIcon(View view) {
		if (view == null) {
			return;
		}
		
		for (ButtonView icon: _icons.values()) {
			icon.setBackgroundColor(new Color(29, 85, 96, 100));
			icon.setBorderColor(null);
		}
		//select((ItemInfo)view.getData());
//		_ui.select((ItemInfo)view.getData());
		view.setBackgroundColor(new Color(29, 85, 96));
		view.setBorderColor(new Color(161, 255, 255));
//		view.setBackgroundColor(Color.RED);
	}
}
