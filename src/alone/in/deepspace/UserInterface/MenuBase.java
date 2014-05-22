package alone.in.deepspace.UserInterface;

import org.jsfml.system.Vector2f;
import org.jsfml.window.event.Event;
import org.jsfml.window.event.TextEvent;

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
