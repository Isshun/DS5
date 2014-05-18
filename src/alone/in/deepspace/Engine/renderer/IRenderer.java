package alone.in.deepspace.Engine.renderer;

import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;

public interface IRenderer {
	void onDraw(RenderWindow app, RenderStates render, int frame);
}
