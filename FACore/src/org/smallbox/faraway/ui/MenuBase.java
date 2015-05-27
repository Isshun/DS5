package org.smallbox.faraway.ui;

import org.jsfml.window.event.Event;
import org.smallbox.faraway.engine.ui.FrameLayout;

public abstract class MenuBase extends FrameLayout {
	public MenuBase(int width, int height) {
		super(width, height);
	}

	public void onKeyDown() {
	}

	public void onKeyUp() {
	}

	public void onKeyEnter() {
	}

	public boolean checkKey(Event event) {
		return false;
	}
}
