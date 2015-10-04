package org.smallbox.faraway.ui.engine.view;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.renderer.GDXRenderer;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.util.StringUtils;

import java.awt.*;

public class UILabel extends View {
	public static final int	REGULAR = 0;
	public static final int	BOLD = 1;
	public static final int	ITALIC = 2;
	public static final int	UNDERLINED = 3;
	private int _hash;

    private String                              _string;
    private int                                 _textSize;
    private com.badlogic.gdx.graphics.Color     _gdxTextColor;
    private Color                               _textColor;

	public UILabel() {
        super(-1, -1);
	}

    @Override
    public void addView(View view) {
    }

    public UILabel(int width, int height) {
		super(width, height);
	}

	public void setText(String string) {
        if (string == null) {
            setStringValue("");
            return;
        }

        int hash = string.hashCode();
        if (hash != _hash) {
            setStringValue(GameData.getData() != null && GameData.getData().hasString(hash) ? GameData.getData().getString(hash) : string);
        }
	}

    public void setText(String string, int value) {
        if (string == null) {
            setStringValue("");
            return;
        }

        // TODO
        int hash = string.hashCode();
//        if (hash != _hash) {
            string = GameData.getData() != null && GameData.getData().hasString(hash) ? GameData.getData().getString(hash) : string;
        setStringValue(String.format(string, value));
//        }
    }

    public void setText(String string, String value) {
        if (string == null) {
            setStringValue("");
            return;
        }

        // TODO
        int hash = string.hashCode();
//        if (hash != _hash) {
            string = GameData.getData() != null && GameData.getData().hasString(hash) ? GameData.getData().getString(hash) : string;
            setStringValue(String.format(string, value));
//        }
    }

    public void setTextColor(Color color) {
        if (color != null && _textColor != color) {
            _gdxTextColor = new com.badlogic.gdx.graphics.Color(color.r / 255f, color.g / 255f, color.b / 255f, color.a / 255f);
        }
        _textColor = color;
    }

    public void setTextColor(int color) {
        _textColor = new Color(color);
        _gdxTextColor = new com.badlogic.gdx.graphics.Color(_textColor.r / 255f, _textColor.g / 255f, _textColor.b / 255f, _textColor.a / 255f);
    }

    public void setStringValue(String string) {
        _string = string;
    }

    public void setTextSize(int size) {
        _textSize = size;
    }

    public void setStyle(int style) {
    }


    public Color getColor() {
        return _textColor;
    }

    public void setDashedString(String label, String value, int nbColumns) {
        _string = StringUtils.getDashedString(label, value, nbColumns);
    }

    public String getString() {
        return _string;
    }

	public void setShortcut(int i) {
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
	}

	public View findById(String string) {
		return null;
	}

    @Override
    public void draw(GDXRenderer renderer, int x, int y) {
        super.draw(renderer, x, y);

        if (_isVisible) {
            if (true) {
                _finalX = x;
                _finalY = y;
                View view = this;
                while (view != null) {
                    _finalX += view.getPosX();
                    _finalY += view.getPosY();
                    view = view.getParent();
                }

                if (_align == Align.CENTER) {
                    _offsetX = (_width - getContentWidth()) / 2;
                    _offsetY = (_height - getContentHeight()) / 2;
                }

                if (_align == Align.CENTER_VERTICAL) {
                    _offsetY = (_height - getContentHeight()) / 2;
                }
            }

//        ((GDXRenderer) renderer).draw(com.badlogic.gdx.graphics.Color.RED, _finalX, _finalY, _width, _height);
            ((GDXRenderer) renderer).draw(_string, _textSize, _finalX + _offsetX + _paddingLeft, _finalY + _offsetY + _paddingTop, _gdxTextColor);
        }
    }

    @Override
    public void refresh() {

    }

    @Override
    public int getContentWidth() {
        if (_string != null) {
            return (int) GDXRenderer.getInstance().getFont(_textSize).getBounds(_string).width;
        }
        return 0;
    }

    @Override
    public int getContentHeight() {
        if (_string != null) {
            return (int) GDXRenderer.getInstance().getFont(_textSize).getBounds(_string).height;
        }
        return 0;
    }

}
