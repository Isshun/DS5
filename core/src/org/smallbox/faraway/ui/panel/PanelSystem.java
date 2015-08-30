package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.Application;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInterface.Mode;
import org.smallbox.faraway.ui.engine.Colors;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.engine.view.FrameLayout;
import org.smallbox.faraway.ui.engine.view.UILabel;
import org.smallbox.faraway.ui.engine.view.View;
import org.smallbox.faraway.util.Constant;

public class PanelSystem extends BasePanel {
	private static final int FRAME_WIDTH = Constant.WINDOW_WIDTH;
	private static final int FRAME_HEIGHT = 32;

	private UILabel _lbRenderTime;
	private UILabel _lbMemoryUsed;
	private UILabel _lbUpdate;
	private UILabel _lbFloor;
	private UILabel _lbFrame;

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
	public void onLayoutLoaded(LayoutModel layout, FrameLayout panel) {
		_lbRenderTime = (UILabel)findById("lb_render_time");
		_lbMemoryUsed = (UILabel)findById("lb_memory");
		_lbUpdate = (UILabel)findById("lb_update");
		_lbFloor = (UILabel)findById("lb_floor");
		_lbFrame = (UILabel)findById("lb_frame");
	}

	@Override
	public void onRefresh(int frame) {
		int mb = 1024 * 1024;
        Runtime runtime = Runtime.getRuntime();
        int used = (int) ((runtime.totalMemory() - runtime.freeMemory()) / mb);
        int total = (int) (runtime.totalMemory() / mb);
        
//        _used = (_used * 7 + used) / 8;

        _lbRenderTime.setString("Rendering: %dms", (int)Application.getRenderTime());
        _lbMemoryUsed.setString("Heap: " + String.valueOf(used) + " / " + String.valueOf(total) + " Mo");
//        _lbUpdate.setString(String.format("Update: %d/%d", Application.getLastUpdateDelay(), Application.getLastLongUpdateDelay()));
//        _lbFloor.setString("FPS: " + MainRenderer.getFPS());
        _lbFrame.setString("tick: " + Game.getInstance().getTick() + " / frame: " + frame);
	}
}
