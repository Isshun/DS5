package org.smallbox.faraway.engine.renderer;

import org.smallbox.faraway.engine.GFXRenderer;
import org.smallbox.faraway.engine.RenderEffect;
import org.smallbox.faraway.util.Log;

public abstract class BaseRenderer {
	public abstract void onDraw(GFXRenderer renderer, RenderEffect effect, double animProgress);
	public abstract void onRefresh(int frame);
	public abstract void invalidate(int x, int y);
	public abstract void invalidate();
	public void draw(GFXRenderer renderer, RenderEffect effect, double animProgress) {
		onDraw(renderer, effect, animProgress);
	}
}
