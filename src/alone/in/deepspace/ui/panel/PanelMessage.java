package alone.in.deepspace.ui.panel;

import org.jsfml.graphics.Color;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.engine.ui.TextView;
import alone.in.deepspace.ui.UserInterface.Mode;
import alone.in.deepspace.util.Constant;
import alone.in.deepspace.util.StringUtils;

public class PanelMessage extends BasePanel {
	private static final int 	LINE_INTERVAL = 20;
	private static final int 	NB_LINES = 8;
	private static final int 	FRAME_HEIGHT = 16 + LINE_INTERVAL * NB_LINES;
	private static final int	FRAME_WIDTH = 440;
	
	private TextView[] 		_texts;

	public PanelMessage() {
		super(Mode.NONE, null, new Vector2f(0, Constant.WINDOW_HEIGHT - FRAME_HEIGHT), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));
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

}
