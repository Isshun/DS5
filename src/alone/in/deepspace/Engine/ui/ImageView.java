package alone.in.deepspace.engine.ui;

import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.system.Vector2f;

public class ImageView extends View {

	private Sprite 			_icon;

	public ImageView() {
		super(new Vector2f(0, 0));
	}
	
	public ImageView(Sprite sprite) {
		super(sprite.getScale());
		
		_icon = sprite;
	}
	
	@Override
	protected void onCreate() {
	}

	public void setPosition(Vector2f pos) {
		super.setPosition(pos);
		if (_icon != null) {
			_icon.setPosition(pos);
		}
	}

	@Override
	public void onRefresh(RenderWindow app, RenderStates render) {
		if (_icon != null) {
			_icon.setPosition(_pos.x + _paddingLeft, _pos.y + _paddingTop);
			app.draw(_icon, render);
		}
	}

	public void setPosition(int i, int j) {
		setPosition(new Vector2f(i, j));
	}

	public void setImage(Sprite icon) {
		_icon = icon;
	}

}

