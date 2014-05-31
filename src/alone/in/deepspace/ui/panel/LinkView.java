package alone.in.deepspace.ui.panel;

import org.jsfml.graphics.Color;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.engine.ui.OnFocusListener;
import alone.in.deepspace.engine.ui.TextView;
import alone.in.deepspace.engine.ui.View;

public class LinkView extends TextView {
	private static final Color COLOR_ACTIVE = new Color(176, 205, 53);

	public LinkView(Vector2f size) {
		super(size);
		setOnFocusListener(null);
	}

	@Override
	public void setOnFocusListener(final OnFocusListener onFocusListener) {
		super.setOnFocusListener(new OnFocusListener() {
			private Color _color;
			
			@Override
			public void onExit(View view) {
				setColor(_color);
				setStyle(TextView.REGULAR);
				if (onFocusListener != null) {
					onFocusListener.onExit(view);
				}
			}
			@Override
			public void onEnter(View view) {
				_color = _text.getColor();
				setStyle(TextView.UNDERLINED);
				setColor(COLOR_ACTIVE);
				if (onFocusListener != null) {
					onFocusListener.onExit(view);
				}
			}
		});
	}

}
