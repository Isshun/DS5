package org.smallbox.faraway.client.layer;

import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.WorldModule;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.GameException;

public abstract class BaseLayer {
    @Inject private GameSelectionManager gameSelectionManager;
    @Inject private ApplicationConfig applicationConfig;
    @Inject private WorldModule worldModule;
    @Inject private Game game;

    private final boolean _isThirdParty;
    private boolean _isVisible;
    private long _cumulateTime;

    protected int _fromX;
    protected int _fromY;
    protected int _toX;
    protected int _toY;
    protected int _frame;

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

    public void toggleVisibility() {
        _isVisible = !_isVisible;
    }

    public void setVisibility(boolean visibility) {
        _isVisible = visibility;
    }

    public boolean isVisible() {
        return _isVisible;
    }

    public final int getLevel() {
        try {
            return getClass().getAnnotation(GameLayer.class).level();
        } catch (NullPointerException e) {
            throw new RuntimeException("GameLayer annotation is missing for " + getClass());
        }
    }

    protected void onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {}

    protected void onDrawParcel(BaseRenderer renderer, Parcel parcel) {}

    public final void draw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {
        if (isVisible()) {
            long time = System.currentTimeMillis();

            _frame++;
            _fromX = (int) Math.max(0, (-viewport.getPosX() / Constant.TILE_SIZE) * viewport.getScale());
            _fromY = (int) Math.max(0, (-viewport.getPosY() / Constant.TILE_SIZE) * viewport.getScale());
            _toX = Math.min(game.getInfo().worldWidth, _fromX + 50);
            _toY = Math.min(game.getInfo().worldHeight, _fromY + 40);

            onDraw(renderer, viewport, animProgress, frame);

            _cumulateTime += (System.currentTimeMillis() - time);
        }
    }

    public final void drawParcel(BaseRenderer renderer, Parcel parcel) {
        if (isVisible()) {
            onDrawParcel(renderer, parcel);
        }
    }

    public long getCumulateTime() {
        return _cumulateTime;
    }

    public long getAverageTime() {
        return _cumulateTime / _frame;
    }

}
