package alone.in.deepspace.ui.panel;

import java.io.IOException;

import org.jsfml.system.Vector2f;

import alone.in.deepspace.ui.UserInterface.Mode;
import alone.in.deepspace.util.Constant;

public class PanelBase extends BasePanel {
	private static final int FRAME_WIDTH = Constant.PANEL_WIDTH;
	private static final int FRAME_HEIGHT = Constant.WINDOW_HEIGHT;

	public PanelBase() throws IOException {
		super(Mode.NONE, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 0), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));
	}

	@Override
	protected void onCreate() {
	}

}
