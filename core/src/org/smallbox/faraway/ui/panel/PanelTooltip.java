package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.game.model.ToolTips;
import org.smallbox.faraway.game.model.ToolTips.ToolTip;
import org.smallbox.faraway.game.model.ToolTips.ToolTipCategory;
import org.smallbox.faraway.ui.UserInterface.Mode;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.engine.view.FrameLayout;
import org.smallbox.faraway.ui.engine.view.UILabel;

public class PanelTooltip extends BasePanel {

	private static final int FRAME_WIDTH = 800;
	private static final int FRAME_HEIGHT = 600;
	private static final int LINE_LENGTH = 80;
	private static final int NB_MAX_LINK = 10;
	private static final int CHARACTER_WIDTH = 8;

	private UILabel 		_lbToolTip;
	private UILabel 		_lbContent;
	private UILabel 		_lbCategory;
	private UILabel[] 		_lbCategories;

	public PanelTooltip(Mode mode, GameEventListener.Key shortcut) {
		super(mode, shortcut, 200, 200, FRAME_WIDTH, FRAME_HEIGHT, null);
	}
	
	@Override
	protected void onCreate(ViewFactory viewFactory) {
		_lbToolTip = viewFactory.createTextView(0, 0);
		_lbToolTip.setTextSize(FONT_SIZE_TITLE);
		_lbToolTip.setPosition(20, 18);
		addView(_lbToolTip);

		_lbContent = viewFactory.createTextView(0, 0);
		_lbContent.setTextSize(FONT_SIZE);
		_lbContent.setPosition(20, 52);
		addView(_lbContent);
		
		FrameLayout layoutCategory = viewFactory.createFrameLayout(0, 0);
		layoutCategory.setPosition(20, FRAME_HEIGHT - 66);
		addView(layoutCategory);
		
		_lbCategory = viewFactory.createTextView(0, 0);
		_lbCategory.setTextSize(FONT_SIZE_TITLE);
		_lbCategory.setPosition(0, 0);
		layoutCategory.addView(_lbCategory);
		
		_lbCategories = new UILabel[NB_MAX_LINK];
		for (int i = 0; i < NB_MAX_LINK; i++) {
			_lbCategories[i] = viewFactory.createTextView();
			_lbCategories[i].setTextSize(FONT_SIZE);
			layoutCategory.addView(_lbCategories[i]);
		}
	}

	private String getFormatedContent(String content) {
		StringBuilder sb = new StringBuilder();
		String str = content;

		while (str.length() > LINE_LENGTH) {
			int spaceIndex = str.substring(0, LINE_LENGTH).lastIndexOf(' ');
			int nlIndex = str.substring(0, LINE_LENGTH).lastIndexOf('\n');
			int cutIndex = nlIndex == -1 ? spaceIndex : nlIndex;
			sb.append(str.substring(0, cutIndex == -1 ? LINE_LENGTH : cutIndex)).append('\n');
			str = str.substring(cutIndex == -1 ? LINE_LENGTH : cutIndex + 1);
		}
		sb.append(str);
		
		return sb.toString();
	}

	public void select(ToolTip tooltip) {

		// Title
		_lbToolTip.setText(tooltip.title);

		// Content
		_lbContent.setText(getFormatedContent(tooltip.content));

		// Category
		ToolTipCategory category = null;
		for (ToolTipCategory c: ToolTips.categories) {
			if (c.contains(tooltip)) {
				category = c;
			}
		}
		if (category != null) {
			_lbCategory.setText("See other tags for [" + category.title + "]");
			int i = 0;
			int posX = 0;
			for (ToolTip t: category.tooltips) {
				final ToolTip ft = t;
				_lbCategories[i].setText("[" + t.title + "]");
				_lbCategories[i].setVisible(true);
				_lbCategories[i].setPosition(posX, 32);
				_lbCategories[i].setSize((t.title.length() + 2) * CHARACTER_WIDTH, LINE_HEIGHT);
				_lbCategories[i].resetPos();
				_lbCategories[i].setOnClickListener(view -> _ui.getSelector().select(ft));
				posX += (t.title.length() + 2) * CHARACTER_WIDTH + 12;
				i++;
			}
			for (; i < NB_MAX_LINK; i++) {
				_lbCategories[i].setVisible(false);
			}
		}
	}

}
