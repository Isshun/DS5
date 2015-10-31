package org.smallbox.faraway.core.game;

import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.engine.renderer.MainRenderer;
import org.smallbox.faraway.core.module.GameModule;
import org.smallbox.faraway.ui.UserInterface;

/**
 * Created by Alex on 21/10/2015.
 */
public abstract class BaseGame {
    public static final int         SPEED_1_TICK_INTERVAL = 320;
    public static final int         SPEED_2_TICK_INTERVAL = 200;
    public static final int         SPEED_3_TICK_INTERVAL = 75;
    public static final int         SPEED_4_TICK_INTERVAL = 10;

    protected boolean               _isRunning;

    // Update
    private int                     _tick;
    private long                    _nextUpdate;
    private long                    _nextUpdateDo;
    private int                     _tickInterval = SPEED_1_TICK_INTERVAL;

    // Render
    private int                     _frame;
    private int                     _renderTime;
    private double                  animationProgress;
    private boolean                 _paused;

    public boolean isPaused() {
        return _paused;
    }

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
        renderer.clear(new Color(0, 0, 0));
        renderer.begin();
        MainRenderer.getInstance().onDraw(renderer, viewport, animationProgress);
        UserInterface.getInstance().onDraw(renderer);
        renderer.end();
        renderer.finish();

        if (_isRunning) {
            onRender(_frame);
        }

        MainRenderer.getInstance().onRefresh(_frame);

        try {
            UserInterface.getInstance().onRefresh(_frame);
        } catch (Exception e) {
            e.printStackTrace();
        }

        _frame++;
        _renderTime = (int)(System.currentTimeMillis() - time);

//            Log.debug("Render finish: " + _renderTime);
    }

    protected abstract void onRender(int frame);

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
    }

}
