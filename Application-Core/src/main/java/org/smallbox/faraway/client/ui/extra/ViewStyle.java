package org.smallbox.faraway.client.ui.extra;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.ui.widgets.View;

public class ViewStyle {
    private final View view;
    public Color backgroundFocusColor;
    public Color backgroundColor;
    public Color borderColor;
    public int borderSize;

    public ViewStyle(View view) {
        this.view = view;
    }

    public void setBorderColor(int color) {
        borderColor = color == 0 ? null : new Color(color);
    }

    public int getBorderSize() {
        return borderSize;
    }

    public void setBorderSize(int borderSize) {
        this.borderSize = borderSize;
    }

    public void setBorderColor(Color color) {
        borderColor = color;
    }

    public void setBackgroundColor(int color) {
        backgroundColor = new Color(color);
    }

    public void setBackgroundColor(Color color) {
        backgroundColor = color;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundFocusColor(int color) {
        backgroundFocusColor = new Color(color);
    }

    public View setBackgroundFocusColor(Color color) {
        backgroundFocusColor = color;
        return view;
    }

    public Color getBackgroundFocusColor() {
        return backgroundFocusColor;
    }
}
