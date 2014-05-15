package alone.in.deepspace;
import java.io.IOException;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.system.Clock;
import org.jsfml.system.Time;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.VideoMode;
import org.jsfml.window.event.Event;

import alone.in.deepspace.Character.ServiceManager;
import alone.in.deepspace.Engine.MainRenderer;
import alone.in.deepspace.UserInterface.MenuBase;
import alone.in.deepspace.UserInterface.MenuLoad;
import alone.in.deepspace.Utils.Constant;


public class Main {

	static final int 				REFRESH_INTERVAL = (1000/60);
	static final int 				UPDATE_INTERVAL = 100;
	private static Game				game;

	public static void main(String[] args) {
		//Create the window
		RenderWindow window = new RenderWindow();
		window.create(new VideoMode(Constant.WINDOW_WIDTH, Constant.WINDOW_HEIGHT), "DS5");

		MainRenderer.getInstance().setWindow(window);

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
	}

	private static void loop(final RenderWindow window) throws IOException, InterruptedException {
		// fixme: actuellement update et refresh se partage les meme timers
		Clock display_timer = new Clock();
		Clock timer = new Clock();
		int renderTime = 0;
		
		MenuBase menu = null;

		Time last_refresh = display_timer.getElapsedTime();
		Time last_update = display_timer.getElapsedTime();

		while (window.isOpen()) {
			timer.restart();

			// Events
			Event event = null;
			while ((event = window.pollEvent()) != null) {
				if (event.type == Event.Type.CLOSED) {
					return;
				}
				if (event.type == Event.Type.KEY_RELEASED) {
					if (event.asKeyEvent().key == Key.ESCAPE) {
						return;
					}
					if (event.asKeyEvent().control && event.asKeyEvent().key == Key.S) {
						game.save("saves/2.sav");
					}
					if (event.asKeyEvent().control && event.asKeyEvent().key == Key.L) {
						menu = new MenuLoad(new OnLoadListener() {
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
						if (menu != null) {
							menu.onKeyDown();
						}
						continue;
					}
	
					if (event.asKeyEvent().key == Key.UP) {
						if (menu != null) {
							menu.onKeyUp();
						}
						continue;
					}
	
					if (event.asKeyEvent().key == Key.RETURN) {
						if (menu != null) {
							menu.onKeyEnter();
						}
						continue;
					}
				}

				game.onEvent(event);
			}

			Time elapsed = display_timer.getElapsedTime();

			long nextUpdate = last_update.asMilliseconds() + UPDATE_INTERVAL - elapsed.asMilliseconds();
			long nextRefresh = last_refresh.asMilliseconds() + REFRESH_INTERVAL - elapsed.asMilliseconds();

			// Refresh
			if (nextRefresh <= 0) {
				//_renderTime = (int) (elapsed.asMilliseconds() - _last_refresh.asMilliseconds());
				last_refresh = elapsed;
				double animProgress = 1 - (double)nextUpdate / UPDATE_INTERVAL;
				game.onRefresh(animProgress, renderTime);
				if (menu != null) {
					menu.refresh(window);
				}
				window.display();
			} else {
				int currentRenderTime = (int) (elapsed.asMilliseconds() - last_refresh.asMilliseconds());
				renderTime = (renderTime * 7 + currentRenderTime) / 8;
				Thread.sleep(nextRefresh);
			}

			// Update
			if (nextUpdate <= 0) {
				last_update = elapsed;
				game.onUpdate();
			}
		}
	}

}
