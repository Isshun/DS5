package org.smallbox.faraway.ui.engine.view;

import org.smallbox.faraway.engine.Color;

public abstract class ColorView extends View {
	public ColorView(int width, int height) {
		super(width, height);
	}

	public Color getColor() {
		return _backgroundColor;
	}
}

