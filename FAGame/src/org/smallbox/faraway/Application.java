package org.smallbox.faraway;

import org.smallbox.faraway.engine.dataLoader.CategoryLoader;
import org.smallbox.faraway.engine.dataLoader.ItemLoader;
import org.smallbox.faraway.engine.dataLoader.StringsLoader;
import org.smallbox.faraway.engine.renderer.MainRenderer;
import org.smallbox.faraway.engine.serializer.LoadListener;
import org.smallbox.faraway.engine.ui.Colors;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.engine.ui.ViewFactory;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.loader.PlanetLoader;
import org.smallbox.faraway.manager.SpriteManager;
import org.smallbox.faraway.model.GameData;
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
    public static final String 	    SAVE_FILE = "data/saves/3.sav";
	
	private static Game				_game;
	private static MenuBase			_menu;
	private static int 				_updateInterval = UPDATE_INTERVAL;
    private static int 				_longUpdateInterval = LONG_UPDATE_INTERVAL;
	private static MainRenderer _gameRenderer;
	private static UserInterface _gameInterface;
	private static LoadListener 	_loadListener;
	private static boolean			_isFullscreen;
    private static GFXRenderer      _renderer;
    private static GameData         _data;
    private static Application      _self;
    private MainMenu                _mainMenu;
    private static long             _lastUpdateDelay;
    private static long             _lastLongUpdateDelay;
    private static int              _frame;

    public Application(GFXRenderer renderer) {
        _self = this;
        _renderer = renderer;
        _loadListener = message -> {
            renderer.clear();
            TextView text = SpriteManager.getInstance().createTextView();
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

	public void create(GFXRenderer renderer, GameData data) {
        _data = data;
		_renderer = renderer;
		_isFullscreen = true;
		_gameRenderer = new MainRenderer();
		_gameInterface = new UserInterface(new LayoutFactory(), ViewFactory.getInstance());
        //_mainMenu = new MainMenu(new LayoutFactory(), ViewFactory.getInstance(), renderer);
    }

    public GameData loadResources() {
        GameData data = new GameData();

        ItemLoader.load(data);
        PlanetLoader.load(data);
        StringsLoader.load(data, "data/strings/", "fr");
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
            case SPACE:
                _game.togglePaused();
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
                    _loadListener.onUpdate("saving [" + SAVE_FILE + "]");
                    _game.save(SAVE_FILE);
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
                            _game = new Game(null);
                            _game.load(path, _loadListener);
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
    public void onMouseEvent(GameTimer timer, Action action, MouseButton button, int x, int y) {
        if (_game != null) {
            _gameInterface.onMouseEvent(timer, action, button, x, y);
        } else {
            _mainMenu.onMouseEvent(timer, action, button, x, y);
        }
    }

    public LoadListener getLoadListener() {
        return _loadListener;
    }

    public void newGame() {
        _game = new Game(_data);
        _game.onCreate();
        _game.newGame(null, _loadListener);
        _gameRenderer.init(_game);
        _gameInterface.onCreate(_game);
    }

    public void loadGame() {
        _game = new Game(_data);
        _game.onCreate();

        _loadListener.onUpdate("Load save");
        _game.load(Application.SAVE_FILE, _loadListener);

        _loadListener.onUpdate("Start game");
        _gameRenderer.init(_game);
        _gameInterface.onCreate(_game);
    }

    public void renderMenu(final GFXRenderer renderer, RenderEffect effect) throws IOException, InterruptedException {
        _mainMenu.draw(renderer, effect);

//		if (_menu == null) {
//			MenuGame menu = new MenuGame(path -> {
//            });
//			menu.addEntry("New game", 0, view -> {
//            });
//			menu.addEntry("Load", 1, view -> {
//            });
//			menu.addEntry("Save", 2, view -> _menu = new MenuSave(_game));
//			menu.addEntry("Feedback", 3, view -> {
//            });
//			menu.addEntry("Exit", 4, view -> renderer.close());
//			_menu = menu;
//		}
//		_menu.draw(renderer, null);
    }

    public void renderGame(double animProgress, int update, long renderTime, GFXRenderer renderer, RenderEffect effect) {
        _frame++;
        if (_game != null) {
            _gameRenderer.onDraw(renderer, effect, animProgress);
            _gameInterface.onDraw(renderer, update, renderTime);
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

    public void update(int updateCount) {
        long time = System.currentTimeMillis();
        _game.onUpdate();
        _lastUpdateDelay = System.currentTimeMillis() - time;
    }

    public void longUpdate(int frame) {
        long time = System.currentTimeMillis();
        _game.onLongUpdate();
        _gameRenderer.setFPS(frame, _longUpdateInterval);
        _lastLongUpdateDelay = System.currentTimeMillis() - time;
    }

    public static int getWindowWidth() {
        return _renderer.getWidth();
    }

    public static int getWindowHeight() {
        return _renderer.getHeight();
    }

    public static GameData getData() {
        return _data;
    }

    public static Application getInstance() {
        return _self;
    }

    public Game getGame() {
        return _game;
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
