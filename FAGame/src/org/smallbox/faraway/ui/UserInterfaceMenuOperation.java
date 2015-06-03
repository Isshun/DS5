package org.smallbox.faraway.ui;

import org.smallbox.faraway.GFXRenderer;
import org.smallbox.faraway.engine.ui.ViewFactory;
import org.smallbox.faraway.ui.UserInterface.Mode;
import org.smallbox.faraway.ui.panel.BasePanel;

import java.io.IOException;

public class UserInterfaceMenuOperation extends BasePanel {
	
	public void toogleJobs() { _isJobsOpen = !_isJobsOpen; }

	boolean			_isJobsOpen;
	
	UserInterfaceMenuOperation(GFXRenderer renderer, int tileIndex) throws IOException {
		super(Mode.NONE, null, 0, 0, 200, 200, null);
	}

	@Override
	protected void onCreate(ViewFactory factory) {
	}
}
