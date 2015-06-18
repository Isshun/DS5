package org.smallbox.farpoint;

import org.smallbox.faraway.engine.RenderEffect;

/**
 * Created by Alex on 04/06/2015.
 */
public class GDXRenderEffect extends RenderEffect {

    private int _x;
    private int _y;

    @Override
    public void setTranslate(int x, int y) {
        _x = x;
        _y = y;
    }

    public float getPosX() {
        return _x;
    }

    public float getPosY() {
        return _y;
    }
}
