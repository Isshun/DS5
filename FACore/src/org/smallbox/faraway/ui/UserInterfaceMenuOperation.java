package org.smallbox.faraway.ui;

import org.smallbox.faraway.Renderer;
import org.smallbox.faraway.ui.UserInterface.Mode;
import org.smallbox.faraway.ui.panel.BasePanel;

import java.io.IOException;

public class UserInterfaceMenuOperation extends BasePanel {
	
	public void toogleJobs() { _isJobsOpen = !_isJobsOpen; }

	boolean			_isJobsOpen;
	
	UserInterfaceMenuOperation(Renderer renderer, int tileIndex) throws IOException {
		super(Mode.NONE, null, 0, 0, 200, 200);
	}

	@Override
	protected void onCreate() {
	}
}
