package org.smallbox.faraway.ui.panel;

import org.jsfml.system.Vector2f;

import org.smallbox.faraway.Color;
import org.smallbox.faraway.Main;
import org.smallbox.faraway.renderer.MainRenderer;
import org.smallbox.faraway.engine.ui.ColorView;
import org.smallbox.faraway.engine.ui.Colors;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.engine.ui.View;
import org.smallbox.faraway.ui.UserInterface.Mode;
import org.smallbox.faraway.engine.util.Constant;

public class PanelSystem extends BasePanel {
	private static final int FRAME_WIDTH = Constant.WINDOW_WIDTH;
	private static final int FRAME_HEIGHT = 32;
	private TextView 	_lbRenderTime;
	private TextView 	_lbMemoryUsed;
	private TextView 	_lbUpdate;
	private TextView 	_lbFloor;
	
	public PanelSystem() {
		super(Mode.NONE, null, 0, 0, FRAME_WIDTH, FRAME_HEIGHT);
	}
	
	@Override
	protected void onCreate() {
		setBackgroundColor(Colors.BT_INACTIVE);
		
		View border = new ColorView((int)_size.x, 4);
		border.setBackgroundColor(Colors.BACKGROUND);
		border.setPosition(_posX, (int)(_posY + _size.y));
		super.addView(border);

		_lbRenderTime = new TextView(10, 10);
		_lbRenderTime.setCharacterSize(14);
		_lbRenderTime.setColor(Color.WHITE);
		_lbRenderTime.setPosition(10, 6);
		addView(_lbRenderTime);
		
		_lbMemoryUsed = new TextView(10, 10);
		_lbMemoryUsed.setCharacterSize(14);
		_lbMemoryUsed.setColor(Color.WHITE);
		_lbMemoryUsed.setPosition(200, 6);
		addView(_lbMemoryUsed);

		_lbUpdate = new TextView(10, 10);
		_lbUpdate.setCharacterSize(14);
		_lbUpdate.setColor(Color.WHITE);
		_lbUpdate.setPosition(380, 6);
		addView(_lbUpdate);

		_lbFloor = new TextView(10, 10);
		_lbFloor.setCharacterSize(14);
		_lbFloor.setColor(Color.WHITE);
		_lbFloor.setPosition(550, 6);
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
