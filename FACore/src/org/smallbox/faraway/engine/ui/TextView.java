package org.smallbox.faraway.engine.ui;

import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;
import org.smallbox.faraway.Color;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.Renderer;
import org.smallbox.faraway.engine.util.StringUtils;
import org.smallbox.faraway.manager.SpriteManager;

public class TextView extends View {

	public static final int	REGULAR = Text.REGULAR;
	public static final int	BOLD = Text.BOLD;
	public static final int	ITALIC = Text.ITALIC;
	public static final int	UNDERLINED = Text.UNDERLINED;
	
	protected Text 			_text;
	protected String 			_value;

	public TextView() {
		super(0, 0);
		init();
	}

	public TextView(int width, int height) {
		super(width, height);
		init();
	}

	private void init() {
		_text = new Text();
		_text.setFont(SpriteManager.getInstance().getFont());
		_text.setColor(new org.jsfml.graphics.Color(Colors.TEXT.r, Colors.TEXT.g, Colors.TEXT.b));
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
		_text.setColor(new org.jsfml.graphics.Color(color.r, color.g, color.b));
	}

	public void setPosition(int x, int y) {
		super.setPosition(x, y);
		_text.setPosition(new Vector2f(x + _paddingLeft, y + _paddingTop));
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
	public void onDraw(Renderer renderer, RenderEffect effect) {
		renderer.draw(_text, effect);
	}

	public void setDashedString(String label, String value, int nbColumns) {
		setString(StringUtils.getDashedString(label, value, nbColumns));
	}

	public String getString() {
		return _value;
	}

}
