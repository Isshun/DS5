package org.smallbox.faraway.renderer;

import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.Renderer;
import org.smallbox.faraway.engine.Viewport;

public interface IRenderer {
	void onDraw(Renderer renderer, RenderEffect effect, double animProgress);
	void onRefresh(int frame);
	void invalidate(int x, int y);
	void invalidate();
}
