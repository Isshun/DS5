package alone.in.deepspace;

import java.io.IOException;
import java.util.ArrayList;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.system.Clock;
import org.jsfml.system.Time;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.VideoMode;
import org.jsfml.window.event.Event;

import alone.in.deepspace.UserInterface.MenuBase;
import alone.in.deepspace.UserInterface.MenuGame;
import alone.in.deepspace.UserInterface.MenuLoad;
import alone.in.deepspace.UserInterface.MenuSave;
import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.engine.loader.CategoryLoader;
import alone.in.deepspace.engine.loader.ItemLoader;
import alone.in.deepspace.engine.loader.StringsLoader;
import alone.in.deepspace.engine.ui.OnClickListener;
import alone.in.deepspace.engine.ui.View;
import alone.in.deepspace.manager.PathManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.GameData;
import alone.in.deepspace.model.ItemInfo;

public class Main {

	static final int 				REFRESH_INTERVAL = (1000/60);
	static final int 				UPDATE_INTERVAL = 100;
	private static final int 		LONG_UPDATE_INTERVAL = 2000;
	private static Game				game;
	private static MenuBase			_menu;
	private static int 				_updateInterval = UPDATE_INTERVAL;
	private static int 				_longUpdateInterval = LONG_UPDATE_INTERVAL;

	public static void main(String[] args) {
		//Create the window
		RenderWindow window = new RenderWindow();
		window.create(new VideoMode(Constant.WINDOW_WIDTH, Constant.WINDOW_HEIGHT), "DS5");

		ServiceManager.setData(new GameData());
		
		ServiceManager.getData().items = new ArrayList<ItemInfo>();

		ItemLoader.load();
		
		StringsLoader.load("data/strings/", "fr");
		
		CategoryLoader.load();
		
		try {
			game = new Game(window);
			game.load("saves/2.sav");
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

		Time last_refresh = display_timer.getElapsedTime();
		Time last_update = display_timer.getElapsedTime();
		Time last_long_update = display_timer.getElapsedTime();

		while (window.isOpen()) {
			timer.restart();

			// Events
			Event event = null;
			while ((event = window.pollEvent()) != null) {
				if (event.type == Event.Type.CLOSED) {
					window.close();

					return;
				}
				if (event.type == Event.Type.KEY_RELEASED) {

					// Events for menu
					if (game.isRunning() == false) {
						if (_menu != null && _menu.isVisible()) {
							if (_menu.checkKey(event)) {
								continue;
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
									game = new Game(window);
									game.load(path);
								} catch (IOException | TextureCreationException e) {
									e.printStackTrace();
								}
							}
						});
						continue;
					}
	
					if (event.asKeyEvent().key == Key.DOWN) {
						if (_menu != null) {
							_menu.onKeyDown();
						}
						continue;
					}
	
					if (event.asKeyEvent().key == Key.UP) {
						if (_menu != null) {
							_menu.onKeyUp();
						}
						continue;
					}
	
					if (event.asKeyEvent().key == Key.RETURN) {
						if (_menu != null) {
							_menu.onKeyEnter();
						}
						continue;
					}
				}

				game.onEvent(event);
			}

			Time elapsed = display_timer.getElapsedTime();

			long nextRefresh = last_refresh.asMilliseconds() + REFRESH_INTERVAL - elapsed.asMilliseconds();

			// Sleep
			if (nextRefresh > 0) {
				int currentRenderTime = (int) (elapsed.asMilliseconds() - last_refresh.asMilliseconds());
				renderTime = (renderTime * 7 + currentRenderTime) / 8;
				Thread.sleep(nextRefresh);
			}
			
			long nextUpdate = last_update.asMilliseconds() + _updateInterval - elapsed.asMilliseconds();
			long nextLongUpdate = last_long_update.asMilliseconds() + _longUpdateInterval - elapsed.asMilliseconds();

			// Refresh
			last_refresh = elapsed;
			if (game.isRunning()) {
				// Draw
				double animProgress = (1 - (double)nextUpdate / _updateInterval);
				game.onDraw(animProgress, renderTime);
				
				// Update
				if (nextUpdate <= 0) {
					last_update = elapsed;
					game.onUpdate();
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
				_menu.refresh(window, null);
			}
			window.display();
		}
	}
	
	public static int getUpdateInterval() {
		return _updateInterval;
	}

	public static void setUpdateInterval(int updateInterval) {
		_updateInterval = updateInterval;
	}
}
