package alone.in.deepspace.ui;

import java.io.IOException;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.ui.UserInterface.Mode;
import alone.in.deepspace.ui.panel.BasePanel;

public class UserInterfaceMenuOperation extends BasePanel {
	
	public void toogleJobs() { _isJobsOpen = !_isJobsOpen; }

	boolean			_isJobsOpen;
	
	UserInterfaceMenuOperation(RenderWindow app, int tileIndex) throws IOException {
		super(Mode.NONE, new Vector2f(0, 0), new Vector2f(200, 200), true);
	}

	@Override
	protected void onCreate() {
	}
}
