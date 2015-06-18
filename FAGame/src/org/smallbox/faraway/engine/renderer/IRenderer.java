package org.smallbox.faraway.engine.renderer;

import org.smallbox.faraway.engine.GFXRenderer;
import org.smallbox.faraway.engine.RenderEffect;

public interface IRenderer {
	void onDraw(GFXRenderer renderer, RenderEffect effect, double animProgress);
	void onRefresh(int frame);
	void invalidate(int x, int y);
	void invalidate();
}
