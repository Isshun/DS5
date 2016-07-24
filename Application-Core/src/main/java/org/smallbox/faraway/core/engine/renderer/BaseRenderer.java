package org.smallbox.faraway.core.engine.renderer;

import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.core.util.Log;

public abstract class BaseRenderer implements GameObserver {
    private final boolean _isThirdParty;
    private long     _totalTime;
    private int     _nbDraw;
    private boolean _isLoaded;

    private int                 _width;
    private int                 _height;
    private int                 _floor;
    private int fromX;
    private int fromY;
    private int toX;
    private int toY;

    public BaseRenderer() {
        _isThirdParty = false;
    }

    public BaseRenderer(boolean isThirdParty) {
        _isThirdParty = isThirdParty;
    }

    public abstract void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress);
    public abstract void onRefresh(int frame);
    public int getLevel() {
        return 0;
    }

    public void update() {
        onUpdate();
    }

    protected void onGameStart(Game game) {

    }

    protected void onLoad(Game game) {
    }

    protected void onUpdate() {
    }

    public void destroy() {
    }

    public void startGame(Game game) {
        Log.info("Load java render: " + getClass().getSimpleName());
        onGameStart(game);
    }

    public void draw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        long time = System.currentTimeMillis();

        fromX = (int) Math.max(0, (-viewport.getPosX() / Constant.TILE_WIDTH) * viewport.getScale());
        fromY = (int) Math.max(0, (-viewport.getPosY() / Constant.TILE_HEIGHT) * viewport.getScale());
        toX = Math.min(_width, fromX + 50);
        toY = Math.min(_height, fromY + 40);

        onDraw(renderer, viewport, animProgress);

        _totalTime += (System.currentTimeMillis() - time);
        _nbDraw++;
    }

    protected boolean parcelInViewport(ParcelModel parcel) {
        return parcel != null
                && parcel.z == _floor
                && parcel.x >= fromX && parcel.x <= toX
                && parcel.y >= fromY && parcel.y <= toY;
    }

    public void dump() {
        if (_nbDraw != 0) {
            Log.notice("Renderer: " + this.getClass().getSimpleName() + ",\tdraw: " + _nbDraw + ",\tavg time: " + _totalTime / _nbDraw);
        }
    }

    public boolean isLoaded() {
        return _isLoaded;
    }

    public void load(Game game) {
        Log.info("[BaseRender] onLoad " + getClass().getSimpleName());

        _width = game.getInfo().worldWidth;
        _height = game.getInfo().worldHeight;

        onLoad(game);

        _isLoaded = true;
    }

    public void unload() {
        Log.info("[BaseRender] unload " + getClass().getSimpleName());
        _isLoaded = false;
    }

    public boolean isMandatory() {
        return false;
    }

    @Override
    public void onFloorChange(int floor) {
        _floor = floor;
    }
}
