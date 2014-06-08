package alone.in.deepspace.ui.panel;

import org.jsfml.window.Keyboard.Key;

import alone.in.deepspace.engine.ui.TextView;
import alone.in.deepspace.ui.UserInterface.Mode;

public class PanelManager extends BaseRightPanel {
	private static final int NB_DATA_MAX = 5;

	private TextView[] 			_labels;

	public PanelManager(Mode mode, Key shortcut) {
		super(mode, shortcut);
	}

	@Override
	protected void onCreate() {
		_labels = new TextView[NB_DATA_MAX];
		for (int i = 0; i < NB_DATA_MAX; i++) {
			_labels[i] = new TextView();
			_labels[i].setPosition(20 + i * 80, 252);
			_labels[i].setCharacterSize(FONT_SIZE);
			addView(_labels[i]);
		}
	}

	@Override
	public void onRefresh(int frame) {
	}
}
