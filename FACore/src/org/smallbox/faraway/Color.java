package org.smallbox.faraway;

/**
 * Created by Alex on 27/05/2015.
 */
public class Color {
    public static final Color CYAN = new Color(0, 255, 255);
    public static final Color GREEN = new Color(0, 255, 0);
    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color BLUE = new Color(0, 0, 255);
    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color RED = new Color(255, 0, 0);
    public static final Color YELLOW = new Color(255, 255, 0);

    public final int r;
    public final int g;
    public final int b;

    private int _rgb;

    public Color(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;

        _rgb = r;
        _rgb = (_rgb << 8) + g;
        _rgb = (_rgb << 8) + b;
        _rgb = (_rgb << 8) + 255;
    }

    public Color(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;

        _rgb = r;
        _rgb = (_rgb << 8) + g;
        _rgb = (_rgb << 8) + b;
        _rgb = (_rgb << 8) + a;
    }
}
