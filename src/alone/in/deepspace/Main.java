package alone.in.deepspace;

import java.io.IOException;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.system.Clock;
import org.jsfml.system.Time;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.VideoMode;
import org.jsfml.window.WindowStyle;
import org.jsfml.window.event.Event;

import alone.in.deepspace.engine.loader.CategoryLoader;
import alone.in.deepspace.engine.loader.ItemLoader;
import alone.in.deepspace.engine.loader.StringsLoader;
import alone.in.deepspace.engine.renderer.MainRenderer;
import alone.in.deepspace.engine.ui.OnClickListener;
import alone.in.deepspace.engine.ui.View;
import alone.in.deepspace.manager.PathManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.GameData;
import alone.in.deepspace.ui.MenuBase;
import alone.in.deepspace.ui.MenuGame;
import alone.in.deepspace.ui.MenuLoad;
import alone.in.deepspace.ui.MenuSave;
import alone.in.deepspace.ui.UserInterface;
import alone.in.deepspace.util.Constant;

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
	private static UserInterface _userInterface;

	public static void main(String[] args) {
		//Create the window
		RenderWindow window = new RenderWindow();
		window.create(new VideoMode(Constant.WINDOW_WIDTH, Constant.WINDOW_HEIGHT), "DS5", WindowStyle.DEFAULT);

		GameData data = new GameData();

		ItemLoader.load(data);
		StringsLoader.load(data, "data/strings/", "fr");
		CategoryLoader.load(data);
		
		_mainRenderer = new MainRenderer(window);
		_userInterface = UserInterface.getInstance();

		try {
			game = new Game(window, data);
			game.onCreate();
			game.load("saves/2.sav");
			_mainRenderer.init(game);
			_userInterface.onCreate(game, window, game.getViewport());
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

			// Events for menu
			if (game.isRunning() == false) {
				if (_menu != null && _menu.isVisible()) {
					if (_menu.checkKey(event)) {
						return;
					}
				}
			}
			
			if (event.asKeyEvent().key == Key.K) {
				window.close();

				return;
			}
			if (event.asKeyEvent().control && event.asKeyEvent().key == Key.S) {
				game.save("saves/2.sav");
			}
			if (event.asKeyEvent().control && event.asKeyEvent().key == Key.L) {
				_menu = new MenuLoad(new OnLoadListener() {
					@Override
					public void onLoad(String path) {
						try {
							ServiceManager.reset();
							// TODO NULL
							game = new Game(window, null);
							game.load(path);
						} catch (IOException | TextureCreationException e) {
							e.printStackTrace();
						}
					}
				});
				return;
			}

			if (event.asKeyEvent().key == Key.DOWN) {
				if (_menu != null) {
					_menu.onKeyDown();
				}
				return;
			}

			if (event.asKeyEvent().key == Key.UP) {
				if (_menu != null) {
					_menu.onKeyUp();
				}
				return;
			}

			if (event.asKeyEvent().key == Key.RETURN) {
				if (_menu != null) {
					_menu.onKeyEnter();
				}
				return;
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
