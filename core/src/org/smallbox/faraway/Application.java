package org.smallbox.faraway;

import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.data.factory.world.WorldFactory;
import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.engine.renderer.GDXRenderer;
import org.smallbox.faraway.engine.renderer.LightRenderer;
import org.smallbox.faraway.engine.renderer.MainRenderer;
import org.smallbox.faraway.engine.renderer.ParticleRenderer;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.GameObserver;
import org.smallbox.faraway.game.model.GameConfig;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.planet.RegionInfo;
import org.smallbox.faraway.game.module.ModuleManager;
import org.smallbox.faraway.game.module.base.WorldModule;
import org.smallbox.faraway.game.module.path.PathManager;
import org.smallbox.faraway.ui.MenuBase;
import org.smallbox.faraway.ui.MenuLoad;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.LayoutFactory;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.mainMenu.MainMenu;
import org.smallbox.faraway.util.Log;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Application implements GameEventListener {
    public static final int 		UPDATE_INTERVAL = 50;
    public static final int 		LONG_UPDATE_INTERVAL = 1000;

    public static final int 		SPEED_1_TICK_INTERVAL = 320;
    public static final int 		SPEED_2_TICK_INTERVAL = 200;
    public static final int 		SPEED_3_TICK_INTERVAL = 75;
    public static final int 		SPEED_4_TICK_INTERVAL = 10;

    private static Game             _game;
    private static MenuBase			_menu;
    private static int 				_updateInterval = UPDATE_INTERVAL;
    private static int 				_longUpdateInterval = LONG_UPDATE_INTERVAL;
    private static MainRenderer     _mainRenderer;
    private static LightRenderer    _lightRenderer;
    private static ParticleRenderer _particleRenderer;
    private static UserInterface    _gameInterface;
    private static boolean			_isFullscreen;
    private static GDXRenderer      _renderer;
    private static GameData         _data;
    private static Application      _self;
    private static int              _frame;

    private MainMenu                _mainMenu;
    private int                     _tick;
    private int                     _nextLongUpdate;
    private static int              _renderTime;
    private long                    _startTime = -1;
    private long                    _elapsed = 0;
    private boolean[]               _directions;
    private long                    _nextTick;
    private long                    _lastTick;
    private int                     _tickInterval = SPEED_1_TICK_INTERVAL;
    private boolean                 _isRunning = true;
    private final BlockingQueue<Runnable> _queue = new LinkedBlockingQueue<>();

    public Application(GDXRenderer renderer) {
        _self = this;
        _renderer = renderer;
    }

    public void create(GDXRenderer renderer, LightRenderer lightRenderer, ParticleRenderer particleRenderer, GameData data, GameConfig config) {
        _data = data;
        _renderer = renderer;
        _isFullscreen = true;
        _mainRenderer = new MainRenderer(renderer, config);
        _lightRenderer = lightRenderer;
        _particleRenderer = particleRenderer;
        _gameInterface = new UserInterface(new LayoutFactory(), ViewFactory.getInstance());
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
        if (_game != null && !_game.isRunning()) {
            if (_menu != null && _menu.isVisible()) {
                if (_menu.checkKey(key)) {
                    return;
                }
            }
        }

        switch (key) {
            case RIGHT:
                _game.getViewport().startMove(0, 0);
                _game.getViewport().update(100, 0);
                break;

            case ESCAPE:
                _game.toggleRunning();
                break;

// Reload UI
            case F5:
                UserInterface.getInstance().reload();
                return;

// Kill
            case F4:
                _renderer.close();
                return;

// Save
            case S:
                if (modifier == Modifier.CONTROL) {
                    _game.save(_game.getFileName());
                    return;
                }
                break;

// Load
            case L:
                if (modifier == Modifier.CONTROL) {
                    try {
                        _menu = new MenuLoad(path -> {
// TODO NULL
                            _game = new Game(0, 0, null, null, null, _particleRenderer, _lightRenderer, null);
                            _game.load();
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                break;

// Fullscreen
            case ENTER:
                if (modifier == Modifier.ALT) {
                    _isFullscreen = !_isFullscreen;
                    _renderer.setFullScreen(_isFullscreen);
                    return;
                }
                break;

            default:
                break;
        }

        _gameInterface.onKeyEvent(action, key, modifier);
    }

    @Override
    public void onWindowEvent(Action action) {
        _gameInterface.onWindowEvent(action);
    }

    @Override
    public void onMouseEvent(Action action, MouseButton button, int x, int y, boolean rightPressed) {
        if (_game != null) {
            if (button == MouseButton.RIGHT && action == Action.PRESSED) {
                _game.getViewport().startMove(x, y);
                return;
            }
            if (button == MouseButton.WHEEL_UP) {
                _renderer.zoomUp();
            }
            else if (button == MouseButton.WHEEL_DOWN) {
                _renderer.zoomDown();
            }
            else {
                _gameInterface.onMouseEvent(action, button, x, y, rightPressed);
            }
        } else {
            _mainMenu.onMouseEvent(action, button, x, y);
        }
    }

    public boolean onDrag(int x, int y) {
        if (_game != null) {
            // Move viewport
//            if (_keyRightPressed && Math.abs(_mouseRightPressX - x) > 5 || Math.abs(_mouseRightPressY - y) > 5) {
            _game.getViewport().update(x, y);
//            }

            return true;
        }
        return false;
    }

    public void whiteRoom() {
        _mainMenu.close();

        _game = new Game(25, 25, _data, GameData.config, null, _particleRenderer, _lightRenderer, GameData.getData().getRegion("white", "default"));
        _game.init(new WorldFactory());

        startGame(false);
    }

    public void newGame(String fileName, RegionInfo  regionInfo) {
        long time = System.currentTimeMillis();

        _mainMenu.close();

        WorldFactory factory = new WorldFactory();

        _game = new Game(50, 50, _data, GameData.config, fileName, _particleRenderer, _lightRenderer, regionInfo);
        _game.init(factory);

        WorldModule world = (WorldModule) ModuleManager.getInstance().getModule(WorldModule.class);
        world.create();
        factory.create(world, regionInfo);
        _game.save(fileName);

        factory.createLandSite(_game);

        startGame(false);

        Log.notice("Create new game (" + (System.currentTimeMillis() - time) + "ms)");
    }

    public void loadGame(String fileName) {
        long time = System.currentTimeMillis();

        _mainMenu.close();

        _game = new Game(250, 250, _data, GameData.config, fileName, _particleRenderer, _lightRenderer, null);

        // TODO
        _game.setRegion(GameData.getData().getRegion("arrakis", "desert"));
        _game.preload();

        startGame(true);

        Log.notice("Load save (" + (System.currentTimeMillis() - time) + "ms)");
    }

    private void startGame(boolean load) {

        long time = System.currentTimeMillis();
        _mainRenderer.init(GameData.config, _game);
        Log.notice("Init renderers (" + (System.currentTimeMillis() - time) + "ms)");

        time = System.currentTimeMillis();
        _gameInterface.onCreate(_game);
        Log.notice("Create UI (" + (System.currentTimeMillis() - time) + "ms)");

        if (_lightRenderer != null) {
            time = System.currentTimeMillis();
            _lightRenderer.init();
            Log.notice("Init light (" + (System.currentTimeMillis() - time) + "ms)");
        }

        _game.init(null);

        time = System.currentTimeMillis();
        PathManager.getInstance().init(Game.getInstance().getInfo().worldWidth, Game.getInstance().getInfo().worldHeight);
        Log.notice("Init paths (" + (System.currentTimeMillis() - time) + "ms)");

        if (load) {
            _game.load();
        }

        Game.getInstance().notify(GameObserver::onStartGame);
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

        if (_game != null) {
            double animProgress = 0;
            if (_game.isRunning()) {
                _elapsed += lastRenderInterval;
                animProgress = ((double) (System.currentTimeMillis() - _nextTick) / _tickInterval);
            }

            renderer.clear(new Color(0, 0, 0));

            renderer.begin();
            _mainRenderer.onDraw(renderer, viewport, animProgress);
            _gameInterface.onDraw(renderer, _tick, 0);
            renderer.end();

            renderer.finish();

            if (_game.isRunning()) {
                updateLocation();
                refreshGame(_frame);
            }

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
        if (_game != null && _game.isRunning()) {

            // Update
            if (System.currentTimeMillis() > _nextTick) {
                _lastTick = System.currentTimeMillis();
                _nextTick = _lastTick + _tickInterval;
//                long timeU = System.currentTimeMillis();
                _game.onUpdate(_tick++);
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

    private void updateLocation() {
        if (_directions[0]) {
            _game.getViewport().move(20, 0);
        }
        if (_directions[1]) {
            _game.getViewport().move(0, 20);
        }
        if (_directions[2]) {
            _game.getViewport().move(-20, 0);
        }
        if (_directions[3]) {
            _game.getViewport().move(0, -20);
        }
    }

    public void refreshGame(int refreshCount) {
        if (_game != null) {
            _mainRenderer.onRefresh(refreshCount);

            try {
                _gameInterface.onRefresh(refreshCount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void refreshMenu(int frame) {
        _mainMenu.refresh(frame);
    }

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
        _directions = directions;
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
                _game.setRunning(false);
                break;

            case 1:
                _tickInterval = SPEED_1_TICK_INTERVAL;
                _game.setRunning(true);
                break;

            case 2:
                _tickInterval = SPEED_2_TICK_INTERVAL;
                _game.setRunning(true);
                break;

            case 3:
                _tickInterval = SPEED_3_TICK_INTERVAL;
                _game.setRunning(true);
                break;
        }
    }
}
