package org.smallbox.faraway.ui.engine;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.SpriteModel;
import org.smallbox.faraway.game.model.GameData;

public abstract class TextView extends View {

	public static final int	REGULAR = 0;
	public static final int	BOLD = 1;
	public static final int	ITALIC = 2;
	public static final int	UNDERLINED = 3;
	protected Color _colorBak;

	public TextView() {
		super(0, 0);
	}

	public TextView(int width, int height) {
		super(width, height);
	}

	public void setString(String string) {
        setStringValue(GameData.getData() != null ? GameData.getData().getString(string) : string);
	}

    public void setString(String string, int value) {
        string = GameData.getData().getString(string);
        string = String.format(string, value);
        setStringValue(string);
    }

    public void setString(String string, String value) {
        string = GameData.getData().getString(string);
        string = String.format(string, value);
        setStringValue(string);
    }

	public abstract void setStringValue(String string);
	public abstract void setCharacterSize(int size);
	public abstract void setStyle(int style);
	public abstract void setColor(Color color);
	public abstract Color getColor();
	public abstract void setDashedString(String label, String value, int nbColumns);
	public abstract String getString();

	@Override
	public void setOnFocusListener(final OnFocusListener onFocusListener) {
		super.setOnFocusListener(new OnFocusListener() {
			@Override
			public void onExit(View view) {
				setColor(_colorBak);
				setStyle(TextView.REGULAR);
				if (onFocusListener != null) {
					onFocusListener.onExit(view);
				}
			}
			@Override
			public void onEnter(View view) {
				_colorBak = getColor();
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

	public void setShortcut(int i) {
		//TODO
	}

	public void setIcon(SpriteModel sprite) {
		//TODO
	}

    public void setTextPadding(int i, int i1) {
        //TODO
    }

    public void setIconPadding(int i, int i1) {
        //TODO
    }

	@Override
	public void init() {
		if (_background != null) {
			_background.setPosition(_x, _y);
		}
	}

	@Override
	public void resetPos() {
//		_width = getContentWidth();
//		_height = getContentHeight();
		super.resetPos();
	}

	public void resetSize() {
		_width = getContentWidth();
		_height = getContentHeight();
	}

	public View findById(String string) {
		return null;
	}
}
