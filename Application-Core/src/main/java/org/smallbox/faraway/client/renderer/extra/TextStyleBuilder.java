package org.smallbox.faraway.client.renderer.extra;

import com.badlogic.gdx.graphics.Color;

public class TextStyleBuilder {
    private TextStyle textStyle;

    public static TextStyleBuilder build() {
        TextStyleBuilder textStyleBuilder = new TextStyleBuilder();
        textStyleBuilder.textStyle = new TextStyle();
        return textStyleBuilder;
    }

    public static TextStyleBuilder build(String font, int size, Color color) {
        TextStyleBuilder textStyleBuilder = new TextStyleBuilder();
        textStyleBuilder.textStyle = new TextStyle();
        textStyleBuilder.textStyle.font = font;
        textStyleBuilder.textStyle.size = size;
        textStyleBuilder.textStyle.color = color;
        return textStyleBuilder;
    }

    public TextStyleBuilder font(String font) {
        textStyle.font = font;
        return this;
    }

    public TextStyleBuilder size(int size) {
        textStyle.size = size;
        return this;
    }

    public TextStyleBuilder color(Color color) {
        textStyle.color = color;
        return this;
    }

    public TextStyleBuilder shadow(int shadow) {
        textStyle.shadow = shadow;
        return this;
    }

    public TextStyleBuilder autoScale(boolean autoScale) {
        textStyle.autoScale = autoScale;
        return this;
    }

    public TextStyle get() {
        return textStyle;
    }

}
