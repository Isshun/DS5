package org.smallbox.faraway.ui.engine.view;

import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.renderer.GDXRenderer;

public abstract class ColorView extends View {
	public ColorView(int width, int height) {
		super(width, height);
	}

	public Color getColor() {
		return _backgroundColor;
	}
}

