package org.smallbox.faraway;

import org.smallbox.faraway.engine.dataLoader.CategoryLoader;
import org.smallbox.faraway.engine.dataLoader.ItemLoader;
import org.smallbox.faraway.engine.dataLoader.StringsLoader;
import org.smallbox.faraway.engine.serializer.LoadListener;
import org.smallbox.faraway.engine.ui.Colors;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.manager.SpriteManager;
import org.smallbox.faraway.model.GameData;
import org.smallbox.faraway.engine.renderer.MainRenderer;
import org.smallbox.faraway.ui.*;
import org.smallbox.faraway.ui.panel.LayoutFactory;

import java.io.IOException;

public class Application implements GameEventListener {
	public static final int 		DRAW_INTERVAL = (1000/60);
    public static final int 		UPDATE_INTERVAL = 100;
    public static final int		    REFRESH_INTERVAL = 200;
    public static final int 		LONG_UPDATE_INTERVAL = 2000;
    public static final String 	    SAVE_FILE = "data/saves/3.sav";
	
	private static Game				_game;
	private static MenuBase			_menu;
	private static int 				_updateInterval = UPDATE_INTERVAL;
    private static int 				_longUpdateInterval = LONG_UPDATE_INTERVAL;
	private static MainRenderer 	_mainRenderer;
	private static UserInterface	_userInterface;
	private static LoadListener 	_loadListener;
	private static boolean			_isFullscreen;
    private GFXRenderer             _renderer;

    public Application(GFXRenderer renderer) {
        _loadListener = message -> {
            renderer.clear();
            TextView text = SpriteManager.getInstance().createTextView();
            text.setString(message);
            text.setCharacterSize(42);
            text.setColor(Colors.LINK_INACTIVE);
            text.setPosition(Constant.WINDOW_WIDTH / 2 - message.length() * 20 / 2, Constant.WINDOW_HEIGHT / 2 - 40);
            text.draw(renderer, null);

            if (_userInterface != null) {
                _userInterface.addMessage(Log.LEVEL_INFO, message);
                _userInterface.onRefresh(0);
                _userInterface.onDraw(renderer, 0, 0);
            }

            renderer.display();
        };
    }

	public void create(GFXRenderer renderer) {
		_renderer = renderer;
		_isFullscreen = true;
		_mainRenderer = new MainRenderer();
		_userInterface = new UserInterface(new LayoutFactory());
	}

    public GameData loadResources() {
        GameData data = new GameData();

        ItemLoader.load(data);
        StringsLoader.load(data, "data/strings/", "fr");
        CategoryLoader.load(data);

        return data;
    }

	public static void manageMenu(final GFXRenderer renderer) throws IOException, InterruptedException {
		if (_menu == null) {
			MenuGame menu = new MenuGame(path -> {
            });
			menu.addEntry("New game", 0, view -> {
            });
			menu.addEntry("Load", 1, view -> {
            });
			menu.addEntry("Save", 2, view -> _menu = new MenuSave(_game));
			menu.addEntry("Feedback", 3, view -> {
            });
			menu.addEntry("Exit", 4, view -> renderer.close());
			_menu = menu;
		}
		_menu.draw(renderer, null);
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
        if (_game.isRunning() == false) {
            if (_menu != null && _menu.isVisible()) {
                if (_menu.checkKey(key)) {
                    return;
                }
            }
        }

        switch (key) {
            // Reload UI
            case F5:
                _userInterface.reload();
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

        _userInterface.onKeyEvent(timer, action, key, modifier);
    }

    @Override
    public void onWindowEvent(GameTimer timer, Action action) {
        _userInterface.onWindowEvent(timer, action);
    }

    @Override
    public void onMouseEvent(GameTimer timer, Action action, MouseButton button, int x, int y) {
        if (x > 0 && x < _renderer.getWidth() && y > 0 && y < _renderer.getHeight()) {
            _userInterface.onMouseEvent(timer, action, button, x, y);
        }
    }

    public LoadListener getLoadListener() {
        return _loadListener;
    }

    public void startGame(Game game) {
        _game = game;
        _mainRenderer.init(_game);
        _userInterface.onCreate(_game);
    }

    public void render(double animProgress, int update, long renderTime, GFXRenderer renderer, RenderEffect effect) {
        _mainRenderer.onDraw(renderer, effect, animProgress);
        _userInterface.onDraw(renderer, update, renderTime);
    }

    public void refresh(int refreshCount) {
        _mainRenderer.onRefresh(refreshCount);
        _userInterface.onRefresh(refreshCount);
    }

    public void update(int updateCount) {
        _game.onUpdate();
    }

    public void longUpdate(int frame) {
        _game.onLongUpdate();
        _mainRenderer.setFPS(frame, _longUpdateInterval);
    }
}
