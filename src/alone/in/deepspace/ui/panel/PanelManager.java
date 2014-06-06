package alone.in.deepspace.ui.panel;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.Image;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard.Key;

import alone.in.deepspace.engine.ui.ImageView;
import alone.in.deepspace.engine.ui.TextView;
import alone.in.deepspace.manager.StatsManager;
import alone.in.deepspace.ui.UserInterface.Mode;
import alone.in.deepspace.util.Constant;

public class PanelManager extends BasePanel {
	private static final int 	FRAME_WIDTH = Constant.PANEL_WIDTH;
	private static final int 	FRAME_HEIGHT = Constant.WINDOW_HEIGHT;
	private static final Color COLOR_BORDER = new Color(22, 50, 56);
	private static final int WIDTH = 300;
	private static final int HEIGHT = 200;
	private static final int NB_DATA_MAX = 5;

	private ImageView 			_imageView;
	private StatsManager		_stats;
	private Image 				_image;
	private TextView[] 			_labels;

	public PanelManager(Mode mode, Key shortcut) {
		super(mode, shortcut, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 0), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT), true);
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
