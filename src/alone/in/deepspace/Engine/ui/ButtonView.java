package alone.in.deepspace.Engine.ui;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.Managers.SpriteManager;

public class ButtonView extends View {

	private Text 			_text;
	private RectangleShape 	_background;
	private Sprite 			_icon;
	private int 			_iconPaddingX;
	private int 			_iconPaddingY;

	public ButtonView(Vector2f size, String string) {
		super(size);
		
		_text = new Text();
		_text.setString(string);
		_text.setCharacterSize(12);
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
	
//	public void setBackground(Color color) {
//		_background = new RectangleShape();
//		_background.setSize(new Vector2f(62, 80));
//		_background.setFillColor(color);
//		if (_pos != null) {
//			_background.setPosition(_posX, _posY);
//		}
//	}

	public void setPosition(Vector2f pos) {
		super.setPosition(pos);
		if (_background != null) {
			_background.setPosition(pos);
		}
		if (_icon != null) {
			_icon.setPosition(pos);
		}
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
		if (_background != null) {
			app.draw(_background, render);
		}

		if (_icon != null) {
			_icon.setPosition(_pos.x + _paddingLeft + _iconPaddingX, _pos.y + _paddingTop + _iconPaddingY);
			app.draw(_icon, render);
		}

		if (_text != null) {
			app.draw(_text, render);
		}
	}

	public void setPosition(int i, int j) {
		setPosition(new Vector2f(i, j));
	}

	public void setIcon(Sprite icon) {
		_icon = icon;
	}

	public void setIconPadding(int x, int y) {
		_iconPaddingX = x;
		_iconPaddingY = y;
	}
}

