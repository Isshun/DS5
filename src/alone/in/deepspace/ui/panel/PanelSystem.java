package alone.in.deepspace.ui.panel;

import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.Main;
import alone.in.deepspace.engine.renderer.MainRenderer;
import alone.in.deepspace.engine.ui.TextView;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.ui.UserSubInterface;
import alone.in.deepspace.util.Constant;

public class PanelSystem extends UserSubInterface {
	private static final int FRAME_WIDTH = Constant.WINDOW_WIDTH;
	private static final int FRAME_HEIGHT = 32;
	private TextView _lbRenderTime;
	private TextView _lbMemoryUsed;
	private int _used;
	private TextView _lbUpdate;
	private TextView _lbFloor;
	
	public PanelSystem(RenderWindow app) throws IOException {
		super(app, 0, new Vector2f(0, 0), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));
		
		setBackgroundColor(new Color(200, 50, 140, 150));
		
		_lbRenderTime = new TextView(new Vector2f(10, 10));
		_lbRenderTime.setCharacterSize(14);
		_lbRenderTime.setColor(Color.WHITE);
		_lbRenderTime.setPosition(new Vector2f(10, 6));
		addView(_lbRenderTime);
		
		_lbMemoryUsed = new TextView(new Vector2f(10, 10));
		_lbMemoryUsed.setCharacterSize(14);
		_lbMemoryUsed.setColor(Color.WHITE);
		_lbMemoryUsed.setPosition(new Vector2f(200, 6));
		addView(_lbMemoryUsed);

		_lbUpdate = new TextView(new Vector2f(10, 10));
		_lbUpdate.setCharacterSize(14);
		_lbUpdate.setColor(Color.WHITE);
		_lbUpdate.setPosition(new Vector2f(380, 6));
		addView(_lbUpdate);

		_lbFloor = new TextView(new Vector2f(10, 10));
		_lbFloor.setCharacterSize(14);
		_lbFloor.setColor(Color.WHITE);
		_lbFloor.setPosition(new Vector2f(550, 6));
		addView(_lbFloor);
	}
	
	@Override
	public void onDraw(RenderWindow app, RenderStates render) {
		int mb = 1024 * 1024;
        Runtime runtime = Runtime.getRuntime();
        int used = (int) ((runtime.totalMemory() - runtime.freeMemory()) / mb);
        int total = (int) (runtime.totalMemory() / mb);
        
        _used = (_used * 7 + used) / 8;

        _lbRenderTime.setString("Rendering: " + MainRenderer.getInstance().getRenderTime() + "ms");
        _lbMemoryUsed.setString("Heap: " + String.valueOf(_used) + " / " + String.valueOf(total) + " Mo");
        _lbUpdate.setString("Update: " + String.valueOf(Main.getUpdateInterval()) + " ms");
        _lbFloor.setString("Floor: " + ServiceManager.getWorldMap().getFloor());
	}
}
