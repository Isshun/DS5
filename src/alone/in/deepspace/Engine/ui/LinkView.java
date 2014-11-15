package alone.in.deepspace.engine.ui;

import org.jsfml.graphics.Color;
import org.jsfml.system.Vector2f;


public class LinkView extends TextView {
	protected Color _colorBak;
	
	public LinkView(Vector2f size) {
		super(size);
		setOnFocusListener(null);
		setColor(Colors.LINK_INACTIVE, true);
	}

	public LinkView() {
		super(new Vector2f(300, 20));
		setOnFocusListener(null);
		setColor(Colors.LINK_INACTIVE, true);
	}

	@Override
	public void setOnFocusListener(final OnFocusListener onFocusListener) {
		super.setOnFocusListener(new OnFocusListener() {
			@Override
			public void onExit(View view) {
				setColor(_colorBak, false);
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
					setColor(Colors.LINK_ACTIVE, false);
				}
				if (onFocusListener != null) {
					onFocusListener.onEnter(view);
				}
			}
		});
	}

	public void setColor(Color color, boolean replace) {
		if (_isFocus == false) {
			setColor(color);
		}
		if (replace) {
			_colorBak = color;
		}
	}
}
