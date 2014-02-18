package alone.in.DeepSpace;

import java.io.File;
import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.Transform;
import org.jsfml.system.Clock;
import org.jsfml.system.Time;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.event.Event;

import alone.in.DeepSpace.Managers.CharacterManager;
import alone.in.DeepSpace.Managers.ResourceManager;
import alone.in.DeepSpace.Managers.SpriteManager;
import alone.in.DeepSpace.Models.BaseItem;
import alone.in.DeepSpace.Models.Room;
import alone.in.DeepSpace.UserInterface.UserInterface;
import alone.in.DeepSpace.Utils.Constant;
import alone.in.DeepSpace.Utils.Log;
import alone.in.DeepSpace.World.WorldArea;
import alone.in.DeepSpace.World.WorldMap;
import alone.in.DeepSpace.World.WorldRenderer;

public class Game {

	static final int REFRESH_INTERVAL = (1000/60);
	static final int UPDATE_INTERVAL = 100;
	public static int renderTime;
	private int _seed;
	private int _renderTime;
	private RenderWindow _app;
	private int _lastInput;
	private int _frame;
	private Viewport _viewport;
	private UserInterface _ui;
	private SpriteManager _spriteManager;
	private WorldRenderer _worldRenderer;
	private int _update;
	private CharacterManager _characterManager;
	private Texture _backgroundTexture;
	private Sprite _background;
	private boolean _run;
	private Time _last_refresh;
	private Time _last_update;

	public Game(RenderWindow app) throws IOException {
	  Log.debug("Game");

	  _seed = 42;
	  _renderTime = 0;

	  _app = app;
	  _lastInput = 0;
	  _frame = 0;
	  _viewport = new Viewport(app);
	  _ui = new UserInterface(app, _viewport);

	  _spriteManager = SpriteManager.getInstance();
	  _worldRenderer = new WorldRenderer(app, _spriteManager, _ui);

	  _update = 0;
	  _characterManager = CharacterManager.getInstance();

	  // Background
	  Log.debug("Game background");
	  _backgroundTexture = new Texture();
	  _backgroundTexture.loadFromFile((new File("res/background.png")).toPath());
	  _background = new Sprite();
	  _background.setTexture(_backgroundTexture);
	  _background.setTextureRect(new IntRect(0, 0, 1920, 1080));

	  app.setKeyRepeatEnabled(true);
	  
	  for (int i = 0; i < 100; i++) {
		  CharacterManager.getInstance().add(i, 0);
	  }

	  Log.info("Game:\tdone");
	}

	void	update() {
	  WorldMap.getInstance().update();

	  // Update item
	  int w = WorldMap.getInstance().getWidth();
	  int h = WorldMap.getInstance().getHeight();

	  for (int i = 0; i < w; i++) {
		for (int j = 0; j < h; j++) {


		  // Update oxygen
		  if (_frame % 6 == 0) {
			WorldArea area = WorldMap.getInstance().getArea(i, j);
			if (area != null && area.isType(BaseItem.Type.STRUCTURE_FLOOR)) {
			  int oxygen = area.getOxygen();
			  int count = 1;

			  WorldArea a1 = WorldMap.getInstance().getArea(i+1, j);
			  if (a1 == null) {
				count++;
			  }
			  else if (a1.isType(BaseItem.Type.STRUCTURE_FLOOR)) {
				oxygen += a1.getOxygen();
				count++;
			  }

			  WorldArea a2 = WorldMap.getInstance().getArea(i-1, j);
			  if (a2 == null) {
				count++;
			  }
			  else if (a2.isType(BaseItem.Type.STRUCTURE_FLOOR)) {
				oxygen += a2.getOxygen();
				count++;
			  }

			  WorldArea a3 = WorldMap.getInstance().getArea(i, j+1);
			  if (a3 == null) {
				count++;
			  }
			  else if (a3.isType(BaseItem.Type.STRUCTURE_FLOOR)) {
				oxygen += a3.getOxygen();
				count++;
			  }

			  WorldArea a4 = WorldMap.getInstance().getArea(i, j-1);
			  if (a4 == null) {
				count++;
			  }
			  else if (a4.isType(BaseItem.Type.STRUCTURE_FLOOR)) {
				oxygen += a4.getOxygen();
				count++;
			  }

			  int value = (int) Math.ceil((double)oxygen / count);

			  // if (a1 != null && a1.isType(BaseItem.Type.STRUCTURE_FLOOR)) {
			  // 	a1.setOxygen(value);
			  // }

			  // if (a2 != null && a2.isType(BaseItem.Type.STRUCTURE_FLOOR)) {
			  // 	a2.setOxygen(value);
			  // }

			  // if (a3 != null && a3.isType(BaseItem.Type.STRUCTURE_FLOOR)) {
			  // 	a3.setOxygen(value);
			  // }

			  // if (a4 != null && a4.isType(BaseItem.Type.STRUCTURE_FLOOR)) {
			  // 	a4.setOxygen(value);
			  // }

			  area.setOxygen(value);
			}
		  }



		  BaseItem item = WorldMap.getInstance().getItem(i, j);
		  if (item != null) {
			  
			// Check zone match
			if (!item.isZoneMatch() && item.getZoneId() == 0) {
			  Room room = WorldMap.getInstance().getRoom(item.getRoomId());
			  if (room != null) {
				room.setZoneId(item.getZoneIdRequired());
			  }
			}
		  }
		}
	  }


	  // assign works
		// if (_update % 10 == 0) {
		//   WorldMap.getInstance().reloadAborted();
		// }

		// int jobsCount = JobManager.getInstance().getCount();
		// if (jobsCount > 0) {
		//   Job job = JobManager.getInstance().getJob();
		//   if (job != null && _characterManager.assignJob(job) == null) {
		// 	JobManager.getInstance().abort(job);
		//   }
		// }

		_characterManager.assignJobs();

		// Character character = null;
		// int charactersCount = _characterManager.getCount();
		// for (int i = 0; (character = _characterManager.getInactive()) != null) {
		//   if (character != null) {
		// 	Job job = JobManager.getInstance().getJob(character);
		// 	if (job != null) {
		// 	  character.setJob(job);
		// 	}
		//   }
		// }

		// int length = WorldMap.getInstance().getBuildListSize();
		// if (length > 0
		// 	&& (character = _characterManager.getUnemployed(Character.PROFESSION_ENGINEER)) != null
		// 	&& (item = WorldMap.getInstance().getItemToBuild()) != null) {

		//   Debug() + "Game: search path from char (x: " + character.getX() + ", y: " + character.getY() + ")";
		//   Debug() + "Game: search path to item (x: " + item.getX() + ", y: " + item.getY() + ")";

		//   character.setBuild(item);
		// }

	  // Character
	  _characterManager.update(_update);

	  _update++;
	}

	void	refresh(double animProgress) throws IOException {
	  // Flush
	  _app.clear(new Color(0, 0, 50));
	  _frame++;

	  // Draw scene
	  draw_surface();

	  Transform transform = new Transform();
	  transform = _viewport.getViewTransform(transform);
	  RenderStates render = new RenderStates(transform);
	  
	  _characterManager.refresh(_app, render, animProgress);

	  // User interface
	  _ui.refresh(_frame, _update, _renderTime);

	  //TODO
	  //srand(_seed + _frame++);
	}

	void	draw_surface() {
	  // Background
	  Transform transform2 = new Transform();
	  RenderStates render2 = new RenderStates(_viewport.getViewTransformBackground(transform2));
	  _app.draw(_background, render2);

	  // Render transformation for viewport
	  Transform transform = new Transform();
	  RenderStates render = new RenderStates(_viewport.getViewTransform(transform));
	  _worldRenderer.draw(render);
	}

	void	loop() throws IOException, InterruptedException {
		// fixme: actuellement update et refresh se partage les meme timers
		Clock display_timer = new Clock();
		Clock action_timer;
		Clock pnj_timer;
		Clock timer = new Clock();

		_run = true;
		_last_refresh = display_timer.getElapsedTime();
		_last_update = display_timer.getElapsedTime();

		while (_run && _app.isOpen()) {
			timer.restart();

			// Events
			Event event = null;
			while ((event = _app.pollEvent()) != null) {
				if (event.type == Event.Type.MOUSE_MOVED) {
					_ui.mouseMoved(event.asMouseEvent().position.x, event.asMouseEvent().position.y);
				}

				if (event.type == Event.Type.MOUSE_BUTTON_PRESSED) {
					_ui.mousePress(event.asMouseButtonEvent().button, event.asMouseButtonEvent().position.x, event.asMouseButtonEvent().position.y);
				}

				if (event.type == Event.Type.MOUSE_BUTTON_RELEASED) {
					_ui.mouseRelease(event.asMouseButtonEvent().button, event.asMouseButtonEvent().position.x, event.asMouseButtonEvent().position.y);
				}

				if (event.type == Event.Type.MOUSE_WHEEL_MOVED) {
					_ui.mouseWheel(event.asMouseWheelEvent().delta, event.asMouseWheelEvent().position.x, event.asMouseWheelEvent().position.y);
				}

				// Check key code
				if (event.type == Event.Type.KEY_RELEASED) {

					if (event.asKeyEvent().control && event.asKeyEvent().key == Key.S) {
						save("saves/2.sav");
						continue;
					}
					
					// If not consumes by UI
					if (_ui.checkKeyboard(event, _frame, _lastInput) == false) {
						Log.info("Game: suspend");

					if (event.asKeyEvent().key == Keyboard.Key.ESCAPE) {
						_run = false;
						Log.info("Game: suspend");
						}
					}
				}

			  if (event.type == Event.Type.CLOSED) {
					_app.setKeyRepeatEnabled(false);
					_app.close();
					Log.info("Bye");
				  }

				  // if (this.event.type == Event.KeyReleased &&
				  // 	  this.event.key.code == Keyboard.Escape) {
					
				  // }

				  if (event.type == Event.Type.KEY_RELEASED && event.asKeyEvent().key == Keyboard.Key.K) {
					_run = false;
					// _app.setKeyRepeatEnabled(false);
					Log.info("Bye");
				  }
			}

			Time elapsed = display_timer.getElapsedTime();

			long nextUpdate = _last_update.asMilliseconds() + UPDATE_INTERVAL - elapsed.asMilliseconds();
			long nextRefresh = _last_refresh.asMilliseconds() + REFRESH_INTERVAL - elapsed.asMilliseconds();

			// Refresh
			if (nextRefresh <= 0) {
			  //_renderTime = (int) (elapsed.asMilliseconds() - _last_refresh.asMilliseconds());
			  _last_refresh = elapsed;
			  double animProgress = 1 - (double)nextUpdate / UPDATE_INTERVAL;
			  refresh(animProgress);
			  _app.display();
			} else {
				int currentRenderTime = (int) (elapsed.asMilliseconds() - _last_refresh.asMilliseconds());
				_renderTime = (_renderTime * 7 + currentRenderTime) / 8;
				renderTime = _renderTime;
			  Thread.sleep(nextRefresh);
			}

			// Update
			if (nextUpdate <= 0) {
			  _last_update = elapsed;
			  update();
			}

		}
	}

	void	create() {
	  Log.info("Game: create");

	  ResourceManager.getInstance().setMatter(Constant.RESSOURCE_MATTER_START);

	  WorldMap.getInstance().create();
	  CharacterManager.getInstance().create();
	}

	void	load(final String filePath) {
//	  Log.info("Game: load");
//
//	  ifstream ifs(filePath);
//	  string line;
//	  std.vector<std.string> vector;
//	  int value;
//	  bool	inBlock = false;
//
//	  if (ifs.is_open()) {
//	    while (getline(ifs, line)) {
//
//		  // Start block
//		  if (line.compare("BEGIN GAME") == 0) {
//			inBlock = true;
//		  }
//
//		  // End block
//		  else if (line.compare("END GAME") == 0) {
//			inBlock = false;
//		  }
//
//		  // Items
//		  else if (inBlock) {
//			std.cout + "line: " + line + std.endl;
//			vector.clear();
//			FileManager.split(line, '\t', vector);
//			if (vector[0].compare("matter") == 0) {
//			  std.istringstream issValue(vector[1]);
//			  issValue >> value;
//			  ResourceManager.getInstance().setMatter(value);
//			}
//		  }
//		}
//	    ifs.close();
//	  } else {
//		Error() + "Unable to open save file: " + filePath;
//	  }
//
	  WorldMap.getInstance().load(filePath);
	  CharacterManager.getInstance().load(filePath);
	}

	void	save(final String filePath) {
//	  Info() + "Game save";
//
//	  ofstream ofs(filePath);
//
//	  if (ofs.is_open()) {
//		ofs + "BEGIN GAME\n";
//		ofs + "matter\t" + ResourceManager.getInstance().getMatter() + "\n";
//		ofs + "END GAME\n";
//		ofs.close();
//	  } else {
//		Error() + "Unable to open save file: " + filePath;
//	  }
//
//	  // ofstream ofs(filePath);
//	  // ofs.close();
//
	  WorldMap.getInstance().save(filePath);
//	  CharacterManager.getInstance().save(filePath);
	}

}
