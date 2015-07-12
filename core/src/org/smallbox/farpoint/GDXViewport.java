package org.smallbox.farpoint;

import org.smallbox.faraway.engine.RenderEffect;
import org.smallbox.faraway.engine.Viewport;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.Log;

/**
 * Created by Alex on 04/06/2015.
 */
public class GDXViewport extends Viewport {
    private static final int    ANIM_FRAME = 10;
    public static float[]       ZOOM_LEVELS = new float[] {
            0.1f,
            0.25f,
            0.5f,
            1f
    };

    private int 		_posX;
    private int 		_posY;
    private int 		_lastPosX;
    private int 		_lastPosY;
    private int 		_width;
    private int 		_toScale;
    private int 		_height;
    private int 		_fromScale;
    private int 		_scaleAnim;
    private int         _zoom = ZOOM_LEVELS.length - 1;

    public GDXViewport(int x, int y) {
        _posX = x;
        _posY = y;
        _lastPosX = 0;
        _lastPosY = 0;
        _width = Constant.WINDOW_WIDTH - Constant.PANEL_WIDTH;
        _height = Constant.WINDOW_HEIGHT;
    }

    @Override
    public void update(int x, int y) {
        x *= (1 + (1 - ZOOM_LEVELS[_zoom])) * 4;
        y *= (1 + (1 - ZOOM_LEVELS[_zoom])) * 4;
        if (x != 0 || y != 0) {
            Log.info("drag: " + (_lastPosX - x) + "x" + (_lastPosY - y));

            _posX -= (_lastPosX - x);
            _posY -= (_lastPosY - y);
            _lastPosX = x;
            _lastPosY = y;
        }
    }

    @Override
    public void moveTo(int x, int y) {
        _posX = (-x * Constant.TILE_WIDTH) + (50/2 * Constant.TILE_WIDTH);
        _posY = (-y * Constant.TILE_HEIGHT) + (40/2 * Constant.TILE_HEIGHT);
    }

    @Override
    public void setPosition(int x, int y) {
        _posX = x;
        _posY = y;
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
    public void setZoom(int zoom) {
        _posX += ((ZOOM_LEVELS[_zoom] * 1500) - (ZOOM_LEVELS[zoom] * 1500));
        _posY += ((ZOOM_LEVELS[_zoom] * 1200) - (ZOOM_LEVELS[zoom] * 1200));
        _zoom = zoom;
    }

    @Override
    public RenderEffect getRenderEffect() {
        return null;
    }

    @Override
    public float getScale() {
        return ZOOM_LEVELS[_zoom];
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
        _lastPosX = (int) ((x * (1 + (1 - ZOOM_LEVELS[_zoom]))) * 4);
        _lastPosY = (int) ((y * (1 + (1 - ZOOM_LEVELS[_zoom]))) * 4);
    }

    @Override
    public void move(int x, int y) {
        _posX += (int) ((x * (1 + (1 - ZOOM_LEVELS[_zoom]))) * 1);
        _posY += (int) ((y * (1 + (1 - ZOOM_LEVELS[_zoom]))) * 1);
    }

    public void getRender() {

    }
}
