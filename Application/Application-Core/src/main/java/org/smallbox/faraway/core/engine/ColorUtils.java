package org.smallbox.faraway.core.engine;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by Alex on 27/05/2015.
 */
public class ColorUtils {

    public static Color COLOR1 = fromHex(0x2ab8ba);
    public static Color COLOR2 = fromHex(0x9afbff);
    public static Color COLOR3 = fromHex(0x132733);

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
