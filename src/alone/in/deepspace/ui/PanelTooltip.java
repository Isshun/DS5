package alone.in.deepspace.ui;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.engine.ui.TextView;

public class PanelTooltip extends UserSubInterface {

	private static final int FRAME_WIDTH = 800;
	private static final int FRAME_HEIGHT = 600;
	private static final int LINE_LENGTH = 80;

	private TextView 	_lbToolTip;

	public PanelTooltip(RenderWindow app) {
		super(app, 0, new Vector2f(200, 200), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT), null);
		
		_lbToolTip = new TextView(null);
		_lbToolTip.setCharacterSize(16);
		_lbToolTip.setPosition(20, 18);
		addView(_lbToolTip);
	}

	public void setTooltip(String tip) {
		StringBuilder sb = new StringBuilder();

		while (tip.length() > LINE_LENGTH) {
			int spaceIndex = tip.substring(0, LINE_LENGTH).lastIndexOf(' ');
			int nlIndex = tip.substring(0, LINE_LENGTH).lastIndexOf('\n');
			int cutIndex = nlIndex == -1 ? spaceIndex : nlIndex;
			sb.append(tip.substring(0, cutIndex == -1 ? LINE_LENGTH : cutIndex)).append('\n');
			tip = tip.substring(cutIndex == -1 ? LINE_LENGTH : cutIndex + 1);
		}
		sb.append(tip);
		
		_lbToolTip.setString(sb.toString());
	}

}
