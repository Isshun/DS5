package org.smallbox.faraway;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Text;
import org.jsfml.system.Clock;
import org.jsfml.window.VideoMode;
import org.jsfml.window.WindowStyle;
import org.jsfml.window.event.Event;
import org.jsfml.window.event.KeyEvent;
import org.smallbox.faraway.engine.dataLoader.CategoryLoader;
import org.smallbox.faraway.engine.dataLoader.ItemLoader;
import org.smallbox.faraway.engine.dataLoader.StringsLoader;
import org.smallbox.faraway.engine.serializer.GameLoadListener;
import org.smallbox.faraway.engine.serializer.LoadListener;
import org.smallbox.faraway.engine.ui.Colors;
import org.smallbox.faraway.engine.ui.OnClickListener;
import org.smallbox.faraway.engine.ui.View;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.manager.PathManager;
import org.smallbox.faraway.manager.SpriteManager;
import org.smallbox.faraway.model.GameData;
import org.smallbox.faraway.renderer.MainRenderer;
import org.smallbox.faraway.ui.*;

import java.io.IOException;

public class Main {

	static final int 				DRAW_INTERVAL = (1000/60);
	static final int 				UPDATE_INTERVAL = 100;
	private static final int		REFRESH_INTERVAL = 200;
	private static final int 		LONG_UPDATE_INTERVAL = 2000;

	private static final String 	SAVE_FILE = "data/saves/3.sav";
	
	private static Game				_game;
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

		final Renderer renderer = new Renderer(window);
		
		GameData data = new GameData();

		ItemLoader.load(data);
		StringsLoader.load(data, "data/strings/", "fr");
		CategoryLoader.load(data);
		
		_isFullscreen = true;
		_mainRenderer = new MainRenderer();
		_userInterface = new UserInterface();

		_loadListener = new LoadListener() {
			@Override
			public void onUpdate(String message) {
				window.clear();
				Text text = new Text(message, SpriteManager.getInstance().getFont());
				text.setCharacterSize(42);
				text.setColor(new org.jsfml.graphics.Color(Colors.LINK_INACTIVE.r, Colors.LINK_INACTIVE.g, Colors.LINK_INACTIVE.b));
				text.setPosition(Constant.WINDOW_WIDTH / 2 - message.length() * 20 / 2, Constant.WINDOW_HEIGHT / 2 - 40);
				window.draw(text);

				_userInterface.addMessage(Log.LEVEL_INFO, message);
				_userInterface.onRefresh(0);
				_userInterface.onDraw(renderer, 0, 0);

				window.display();
			}
			
		};
		
		try {
			_game = new Game(window, data);
			_game.onCreate();
			//_game.newGame(SAVE_FILE, _loadListener);
			_game.load(SAVE_FILE, _loadListener);

			_loadListener.onUpdate("Init _renderEffect");
			_mainRenderer.init(_game);
			_userInterface.onCreate(_game);

			_loadListener.onUpdate("Start game");
			loop(renderer, window);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//		//Limit the framerate
		//		window.setFramerateLimit(30);

		window.close();
		PathManager.getInstance().close();
	}

	private static void loop(final Renderer renderer, final RenderWindow window) throws IOException, InterruptedException {
		Clock timer = new Clock();
		long renderTime = 0;

		int update = 0;
		int refresh = 0;
		int frame = 0;
		long nextDraw = 0;
		long nextUpdate = 0;
		long nextRefresh = 0;
		long nextLongUpdate = 0;
		
		while (window.isOpen()) {
			// Events
			Event event = null;
			while ((event = window.pollEvent()) != null) {
				manageEvent(event, window, timer);
			}

			long elapsed = timer.getElapsedTime().asMilliseconds();

			// Sleep
			if (elapsed < nextDraw) {
				//int currentRenderTime = (int) (DRAW_INTERVAL - (nextDraw - elapsed));
				//renderTime = (renderTime * 7 + currentRenderTime) / 8;
				Thread.sleep(nextDraw - elapsed);
			}
			
			
			if (_game.isRunning()) {
				// Draw
                RenderEffect effect = new RenderEffect();
                effect.setRender(_game.getViewport().getRender());

                double animProgress = (1 - (double)(nextUpdate - elapsed) / _updateInterval);
				_mainRenderer.onDraw(renderer, effect, animProgress);
				_userInterface.onDraw(renderer, update, renderTime);

				// Refresh
				if (elapsed >= nextRefresh) {
					_mainRenderer.onRefresh(refresh);
					_userInterface.onRefresh(refresh);
					refresh++;
					nextRefresh += REFRESH_INTERVAL;
				}
				
				// Update
				if (elapsed >= nextUpdate) {
					_game.onUpdate();
					update++;
					nextUpdate += _updateInterval;
				}
				
				// Long update
				if (elapsed >= nextLongUpdate) {
					_game.onLongUpdate();
					_mainRenderer.setFPS(frame, _longUpdateInterval);
					nextLongUpdate += _longUpdateInterval;
				}
			}
			else {
				manageMenu(renderer, window);
			}

			// Draw
			window.display();
			nextDraw += DRAW_INTERVAL;
			frame++;
		}
	}
	
	private static void manageMenu(final Renderer renderer, final RenderWindow window) throws IOException, InterruptedException {
		if (_menu == null) {
			MenuGame menu = new MenuGame(new GameLoadListener() {
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
					_menu = new MenuSave(_game);
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
		_menu.draw(renderer, null);
	}

	private static void manageEvent(final Event event, final RenderWindow window, Clock timer) throws IOException {
		if (event.type == Event.Type.CLOSED) {
			window.close();

			return;
		}
		if (event.type == Event.Type.KEY_RELEASED) {
			KeyEvent keyEvent = event.asKeyEvent();

			// Events for menu
			if (_game.isRunning() == false) {
				if (_menu != null && _menu.isVisible()) {
					if (_menu.checkKey(event)) {
						return;
					}
				}
			}
			
			switch (keyEvent.key) {

			// Kill
			case F4:
				window.close();
				return;
				
			// Save
			case S:
				if (keyEvent.control) {
					_loadListener.onUpdate("saving [" + SAVE_FILE + "]");
					_game.save(SAVE_FILE);
					_loadListener.onUpdate("save done");
					return;
				}
				break;
			
			// Load
			case L:
				if (keyEvent.control) {
					_menu = new MenuLoad(new GameLoadListener() {
						@Override
						public void onLoad(String path) {
							// TODO NULL
							_game = new Game(window, null);
							_game.load(path, _loadListener);
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
					return;
				}
				break;

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
