package alone.in.deepspace.ui;

import org.jsfml.system.Vector2f;
import org.jsfml.window.event.Event;

import alone.in.deepspace.engine.ui.FrameLayout;

public abstract class MenuBase extends FrameLayout {
	public MenuBase(Vector2f size) {
		super(size);
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