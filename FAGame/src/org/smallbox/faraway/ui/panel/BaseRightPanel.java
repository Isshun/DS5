package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.GameEventListener;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.engine.ui.*;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.manager.SpriteManager;
import org.smallbox.faraway.ui.UserInteraction;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.UserInterface.Mode;

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
			TextView btBack = ViewFactory.getInstance().createTextView(100, 32);
			btBack.setTextPadding(1, 6);
			btBack.setCharacterSize(FONT_SIZE_TITLE);
			btBack.setString("[    ]");
			btBack.setPosition(25, -32);
			btBack.setOnClickListener(view -> _ui.back());
			addView(btBack);
			
			TextView lbBack = SpriteManager.getInstance().createTextView();
			lbBack.setString("Back");
			lbBack.setCharacterSize(FONT_SIZE_TITLE);
			lbBack.setColor(Colors.LINK_ACTIVE);
			lbBack.setPosition(20, -32);
			lbBack.setPadding(1, 20);
			addView(lbBack);
			
			ColorView rectangleUnderline = ViewFactory.getInstance().createColorView((int)(4 * 12.5), 1);
			rectangleUnderline.setPosition(40, 36);
			rectangleUnderline.setBackgroundColor(Colors.LINK_ACTIVE);
			addView(rectangleUnderline);
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
