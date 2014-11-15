package alone.in.deepspace;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import alone.in.deepspace.engine.dataLoader.CategoryLoader;
import alone.in.deepspace.engine.dataLoader.ItemLoader;
import alone.in.deepspace.engine.dataLoader.StringsLoader;
import alone.in.deepspace.engine.renderer.MainRenderer;
import alone.in.deepspace.engine.serializer.LoadListener;
import alone.in.deepspace.manager.PathManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.GameData;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.character.CharacterInfo;
import alone.in.deepspace.model.item.ItemInfo;
import alone.in.deepspace.ui.MenuBase;
import alone.in.deepspace.ui.UserInterface;
import alone.in.deepspace.util.Constant;

public class Main {
	private int[] _map;
	private int number;
	private int[] array;
	public native void init(Object[] files);

	public Main() {
		array = new int[Constant.WORLD_WIDTH * Constant.WORLD_HEIGHT];
	}

	private void onClick(int x, int y) {
		_userInterface.onEventLeftClick(x, y);
	}
	
	private void onRefresh(int refresh) {
		//_userInterface.onRefresh(refresh);
	}

	private Object getItems() {
		return ServiceManager.getWorldMap().getArrayItems();
	}

	private Object getItemInfos() {
		List<Object> objs = new ArrayList<Object>();
		objs.addAll(data.items);
		objs.add(Game.getData().characterInfo);
		return objs;
	}

	private Object getUI() {
		return _userInterface.getObjects();
	}

	private Object getUITexts() {
		return _userInterface.getObjectTexts();
	}

	private int[] getCharacters() {
		for (int i = 0; i < Constant.WORLD_WIDTH * Constant.WORLD_HEIGHT; i++) {
			array[i] = 0;
		}

		List<Character> characters = Game.getCharacterManager().getList();
		for (Character character: characters) {
			array[character.getX() * Constant.WORLD_WIDTH + character.getY()] = 61;//Game.getData().characterInfo.spriteId;
		}

		return array;
	}

	static final int 				DRAW_INTERVAL = (1000/60);
	static final int 				UPDATE_INTERVAL = 100;
	private static final int		REFRESH_INTERVAL = 200;
	private static final int 		LONG_UPDATE_INTERVAL = 2000;

	private static final String 	SAVE_FILE = "saves/3.sav";

	private static Game				_game;
	private static MenuBase			_menu;
	private static int 				_updateInterval = UPDATE_INTERVAL;
	private static int 				_longUpdateInterval = LONG_UPDATE_INTERVAL;
	private static MainRenderer 	_mainRenderer;
	private static UserInterface	_userInterface;
	private static LoadListener 	_loadListener;
	private static boolean			_isFullscreen;
	private static GameData data;

	static {
		//System.loadLibrary("DS5Render.dll");
		System.load("C:\\Users\\Alex\\workspace\\DS5Render\\Debug\\DS5Render.dll");
	}

	public static void main(String[] args) {
		//Create the window
		//		final RenderWindow window = new RenderWindow();
		//		window.create(new VideoMode(Constant.WINDOW_WIDTH, Constant.WINDOW_HEIGHT), "DS5", WindowStyle.DEFAULT);

		data = new GameData();

		ItemLoader.load(data);
		StringsLoader.load(data, "data/strings/", "fr");
		CategoryLoader.load(data);

		int i = 1;
		List<String> infos = new ArrayList<String>();

		for (ItemInfo info: data.items) {
			infos.add(info.name);
			//			infos[i] = info.fileName;
			info.spriteId = i;
			//			parameterMap.put(info.spriteId, info.fileName);
			i++;
		}
		data.characterInfo = new CharacterInfo();
		data.characterInfo.spriteId = i++;

		_isFullscreen = true;
		_mainRenderer = new MainRenderer(null);
		_userInterface = new UserInterface(null);

		_loadListener = new LoadListener() {
			@Override
			public void onUpdate(String message) {
				//				window.clear();
				//				Text text = new Text(message, SpriteManager.getInstance().getFont());
				//				text.setCharacterSize(42);
				//				text.setColor(Colors.LINK_INACTIVE);
				//				text.setPosition(Constant.WINDOW_WIDTH / 2 - message.length() * 20 / 2, Constant.WINDOW_HEIGHT / 2 - 40);
				//				window.draw(text);
				//
				//				_userInterface.addMessage(Log.LEVEL_INFO, message);
				//				_userInterface.onRefresh(0);
				//				_userInterface.onDraw(0, 0);
				//
				//				window.display();
			}

		};

		//		try {
		_game = new Game(data);
		_game.onCreate();

		//_game.newGame(SAVE_FILE, _loadListener);
		_game.load(SAVE_FILE, _loadListener);

		_loadListener.onUpdate("Init render");
		_mainRenderer.init(_game);
		_userInterface.onCreate(_game);

		_loadListener.onUpdate("Start game");

		Executor executor = Executors.newSingleThreadExecutor();
		executor.execute(new Runnable() {
			public void run() {
				int count = 0;
				while (true) {
					try {
						// Refresh UI
						if (count % 20 == 0) {
						}
						
						// Handle game
						if (count % 10 == 0) {
							_userInterface.onRefresh(count / 10);
							_game.onUpdate();
						}
						if (count % 200 == 0) {
							_game.onLongUpdate();
						}
						count++;

						// Sleep
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		Main bridge = new Main();
		bridge.init(infos.toArray());

		PathManager.getInstance().close();
	}
//
//	private static void loop(final RenderWindow window) throws IOException, InterruptedException {
//		Clock timer = new Clock();
//		long renderTime = 0;
//
//		int update = 0;
//		int refresh = 0;
//		int frame = 0;
//		long nextDraw = 0;
//		long nextUpdate = 0;
//		long nextRefresh = 0;
//		long nextLongUpdate = 0;
//
//		while (window.isOpen()) {
//			// Events
//			Event event = null;
//			while ((event = window.pollEvent()) != null) {
//				manageEvent(event, window, timer);
//			}
//
//			long elapsed = timer.getElapsedTime().asMilliseconds();
//
//			// Sleep
//			if (elapsed < nextDraw) {
//				//int currentRenderTime = (int) (DRAW_INTERVAL - (nextDraw - elapsed));
//				//renderTime = (renderTime * 7 + currentRenderTime) / 8;
//				Thread.sleep(nextDraw - elapsed);
//			}
//
//
//			if (_game.isRunning()) {
//				// Draw
//				double animProgress = (1 - (double)(nextUpdate - elapsed) / _updateInterval);
//				_mainRenderer.onDraw(window, null, animProgress);
//				_userInterface.onDraw(update, renderTime);
//
//				// Refresh
//				if (elapsed >= nextRefresh) {
//					_mainRenderer.onRefresh(refresh);
//					_userInterface.onRefresh(refresh);
//					refresh++;
//					nextRefresh += REFRESH_INTERVAL;
//				}
//
//				// Update
//				if (elapsed >= nextUpdate) {
//					_game.onUpdate();
//					update++;
//					nextUpdate += _updateInterval;
//				}
//
//				// Long update
//				if (elapsed >= nextLongUpdate) {
//					_game.onLongUpdate();
//					_mainRenderer.setFPS(frame, _longUpdateInterval);
//					nextLongUpdate += _longUpdateInterval;
//				}
//			}
//			else {
//				manageMenu(window);
//			}
//
//			// Draw
//			window.display();
//			nextDraw += DRAW_INTERVAL;
//			frame++;
//		}
//	}
//
//	private static void manageMenu(final RenderWindow window) throws IOException, InterruptedException {
//		if (_menu == null) {
//			MenuGame menu = new MenuGame(new GameLoadListener() {
//				@Override
//				public void onLoad(String path) {
//				}
//			});
//			menu.addEntry("New game", 0, new OnClickListener() {
//				@Override
//				public void onClick(View view) {
//
//				}
//			});
//			menu.addEntry("Load", 1, new OnClickListener() {
//				@Override
//				public void onClick(View view) {
//
//				}
//			});
//			menu.addEntry("Save", 2, new OnClickListener() {
//				@Override
//				public void onClick(View view) {
//					_menu = new MenuSave(_game);
//				}
//			});
//			menu.addEntry("Feedback", 3, new OnClickListener() {
//				@Override
//				public void onClick(View view) {
//
//				}
//			});
//			menu.addEntry("Exit", 4, new OnClickListener() {
//				@Override
//				public void onClick(View view) {
//					window.close();
//				}
//			});
//			_menu = menu;
//		}
//		_menu.draw(window, null);
//	}
//
//	private static void manageEvent(final Event event, final RenderWindow window, Clock timer) throws IOException {
//		if (event.type == Event.Type.CLOSED) {
//			window.close();
//
//			return;
//		}
//		if (event.type == Event.Type.KEY_RELEASED) {
//			KeyEvent keyEvent = event.asKeyEvent();
//
//			// Events for menu
//			if (_game.isRunning() == false) {
//				if (_menu != null && _menu.isVisible()) {
//					if (_menu.checkKey(event)) {
//						return;
//					}
//				}
//			}
//
//			switch (keyEvent.key) {
//
//			// Kill
//			case F4:
//				window.close();
//				return;
//
//				// Save
//			case S:
//				if (keyEvent.control) {
//					_loadListener.onUpdate("saving [" + SAVE_FILE + "]");
//					_game.save(SAVE_FILE);
//					_loadListener.onUpdate("save done");
//					return;
//				}
//				break;
//
//				// Load
//			case L:
//				if (keyEvent.control) {
//					_menu = new MenuLoad(new GameLoadListener() {
//						@Override
//						public void onLoad(String path) {
//							// TODO NULL
//							_game = new Game(null);
//							_game.load(path, _loadListener);
//						}
//					});
//					return;
//				}
//				break;
//
//				// Fullscreen
//			case RETURN:
//				if (keyEvent.alt) {
//					_isFullscreen = !_isFullscreen;
//					window.create(new VideoMode(Constant.WINDOW_WIDTH, Constant.WINDOW_HEIGHT), "DS5", _isFullscreen ? WindowStyle.NONE : WindowStyle.DEFAULT);
//					return;
//				}
//				break;
//
//			default:
//				break;
//			}
//		}
//
//		//_userInterface.onEvent(event, timer);
//	}

	public static int getUpdateInterval() {
		return _updateInterval;
	}

	public static void setUpdateInterval(int updateInterval) {
		_updateInterval = updateInterval;
	}
}
