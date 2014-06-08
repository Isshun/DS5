package alone.in.deepspace.ui.panel;

import org.jsfml.graphics.Color;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.Main;
import alone.in.deepspace.engine.renderer.MainRenderer;
import alone.in.deepspace.engine.ui.ColorView;
import alone.in.deepspace.engine.ui.Colors;
import alone.in.deepspace.engine.ui.TextView;
import alone.in.deepspace.engine.ui.View;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.ui.UserInterface.Mode;
import alone.in.deepspace.util.Constant;

public class PanelSystem extends BasePanel {
	private static final int FRAME_WIDTH = Constant.WINDOW_WIDTH;
	private static final int FRAME_HEIGHT = 32;
	private TextView 	_lbRenderTime;
	private TextView 	_lbMemoryUsed;
	private TextView 	_lbUpdate;
	private TextView 	_lbFloor;
	
	public PanelSystem() {
		super(Mode.NONE, null, new Vector2f(0, 0), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));
	}
	
	@Override
	protected void onCreate() {
		setBackgroundColor(Colors.BT_INACTIVE);
		
		View border = new ColorView(new Vector2f(_size.x, 4));
		border.setBackgroundColor(Colors.BACKGROUND);
		border.setPosition(_posX, (int)(_posY + _size.y));
		super.addView(border);

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

		setAlwaysVisible(true);
	}

	@Override
	public void onRefresh(int frame) {
		int mb = 1024 * 1024;
        Runtime runtime = Runtime.getRuntime();
        int used = (int) ((runtime.totalMemory() - runtime.freeMemory()) / mb);
        int total = (int) (runtime.totalMemory() / mb);
        
//        _used = (_used * 7 + used) / 8;

        _lbRenderTime.setString("Rendering: " + MainRenderer.getRenderTime() + "ms");
        _lbMemoryUsed.setString("Heap: " + String.valueOf(used) + " / " + String.valueOf(total) + " Mo");
        _lbUpdate.setString("Update: " + String.valueOf(Main.getUpdateInterval()) + " ms");
        _lbFloor.setString("FPS: " + MainRenderer.getFPS());
	}
}
