package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.Color;
import org.smallbox.faraway.engine.ui.FrameLayout;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.engine.ui.View;
import org.smallbox.faraway.engine.ui.ViewFactory;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.engine.util.StringUtils;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInterface.Mode;

public class PanelConsole extends BasePanel {
	private static final int 	LINE_INTERVAL = 20;
	private static final int 	NB_LINES = 8;
	private static final int 	FRAME_HEIGHT = 64 + LINE_INTERVAL * NB_LINES;
	private static final int	FRAME_WIDTH = 440;
	
	private TextView[] 		_texts;
	private int 			_level = Log.LEVEL_DEBUG;

	public PanelConsole() {
		super(Mode.NONE, null, 0, Constant.WINDOW_HEIGHT - FRAME_HEIGHT, FRAME_WIDTH, FRAME_HEIGHT, "data/ui/panels/console.yml");
		setAlwaysVisible(true);
	}		  

	@Override
	protected void onCreate(ViewFactory factory) {
		setBackgroundColor(new Color(18, 28, 30, 80));
	}

	@Override
	public void onLayoutLoaded(LayoutModel layout) {
		findById("bt_debug").setOnClickListener(view -> select("frame_debug", Log.LEVEL_DEBUG));
		findById("bt_info").setOnClickListener(view -> select("frame_info", Log.LEVEL_INFO));
		findById("bt_warning").setOnClickListener(view -> select("frame_warning", Log.LEVEL_WARNING));
		findById("bt_error").setOnClickListener(view -> select("frame_error", Log.LEVEL_ERROR));

		FrameLayout frameEntries = (FrameLayout) findById("frame_entries");
		_texts = new TextView[NB_LINES];
		for (int i = 0; i < NB_LINES; i++) {
			_texts[i] = ViewFactory.getInstance().createTextView();
			_texts[i].setCharacterSize(FONT_SIZE);
			_texts[i].setPosition(14, 8 + i * LINE_INTERVAL);
			frameEntries.addView(_texts[i]);
		}

		select("frame_debug", Log.LEVEL_DEBUG);
	}

	private void select(String frameName, int level) {
		findById("frame_debug").setVisible(false);
		findById("frame_info").setVisible(false);
		findById("frame_warning").setVisible(false);
		findById("frame_error").setVisible(false);

		findById(frameName).setVisible(true);

		_level = level;
	}

	public void addMessage(int level, String message) {
		if (_level < level) return;

		for (int i = 0; i < NB_LINES - 1; i++) {
			_texts[i].setString(_texts[i + 1].getString());
		}
		_texts[NB_LINES - 1].setString(StringUtils.getDashedString(message, "", 52));
	}
}
