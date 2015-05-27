package org.smallbox.faraway.engine.ui;

import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import org.smallbox.faraway.Color;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.Renderer;
import org.smallbox.faraway.SpriteModel;
import org.smallbox.faraway.engine.Viewport;
import org.smallbox.faraway.manager.SpriteManager;

public class ButtonView extends View {

	private Text 			_text;
	private Text 			_textShortcut;
	private RectangleShape	_rectShortcut;
	protected int 			_textPaddingLeft;
	protected int 			_textPaddingTop;
	private SpriteModel 	_icon;
	protected int 			_iconPaddingLeft;
	protected int 			_iconPaddingTop;
	private RectangleShape 	_overlay;
	private String 			_string;
	private int 			_shortcutPos;
	private int 			_textSize;

	public ButtonView(int width, int height) {
		super(width, height);

		_text = new Text();
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
		_text.setColor(new org.jsfml.graphics.Color(color.r, color.g, color.b));
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

	public void setPosition(int x, int y) {
		super.setPosition(x, y);
		if (_icon != null) {
			_icon.setPosition(x, y);
		}
		if (_overlay != null) {
			_overlay.setPosition(x, y);
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
	public void onDraw(Renderer renderer, RenderEffect effect) {
//		if (_background != null) {
//			app.draw(_background, _renderEffect);
//		}

		if (_icon != null) {
			_icon.setPosition((int)(_pos.x + _paddingLeft + _iconPaddingLeft), (int)(_pos.y + _paddingTop + _iconPaddingTop));
			renderer.draw(_icon, effect);
		}

		if (_text != null) {
			renderer.draw(_text, effect);
		}
		
		if (_textShortcut != null) {
			renderer.draw(_textShortcut, effect);
		}
		
		if (_rectShortcut != null) {
			renderer.draw(_rectShortcut, effect);
		}
		
		if (_overlay != null) {
			renderer.draw(_overlay, effect);
		}
	}

	public void setIcon(SpriteModel icon) {
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
		_overlay.setFillColor(new org.jsfml.graphics.Color(color.r, color.g, color.b));
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
		
		_textShortcut = new Text();
		_textShortcut.setFont(SpriteManager.getInstance().getFont());
		_textShortcut.setString(String.valueOf(shortcut));
		_textShortcut.setColor(new org.jsfml.graphics.Color(Colors.LINK_ACTIVE.r, Colors.LINK_ACTIVE.g, Colors.LINK_ACTIVE.b));
		_rectShortcut = new RectangleShape(new Vector2f(12, 1));
		_rectShortcut.setFillColor(new org.jsfml.graphics.Color(Colors.LINK_ACTIVE.r, Colors.LINK_ACTIVE.g, Colors.LINK_ACTIVE.b));
		
		_invalid = true;
	}
}

