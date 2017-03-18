package org.smallbox.faraway.client.renderer;

import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.Log;

/**
 * Created by Alex on 04/06/2015.
 */
public class Viewport {
    private final static int    ANIM_FRAME = 10;
    final static float[]       ZOOM_LEVELS = new float[] {
            0.1f,
            0.25f,
            0.5f,
            1f
    };

    private int         _posX;
    private int         _posY;
    private int         _lastPosX;
    private int         _lastPosY;
    private int         _width;
    private int         _toScale;
    private int         _height;
    private int         _fromScale;
    private int         _scaleAnim;
    private int         _zoom = ZOOM_LEVELS.length - 1;
    private int         _floor;
    private int         _worldX;
    private int         _worldY;

    public Viewport(int x, int y) {
        setPosition(x, y);
        _lastPosX = 0;
        _lastPosY = 0;
        _width = Constant.WINDOW_WIDTH - Constant.PANEL_WIDTH;
        _height = Constant.WINDOW_HEIGHT;
    }

    public void update(int x, int y) {
        x *= (1 + (1 - ZOOM_LEVELS[_zoom])) * 4;
        y *= (1 + (1 - ZOOM_LEVELS[_zoom])) * 4;

        if (x != 0 || y != 0) {
            setPosition(_posX - (_lastPosX - x), _posY - (_lastPosY - y));
            _lastPosX = x;
            _lastPosY = y;
        }
    }

    public void moveTo(int x, int y) {
        setPosition(
                (-x * Constant.TILE_WIDTH) + (50/2 * Constant.TILE_WIDTH),
                (-y * Constant.TILE_HEIGHT) + (40/2 * Constant.TILE_HEIGHT));
    }

    public void setPosition(int x, int y) {
        Log.debug("set position to: " + x + "x" + y);

        _posX = x;
        _posY = y;

        _worldX = (int) Math.max(0, (-_posX / Constant.TILE_WIDTH) * getScale());
        _worldY = (int) Math.max(0, (-_posY / Constant.TILE_HEIGHT) * getScale());
    }

    public int  getPosX() { return _posX; }
    public int  getPosY() { return _posY; }
    public int  getWidth() { return _width; }
    public int  getHeight() { return _height; }

    public int  getWorldPosX() { return getWorldPosX(0); }
    public int  getWorldPosX(int x) { return (int) ((-getPosX() + x) / getScale() / Constant.TILE_WIDTH); }
    public int  getWorldPosY() { return getWorldPosY(0); }
    public int  getWorldPosY(int y) { return (int) ((-getPosY() + y) / getScale() / Constant.TILE_HEIGHT); }

    public int  getScreenPosX(int parcelX) { return parcelX * Constant.TILE_WIDTH + getPosX(); }
    public int  getScreenPosY(int parcelY) { return parcelY * Constant.TILE_HEIGHT + getPosY(); }

    public void setZoom(int zoom) {
        setPosition(
                (int)(_posX + ((ZOOM_LEVELS[_zoom] * 1500) - (ZOOM_LEVELS[zoom] * 1500))),
                (int)(_posY + ((ZOOM_LEVELS[_zoom] * 1200) - (ZOOM_LEVELS[zoom] * 1200))));
        _zoom = zoom;
    }

    public float getScale() {
        return ZOOM_LEVELS[_zoom];
    }

    public void startMove(int x, int y) {
        _lastPosX = (int) ((x * (1 + (1 - ZOOM_LEVELS[_zoom]))) * 4);
        _lastPosY = (int) ((y * (1 + (1 - ZOOM_LEVELS[_zoom]))) * 4);
    }

    public void move(int x, int y) {
        _posX += (int) ((x * (1 + (1 - ZOOM_LEVELS[_zoom]))) * 1);
        _posY += (int) ((y * (1 + (1 - ZOOM_LEVELS[_zoom]))) * 1);
    }

    public int getFloor() {
        return _floor;
    }

    public void setFloor(int floor) {
        if (Application.gameManager.isRunning()) {
            if (floor >= 0 && floor < Application.gameManager.getGame().getInfo().worldFloors) {
                _floor = floor;
                ApplicationClient.notify(gameObserver -> gameObserver.onFloorChange(_floor));
            }
        }
    }

    public void setPosition(int x, int y, int z) {
        setPosition(x, y);
        _floor = z;
    }

    public boolean hasParcel(ParcelModel parcel) {
        return parcel != null
                && parcel.z == _floor
                && parcel.x >= _worldX && parcel.x <= _worldX + 50
                && parcel.y >= _worldY && parcel.y <= _worldY + 50;
    }
}
