package org.smallbox.faraway.client.ui.engine.views;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.core.engine.ColorUtils;

public class ViewStyle {
    private final View view;
    protected Color _backgroundFocusColor;
    protected Color _backgroundColor;
    protected Color _borderColor;

    public ViewStyle(View view) {
        this.view = view;
    }

    public void setBorderColor(long color) {
        _borderColor = color == 0 ? null : ColorUtils.fromHex(color);
    }

    public void setBorderColor(Color color) {
        _borderColor = color;
    }

    public View setBackgroundColor(long color) {
        _backgroundColor = ColorUtils.fromHex(color);
        return view;
    }

    public View setBackgroundColor(Color color) {
        _backgroundColor = color;
        return view;
    }

    public Color getBackgroundColor() {
        return _backgroundColor;
    }

    public void setBackgroundFocusColor(long color) {
        _backgroundFocusColor = ColorUtils.fromHex(color);
    }

    public View setBackgroundFocusColor(Color color) {
        _backgroundFocusColor = color;
        return view;
    }

}
