package org.smallbox.faraway.core.game;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.engine.renderer.MainRenderer;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.ui.GameActionExtra;
import org.smallbox.faraway.ui.UICursor;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.GameSelectionExtra;

/**
 * Created by Alex on 21/10/2015.
 */
public abstract class BaseGame {
    public static final int         SPEED_1_TICK_INTERVAL = 320;
    public static final int         SPEED_2_TICK_INTERVAL = 200;
    public static final int         SPEED_3_TICK_INTERVAL = 75;
    public static final int         SPEED_4_TICK_INTERVAL = 10;

    protected boolean               _isRunning;
    protected GameActionExtra       _action;
    protected GameSelectionExtra    _selector;

    // Update
    private int                     _tick;
    private long                    _nextUpdate;
    private long                    _nextUpdateDo;
    private int                     _tickInterval = SPEED_1_TICK_INTERVAL;

    // Render
    private int                     _frame;
    private int                     _renderTime;
    private double                  animationProgress;
    protected boolean                 _paused;
    protected boolean[]                       _directions = new boolean[4];
    protected Viewport                         _viewport;

    public boolean isPaused() {
        return _paused;
    }

    public void                     clearCursor() { _action.setCursor(null); }
    public void                     clearSelection() { _selector.clear(); }
    public void                     setCursor(UICursor cursor) { _action.setCursor(cursor); }
    public void                     setCursor(String cursorName) { _action.setCursor(Data.getData().getCursor(cursorName)); }

    public void update() {
        // Update
        if (_nextUpdate < System.currentTimeMillis() && !_paused) {
            _nextUpdate = System.currentTimeMillis() + _tickInterval;
            _tick += 1;
            MainRenderer.getInstance().onUpdate();
            onUpdate(_tick);
        }

        // Update
        if (_nextUpdateDo < System.currentTimeMillis() && !_paused) {
            _nextUpdateDo = System.currentTimeMillis() + _tickInterval / 4;
            onUpdateDo();
        }
    }

    public void onUpdateDo() {
    }

    protected abstract void onUpdate(int tick);

    public void render(GDXRenderer renderer, Viewport viewport, long lastRenderInterval) {
        long time = System.currentTimeMillis();

        // Draw
        if (!GameManager.getInstance().isPaused()) {
            animationProgress = 1 - ((double) (_nextUpdate - System.currentTimeMillis()) / _tickInterval);
        }

        MainRenderer.getInstance().onDraw(renderer, viewport, animationProgress);

        if (_isRunning) {
            if (_directions[0]) { _viewport.move(20, 0); }
            if (_directions[1]) { _viewport.move(0, 20); }
            if (_directions[2]) { _viewport.move(-20, 0); }
            if (_directions[3]) { _viewport.move(0, -20); }
        }

        MainRenderer.getInstance().onRefresh(_frame);

        // TODO
        try {
            UserInterface.getInstance().onRefresh(_frame);
        } catch (Exception e) {
            e.printStackTrace();
        }

        _action.draw(renderer);

        _frame++;
        _renderTime = (int)(System.currentTimeMillis() - time);

//            Log.debug("Render finish: " + _renderTime);
    }

    public void setSpeed(int speed) {
        switch (speed) {
            case 0:
                _paused = !_paused;
                if (GameManager.getInstance().isRunning()) {
                    GameManager.getInstance().getGame().setRunning(!_paused);
                }
                break;

            case 1:
                _paused = false;
                _tickInterval = SPEED_1_TICK_INTERVAL;
                if (GameManager.getInstance().isRunning()) {
                    GameManager.getInstance().getGame().setRunning(true);
                }
                break;

            case 2:
                _paused = false;
                _tickInterval = SPEED_2_TICK_INTERVAL;
                if (GameManager.getInstance().isRunning()) {
                    GameManager.getInstance().getGame().setRunning(true);
                }
                break;

            case 3:
                _paused = false;
                _tickInterval = SPEED_3_TICK_INTERVAL;
                if (GameManager.getInstance().isRunning()) {
                    GameManager.getInstance().getGame().setRunning(true);
                }
                break;

            case 4:
                _paused = false;
                _tickInterval = SPEED_4_TICK_INTERVAL;
                if (GameManager.getInstance().isRunning()) {
                    GameManager.getInstance().getGame().setRunning(true);
                }
                break;
        }
        Application.getInstance().notify(observer -> observer.onSpeedChange(speed));
    }

    public void setInputDirection(boolean[] directions) {
        _directions = directions;
    }

}