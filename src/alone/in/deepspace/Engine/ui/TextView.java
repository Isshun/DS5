package alone.in.deepspace.engine.ui;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.manager.SpriteManager;
import alone.in.deepspace.util.StringUtils;

public class TextView extends View {

	public static final int	REGULAR = Text.REGULAR;
	public static final int	BOLD = Text.BOLD;
	public static final int	ITALIC = Text.ITALIC;
	public static final int	UNDERLINED = Text.UNDERLINED;
	
	protected FakeText 			_text;
	protected String 			_value;

	public TextView() {
		super(new Vector2f(0, 0));
		init();
	}

	public TextView(Vector2f size) {
		super(size);
		init();
	}

	private void init() {
		_text = new FakeText();
		_text.setFont(SpriteManager.getInstance().getFont());
		_text.setColor(Colors.TEXT);
	}

	public void setString(String string) {
		if (string != null && string.equals(_value) == false) {
			_text.setString(string);
			_value = string;
		}
	}

	public void setCharacterSize(int size) {
		_text.setCharacterSize(size);
	}

	public void setStyle(int style) {
		_text.setStyle(style);
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
	public void setPadding(int t, int r) {
		super.setPadding(t, r);
		if (_pos != null) {
			_text.setPosition(new Vector2f(_pos.x + _paddingLeft, _pos.y + _paddingTop));
		}
	}

	@Override
	public void onDraw(RenderWindow app, RenderStates render) {
		app.draw(_text, render);
	}

	public void setPosition(int i, int j) {
		setPosition(new Vector2f(i, j));
	}

	public void setDashedString(String label, String value, int nbColumns) {
		setString(StringUtils.getDashedString(label, value, nbColumns));
	}

	public String getString() {
		return _value;
	}
}
