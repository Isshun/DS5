package alone.in.deepspace;

import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Text;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.system.Clock;
import org.jsfml.system.Time;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.VideoMode;
import org.jsfml.window.WindowStyle;
import org.jsfml.window.event.Event;
import org.jsfml.window.event.KeyEvent;

import alone.in.deepspace.engine.loader.CategoryLoader;
import alone.in.deepspace.engine.loader.ItemLoader;
import alone.in.deepspace.engine.loader.StringsLoader;
import alone.in.deepspace.engine.renderer.MainRenderer;
import alone.in.deepspace.engine.ui.Colors;
import alone.in.deepspace.engine.ui.OnClickListener;
import alone.in.deepspace.engine.ui.View;
import alone.in.deepspace.manager.PathManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.manager.SpriteManager;
import alone.in.deepspace.model.GameData;
import alone.in.deepspace.ui.MenuBase;
import alone.in.deepspace.ui.MenuGame;
import alone.in.deepspace.ui.MenuLoad;
import alone.in.deepspace.ui.MenuSave;
import alone.in.deepspace.ui.UserInterface;
import alone.in.deepspace.util.Constant;
import alone.in.deepspace.util.Log;

public class Main {

	static final int 				DRAW_INTERVAL = (1000/60);
	static final int 				UPDATE_INTERVAL = 100;
	private static final int		REFRESH_INTERVAL = 200;
	private static final int 		LONG_UPDATE_INTERVAL = 2000;
	private static Game				game;
	private static MenuBase			_menu;
	private static int 				_updateInterval = UPDATE_INTERVAL;
	private static int 				_longUpdateInterval = LONG_UPDATE_INTERVAL;
	private static MainRenderer 	_mainRenderer;
	private static UserInterface	_userInterface;
	private static LoadListener 	_loadListener;
	private static boolean			_isFullscreen;

	public static void main(String[] args) {
		//Create the window
		final RenderWindow window = new RenderWindow();
		window.create(new VideoMode(Constant.WINDOW_WIDTH, Constant.WINDOW_HEIGHT), "DS5", WindowStyle.DEFAULT);
		
		GameData data = new GameData();

		ItemLoader.load(data);
		StringsLoader.load(data, "data/strings/", "fr");
		CategoryLoader.load(data);
		
		_mainRenderer = new MainRenderer(window);
		_userInterface = new UserInterface(window);

		_loadListener = new LoadListener() {
			@Override
			public void onUpdate(String message) {
				window.clear();
				Text text = new Text(message, SpriteManager.getInstance().getFont());
				text.setCharacterSize(42);
				text.setColor(Colors.LINK_INACTIVE);
				text.setPosition(Constant.WINDOW_WIDTH / 2 - message.length() * 20 / 2, Constant.WINDOW_HEIGHT / 2 - 40);
				window.draw(text);

				_userInterface.addMessage(Log.LEVEL_INFO, message);
				_userInterface.onRefresh(0);
				_userInterface.onDraw(0, 0);

				window.display();
			}
			
		};
		
		try {
			game = new Game(window, data);
			game.onCreate();
			game.load("saves/2.sav", _loadListener);
			_mainRenderer.init(game);
			_userInterface.onCreate(game, game.getViewport());

			loop(window);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (TextureCreationException e) {
			e.printStackTrace();
		}

		//		//Limit the framerate
		//		window.setFramerateLimit(30);

		window.close();
		
		PathManager.getInstance().close();
	}

	private static void loop(final RenderWindow window) throws IOException, InterruptedException {
		// fixme: actuellement update et refresh se partage les meme timers
		Clock display_timer = new Clock();
		Clock timer = new Clock();
		int renderTime = 0;

		Time last_draw = display_timer.getElapsedTime();
		Time last_refresh = display_timer.getElapsedTime();
		Time last_update = display_timer.getElapsedTime();
		Time last_long_update = display_timer.getElapsedTime();

		int update = 0;
		int refresh = 0;
		
		while (window.isOpen()) {
			// Events
			Event event = null;
			while ((event = window.pollEvent()) != null) {
				manageEvent(event, window, timer);
			}

			Time elapsed = display_timer.getElapsedTime();
			long elapsedMs = elapsed.asMilliseconds();
			long nextDraw = last_draw.asMilliseconds() + DRAW_INTERVAL - elapsedMs;

			// Sleep
			if (nextDraw > 0) {
				int currentRenderTime = (int) (elapsedMs - last_draw.asMilliseconds());
				renderTime = (renderTime * 7 + currentRenderTime) / 8;
				Thread.sleep(nextDraw);
			}
			
			long nextUpdate = last_update.asMilliseconds() + _updateInterval - elapsedMs;
			long nextRefresh = last_refresh.asMilliseconds() + REFRESH_INTERVAL - elapsedMs;
			long nextLongUpdate = last_long_update.asMilliseconds() + _longUpdateInterval - elapsedMs;

			// Refresh
			last_draw = elapsed;
			if (game.isRunning()) {
				// Draw
				double animProgress = (1 - (double)nextUpdate / _updateInterval);
				_mainRenderer.draw(window, animProgress, renderTime);
				_userInterface.onDraw(update, renderTime);

				// Refresh
				if (nextRefresh <= 0) {
					last_refresh = elapsed;
					_mainRenderer.refresh(refresh);
					_userInterface.onRefresh(refresh);
					refresh++;
				}
				
				// Update
				if (nextUpdate <= 0) {
					last_update = elapsed;
					game.onUpdate();
					update++;
				}
				
				// Long update
				if (nextLongUpdate <= 0) {
					last_long_update = elapsed;
					game.onLongUpdate();
				}
			}
			else {
				if (_menu == null) {
					MenuGame menu = new MenuGame(new OnLoadListener() {
						@Override
						public void onLoad(String path) {
						}
					});
					menu.addEntry("New game", 0, new OnClickListener() {
						@Override
						public void onClick(View view) {
							
						}
					});
					menu.addEntry("Load", 1, new OnClickListener() {
						@Override
						public void onClick(View view) {
							
						}
					});
					menu.addEntry("Save", 2, new OnClickListener() {
						@Override
						public void onClick(View view) {
							_menu = new MenuSave(game);
						}
					});
					menu.addEntry("Feedback", 3, new OnClickListener() {
						@Override
						public void onClick(View view) {
							
						}
					});
					menu.addEntry("Exit", 4, new OnClickListener() {
						@Override
						public void onClick(View view) {
							window.close();
						}
					});
					_menu = menu;
				}
				_menu.draw(window, null);
			}
			window.display();
		}
	}
	
	private static void manageEvent(final Event event, final RenderWindow window, Clock timer) throws IOException {
		if (event.type == Event.Type.CLOSED) {
			window.close();

			return;
		}
		if (event.type == Event.Type.KEY_RELEASED) {
			KeyEvent keyEvent = event.asKeyEvent();

			// Events for menu
			if (game.isRunning() == false) {
				if (_menu != null && _menu.isVisible()) {
					if (_menu.checkKey(event)) {
						return;
					}
				}
			}
			
			switch (keyEvent.key) {

			// Kill
			case K:
				window.close();
				return;
				
			// Save
			case S:
				if (keyEvent.control) {
					game.save("saves/2.sav");
				}
				break;
			
			// Load
			case L:
				if (keyEvent.control) {
					_menu = new MenuLoad(new OnLoadListener() {
						@Override
						public void onLoad(String path) {
							try {
								ServiceManager.reset();
								// TODO NULL
								game = new Game(window, null);
								game.load(path, _loadListener);
							} catch (IOException | TextureCreationException e) {
								e.printStackTrace();
							}
						}
					});
					return;
				}
				break;
				
				// Fullscreen
			case RETURN:
				if (keyEvent.alt) {
					_isFullscreen = !_isFullscreen;
					window.create(new VideoMode(Constant.WINDOW_WIDTH, Constant.WINDOW_HEIGHT), "DS5", _isFullscreen ? WindowStyle.NONE : WindowStyle.DEFAULT);
				}

			default:
				break;
			}
		}

		_userInterface.onEvent(event, timer);
	}

	public static int getUpdateInterval() {
		return _updateInterval;
	}

	public static void setUpdateInterval(int updateInterval) {
		_updateInterval = updateInterval;
	}
}
