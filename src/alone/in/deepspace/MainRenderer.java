package alone.in.DeepSpace;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;


public class MainRenderer {
	void setWindow(RenderWindow app) { _app = app; }
	  RenderWindow getWindow() { return _app; }
	  public void draw(Sprite sprite, RenderStates render) { _app.draw(sprite, render); }

	  static MainRenderer 	_self;
	  RenderWindow			_app;

	  public static MainRenderer getInstance() {
		if (_self == null) {
			_self = new MainRenderer();
		}
		return _self;
	}

}
