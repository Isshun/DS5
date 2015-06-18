package org.smallbox.faraway;

import org.smallbox.faraway.data.loader.CategoryLoader;
import org.smallbox.faraway.data.loader.ItemLoader;
import org.smallbox.faraway.engine.*;
import org.smallbox.faraway.engine.renderer.LightRenderer;
import org.smallbox.faraway.engine.renderer.MainRenderer;
import org.smallbox.faraway.data.serializer.LoadListener;
import org.smallbox.faraway.engine.renderer.ParticleRenderer;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.ui.engine.Colors;
import org.smallbox.faraway.ui.engine.TextView;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.data.loader.PlanetLoader;
import org.smallbox.faraway.game.model.GameConfig;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.ui.MenuBase;
import org.smallbox.faraway.ui.MenuLoad;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.mainMenu.MainMenu;
import org.smallbox.faraway.ui.panel.LayoutFactory;

import java.io.IOException;

public class Application implements GameEventListener {
	public static final int 		DRAW_INTERVAL = (1000/60);
    public static final int 		UPDATE_INTERVAL = 50;
    public static final int		    REFRESH_INTERVAL = 200;
    public static final int 		LONG_UPDATE_INTERVAL = 1000;

	private static Game             _game;
	private static MenuBase			_menu;
	private static int 				_updateInterval = UPDATE_INTERVAL;
    private static int 				_longUpdateInterval = LONG_UPDATE_INTERVAL;
	private static MainRenderer     _gameRenderer;
	private static LightRenderer    _lightRenderer;
    private static ParticleRenderer _particleRenderer;
	private static UserInterface    _gameInterface;
	private static LoadListener 	_loadListener;
	private static boolean			_isFullscreen;
    private static GFXRenderer      _renderer;
    private static GameData         _data;
    private static Application      _self;
    private MainMenu                _mainMenu;
    private static long             _lastUpdateDelay;
    private static long             _lastLongUpdateDelay;

    private static int              _frame;
    private int                     _tick;
    private int                     _nextUpdate;
    private int                     _nextRefresh;
    private int                     _nextLongUpdate;
    private int                     _renderTime;
    private int                     _refresh;
    private long                    _startTime = -1;
    private long                    _elapsed = 0;

    public Application(GFXRenderer renderer) {
        _self = this;
        _renderer = renderer;
        _loadListener = message -> {
            renderer.clear();
            TextView text = ViewFactory.getInstance().createTextView();
            text.setString(message);
            text.setCharacterSize(42);
            text.setColor(Colors.LINK_INACTIVE);
            text.setPosition(Constant.WINDOW_WIDTH / 2 - message.length() * 20 / 2, Constant.WINDOW_HEIGHT / 2 - 40);
            text.draw(renderer, null);

            if (_gameInterface != null) {
                _gameInterface.addMessage(Log.LEVEL_INFO, message);
                _gameInterface.onRefresh(0);
                _gameInterface.onDraw(renderer, 0, 0);
            }

            renderer.display();
        };
    }

	public void create(GFXRenderer renderer, LightRenderer lightRenderer, ParticleRenderer particleRenderer, GameData data, GameConfig config) {
        _data = data;
		_renderer = renderer;
		_isFullscreen = true;
		_gameRenderer = new MainRenderer(renderer, config);
        _lightRenderer = lightRenderer;
        _particleRenderer = particleRenderer;
		_gameInterface = new UserInterface(new LayoutFactory(), ViewFactory.getInstance());
        _mainMenu = new MainMenu(new LayoutFactory(), ViewFactory.getInstance(), renderer);
        _mainMenu.open();
    }

    public GameData loadResources() {
        GameData data = new GameData();

        ItemLoader.load(data);
        PlanetLoader.load(data);
        CategoryLoader.load(data);

        return data;
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
    public void onKeyEvent(GameTimer timer, Action action, Key key, Modifier modifier) {
        // Events for menu
        if (_game != null && _game.isRunning() == false) {
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

            case D_1:
                _game.setSpeed(1);
                break;

            case D_2:
                _game.setSpeed(2);
                break;

            case D_3:
                _game.setSpeed(3);
                break;

            // Reload UI
            case F5:
                _gameInterface.reload();
                return;

            // Kill
            case F4:
                _renderer.close();
                return;

            // Save
            case S:
                if (modifier == Modifier.CONTROL) {
                    _loadListener.onUpdate("saving");
                    _game.save(_game.getFileName());
                    _loadListener.onUpdate("save done");
                    return;
                }
                break;

            // Load
            case L:
                if (modifier == Modifier.CONTROL) {
                    try {
                        _menu = new MenuLoad(path -> {
                            // TODO NULL
                            _game = new Game(null, null, null, _particleRenderer, _lightRenderer);
                            _game.load(_loadListener);
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

        _gameInterface.onKeyEvent(timer, action, key, modifier);
    }

    @Override
    public void onWindowEvent(GameTimer timer, Action action) {
        _gameInterface.onWindowEvent(timer, action);
    }

    @Override
    public void onMouseEvent(GameTimer timer, Action action, MouseButton button, int x, int y, boolean rightPressed) {
        if (_game != null) {
            _gameInterface.onMouseEvent(timer, action, button, x, y, rightPressed);
        } else {
            _mainMenu.onMouseEvent(timer, action, button, x, y);

        }
    }

    public LoadListener getLoadListener() {
        return _loadListener;
    }

    public void newGame(String fileName) {
        _game = new Game(_data, _data.config, fileName, _particleRenderer, _lightRenderer);
        _game.init(false);
        _game.newGame(_loadListener);
        PathHelper.getInstance().init(Game.getWorldManager().getWidth(), Game.getWorldManager().getHeight());
        _gameRenderer.init(_game);
        _gameInterface.onCreate(_game);
    }

    public void loadGame(String fileName) {
        _mainMenu.close();

        _game = new Game(_data, _data.config, fileName, _particleRenderer, _lightRenderer);
        _game.init(true);
        _loadListener.onUpdate("Load save");
        _game.load(_loadListener);
        PathHelper.getInstance().init(Game.getWorldManager().getWidth(), Game.getWorldManager().getHeight());

        _loadListener.onUpdate("Start game");
        _gameRenderer.init(_game);
        _gameInterface.onCreate(_game);

        _lightRenderer.init();

        startGame();
    }

    private void startGame() {
        _game.addObserver(_lightRenderer);
    }

    public void render(GFXRenderer renderer, RenderEffect effect, long lastRenderInterval) {
        if (_startTime == -1) {
            _startTime = System.currentTimeMillis();
        }
//        long _elapsed = System.currentTimeMillis() - _startTime;

//        Log.info("elapsed: " + (_elapsed / 1000));

        if (_mainMenu != null && _mainMenu.isOpen()) {
            _mainMenu.draw(renderer, effect);
            return;
        }

        if (_game != null) {
            double animProgress = 0;
            if (_game.isRunning()) {
                _elapsed += lastRenderInterval;
                animProgress = (1 - (double) (_nextUpdate - _elapsed) / Application.getUpdateInterval());
            }

            renderer.clear(new Color(0, 0, 0));
            _gameRenderer.onDraw(renderer, effect, animProgress);
            _lightRenderer.onDraw(renderer, -effect.getViewport().getPosX(), -effect.getViewport().getPosY());
            _particleRenderer.onDraw(renderer, -effect.getViewport().getPosX(), -effect.getViewport().getPosY());
            _gameRenderer.onDrawHUD(renderer, effect, animProgress);
            _gameInterface.onDraw(renderer, _tick, 0);
            renderer.finish();

            if (_game.isRunning()) {

                // Refresh
                if (_elapsed >= _nextRefresh) {
                    refreshGame(_refresh++);
                    _nextRefresh += Application.REFRESH_INTERVAL;
                }

                // Update
                if (_elapsed >= _nextUpdate) {
                    update(_tick++);
                    _nextUpdate += Application.getUpdateInterval();
                }

                // Long _tick
                if (_elapsed >= _nextLongUpdate) {
                    longUpdate(_frame);
                    _nextLongUpdate += Application.getLongUpdateInterval();
                }
            }
        }

        _frame++;
    }

    public void renderGame(double animProgress, int update, long renderTime, GFXRenderer renderer, RenderEffect effect) {
    }

    public void refreshConfig() {
        if (_particleRenderer != null) {
            _particleRenderer.refresh();
        }
    }

    public void refreshGame(int refreshCount) {
        if (_game != null) {
            _gameRenderer.onRefresh(refreshCount);
            _gameInterface.onRefresh(refreshCount);
        }
    }

    public void refreshMenu(int refreshCount) {
        _mainMenu.refresh(refreshCount);
    }

    public void update(int tick) {
        long time = System.currentTimeMillis();
        _game.onUpdate(tick);
        _lastUpdateDelay = System.currentTimeMillis() - time;
    }

    public void longUpdate(int longTick) {
        long time = System.currentTimeMillis();
        _gameRenderer.setFPS(longTick, _longUpdateInterval);
        _lastLongUpdateDelay = System.currentTimeMillis() - time;
        GameData.getData().reloadConfig();
    }

    public static Application getInstance() {
        return _self;
    }

    public static long getLastUpdateDelay() {
        return _lastUpdateDelay;
    }

    public static long getLastLongUpdateDelay() {
        return _lastLongUpdateDelay;
    }

    public static int getFrame() {
        return _frame;
    }
}
