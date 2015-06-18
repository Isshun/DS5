package org.smallbox.faraway.ui.engine;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.GFXRenderer;
import org.smallbox.faraway.engine.RenderEffect;

public abstract class ColorView extends View {
	public ColorView(int width, int height) {
		super(width, height);
	}
	
	@Override
	public void onDraw(GFXRenderer renderer, RenderEffect effect) {
	}

	public Color getColor() {
		return _backgroundColor;
	}
}

