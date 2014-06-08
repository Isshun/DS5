package alone.in.deepspace.engine.ui;

import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

public class ColorView extends View {

	public ColorView(Vector2f size) {
		super(size);
	}
	
	@Override
	public void onDraw(RenderWindow app, RenderStates render) {
	}

	public void setPosition(int x, int y) {
		setPosition(new Vector2f(x, y));
	}

}

