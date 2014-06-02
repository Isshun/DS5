package alone.in.deepspace.ui.panel;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.engine.ui.FrameLayout;
import alone.in.deepspace.engine.ui.LinkView;
import alone.in.deepspace.engine.ui.OnClickListener;
import alone.in.deepspace.engine.ui.TextView;
import alone.in.deepspace.engine.ui.View;
import alone.in.deepspace.model.ToolTips;
import alone.in.deepspace.model.ToolTips.ToolTip;
import alone.in.deepspace.model.ToolTips.ToolTipCategory;
import alone.in.deepspace.ui.UserSubInterface;

public class PanelTooltip extends UserSubInterface {

	private static final int FRAME_WIDTH = 800;
	private static final int FRAME_HEIGHT = 600;
	private static final int LINE_LENGTH = 80;
	private static final int NB_MAX_LINK = 10;
	private static final int CHARACTER_WIDTH = 8;

	private TextView _lbToolTip;
	private TextView _lbContent;
	private TextView _lbCategory;
	private LinkView[] _lbCategories;

	public PanelTooltip(RenderWindow app) {
		super(app, 0, new Vector2f(200, 200), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT), null);
		
		_lbToolTip = new TextView(null);
		_lbToolTip.setCharacterSize(FONT_SIZE_TITLE);
		_lbToolTip.setPosition(20, 18);
		addView(_lbToolTip);

		_lbContent = new TextView(null);
		_lbContent.setCharacterSize(FONT_SIZE);
		_lbContent.setPosition(20, 52);
		addView(_lbContent);
		
		FrameLayout layoutCategory = new FrameLayout(null);
		layoutCategory.setPosition(20, FRAME_HEIGHT - 66);
		addView(layoutCategory);
		
		_lbCategory = new TextView(null);
		_lbCategory.setCharacterSize(FONT_SIZE_TITLE);
		_lbCategory.setPosition(0, 0);
		layoutCategory.addView(_lbCategory);
		
		_lbCategories = new LinkView[NB_MAX_LINK];
		for (int i = 0; i < NB_MAX_LINK; i++) {
			_lbCategories[i] = new LinkView();
			_lbCategories[i].setCharacterSize(FONT_SIZE);
			_lbCategories[i].setColor(COLOR_TEXT);
			layoutCategory.addView(_lbCategories[i]);
		}
	}

	public void select(ToolTip toolTip) {
		StringBuilder sb = new StringBuilder();

		// Title
		_lbToolTip.setString(toolTip.title);
		
		// Content
		String content = toolTip.content;
		while (content.length() > LINE_LENGTH) {
			int spaceIndex = content.substring(0, LINE_LENGTH).lastIndexOf(' ');
			int nlIndex = content.substring(0, LINE_LENGTH).lastIndexOf('\n');
			int cutIndex = nlIndex == -1 ? spaceIndex : nlIndex;
			sb.append(content.substring(0, cutIndex == -1 ? LINE_LENGTH : cutIndex)).append('\n');
			content = content.substring(cutIndex == -1 ? LINE_LENGTH : cutIndex + 1);
		}
		sb.append(content);
		_lbContent.setString(sb.toString());
		
		// Category
		ToolTipCategory category = null;
		for (ToolTipCategory c: ToolTips.categories) {
			if (c.contains(toolTip)) {
				category = c;
			}
		}
		if (category != null) {
			_lbCategory.setString("See other tags for [" + category.title + "]");
			int i = 0;
			int posX = 0;
			for (ToolTip t: category.tooltips) {
				final ToolTip ft = t;
				_lbCategories[i].setString("[" + t.title + "]");
				_lbCategories[i].setVisible(true);
				_lbCategories[i].setPosition(posX, 32);
				_lbCategories[i].setSize(new Vector2f((t.title.length() + 2) * CHARACTER_WIDTH, LINE_HEIGHT));
				_lbCategories[i].resetPos();
				_lbCategories[i].setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						select(ft);
					}
				});
				posX += (t.title.length() + 2) * CHARACTER_WIDTH + 12;
				i++;
			}
			for (; i < NB_MAX_LINK; i++) {
				_lbCategories[i].setVisible(false);
			}
		}
	}

}
