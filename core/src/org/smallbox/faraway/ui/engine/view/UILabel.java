package org.smallbox.faraway.ui.engine.view;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.game.model.GameData;

import java.awt.*;

public abstract class UILabel extends View {
	public static final int	REGULAR = 0;
	public static final int	BOLD = 1;
	public static final int	ITALIC = 2;
	public static final int	UNDERLINED = 3;
	private int _hash;

	public UILabel() {
        super(-1, -1);
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

	public abstract void setStringValue(String string);
	public abstract void setTextSize(int size);
	public abstract void setStyle(int style);
	public abstract void setTextColor(Color color);
	public abstract void setTextColor(int color);
	public abstract Color getColor();
	public abstract void setDashedString(String label, String value, int nbColumns);
	public abstract String getString();

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
}
