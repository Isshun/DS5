package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.engine.RenderEffect;
import org.smallbox.faraway.ui.LinkFocusListener;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.ui.UserInteraction;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.UserInterface.Mode;
import org.smallbox.faraway.ui.engine.*;

public abstract class BaseRightPanel extends BasePanel {
	protected static final int FRAME_WIDTH = Constant.PANEL_WIDTH;
	protected static final int FRAME_HEIGHT = Constant.WINDOW_HEIGHT;

    public BaseRightPanel(Mode mode, GameEventListener.Key shortcut) {
		super(mode, shortcut, Constant.WINDOW_WIDTH - FRAME_WIDTH, 0, FRAME_WIDTH, FRAME_HEIGHT, null);
	}

    public BaseRightPanel(Mode mode, GameEventListener.Key shortcut, String layoutPath) {
        super(mode, shortcut, Constant.WINDOW_WIDTH - FRAME_WIDTH, 0, FRAME_WIDTH, FRAME_HEIGHT, layoutPath);
    }

    @Override
	public void init(ViewFactory viewFactory, LayoutFactory factory, UserInterface ui, UserInteraction interaction, RenderEffect effect) {
		super.init(viewFactory, factory, ui, interaction, effect);

		setBackgroundColor(Colors.BACKGROUND);
		View border = ViewFactory.getInstance().createColorView(4, FRAME_HEIGHT);
		border.setBackgroundColor(Colors.BORDER);
		addView(border);
		
		if (_mode != Mode.NONE) {
			TextView lbBack = ViewFactory.getInstance().createTextView();
			lbBack.setString("[Back]");
			lbBack.setCharacterSize(FONT_SIZE_TITLE);
			lbBack.setColor(Colors.LINK_INACTIVE);
			lbBack.setPosition(22, -22);
			lbBack.setSize(120, 32);
			lbBack.setAlign(Align.CENTER);
			lbBack.setBackgroundColor(new Color(0x1d5560));
			lbBack.setOnClickListener(view -> _ui.back());
			lbBack.setOnFocusListener(new LinkFocusListener());
			addView(lbBack);
		}
	}

    @Override
	public void addView(View view) {
		if (_mode != Mode.NONE) {
			view.setPosition(view.getPosX(), view.getPosY() + 42);
		}
		super.addView(view);
	}
}
