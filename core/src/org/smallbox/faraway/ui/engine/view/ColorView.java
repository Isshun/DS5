package org.smallbox.faraway.ui.engine.view;

import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.core.renderer.GDXRenderer;
import org.smallbox.faraway.engine.Color;

public abstract class ColorView extends View {
	public ColorView(int width, int height) {
		super(width, height);
	}
	
	@Override
	public void onDraw(GDXRenderer renderer, Viewport viewport) {
	}

	public Color getColor() {
		return _backgroundColor;
	}
}

