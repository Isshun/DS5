package alone.in.deepspace;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.graphics.Transform;
import org.jsfml.system.Clock;
import org.jsfml.system.Time;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.Mouse.Button;
import org.jsfml.window.event.Event;
import org.jsfml.window.event.MouseButtonEvent;

import alone.in.deepspace.Character.CharacterManager;
import alone.in.deepspace.Engine.ISavable;
import alone.in.deepspace.Engine.Viewport;
import alone.in.deepspace.Managers.DynamicObjectManager;
import alone.in.deepspace.Managers.FoeManager;
import alone.in.deepspace.Managers.JobManager;
import alone.in.deepspace.Managers.ResourceManager;
import alone.in.deepspace.Managers.RoomManager;
import alone.in.deepspace.Managers.SpriteManager;
import alone.in.deepspace.UserInterface.EventManager;
import alone.in.deepspace.UserInterface.UserInterface;
import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.Utils.Log;
import alone.in.deepspace.World.BaseItem;
import alone.in.deepspace.World.WorldArea;
import alone.in.deepspace.World.WorldMap;
import alone.in.deepspace.World.WorldRenderer;

public class Game implements ISavable {

	static final int 				REFRESH_INTERVAL = (1000/60);
	static final int 				UPDATE_INTERVAL = 100;
	
	public static int 				renderTime;
	private int 					_renderTime;
	private RenderWindow			_app;
	private int 					_lastInput;
	private static int 				_frame;
	private Viewport 				_viewport;
	private UserInterface 			_ui;
	private SpriteManager 			_spriteManager;
	private WorldRenderer 			_worldRenderer;
	private int 					_update;
	private CharacterManager		_characterManager;
	private Texture 				_backgroundTexture;
	private Sprite 					_background;
	private boolean 				_run;
	private Time 					_last_refresh;
	private Time 					_last_update;
	private FoeManager 				_FoeManager;
	private DynamicObjectManager	_dynamicObjectManager;
	private int _lastLeftClick;
	
	public static int getFrame() { return _frame; }

	public Game(RenderWindow app) throws IOException, TextureCreationException {
	  Log.debug("Game");

	  _renderTime = 0;

	  _app = app;
	  _lastInput = 0;
	  _frame = 0;
	  _viewport = new Viewport(app);
	  _ui = UserInterface.getInstance();
	  _ui.init(app, _viewport);

	  _spriteManager = SpriteManager.getInstance();
	  _worldRenderer = new WorldRenderer(app, _spriteManager, _ui);
	  _dynamicObjectManager = DynamicObjectManager.getInstance();

	  _update = 0;
	  _characterManager = CharacterManager.getInstance();
	  _FoeManager = FoeManager.getInstance();

	  // Background
	  Log.debug("Game background");
	  _backgroundTexture = new Texture();
	  _backgroundTexture.loadFromFile((new File("res/background.png")).toPath());
	  _background = new Sprite();
	  _background.setTexture(_backgroundTexture);
	  _background.setTextureRect(new IntRect(0, 0, 1920, 1080));

	  app.setKeyRepeatEnabled(true);
	  
	  Log.info("Game:\tdone");
	}

	void	update() {
		_dynamicObjectManager.update();
		
		WorldMap.getInstance().update();

		// Update item
		int w = WorldMap.getInstance().getWidth();
		int h = WorldMap.getInstance().getHeight();

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {


//				// Update oxygen
//				if (_frame % 6 == 0) {
//					WorldArea area = WorldMap.getInstance().getArea(i, j);
//					if (area.getStructure() != null && area.getStructure().isType(BaseItem.Type.STRUCTURE_FLOOR)) {
//						int oxygen = area.getOxygen();
//						int count = 1;
//
//						WorldArea a1 = WorldMap.getInstance().getArea(i+1, j);
//						if (a1.getStructure() == null) {
//							count++;
//						}
//						else if (area.getStructure().isType(BaseItem.Type.STRUCTURE_FLOOR)) {
//							oxygen += a1.getOxygen();
//							count++;
//						}
//
//						WorldArea a2 = WorldMap.getInstance().getArea(i-1, j);
//						if (a2.getStructure() == null) {
//							count++;
//						}
//						else if (a2.getStructure().isType(BaseItem.Type.STRUCTURE_FLOOR)) {
//							oxygen += a2.getOxygen();
//							count++;
//						}
//
//						WorldArea a3 = WorldMap.getInstance().getArea(i, j+1);
//						if (a3.getStructure() == null) {
//							count++;
//						}
//						else if (a3.getStructure().isType(BaseItem.Type.STRUCTURE_FLOOR)) {
//							oxygen += a3.getOxygen();
//							count++;
//						}
//			
//						WorldArea a4 = WorldMap.getInstance().getArea(i, j-1);
//						if (a4.getStructure() == null) {
//							count++;
//						}
//						else if (a4.getStructure().isType(BaseItem.Type.STRUCTURE_FLOOR)) {
//							oxygen += a4.getOxygen();
//							count++;
//						}
//			
//						int value = (int) Math.ceil((double)oxygen / count);
//			
//						  // if (a1 != null && a1.isType(BaseItem.Type.STRUCTURE_FLOOR)) {
//						  // 	a1.setOxygen(value);
//						  // }
//			
//						  // if (a2 != null && a2.isType(BaseItem.Type.STRUCTURE_FLOOR)) {
//						  // 	a2.setOxygen(value);
//						  // }
//			
//						  // if (a3 != null && a3.isType(BaseItem.Type.STRUCTURE_FLOOR)) {
//						  // 	a3.setOxygen(value);
//						  // }
//			
//						  // if (a4 != null && a4.isType(BaseItem.Type.STRUCTURE_FLOOR)) {
//						  // 	a4.setOxygen(value);
//						  // }
//			
//						area.setOxygen(value);
//					}
//				}
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

	  // Characters
	  _characterManager.assignJobs();
	  _characterManager.update(_update);

	  // Foes
	  _FoeManager.checkSurroundings();

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
	  _FoeManager.refresh(_app, render, animProgress);

		_dynamicObjectManager.refresh(_app, render, animProgress);
	  
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
	  _worldRenderer.refresh(render);
	}

	void	loop() throws IOException, InterruptedException {
		// fixme: actuellement update et refresh se partage les meme timers
		Clock display_timer = new Clock();
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
					_ui.onMouseMove(event.asMouseEvent().position.x, event.asMouseEvent().position.y);
				}

				if (event.type == Event.Type.MOUSE_BUTTON_PRESSED || event.type == Event.Type.MOUSE_BUTTON_RELEASED) {
					MouseButtonEvent mouseButtonEvent = event.asMouseButtonEvent();
					if (mouseButtonEvent.button == Button.LEFT) {
						if (event.type == Event.Type.MOUSE_BUTTON_PRESSED) {
							_ui.onLeftPress(mouseButtonEvent.position.x, mouseButtonEvent.position.y);
						} else {
							// Is consume by EventManager
							if (EventManager.getInstance().leftClick(mouseButtonEvent.position.x, mouseButtonEvent.position.y)) {
								// Nothing to do !
							}
							// Is double click
							else if (_lastLeftClick + 20 > _frame) {
								_ui.onDoubleClick(mouseButtonEvent.position.x, mouseButtonEvent.position.y);
							}
							// Is simple click
							else {
								_ui.onLeftClick(mouseButtonEvent.position.x, mouseButtonEvent.position.y);
							}
							_lastLeftClick = _frame;
						}
					} else if (mouseButtonEvent.button == Button.RIGHT) {
						if (event.type == Event.Type.MOUSE_BUTTON_PRESSED) {
							_ui.onRightPress(mouseButtonEvent.position.x, mouseButtonEvent.position.y);
						} else {
							_ui.onRightClick(mouseButtonEvent.position.x, mouseButtonEvent.position.y);
						}
					}
					//_ui.mouseRelease(event.asMouseButtonEvent().button, event.asMouseButtonEvent().position.x, event.asMouseButtonEvent().position.y);
				}

				if (event.type == Event.Type.MOUSE_WHEEL_MOVED) {
					_ui.onMouseWheel(event.asMouseWheelEvent().delta, event.asMouseWheelEvent().position.x, event.asMouseWheelEvent().position.y);
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

	public void	load(final String filePath) {
		Log.error("Load game: " + filePath);

		boolean	inBlock = false;
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line = null;

			while ((line = br.readLine()) != null) {

				// Start block
				if ("BEGIN GAME".equals(line)) {
					inBlock = true;
				}

				// End block
				else if ("END GAME".equals(line)) {
					inBlock = false;
				}

				else if (inBlock) {
					String[] values = line.split("\t");
					if (values.length == 2) {
						if ("MATTER".equals(values[0])) {
							ResourceManager.getInstance().setMatter(Integer.valueOf(values[1]));
						}
						if ("SPICE".equals(values[0])) {
							ResourceManager.getInstance().setSpice(Integer.valueOf(values[1]));
						}
					}
				}

			}
		}
		catch (FileNotFoundException e) {
			Log.error("Unable to open save file: " + filePath);
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	  WorldMap.getInstance().load(filePath);
	  CharacterManager.getInstance().load(filePath);
	  RoomManager.getInstance().load(filePath);
	  JobManager.getInstance().load(filePath);
	}

	public void	save(final String filePath) {
		Log.info("Save game: " + filePath);

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
			bw.write("BEGIN GAME\n");
			bw.write("MATTER\t" + ResourceManager.getInstance().getMatter() + "\n");
			bw.write("SPICE\t" + ResourceManager.getInstance().getSpice() + "\n");
			bw.write("END GAME\n");
		} catch (FileNotFoundException e) {
			Log.error("Unable to open save file: " + filePath);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.info("Save game: " + filePath + " done");

	  WorldMap.getInstance().save(filePath);
	  CharacterManager.getInstance().save(filePath);
	  RoomManager.getInstance().save(filePath);
	  JobManager.getInstance().save(filePath);
	}

}
