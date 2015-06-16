package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.Color;
import org.smallbox.faraway.engine.ui.FrameLayout;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.engine.ui.ViewFactory;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.engine.util.StringUtils;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInterface.Mode;

public class PanelConsole extends BasePanel {
	private static final int 	LINE_INTERVAL = 20;
	private static final int 	NB_LINES = 8;
	private static final int 	FRAME_HEIGHT = 65 + LINE_INTERVAL * NB_LINES;
	private static final int	FRAME_WIDTH = 540;
	private static final int 	NB_CONSOLE_COLUMNS = 62;
	private static final Color 	COLOR_DEBUG = new Color(0x888888);
	private static final Color 	COLOR_INFO = new Color(0xffffff);
	private static final Color 	COLOR_WARNING = new Color(0xffdd00);
	private static final Color 	COLOR_ERROR = new Color(0xdd0000);
	private static final Color 	COLOR_FATAL = new Color(0xff0000);

	private String[][] 		_data = new String[5][NB_LINES];
	private TextView[] 		_texts;
	private int 			_level;

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
			_texts[i].setPosition(0, i * LINE_INTERVAL);
			frameEntries.addView(_texts[i]);
		}

		select("frame_debug", Log.LEVEL_INFO);
	}

	private void select(String frameName, int level) {
		findById("frame_debug").setVisible(false);
		findById("frame_info").setVisible(false);
		findById("frame_warning").setVisible(false);
		findById("frame_error").setVisible(false);

		findById(frameName).setVisible(true);

		_level = level;

		refreshLabels();
	}

	public void addMessage(int level, String message) {
		if (_level < level) return;

		message = Log.getPrefix(level) + StringUtils.getDashedString(message, "", NB_CONSOLE_COLUMNS);

		// Store message
		for (int l = Log.LEVEL_FATAL; l <= Log.LEVEL_DEBUG; l++) {
			if (l >= level) {
				for (int i = 0; i < NB_LINES - 1; i++) {
					_data[l][i] = _data[l][i + 1];
				}
				_data[l][NB_LINES - 1] = message;
			}
		}

		refreshLabels();
	}

	private void refreshLabels() {
		for (int i = 0; i <= NB_LINES - 1; i++) {
			_texts[i].setString(_data[_level][i]);

			// Set color
			if (_data[_level][i] != null && _data[_level][i].startsWith("[D]")) _texts[i].setColor(COLOR_DEBUG);
			if (_data[_level][i] != null && _data[_level][i].startsWith("[I]")) _texts[i].setColor(COLOR_INFO);
			if (_data[_level][i] != null && _data[_level][i].startsWith("[W]")) _texts[i].setColor(COLOR_WARNING);
			if (_data[_level][i] != null && _data[_level][i].startsWith("[E]")) _texts[i].setColor(COLOR_ERROR);
			if (_data[_level][i] != null && _data[_level][i].startsWith("[F]")) _texts[i].setColor(COLOR_FATAL);
		}
	}
}
