package org.smallbox.faraway.client.layer;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.asset.SpriteManager;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.game.world.ObjectModel;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.GameException;
import org.smallbox.faraway.util.log.Log;

public abstract class BaseLayer {
    @Inject private GameSelectionManager gameSelectionManager;
    @Inject private Game game;

    private final boolean       _isThirdParty;
    private long                _totalDrawDelay;
    private long                _lastDrawDelay;
    private int                 _nbDraw;
    private boolean             _isVisible = true;

    protected int                 _fromX;
    protected int                 _fromY;
    protected int                 _toX;
    protected int                 _toY;
    protected int               _frame;

    private long                _lastTime;
    private long                _cumulateTime;
    private double _selectionChange = 0.5;
    private double _selectionOffset;

    public void onInitLayer() {}
    public void onUpdate(Object object) {}

    public BaseLayer() {
        if (!getClass().isAnnotationPresent(GameLayer.class)) {
            throw new GameException(getClass(), "GameLayer annotation is missing");
        }

        _isThirdParty = false;
        _isVisible = getClass().getAnnotation(GameLayer.class).visible();
    }

    public BaseLayer(boolean isThirdParty) {
        _isThirdParty = isThirdParty;
        _isVisible = getClass().getAnnotation(GameLayer.class).visible();
    }

    protected void drawSelection(BaseRenderer renderer, SpriteManager spriteManager, ObjectModel object, int posX, int posY, int width, int height, int offsetX, int offsetY) {
        if (gameSelectionManager.selectContains(object)) {
            if (_selectionOffset > 2) {
                _selectionChange = -0.2;
            } else if (_selectionOffset < -2) {
                _selectionChange = 0.2;
            }
            _selectionOffset += _selectionChange;
            int index = (int)_selectionOffset;
            renderer.drawRectangle(posX, posY, 128, 128, Color.WHITE, false);
//            renderer.draw(spriteManager.getSelectorCorner(0), offsetX - 4 + posX - index,            offsetY - 4 + posY - index);
//            renderer.draw(spriteManager.getSelectorCorner(1), offsetX - 4 + posX + width + index,    offsetY - 4 + posY - index);
//            renderer.draw(spriteManager.getSelectorCorner(2), offsetX - 4 + posX - index,            offsetY - 4 + posY + height + index);
//            renderer.draw(spriteManager.getSelectorCorner(3), offsetX - 4 + posX + width + index,    offsetY - 4 + posY + height + index);
        }
    }

    protected void drawSelectionOnMap(BaseRenderer renderer, SpriteManager spriteManager, Viewport viewport, ObjectModel object, int mapX, int mapY, int width, int height, int offsetX, int offsetY) {
        drawSelection(renderer, spriteManager, object, viewport.getPosX() + (mapX * Constant.TILE_SIZE), viewport.getPosY() + (mapY * Constant.TILE_SIZE), width, height, offsetX, offsetY);
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

    public void onInit() {
    }

//    protected void onRenderUpdate() {}
    protected void onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {}

    public final void draw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {
        if (isVisible()) {
            long time2 = System.nanoTime() / 1000;

            //                if (render.isMandatory() || (game.hasDisplay(render.getClass().getName()))) {

            long time = System.currentTimeMillis();

            _frame++;
            _fromX = (int) Math.max(0, (-viewport.getPosX() / Constant.TILE_SIZE) * viewport.getScale());
            _fromY = (int) Math.max(0, (-viewport.getPosY() / Constant.TILE_SIZE) * viewport.getScale());
            _toX = Math.min(game.getInfo().worldWidth, _fromX + 50);
            _toY = Math.min(game.getInfo().worldHeight, _fromY + 40);

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
            Log.info("Layer: " + this.getClass().getSimpleName() + ",\tdrawPixel: " + _nbDraw + ",\tavg time: " + _totalDrawDelay / _nbDraw);
        }
    }

    public long getTotalDrawDelay() { return _totalDrawDelay; }
    public long getLastDrawDelay() { return _lastDrawDelay; }

    public boolean isMandatory() {
        return false;
    }

}
