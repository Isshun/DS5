package org.smallbox.faraway.core.engine;

import com.badlogic.gdx.graphics.Color;

public class ColorUtils {

    public static Color fromHex(long rgba) {
        return new Color(
                ((rgba >> 24) & 0xFF) / 255f,
                ((rgba >> 16) & 0xFF) / 255f,
                ((rgba >> 8) & 0xFF) / 255f,
                (rgba & 0xFF) / 255f);
    }

    public static Color fromHex(int r, int g, int b) {
        return new Color(r / 255f, g / 255f, b / 255f, 1f);
    }

    public static Color fromHex(int r, int g, int b, int a) {
        return new Color(r / 255f, g / 255f, b / 255f, a / 255f);
    }
}
