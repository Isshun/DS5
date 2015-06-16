package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.Application;
import org.smallbox.faraway.engine.renderer.MainRenderer;
import org.smallbox.faraway.engine.ui.Colors;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.engine.ui.View;
import org.smallbox.faraway.engine.ui.ViewFactory;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInterface.Mode;

public class PanelSystem extends BasePanel {
	private static final int FRAME_WIDTH = Constant.WINDOW_WIDTH;
	private static final int FRAME_HEIGHT = 32;

	private TextView 	    _lbRenderTime;
	private TextView 	    _lbMemoryUsed;
	private TextView 	    _lbUpdate;
	private TextView 	    _lbFloor;
	private TextView 		_lbFrame;

	public PanelSystem() {
		super(Mode.NONE, null, 0, 32, FRAME_WIDTH, FRAME_HEIGHT, "data/ui/panels/system.yml");
	}
	
	@Override
	protected void onCreate(ViewFactory factory) {
        setBackgroundColor(Colors.BT_INACTIVE);

        View border = factory.createColorView(_width, 4);
        border.setBackgroundColor(Colors.BACKGROUND);
        border.setPosition(_x, _y + _height);
        addView(border);

     	setAlwaysVisible(true);
	}

	@Override
	public void onLayoutLoaded(LayoutModel layout) {
		_lbRenderTime = (TextView)findById("lb_render_time");
		_lbMemoryUsed = (TextView)findById("lb_memory");
		_lbUpdate = (TextView)findById("lb_update");
		_lbFloor = (TextView)findById("lb_floor");
		_lbFrame = (TextView)findById("lb_frame");
	}

	@Override
	public void onRefresh(int tick) {
		int mb = 1024 * 1024;
        Runtime runtime = Runtime.getRuntime();
        int used = (int) ((runtime.totalMemory() - runtime.freeMemory()) / mb);
        int total = (int) (runtime.totalMemory() / mb);
        
//        _used = (_used * 7 + used) / 8;

        _lbRenderTime.setString("Rendering: %dms", (int)MainRenderer.getRenderTime());
        _lbMemoryUsed.setString("Heap: " + String.valueOf(used) + " / " + String.valueOf(total) + " Mo");
        _lbUpdate.setString(String.format("Update: %d/%d", Application.getLastUpdateDelay(), Application.getLastLongUpdateDelay()));
        _lbFloor.setString("FPS: " + MainRenderer.getFPS());
        _lbFrame.setString("tick: " + tick + " / frame: " + Application.getFrame());
	}
}
