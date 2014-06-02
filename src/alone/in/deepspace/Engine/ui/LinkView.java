package alone.in.deepspace.engine.ui;

import org.jsfml.graphics.Color;
import org.jsfml.system.Vector2f;


public class LinkView extends TextView {
	protected Color _colorBak;
	
	public LinkView(Vector2f size) {
		super(size);
		setOnFocusListener(null);
		setColor(Colors.LINK_INACTIVE);
	}

	public LinkView() {
		super(new Vector2f(300, 20));
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
					_text.setColor(Colors.LINK_ACTIVE);
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
			_text.setColor(color);
		}
		_colorBak = color;
	}
}
