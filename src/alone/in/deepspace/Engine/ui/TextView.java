package alone.in.deepspace.engine.ui;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.manager.SpriteManager;

public class TextView extends View {

	private Text _text;

	public TextView(Vector2f size) {
		super(size);
		
		_text = new Text();
		_text.setFont(SpriteManager.getInstance().getFont());
	}

	public void setString(String string) {
		_text.setString(string);
	}

	public void setCharacterSize(int size) {
		_text.setCharacterSize(size);
	}

	public void setColor(Color color) {
		_text.setColor(color);
	}

	public void setPosition(Vector2f pos) {
		super.setPosition(pos);
		_text.setPosition(new Vector2f(pos.x + _paddingLeft, pos.y + _paddingTop));
	}

	@Override
	public void setPadding(int t, int r, int b, int l) {
		super.setPadding(t, r, b, l);
		if (_pos != null) {
			_text.setPosition(new Vector2f(_pos.x + _paddingLeft, _pos.y + _paddingTop));
		}
	}

	@Override
	public void onRefresh(RenderWindow app, RenderStates render) {
		app.draw(_text, render);
	}

	public void setPosition(int i, int j) {
		setPosition(new Vector2f(i, j));
	}
}
