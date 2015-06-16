package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.Color;
import org.smallbox.faraway.GameEventListener;
import org.smallbox.faraway.engine.ui.*;
import org.smallbox.faraway.engine.util.StringUtils;
import org.smallbox.faraway.manager.SpriteManager;
import org.smallbox.faraway.model.CategoryInfo;
import org.smallbox.faraway.model.GameData;
import org.smallbox.faraway.model.item.ItemInfo;
import org.smallbox.faraway.ui.UserInteraction;
import org.smallbox.faraway.ui.UserInterface.Mode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PanelBuild extends BaseRightPanel {
	public enum PanelMode {
		NONE,
		BUILD_STRUCTURE,
		BUILD_ITEM,
		REMOVE_STRUCTURE,
		REMOVE_ITEM
	};

	private static final Color				COLOR_INACTIVE = new Color(29, 85, 96, 100);
	private static final int 				GRID_WIDTH = 90;
	private static final int 				GRID_HEIGHT = 110;

	private Map<ItemInfo, View> 	        _icons;
	private List<View>						_iconsList;
	protected ItemInfo 						_currentSelected;
	protected PanelMode 					_panelMode;
	private boolean 						_animRunning;
	private Map<CategoryInfo, FrameLayout>	_layouts;
	private CategoryInfo 					_currentCategory;
	private TextView[] 						_iconShortcut;

	public PanelBuild(Mode mode, GameEventListener.Key shortcut) {
		super(mode, shortcut, "data/ui/panels/build.yml");
	}

	@Override
	protected void onCreate(ViewFactory factory) {
        clearAllViews();
		_iconShortcut = new TextView[10];
		_layouts = new HashMap<>();
		_iconsList = new ArrayList<>();
		_icons = new HashMap<>();
		_panelMode = PanelMode.NONE;

		drawPanel(true);
	}

	public PanelMode getPanelMode() { return _panelMode; }
	public ItemInfo getSelectedItem() { return _currentSelected; }

	// TODO: ugly
	protected void	drawPanel(boolean anim) {
		_animRunning = true;
		clearAllViews();
		_icons.clear();

		// TODO
		int posY = 0;
		List<CategoryInfo> categories = GameData.getData().categories;
		_iconsList.clear();
		_layouts.clear();

		for (CategoryInfo c: categories) {
			final CategoryInfo category = c;

			// Content
			final FrameLayout layout = ViewFactory.getInstance().createFrameLayout();
			layout.setPosition(20, posY + 52);
			addView(layout);
			_layouts.put(category, layout);

			// Title
			TextView lbTitle = ViewFactory.getInstance().createTextView(380, 28);
			lbTitle.setDashedString(c.label.toUpperCase(), c.items.size() + " items", NB_COLUMNS_TITLE);
			lbTitle.setCharacterSize(FONT_SIZE_TITLE);
			lbTitle.setPosition(20, posY + 8);
			lbTitle.setColor(Colors.TEXT);
			lbTitle.setOnClickListener(view -> toggleCategory(category));
			addView(lbTitle);

//            // Shortcut
//            TextView lbShortcut = ViewFactory.getInstance().createTextView();
//            lbShortcut.setString(c.shortcut.toUpperCase());
//            lbShortcut.setCharacterSize(FONT_SIZE_TITLE);
//            lbShortcut.setColor(Colors.LINK_ACTIVE);
//            lbShortcut.setPosition(c.shortcutPos * 12 + 20, posY + 8);
//            addView(lbShortcut);
//
//            // Underline -- because at FONT_SIZE_TITLE regular underline getRoom bold state...
//            View underline = ViewFactory.getInstance().createColorView(12, 1);
//            underline.setBackgroundColor(Colors.LINK_ACTIVE);
//            underline.setPosition(c.shortcutPos * 12 + 20, posY + 33);
//            addView(underline);

			posY += 44;

			// Items
			if (category == _currentCategory) {
                posY = refreshCategory(category, layout, posY, anim);
			}
		}

		View border = ViewFactory.getInstance().createColorView(4, FRAME_HEIGHT);
		border.setBackgroundColor(new Color(37, 70, 72));
		addView(border);
	}

	private int refreshCategory(CategoryInfo category, FrameLayout layout, int posY, boolean anim) {
		int i = 0;
		for (ItemInfo info: category.items) {
			if (info.isUserItem || info.isStructure) {
				drawIcon(layout, i, info, posY > 42);
                _icons.get(info).setVisible(!anim);
				_iconsList.add(_icons.get(info));
				i++;
			}
		}
		posY += Math.ceil((double)i / 4) * GRID_HEIGHT;
		for (; i < 10; i++) {
			_iconShortcut[i] = null;
		}
        return posY;
	}

	protected void toggleCategory(CategoryInfo category) {
		_currentCategory = _currentCategory != category ? category : null;

		if (_currentCategory != null) {
			refreshPanel(_currentCategory);
		} else {
			drawPanel(false);			
		}
	}

	protected void refreshPanel(CategoryInfo category) {
		boolean withAnim = _currentCategory != category;

		_currentCategory = category;
		
//		for (FrameLayout l: _layouts.values()) {
//			l.setVisible(false);
//		}
//		if (_currentCategory != null) {
//			_layouts.getRoom(_currentCategory).setVisible(true);
//		}
		drawPanel(withAnim);
	}

	private void drawIcon(FrameLayout layout, int index, final ItemInfo info, boolean visible) {
		if (!_icons.containsKey(info)) {
			int x = (index % 4) * GRID_WIDTH;
			int y = (index / 4) * GRID_HEIGHT;

			ViewFactory.getInstance().load("data/ui/panels/view_build_entry.yml", view -> {
                String label = info.label.length() > 9 ? info.label.substring(0, 9) : info.label;
                ((TextView)view.findById("lb_item")).setString(label);
                ((ImageView)view.findById("img_item")).setImage(SpriteManager.getInstance().getIcon(info));
                view.findById("frame_select").setVisible(false);
                view.findById("frame_material").setVisible(false);

//                view.setOnFocusListener(new OnFocusListener() {
//                    @Override
//                    public void onEnter(View view) {
//                        view.findById("frame_select").setVisible(true);
//                    }
//
//                    @Override
//                    public void onExit(View view) {
//                        view.findById("frame_select").setVisible(true);
//                    }
//                });
                view.setOnClickListener(v -> {
                    _interaction.set(UserInteraction.Action.BUILD_ITEM, info);
                    _icons.values().forEach(view1 -> view1.findById("frame_select").setVisible(false));
                    v.findById("frame_select").setVisible(true);
                });
                view.setOnRightClickListener(v -> {
                    FrameLayout frameMaterial = (FrameLayout)v.findById("frame_material");
                    frameMaterial.setVisible(!frameMaterial.isVisible());
                });
			    view.setData(info);
                view.setPosition(x, y);
                view.setSize(72, 96);
                layout.addView(view);
                layout.resetAllPos();
                _icons.put(info, view);
            });

//			icon.setOnFocusListener(new OnFocusListener() {
//				@Override
//				public void onEnter(View view) {
//					view.setBackgroundColor(new Color(29, 85, 96, 180));
//				}
//
//				@Override
//				public void onExit(View view) {
//					view.setBackgroundColor(info.equals(_currentSelected) ? new Color(29, 85, 96) : new Color(29, 85, 96, 100));
//				}
//			});
//			icon.setOnClickListener(view -> {
//                clickOnIcon(view);
//                _interaction.set(Action.BUILD_ITEM, info);
//            });
//			if (index < 10) {
//				_iconShortcut[index] = icon;
//			}

//			TextView lbIndex = ViewFactory.getInstance().createTextView();
//			lbIndex.setString(String.valueOf(index+1));
//			lbIndex.setColor(Colors.LINK_ACTIVE);
//			lbIndex.setStyle(TextView.UNDERLINED);
//			lbIndex.setCharacterSize(FONT_SIZE);
//			lbIndex.setPosition(x+4, y);
//			layout.addView(lbIndex);

//			frame.addView(icon);
		}
//		frame.resetPos();
	}

	@Override
	protected void onRefresh(int frame) {
		if (_animRunning) {
			int i = 0;
			for (View icon: _iconsList) {
				if (!icon.isVisible()) {
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
	protected boolean onKey(GameEventListener.Key key) {
		String shortcut = StringUtils.getStringFromKey(key);
		if (shortcut != null) {
			List<CategoryInfo> categories = GameData.getData().categories;
			for (CategoryInfo category: categories) {
				if (shortcut.equals(category.shortcut)) {
					refreshPanel(category);
					return true;
				}
			}
		}
		
		switch (key) {
		case D_0: clickOnIcon(_iconShortcut[9]); break;
		case D_1: clickOnIcon(_iconShortcut[0]); break;
		case D_2: clickOnIcon(_iconShortcut[1]); break;
		case D_3: clickOnIcon(_iconShortcut[2]); break;
		case D_4: clickOnIcon(_iconShortcut[3]); break;
		case D_5: clickOnIcon(_iconShortcut[4]); break;
		case D_6: clickOnIcon(_iconShortcut[5]); break;
		case D_7: clickOnIcon(_iconShortcut[6]); break;
		case D_8: clickOnIcon(_iconShortcut[7]); break;
		case D_9: clickOnIcon(_iconShortcut[8]); break;
		default: break;
		}

		return false;
	}

	private void clickOnIcon(View view) {
		if (view == null) {
			return;
		}

		for (View icon: _icons.values()) {
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
