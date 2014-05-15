package alone.in.deepspace.UserInterface.Panels;

import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.Game;
import alone.in.deepspace.UserInterface.UserSubInterface;
import alone.in.deepspace.UserInterface.Utils.UIText;
import alone.in.deepspace.Utils.Constant;

public class PanelSystem extends UserSubInterface {
	private static final int FRAME_WIDTH = Constant.WINDOW_WIDTH;
	private static final int FRAME_HEIGHT = 32;
	private UIText _lbRenderTime;
	private UIText _lbMemoryUsed;
	private int _used;
	
	public PanelSystem(RenderWindow app) throws IOException {
		super(app, 0, new Vector2f(0, 0), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));
		
		setBackgroundColor(new Color(200, 50, 140, 150));
		
		_lbRenderTime = new UIText(new Vector2f(10, 10));
		_lbRenderTime.setCharacterSize(14);
		_lbRenderTime.setColor(Color.WHITE);
		_lbRenderTime.setPosition(new Vector2f(10, 6));
		addView(_lbRenderTime);
		
		_lbMemoryUsed = new UIText(new Vector2f(10, 10));
		_lbMemoryUsed.setCharacterSize(14);
		_lbMemoryUsed.setColor(Color.WHITE);
		_lbMemoryUsed.setPosition(new Vector2f(200, 6));
		addView(_lbMemoryUsed);
	}
	
	@Override
	public void onRefresh(RenderWindow app) {
		int mb = 1024 * 1024;
        Runtime runtime = Runtime.getRuntime();
        int used = (int) ((runtime.totalMemory() - runtime.freeMemory()) / mb);
        int total = (int) (runtime.totalMemory() / mb);
        
        _used = (_used * 7 + used) / 8;

        _lbRenderTime.setString("Rendering: " + Game.renderTime + "ms");
        _lbMemoryUsed.setString("Heap: " + String.valueOf(_used) + " / " + String.valueOf(total) + " Mo");
	}
}
