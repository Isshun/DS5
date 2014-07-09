package alone.in.deepspace.engine.ui;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.manager.SpriteManager;

public class ButtonView extends View {

	private FakeText 			_text;
	private FakeText 			_textShortcut;
	private FakeRectangleShape	_rectShortcut;
	protected int 			_textPaddingLeft;
	protected int 			_textPaddingTop;
	private Sprite 			_icon;
	protected int 			_iconPaddingLeft;
	protected int 			_iconPaddingTop;
	private RectangleShape 	_overlay;
	private String 			_string;
	private int 			_shortcutPos;
	private int 			_textSize;

	public ButtonView(Vector2f size) {
		super(size);

		_text = new FakeText();
		_text.setFont(SpriteManager.getInstance().getFont());

		setBackgroundColor(Colors.BT_INACTIVE);
		setColor(Colors.BT_TEXT);
		setOnFocusListener(null);
	}

	public void setString(String string) {
		_string = string;
		_text.setString(string);
	}

	public void setCharacterSize(int size) {
		_textSize = size;
		_text.setCharacterSize(size);
	}

	public void setColor(Color color) {
		_text.setColor(color);
	}

	@Override
	protected void refresh() {
		super.refresh();
		
		if (_textShortcut != null) {
			_textShortcut.setPosition(_pos.x + _paddingLeft + _textPaddingLeft + ((_shortcutPos + 1) * 12), _pos.y + _paddingTop + _textPaddingTop);
			_textShortcut.setCharacterSize(_textSize);
		}
		
		if (_rectShortcut != null) {
			_rectShortcut.setPosition(_pos.x + _paddingLeft + _textPaddingLeft + ((_shortcutPos + 1) * 12), _pos.y + _paddingTop + _textPaddingTop + 24);
		}
	}

	@Override
	public void setOnFocusListener(final OnFocusListener onFocusListener) {
		super.setOnFocusListener(new OnFocusListener() {
			private Color _color;
			
			@Override
			public void onEnter(View view) {
				_color = _backgroundColor;
				//view.setBackgroundColor(new Color(29, 85, 96, 180));
				view.setBackgroundColor(Colors.BT_ACTIVE);
				if (onFocusListener != null) {
					onFocusListener.onEnter(view);
				}
			}

			@Override
			public void onExit(View view) {
				view.setBackgroundColor(_color);
				if (onFocusListener != null) {
					onFocusListener.onExit(view);
				}
			}
		});
	}
	public void setPosition(Vector2f pos) {
		super.setPosition(pos);
		if (_icon != null) {
			_icon.setPosition(pos);
		}
		if (_overlay != null) {
			_overlay.setPosition(pos);
		}
		_text.setPosition(new Vector2f(_pos.x + _paddingLeft + _textPaddingLeft, _pos.y + _paddingTop + _textPaddingTop));
	}

	@Override
	public void setPadding(int t, int r, int b, int l) {
		super.setPadding(t, r, b, l);
		if (_pos != null) {
			_text.setPosition(new Vector2f(_pos.x + _paddingLeft, _pos.y + _paddingTop));
		}
		_invalid = true;
	}

	@Override
	public void onDraw(RenderWindow app, RenderStates render) {
//		if (_background != null) {
//			app.draw(_background, render);
//		}

		if (_icon != null) {
			_icon.setPosition(_pos.x + _paddingLeft + _iconPaddingLeft, _pos.y + _paddingTop + _iconPaddingTop);
			app.draw(_icon, render);
		}

		if (_text != null) {
			app.draw(_text, render);
		}
		
		if (_textShortcut != null) {
			app.draw(_textShortcut, render);
		}
		
		if (_rectShortcut != null) {
			app.draw(_rectShortcut, render);
		}
		
		if (_overlay != null) {
			app.draw(_overlay, render);
		}
	}

	public void setPosition(int i, int j) {
		setPosition(new Vector2f(i, j));
	}

	public void setIcon(Sprite icon) {
		_icon = icon;
	}

	public void setIconPadding(int x, int y) {
		_iconPaddingLeft = x;
		_iconPaddingTop = y;
		_invalid = true;
	}

	public void setOverlayColor(Color color) {
		if (color == null) {
			_overlay = null;
		}
		
		_overlay = new RectangleShape(_size);
		_overlay.setFillColor(color);
		if (_overlay != null && _pos != null) {
			_overlay.setPosition(_pos);
		}
	}

	public void setTextPadding(int top, int left) {
		_textPaddingTop = top;
		_textPaddingLeft = left;
		_invalid = true;
	}

	public void setShortcut(int index) {
		char shortcut = _string.charAt(index);
		_string = "[" + _string.substring(0, index) + " " + _string.substring(index + 1) + "]";
		_shortcutPos = index * 12;
		_text.setString(_string);
		
		_textShortcut = new FakeText();
		_textShortcut.setFont(SpriteManager.getInstance().getFont());
		_textShortcut.setString(String.valueOf(shortcut));
		_textShortcut.setColor(Colors.LINK_ACTIVE);
		_rectShortcut = new FakeRectangleShape(new Vector2f(12, 1));
		_rectShortcut.setFillColor(Colors.LINK_ACTIVE);
		
		_invalid = true;
	}
}

