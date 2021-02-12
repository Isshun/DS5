package org.smallbox.faraway.client.renderer;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.log.Log;

@GameObject
public class Viewport {
    @Inject private WorldCameraManager worldCameraManager;
    @Inject private ApplicationConfig applicationConfig;
    @Inject private GameManager gameManager;
    @Inject private MapRenderer mapRenderer;
    @Inject private Game game;

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
    private int _height;
    private final int _zoom = ZOOM_LEVELS.length - 1;
    private int _floor;

    public Viewport() {
        _lastPosX = 0;
        _lastPosY = 0;
    }

    @OnInit
    private void onInit() {
        _width = applicationConfig.getResolutionWidth() - Constant.PANEL_WIDTH;
        _height = applicationConfig.getResolutionHeight();
        _floor = game.getInfo().worldFloors - 1;
//        centerOnMap(game.getInfo().worldWidth / 2, game.getInfo().worldHeight / 2);
    }

    public void centerOnMap(int parcelX, int parcelY) {
        setPosition((_width / 2) - (parcelX * Constant.TILE_SIZE), (_height / 2) - (parcelY * Constant.TILE_SIZE));
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

    public void setPosition(int x, int y) {
        Log.debug("set position to: " + x + "x" + y);

        //worldCameraManager.setPosition(x, y);

        _posX = x;
        _posY = y;
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
        double viewportOffset = (getPosX() / worldCameraManager.getZoom() + applicationConfig.getResolutionWidth() / 2f - x) / (Constant.TILE_SIZE / worldCameraManager.getZoom());
        double cameraOffset = (worldCameraManager.getPosition().x / Constant.TILE_SIZE);
        return (int) (cameraOffset - viewportOffset);
    }

    public int getWorldPosY(int y) {
        double viewportOffset = (getPosY() / worldCameraManager.getZoom() + applicationConfig.getResolutionHeight() / 2f - y) / (Constant.TILE_SIZE / worldCameraManager.getZoom());
        double cameraOffset = (worldCameraManager.getPosition().y / Constant.TILE_SIZE);
        return (int) (cameraOffset - viewportOffset);
    }

    public int getScreenPosX(int parcelX) {
        return parcelX * Constant.TILE_SIZE + getPosX();
    }

    public int getScreenPosY(int parcelY) {
        return parcelY * Constant.TILE_SIZE + getPosY();
    }

    public float getScale() {
        return ZOOM_LEVELS[_zoom];
    }

    public void move(int x, int y) {
        setPosition(_posX + (int) ((x * (1 + (1 - 0.5))) * 1), _posY + (int) ((y * (1 + (1 - 0.5))) * 1));
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

    // TODO
    public boolean hasParcel(Parcel parcel) {
        return parcel != null
                && parcel.z == _floor;
//                && parcel.x >= _worldX && parcel.x <= _worldX + 50
//                && parcel.y >= _worldY && parcel.y <= _worldY + 50;
    }
}
