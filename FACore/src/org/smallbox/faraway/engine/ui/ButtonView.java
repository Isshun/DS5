//package org.smallbox.faraway.engine.ui;
//
//import org.smallbox.faraway.Color;
//import org.smallbox.faraway.GFXRenderer;
//import org.smallbox.faraway.RenderEffect;
//import org.smallbox.faraway.SpriteModel;
//import org.smallbox.faraway.renderer.SpriteManager;
//
//public abstract class ButtonView extends View {
//
//	protected TextView 		_text;
//	protected TextView 		_textShortcut;
//	protected ColorView		_rectShortcut;
//	protected int 			_textPaddingLeft;
//	protected int 			_textPaddingTop;
//	protected SpriteModel 	_icon;
//	protected int 			_iconPaddingLeft;
//	protected int 			_iconPaddingTop;
//	protected ColorView		_overlay;
//	protected String 		_string;
//	protected int 			_shortcutPos;
//	protected int 			_textSize;
//
//	public ButtonView(int width, int height) {
//		super(width, height);
//
//		_text = SpriteManager.getInstance().createTextView();
//
//		setBackgroundColor(Colors.BT_INACTIVE);
//		setColor(Colors.BT_TEXT);
//		setOnFocusListener(null);
//	}
//
//	public abstract void refresh();
//
//	public void setString(String string) {
//		_string = string;
//		_text.setString(string);
//	}
//
//	public void setCharacterSize(int size) {
//		_textSize = size;
//		_text.setCharacterSize(size);
//	}
//
//	public void setColor(Color color) {
//		_text.setColor(color);
//	}
//
//	@Override
//	public void setOnFocusListener(final OnFocusListener onFocusListener) {
//		super.setOnFocusListener(new OnFocusListener() {
//			private Color _color;
//
//			@Override
//			public void onEnter(View view) {
//				//_color = view.getBa;
//				//view.setBackgroundColor(new Color(29, 85, 96, 180));
//				view.setBackgroundColor(Colors.BT_ACTIVE);
//				if (onFocusListener != null) {
//					onFocusListener.onEnter(view);
//				}
//			}
//
//			@Override
//			public void onExit(View view) {
//                // TODO
//				//view.setBackgroundColor(_color);
//				if (onFocusListener != null) {
//					onFocusListener.onExit(view);
//				}
//			}
//		});
//	}
//
//	public void setPosition(int x, int y) {
//		super.setPosition(x, y);
//		if (_icon != null) {
//			_icon.setPosition(x, y);
//		}
//		if (_overlay != null) {
//			_overlay.setPosition(x, y);
//		}
//		_text.setPosition(_x + _paddingLeft + _textPaddingLeft, _y + _paddingTop + _textPaddingTop);
//	}
//
//	@Override
//	public void setPadding(int t, int r, int b, int l) {
//		super.setPadding(t, r, b, l);
//        _text.setPosition(_x + _paddingLeft, _y + _paddingTop);
//		_invalid = true;
//	}
//
//	@Override
//	public void onDraw(GFXRenderer renderer, RenderEffect effect) {
////		if (_background != null) {
////			app.draw(_background, _renderEffect);
////		}
//
//		if (_icon != null) {
//			_icon.setPosition(_x + _paddingLeft + _iconPaddingLeft, _y + _paddingTop + _iconPaddingTop);
//			renderer.draw(_icon, effect);
//		}
//
//		if (_text != null) {
//			renderer.draw(_text, effect);
//		}
//
//		if (_textShortcut != null) {
//			renderer.draw(_textShortcut, effect);
//		}
//
//		if (_rectShortcut != null) {
//			renderer.draw(_rectShortcut, effect);
//		}
//
//		if (_overlay != null) {
//			renderer.draw(_overlay, effect);
//		}
//	}
//
//	public void setIcon(SpriteModel icon) {
//		_icon = icon;
//	}
//
//	public void setIconPadding(int x, int y) {
//		_iconPaddingLeft = x;
//		_iconPaddingTop = y;
//		_invalid = true;
//	}
//
//	public void setOverlayColor(Color color) {
//		if (color == null) {
//			_overlay = null;
//		}
//
//		_overlay = new ColorView(_width, _height);
//		_overlay.setBackgroundColor(color);
//		if (_overlay != null) {
//			_overlay.setPosition(_x, _y);
//		}
//	}
//
//	public void setTextPadding(int top, int left) {
//		_textPaddingTop = top;
//		_textPaddingLeft = left;
//		_invalid = true;
//	}
//
//	public void setShortcut(int index) {
//		char shortcut = _string.charAt(index);
//		_string = "[" + _string.substring(0, index) + " " + _string.substring(index + 1) + "]";
//		_shortcutPos = index * 12;
//		_text.setString(_string);
//
//		_textShortcut = SpriteManager.getInstance().createTextView();
//		_textShortcut.setString(String.valueOf(shortcut));
//		_textShortcut.setColor(Colors.LINK_ACTIVE);
//		_rectShortcut = new ColorView(12, 1);
//		_rectShortcut.setBackgroundColor(Colors.LINK_ACTIVE);
//
//		_invalid = true;
//	}
//}
//
