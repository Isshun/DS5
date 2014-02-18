package alone.in.deepspace.UserInterface;

import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.Utils.Constant;

public class PanelBase extends UserSubInterface {
	private static final int FRAME_WIDTH = 380;
	private static final int FRAME_HEIGHT = Constant.WINDOW_HEIGHT;

	public PanelBase(RenderWindow app) throws IOException {
		super(app, 0, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 0), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));
		
		setBackgroundColor(new Color(0, 0, 0, 150));
	}

	@Override
	public void onRefresh(RenderWindow app) {
	}

}
