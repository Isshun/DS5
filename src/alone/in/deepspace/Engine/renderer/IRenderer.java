package alone.in.deepspace.engine.renderer;

import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;

public interface IRenderer {
	void onDraw(RenderWindow app, RenderStates render, double animProgress);
	void onRefresh(int frame);
	void invalidate(int x, int y);
	void invalidate();
}
