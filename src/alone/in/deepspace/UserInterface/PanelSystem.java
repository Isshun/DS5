package alone.in.DeepSpace.UserInterface;

import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import alone.in.DeepSpace.CharacterManager;
import alone.in.DeepSpace.Models.Profession;
import alone.in.DeepSpace.UserInterface.Utils.OnClickListener;
import alone.in.DeepSpace.UserInterface.Utils.UIText;
import alone.in.DeepSpace.UserInterface.Utils.UIView;
import alone.in.DeepSpace.Utils.Constant;

public class PanelSystem extends UserSubInterface {
	private static final int FRAME_WIDTH = Constant.WINDOW_WIDTH;
	private static final int FRAME_HEIGHT = 32;
	private UIText _lbRenderTime;
	private UIText _lbMemoryUsed;
	private UIText _lbMemoryTotal;
	
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
		_lbMemoryUsed.setPosition(new Vector2f(100, 6));
		addView(_lbMemoryUsed);
		
		_lbMemoryTotal = new UIText(new Vector2f(10, 10));
		_lbMemoryTotal.setCharacterSize(14);
		_lbMemoryTotal.setColor(Color.WHITE);
		_lbMemoryTotal.setPosition(new Vector2f(200, 6));
		addView(_lbMemoryTotal);
	}
	
	public void refresh(int renderTime) {
		super.refresh();
		
		int mb = 1024 * 1024;
        Runtime runtime = Runtime.getRuntime();
        int used = (int) ((runtime.totalMemory() - runtime.freeMemory()) / mb);
        int total = (int) (runtime.totalMemory() / mb);
        int max = (int) (runtime.maxMemory() / mb);
        
        _lbRenderTime.setString(renderTime + "ms");
        _lbMemoryUsed.setString(String.valueOf(used));
        _lbMemoryTotal.setString(String.valueOf(total));
	}
}
