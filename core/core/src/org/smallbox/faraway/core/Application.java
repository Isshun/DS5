package org.smallbox.faraway.core;

import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.engine.renderer.LightRenderer;
import org.smallbox.faraway.core.engine.renderer.ParticleRenderer;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.model.GameConfig;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.model.planet.RegionInfo;
import org.smallbox.faraway.ui.MenuBase;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.LayoutFactory;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.mainMenu.MainMenu;
import org.smallbox.faraway.core.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Application implements GameEventListener {
    public static final int         UPDATE_INTERVAL = 50;
    public static final int         LONG_UPDATE_INTERVAL = 1000;

    public static final int         SPEED_1_TICK_INTERVAL = 320;
    public static final int         SPEED_2_TICK_INTERVAL = 200;
    public static final int         SPEED_3_TICK_INTERVAL = 75;
    public static final int         SPEED_4_TICK_INTERVAL = 10;

    private static MenuBase            _menu;
    private static int                 _updateInterval = UPDATE_INTERVAL;
    private static int                 _longUpdateInterval = LONG_UPDATE_INTERVAL;
    private static Application      _self;
    private static int              _frame;

    private MainMenu                _mainMenu;
    private int                     _tick;
    private int                     _nextLongUpdate;
    private static int              _renderTime;
    private long                    _startTime = -1;
    private long                    _elapsed = 0;
    private long                    _nextTick;
    private long                    _lastTick;
    private int                     _tickInterval = SPEED_1_TICK_INTERVAL;
    private boolean                 _isRunning = true;
    private final BlockingQueue<Runnable> _queue = new LinkedBlockingQueue<>();

    public Application(GDXRenderer renderer) {
        _self = this;
    }

    public void create(GDXRenderer renderer, LightRenderer lightRenderer, ParticleRenderer particleRenderer, GameData data, GameConfig config) {
        _mainMenu = new MainMenu(new LayoutFactory(), ViewFactory.getInstance(), renderer);
        _mainMenu.open();
    }

    public static int getUpdateInterval() {
        return _updateInterval;
    }

    public static int getLongUpdateInterval() {
        return _longUpdateInterval;
    }

    public static void setUpdateInterval(int updateInterval) {
        _updateInterval = updateInterval;
    }

    @Override
    public void onKeyEvent(Action action, Key key, Modifier modifier) {
        // Events for menu
        if (!GameManager.getInstance().isRunning()) {
            if (_menu != null && _menu.isVisible()) {
                if (_menu.checkKey(key)) {
                    return;
                }
            }
        }

        ApplicationShortcutManager.onKeyPress(key, modifier);

        UserInterface.getInstance().onKeyEvent(action, key, modifier);

        if (GameManager.getInstance().isRunning()) {
            GameManager.getInstance().getGame().notify(observer -> observer.onKeyPress(key));
        }
    }

    @Override
    public void onWindowEvent(Action action) {
        UserInterface.getInstance().onWindowEvent(action);
    }

    @Override
    public void onMouseEvent(Action action, MouseButton button, int x, int y, boolean rightPressed) {
        if (GameManager.getInstance().isRunning()) {
            ApplicationShortcutManager.onMouseEvent(action, button, x, y, rightPressed);
        } else {
            _mainMenu.onMouseEvent(action, button, x, y);
        }
    }

    public boolean onDrag(int x, int y) {
        if (GameManager.getInstance().isRunning()) {
            // Move viewport
//            if (_keyRightPressed && Math.abs(_mouseRightPressX - x) > 5 || Math.abs(_mouseRightPressY - y) > 5) {
            GameManager.getInstance().getGame().getViewport().update(x, y);
//            }

            return true;
        }
        return false;
    }

//    public void whiteRoom() {
//        _mainMenu.close();
//
//        _game = new Game(25, 25, _data, GameData.config, null, _particleRenderer, _lightRenderer, GameData.getData().getRegion("white", "default"));
//        _game.init(new WorldFactory());
//
//        startGame(false);
//    }

    public void newGame(String fileName, RegionInfo  regionInfo) {
        _mainMenu.close();
        GameManager.getInstance().create(fileName, regionInfo);
    }

    public void loadGame(String fileName) {
        _mainMenu.close();
        GameManager.getInstance().load(fileName);
    }

    private void startGame(boolean load) {
        GameManager.getInstance().startGame(load);
    }

    public void render(GDXRenderer renderer, Viewport viewport, long lastRenderInterval) {
        long time = System.currentTimeMillis();

        if (_startTime == -1) {
            _startTime = System.currentTimeMillis();
        }
//        long _elapsed = System.currentTimeMillis() - _startTime;
//        printInfo("elapsed: " + (_elapsed / 1000));

        if (_mainMenu != null && _mainMenu.isOpen()) {
            _mainMenu.draw(renderer, viewport);
            return;
        }

        if (GameManager.getInstance().isRunning()) {
            _elapsed += lastRenderInterval;

            double animProgress = 0;
            animProgress = ((double) (System.currentTimeMillis() - _nextTick) / _tickInterval);

            GameManager.getInstance().render(renderer, viewport, lastRenderInterval, animProgress, _tick, _frame);
            _frame++;
            _renderTime = (int)(System.currentTimeMillis() - time);
//            Log.debug("Render finish: " + _renderTime);

            try {
                if (!_queue.isEmpty()) {
                    Log.info("--------------- take from queue ---------------");
                    _queue.take().run();
                    Log.info("------------------ it's done ------------------");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        if (GameManager.getInstance().isRunning()) {
            // Update
            if (System.currentTimeMillis() > _nextTick) {
                _lastTick = System.currentTimeMillis();
                _nextTick = _lastTick + _tickInterval;
//                long timeU = System.currentTimeMillis();
                GameManager.getInstance().getGame().onUpdate(_tick++);
//                printInfo("update time: " + (System.currentTimeMillis() - timeU) + "ms");
            }

            // TODO
            // Long update
            if (_elapsed >= _nextLongUpdate) {
                addTask(() -> {
                    Log.info("Reload config");
                    GameData.getData().reloadConfig();
                    UserInterface.getInstance().reloadTemplates();
                });
                _nextLongUpdate += Application.getLongUpdateInterval();
            }
        }
    }

//
//    public void refreshMenu(int frame) {
//        _mainMenu.refresh(frame);
//    }

    public static Application getInstance() {
        return _self;
    }

    public static int getFrame() {
        return _frame;
    }

    public static int getRenderTime() {
        return _renderTime;
    }

    public void setInputDirection(boolean[] directions) {
        GameManager.getInstance().setInputDirection(directions);
    }

    public void addTask(Runnable runnable) {
        _queue.add(runnable);
    }

    public boolean isRunning() {
        return _isRunning;
    }

    public void setSpeed(int speed) {
        switch (speed) {
            case 0:
                _tickInterval = Integer.MAX_VALUE;
                if (GameManager.getInstance().isRunning()) {
                    GameManager.getInstance().getGame().setRunning(false);
                }
                break;

            case 1:
                _tickInterval = SPEED_1_TICK_INTERVAL;
                if (GameManager.getInstance().isRunning()) {
                    GameManager.getInstance().getGame().setRunning(true);
                }
                break;

            case 2:
                _tickInterval = SPEED_2_TICK_INTERVAL;
                if (GameManager.getInstance().isRunning()) {
                    GameManager.getInstance().getGame().setRunning(true);
                }
                break;

            case 3:
                _tickInterval = SPEED_3_TICK_INTERVAL;
                if (GameManager.getInstance().isRunning()) {
                    GameManager.getInstance().getGame().setRunning(true);
                }
                break;
        }
    }
}
