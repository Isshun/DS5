package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.Color;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.Renderer;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.engine.util.StringUtils;
import org.smallbox.faraway.ui.UserInterface.Mode;

public class PanelConsole extends BasePanel {
	private static final int 	LINE_INTERVAL = 20;
	private static final int 	NB_LINES = 8;
	private static final int 	FRAME_HEIGHT = 16 + LINE_INTERVAL * NB_LINES;
	private static final int	FRAME_WIDTH = 440;
	
	private TextView[] 		_texts;

	public PanelConsole() {
		super(Mode.NONE, null, 0, Constant.WINDOW_HEIGHT - FRAME_HEIGHT, FRAME_WIDTH, FRAME_HEIGHT);
		setAlwaysVisible(true);
	}		  

	@Override
	protected void onCreate() {
		_texts = new TextView[NB_LINES];
		for (int i = 0; i < NB_LINES; i++) {
			_texts[i] = new TextView();
			_texts[i].setCharacterSize(FONT_SIZE);
			_texts[i].setPosition(14, 8 + i * LINE_INTERVAL);
			addView(_texts[i]);
		}
		
		setBackgroundColor(new Color(18, 28, 30, 80));
	}

	public void addMessage(int level, String message) {
		for (int i = 0; i < NB_LINES - 1; i++) {
			_texts[i].setString(_texts[i+1].getString());
		}
		_texts[NB_LINES - 1].setString(StringUtils.getDashedString(message, "", 52));
	}

	@Override
	protected void onDraw(Renderer renderer, RenderEffect effect) {
        super.onDraw(renderer, effect);
	}
}
