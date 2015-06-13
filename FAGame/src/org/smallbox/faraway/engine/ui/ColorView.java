package org.smallbox.faraway.engine.ui;

import org.smallbox.faraway.Color;
import org.smallbox.faraway.GFXRenderer;
import org.smallbox.faraway.RenderEffect;

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

