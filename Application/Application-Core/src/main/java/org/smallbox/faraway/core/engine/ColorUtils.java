package org.smallbox.faraway.core.engine;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by Alex on 27/05/2015.
 */
public class ColorUtils {
    public static final Color CYAN = ColorUtils.fromHex(0, 255, 255);
    public static final Color GREEN = ColorUtils.fromHex(0, 255, 0);
    public static final Color BLACK = ColorUtils.fromHex(0, 0, 0);
    public static final Color BLUE = ColorUtils.fromHex(0, 0, 255);
    public static final Color WHITE = ColorUtils.fromHex(255, 255, 255);
    public static final Color RED = ColorUtils.fromHex(255, 0, 0);
    public static final Color YELLOW = ColorUtils.fromHex(255, 255, 0);

    public static Color fromHex(long rgb) {
        int a = rgb > 0xffffff || rgb < 0 ? (int) ((rgb >> 24) & 0xFF) : 255;
        int r = (int) ((rgb >> 16) & 0xFF);
        int g = (int) ((rgb >> 8) & 0xFF);
        int b = (int) (rgb & 0xFF);
        return new Color(r / 255f, g / 255f, b / 255f, a / 255f);
    }

    public static Color fromHex(int r, int g, int b) {
        return new Color(r / 255f, g / 255f, b / 255f, 1f);
    }

    public static Color fromHex(int r, int g, int b, int a) {
        return new Color(r / 255f, g / 255f, b / 255f, a / 255f);
    }
}
