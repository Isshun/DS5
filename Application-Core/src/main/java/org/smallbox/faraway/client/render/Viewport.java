package org.smallbox.faraway.client.render;

import org.smallbox.faraway.common.ParcelCommon;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfig;
import org.smallbox.faraway.core.module.world.model.Parcel;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.log.Log;

@GameObject
public class Viewport {

    @Inject
    private ApplicationConfig applicationConfig;

    @Inject
    private Game game;

    @Inject
    private GameManager gameManager;

    @Inject
    private GDXRenderer gdxRenderer;

    private final static int ANIM_FRAME = 10;
    public final static float[] ZOOM_LEVELS = new float[]{
            0.1f,
            0.25f,
            0.5f,
            1f
    };

    private int _posX;
    private int _posY;
    private int _lastPosX;
    private int _lastPosY;
    private int _width;
    private int _toScale;
    private int _height;
    private int _fromScale;
    private int _scaleAnim;
    private final int _zoom = ZOOM_LEVELS.length - 1;
    private int _floor;
    private int _worldX;
    private int _worldY;

    public Viewport() {
        _lastPosX = 0;
        _lastPosY = 0;
    }

    @OnInit
    private void onInit() {
        _width = applicationConfig.getResolutionWidth() - Constant.PANEL_WIDTH;
        _height = applicationConfig.getResolutionHeight();
        _floor = game.getInfo().worldFloors - 1;
        centerOnMap(game.getInfo().worldWidth / 2, game.getInfo().worldHeight / 2);
    }

    public void centerOnMap(int parcelX, int parcelY) {
        _posX = (_width / 2) - (parcelX * Constant.TILE_SIZE);
        _posY = (_height / 2) - (parcelY * Constant.TILE_SIZE);
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

//    public void moveTo(int x, int y) {
//        setPosition(
//                (-x * Constant.TILE_WIDTH) + (50/2 * Constant.TILE_WIDTH),
//                (-y * Constant.TILE_HEIGHT) + (40/2 * Constant.TILE_HEIGHT));
//    }

    public void setPosition(int x, int y) {
        Log.debug("set position to: " + x + "x" + y);

        _posX = x;
        _posY = y;

        _worldX = (int) Math.max(0, (-_posX / Constant.TILE_SIZE) * getScale());
        _worldY = (int) Math.max(0, (-_posY / Constant.TILE_SIZE) * getScale());
    }

    public int getPosX() {
        return _posX;
    }

    public int getPosY() {
        return _posY;
    }

    public int getWidth() {
        return _width;
    }

    public int getHeight() {
        return _height;
    }

    public int getWorldPosX(int x) {
        double viewportOffset = (getPosX() / gdxRenderer.getZoom() + applicationConfig.getResolutionWidth() / 2f - x) / (Constant.TILE_SIZE / gdxRenderer.getZoom());
        double cameraOffset = (gdxRenderer.getCamera().position.x / Constant.TILE_SIZE);
        return (int) (cameraOffset - viewportOffset);
    }

    public int getWorldPosY(int y) {
        double viewportOffset = (getPosY() / gdxRenderer.getZoom() + applicationConfig.getResolutionHeight() / 2f - y) / (Constant.TILE_SIZE / gdxRenderer.getZoom());
        double cameraOffset = (gdxRenderer.getCamera().position.y / Constant.TILE_SIZE);
        return (int) (cameraOffset - viewportOffset);
    }

    public int getScreenPosX(int parcelX) {
        return parcelX * Constant.TILE_SIZE + getPosX();
    }

    public int getScreenPosY(int parcelY) {
        return parcelY * Constant.TILE_SIZE + getPosY();
    }

//    public void setZoom(int zoom) {
//        setPosition(
//                (int)(_posX + ((ZOOM_LEVELS[_zoom] * 1500) - (ZOOM_LEVELS[zoom] * 1500))),
//                (int)(_posY + ((ZOOM_LEVELS[_zoom] * 1200) - (ZOOM_LEVELS[zoom] * 1200))));
//        _zoom = zoom;
//    }

    public float getScale() {
        return ZOOM_LEVELS[_zoom];
    }

//    public void startMove(int x, int y) {
//        _lastPosX = (int) ((x * (1 + (1 - ZOOM_LEVELS[_zoom]))) * 4);
//        _lastPosY = (int) ((y * (1 + (1 - ZOOM_LEVELS[_zoom]))) * 4);
//    }

    public void move(int x, int y) {
        _posX += (int) ((x * (1 + (1 - 0.5))) * 1);
        _posY += (int) ((y * (1 + (1 - 0.5))) * 1);
    }

    public int getFloor() {
        return _floor;
    }

    public void setFloor(int floor) {
        if (gameManager.isRunning()) {
            if (floor >= 0 && floor < game.getInfo().worldFloors) {
                _floor = floor;
                Application.notifyClient(gameObserver -> gameObserver.onFloorChange(_floor));
            }
        }
    }

//    public void setPosition(int x, int y, int z) {
//        setPosition(x, y);
//        _floor = z;
//    }

    public boolean hasParcel(ParcelCommon parcel) {
        return parcel != null
                && parcel.z == _floor
                && parcel.x >= _worldX && parcel.x <= _worldX + 50
                && parcel.y >= _worldY && parcel.y <= _worldY + 50;
    }

    public boolean hasParcel(Parcel parcel) {
        return parcel != null
                && parcel.z == _floor
                && parcel.x >= _worldX && parcel.x <= _worldX + 50
                && parcel.y >= _worldY && parcel.y <= _worldY + 50;
    }
}
