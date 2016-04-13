package org.smallbox.faraway.core.engine.renderer;

import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.util.Log;

public abstract class BaseRenderer implements GameObserver {
    private final boolean _isThirdParty;
    private long     _totalTime;
    private int     _nbDraw;
    private boolean _isLoaded;

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
        onDraw(renderer, viewport, animProgress);
        _totalTime += (System.currentTimeMillis() - time);
        _nbDraw++;
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
        Log.info("[BaseRender] load " + getClass().getSimpleName());
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
}
