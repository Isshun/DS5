package alone.in.deepspace.Engine.renderer;
import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;


public class MainRenderer {
	public void setWindow(RenderWindow app) { _app = app; }
	  RenderWindow getWindow() { return _app; }

	  public void draw(Drawable sprite, RenderStates render) {
		  if (render != null) {
			  _app.draw(sprite, render);
		  } else {
			  _app.draw(sprite);
		  }
	  }

	  static MainRenderer 	_self;
	  RenderWindow			_app;

	  public static MainRenderer getInstance() {
		if (_self == null) {
			_self = new MainRenderer();
		}
		return _self;
	}

}
