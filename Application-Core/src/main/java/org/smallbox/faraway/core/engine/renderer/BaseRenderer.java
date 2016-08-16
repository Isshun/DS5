package org.smallbox.faraway.core.engine.renderer;

import org.smallbox.faraway.core.DependencyInjector;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.core.util.Log;

public abstract class BaseRenderer implements GameObserver {
    private final boolean       _isThirdParty;
    private long                _totalTime;
    private int                 _nbDraw;
    private boolean             _isLoaded;

    private int                 _width;
    private int                 _height;
    private int                 _floor;
    private int                 _fromX;
    private int                 _fromY;
    private int                 _toX;
    private int                 _toY;

    public BaseRenderer() {
        _isThirdParty = false;
        DependencyInjector.getInstance().register(this);
    }

    public BaseRenderer(boolean isThirdParty) {
        _isThirdParty = isThirdParty;
        DependencyInjector.getInstance().register(this);
    }

    public int getLevel() {
        return 0;
    }

    protected void onGameStart(Game game) {}
    protected void onGameUpdate() {}
    protected void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {}
    protected void onRefresh(int frame) {}

    public final void draw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        long time = System.currentTimeMillis();

        _floor = viewport.getFloor();
        _fromX = (int) Math.max(0, (-viewport.getPosX() / Constant.TILE_WIDTH) * viewport.getScale());
        _fromY = (int) Math.max(0, (-viewport.getPosY() / Constant.TILE_HEIGHT) * viewport.getScale());
        _toX = Math.min(_width, _fromX + 50);
        _toY = Math.min(_height, _fromY + 40);

        onDraw(renderer, viewport, animProgress);

        _totalTime += (System.currentTimeMillis() - time);
        _nbDraw++;
    }

    protected boolean parcelInViewport(ParcelModel parcel) {
        return parcel != null
                && parcel.z == _floor
                && parcel.x >= _fromX && parcel.x <= _toX
                && parcel.y >= _fromY && parcel.y <= _toY;
    }

    public void dump() {
        if (_nbDraw != 0) {
            Log.notice("Renderer: " + this.getClass().getSimpleName() + ",\tdraw: " + _nbDraw + ",\tavg time: " + _totalTime / _nbDraw);
        }
    }

    public boolean isLoaded() {
        return _isLoaded;
    }

    public final void gameStart(Game game) {
        Log.info("[BaseRender] gameStart: " + getClass().getSimpleName());

        _width = game.getInfo().worldWidth;
        _height = game.getInfo().worldHeight;

        onGameStart(game);

        _isLoaded = true;
    }

    public final void gameUpdate() {
        onGameUpdate();
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
