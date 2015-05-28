package org.smallbox.faraway.renderer;

import org.smallbox.faraway.GFXRenderer;
import org.smallbox.faraway.RenderEffect;

public interface IRenderer {
	void onDraw(GFXRenderer renderer, RenderEffect effect, double animProgress);
	void onRefresh(int frame);
	void invalidate(int x, int y);
	void invalidate();
}
