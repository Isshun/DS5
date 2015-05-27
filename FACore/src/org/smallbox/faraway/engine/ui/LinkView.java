package org.smallbox.faraway.engine.ui;

import org.smallbox.faraway.Color;

public class LinkView extends TextView {
	protected org.jsfml.graphics.Color _colorBak;
	
	public LinkView(int width, int height) {
		super(width, height);
		setOnFocusListener(null);
		setColor(Colors.LINK_INACTIVE);
	}

	public LinkView() {
		super(300, 20);
		setOnFocusListener(null);
		setColor(Colors.LINK_INACTIVE);
	}

	@Override
	public void setOnFocusListener(final OnFocusListener onFocusListener) {
		super.setOnFocusListener(new OnFocusListener() {
			@Override
			public void onExit(View view) {
				_text.setColor(_colorBak);
				setStyle(TextView.REGULAR);
				if (onFocusListener != null) {
					onFocusListener.onExit(view);
				}
			}
			@Override
			public void onEnter(View view) {
				_colorBak = _text.getColor();
				if (_onClickListener != null) {
					setStyle(TextView.UNDERLINED);
					setColor(Colors.LINK_ACTIVE);
				}
				if (onFocusListener != null) {
					onFocusListener.onEnter(view);
				}
			}
		});
	}

	@Override
	public void setColor(Color color) {
		if (_isFocus == false) {
			super.setColor(color);
		}
		_colorBak = new org.jsfml.graphics.Color(color.r, color.g, color.b);
	}
}
