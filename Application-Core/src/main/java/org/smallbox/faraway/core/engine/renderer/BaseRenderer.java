package org.smallbox.faraway.core.engine.renderer;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.ModuleRenderer;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.core.util.Log;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRenderer<T> implements GameObserver {
    private final boolean       _isThirdParty;
    private long                _totalDrawDelay;
    private long                _lastDrawDelay;
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
        Application.dependencyInjector.register(this);
    }

    public BaseRenderer(boolean isThirdParty) {
        _isThirdParty = isThirdParty;
        Application.dependencyInjector.register(this);
    }

    public int getLevel() {
        return 0;
    }

    protected void onGameUpdate() {}
    protected void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {}
    protected void onRefresh(int frame) {}

    public final void draw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        //                if (render.isMandatory() || (game.hasDisplay(render.getClass().getName()))) {

        long time = System.currentTimeMillis();

        _floor = viewport.getFloor();
        _fromX = (int) Math.max(0, (-viewport.getPosX() / Constant.TILE_WIDTH) * viewport.getScale());
        _fromY = (int) Math.max(0, (-viewport.getPosY() / Constant.TILE_HEIGHT) * viewport.getScale());
        _toX = Math.min(_width, _fromX + 50);
        _toY = Math.min(_height, _fromY + 40);

        onDraw(renderer, viewport, animProgress);

        _lastDrawDelay = (System.currentTimeMillis() - time);
        _totalDrawDelay += _lastDrawDelay;
        _nbDraw++;
    }

    public void dump() {
        if (_nbDraw != 0) {
            Log.notice("Renderer: " + this.getClass().getSimpleName() + ",\tdraw: " + _nbDraw + ",\tavg time: " + _totalDrawDelay / _nbDraw);
        }
    }

    public boolean isLoaded() {
        return _isLoaded;
    }
    public long getTotalDrawDelay() { return _totalDrawDelay; }
    public long getLastDrawDelay() { return _lastDrawDelay; }

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

    public static List<BaseRenderer<GameModule>> createRenderer(GameModule module) {
        List<BaseRenderer<GameModule>> rendererList = new ArrayList<>();
        try {
            for (Class<? extends BaseRenderer> cls: module.getClass().getAnnotation(ModuleRenderer.class).value()) {
                rendererList.add(cls.newInstance());
            }
        } catch ( IllegalAccessException | InstantiationException e) {
            Log.error(e);
        }
        return rendererList;
    }
}
