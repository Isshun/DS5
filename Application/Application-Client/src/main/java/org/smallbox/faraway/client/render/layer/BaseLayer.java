package org.smallbox.faraway.client.render.layer;

import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.GameClientObserver;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.model.ObjectModel;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.Log;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseLayer<T> implements GameObserver, GameClientObserver {
    private final boolean       _isThirdParty;
    private long                _totalDrawDelay;
    private long                _lastDrawDelay;
    private int                 _nbDraw;
    private boolean             _isLoaded;
    private boolean             _isVisible = true;

    private int                 _width;
    private int                 _height;
    private int                 _floor;
    private int                 _fromX;
    private int                 _fromY;
    private int                 _toX;
    private int                 _toY;
    protected int               _frame;

    private long                _lastTime;
    private long                _cumulateTime;

    public BaseLayer() {
        if (!getClass().isAnnotationPresent(GameLayer.class)) {
            throw new GameException(getClass(), "GameLayer annotation is missing");
        }

        _isThirdParty = false;
        _isVisible = getClass().getAnnotation(GameLayer.class).visible();
        Application.dependencyInjector.register(this);
    }

    public BaseLayer(boolean isThirdParty) {
        _isThirdParty = isThirdParty;
        _isVisible = getClass().getAnnotation(GameLayer.class).visible();
        Application.dependencyInjector.register(this);
    }

    protected void drawSelection(GDXRenderer renderer, SpriteManager spriteManager, ObjectModel object, int posX, int posY) {
        if (ApplicationClient.selected != null && ApplicationClient.selected.contains(object)) {
            renderer.draw(posX, posY + -4, spriteManager.getSelectorCorner(0));
            renderer.draw(posX + 24, posY + -4, spriteManager.getSelectorCorner(1));
            renderer.draw(posX, posY + 28, spriteManager.getSelectorCorner(2));
            renderer.draw(posX + 24, posY + 28, spriteManager.getSelectorCorner(3));
        }
    }

    protected void drawSelectionOnMap(GDXRenderer renderer, SpriteManager spriteManager, Viewport viewport, ObjectModel object, int mapX, int mapY) {
        drawSelection(renderer, spriteManager, object, viewport.getPosX() + (mapX * 32), viewport.getPosY() + (mapY * 32));
    }

    public void toggleVisibility() { _isVisible = !_isVisible; }
    public void setVisibility(boolean visibility) { _isVisible = visibility; }
    public boolean isVisible() { return _isVisible; }

    public final int getLevel() {
        try {
            return getClass().getAnnotation(GameLayer.class).level();
        } catch (NullPointerException e) {
            throw new RuntimeException("GameLayer annotation is missing for " + getClass());
        }
    }

//    protected void onRenderUpdate() {}
    protected void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {}

    public final void draw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        if (isVisible()) {
            long time2 = System.nanoTime() / 1000;

            //                if (render.isMandatory() || (game.hasDisplay(render.getClass().getName()))) {

            long time = System.currentTimeMillis();

            _frame++;
            _floor = viewport.getFloor();
            _fromX = (int) Math.max(0, (-viewport.getPosX() / Constant.TILE_WIDTH) * viewport.getScale());
            _fromY = (int) Math.max(0, (-viewport.getPosY() / Constant.TILE_HEIGHT) * viewport.getScale());
            _toX = Math.min(_width, _fromX + 50);
            _toY = Math.min(_height, _fromY + 40);

            onDraw(renderer, viewport, animProgress, frame);

            _lastDrawDelay = (System.currentTimeMillis() - time);
            _totalDrawDelay += _lastDrawDelay;
            _nbDraw++;

            _lastTime = System.nanoTime() / 1000 - time2;
            _cumulateTime += _lastTime;
        }
    }

    public long         getLastTime() { return _lastTime; }
    public long         getCumulateTime() { return _cumulateTime; }

    public void dump() {
        if (_nbDraw != 0) {
            Log.notice("Layer: " + this.getClass().getSimpleName() + ",\tdrawPixel: " + _nbDraw + ",\tavg time: " + _totalDrawDelay / _nbDraw);
        }
    }

    public boolean isLoaded() {
        return _isLoaded;
    }
    public long getTotalDrawDelay() { return _totalDrawDelay; }
    public long getLastDrawDelay() { return _lastDrawDelay; }

    public final void gameStart(Game game) {
        Log.debug(getClass(), "start rendere");

        _width = game.getInfo().worldWidth;
        _height = game.getInfo().worldHeight;

        onGameStart(game);

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

    public static List<BaseLayer> createLayer(Class<? extends BaseLayer> cls) {
        List<BaseLayer> layerList = new ArrayList<>();
        try {
//            for (Class<? extends BaseLayer> cls: module.getClass().getAnnotation(ModuleLayer.class).value()) {
                BaseLayer layer = cls.newInstance();
                Application.dependencyInjector.register(layer);
                layerList.add(layer);
//            }
        } catch ( IllegalAccessException | InstantiationException e) {
            throw new GameException(BaseLayer.class, e);
        }
        return layerList;
    }
}
