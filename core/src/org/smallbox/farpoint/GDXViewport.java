package org.smallbox.farpoint;

import org.smallbox.faraway.engine.RenderEffect;
import org.smallbox.faraway.engine.Viewport;
import org.smallbox.faraway.util.Constant;

/**
 * Created by Alex on 04/06/2015.
 */
public class GDXViewport extends Viewport {
    private static final int ANIM_FRAME = 10;

    private int 		_posX;
    private int 		_posY;
    private int 		_lastPosX;
    private int 		_lastPosY;
    private int 		_width;
    private int 		_toScale;
    private int 		_height;
    private int 		_fromScale;
    private int 		_scaleAnim;

    public GDXViewport(int x, int y) {
        _posX = x;
        _posY = y;
        _lastPosX = 0;
        _lastPosY = 0;
        _width = Constant.WINDOW_WIDTH - Constant.PANEL_WIDTH;
        _height = Constant.WINDOW_HEIGHT;
        _toScale = 0;
    }

    @Override
    public void update(int x, int y) {
        if (x != 0 || y != 0) {
            _posX -= (_lastPosX - x);
            _posY -= (_lastPosY - y);
            _lastPosX = x;
            _lastPosY = y;
        }
    }

    @Override
    public void moveTo(int x, int y) {
        _posX = (x + 15) * Constant.TILE_WIDTH;
        _posY = (y + 10) * Constant.TILE_HEIGHT;
    }

    @Override
    public int   getPosX() { return _posX; }

    @Override
    public int   getPosY() { return _posY; }

    public int   getWidth() { return _width; }
    public int   getHeight() { return _height; }

    @Override
    public void setScale(int delta, int x, int y) {
    }

    @Override
    public RenderEffect getRenderEffect() {
        return null;
    }

    @Override
    public float getScale() {
        return 1;
    }

    @Override
    public float getMinScale() {
        return 1;
    }

    @Override
    public float getMaxScale() {
        return 1;
    }

    @Override
    public void startMove(int x, int y) {
        _lastPosX = x;
        _lastPosY = y;
    }

    public void getRender() {

    }
}
