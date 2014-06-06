package alone.in.deepspace.ui.panel;

import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard.Key;

import alone.in.deepspace.ui.UserInterface.Mode;

public class PanelSecurity extends BasePanel {

	public PanelSecurity(Mode mode, Key shortcut) {
		super(mode, shortcut, new Vector2f(0, 0), new Vector2f(200, 200), true);
	}

	@Override
	protected void onCreate() {
		// TODO Auto-generated method stub
		
	}
}
