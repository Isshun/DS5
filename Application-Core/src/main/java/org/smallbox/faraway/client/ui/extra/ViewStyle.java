package org.smallbox.faraway.client.ui.extra;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.ui.widgets.View;

public class ViewStyle {
    private final View view;
    public Color _backgroundFocusColor;
    public Color _backgroundColor;
    public Color _borderColor;

    public ViewStyle(View view) {
        this.view = view;
    }

    public void setBorderColor(int color) {
        _borderColor = color == 0 ? null : new Color(color);
    }

    public void setBorderColor(Color color) {
        _borderColor = color;
    }

    public void setBackgroundColor(int color) {
        _backgroundColor = new Color(color);
    }

    public void setBackgroundColor(Color color) {
        _backgroundColor = color;
    }

    public Color getBackgroundColor() {
        return _backgroundColor;
    }

    public void setBackgroundFocusColor(int color) {
        _backgroundFocusColor = new Color(color);
    }

    public View setBackgroundFocusColor(Color color) {
        _backgroundFocusColor = color;
        return view;
    }

}
