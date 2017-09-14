package org.smallbox.faraway.common;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by Alex on 27/05/2015.
 */
public class ColorUtils {

    public static Color COLOR1 = fromHex(0x2ab8baff);
    public static Color COLOR2 = fromHex(0x9afbffff);
    public static Color COLOR3 = fromHex(0x132733ff);

    public static Color fromHex(long rgb) {
        return new Color(
                ((rgb >> 24) & 0xFF) / 255f,
                ((rgb >> 16) & 0xFF) / 255f,
                ((rgb >> 8) & 0xFF) / 255f,
                (rgb & 0xFF) / 255f);
    }

    public static Color fromHex(int r, int g, int b) {
        return new Color(r / 255f, g / 255f, b / 255f, 1f);
    }

    public static Color fromHex(int r, int g, int b, int a) {
        return new Color(r / 255f, g / 255f, b / 255f, a / 255f);
    }
}
