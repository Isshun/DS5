package org.smallbox.faraway.renderer;

import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.Renderer;

public interface IRenderer {
	void onDraw(Renderer renderer, RenderEffect effect, double animProgress);
	void onRefresh(int frame);
	void invalidate(int x, int y);
	void invalidate();
}
