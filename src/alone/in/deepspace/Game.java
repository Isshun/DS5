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
import org.jsfml.window.Mouse.Button;
import org.jsfml.window.event.Event;
import org.jsfml.window.event.MouseButtonEvent;

import alone.in.deepspace.Character.CharacterManager;
import alone.in.deepspace.Character.ServiceManager;
import alone.in.deepspace.Engine.ISavable;
import alone.in.deepspace.Engine.Viewport;
import alone.in.deepspace.Managers.DynamicObjectManager;
import alone.in.deepspace.Managers.FoeManager;
import alone.in.deepspace.Managers.JobManager;
import alone.in.deepspace.Managers.ResourceManager;
import alone.in.deepspace.Managers.RoomManager;
import alone.in.deepspace.Managers.SpriteManager;
import alone.in.deepspace.UserInterface.EventManager;
import alone.in.deepspace.UserInterface.MenuBase;
import alone.in.deepspace.UserInterface.UserInterface;
import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.Utils.Log;
import alone.in.deepspace.World.WorldRenderer;

public class Game implements ISavable {
	public static int 				renderTime;
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
	private FoeManager 				_FoeManager;
	private DynamicObjectManager	_dynamicObjectManager;
	private int 					_lastLeftClick;
	private MenuBase 				_menu;

	public static int getFrame() { return _frame; }

	public Game(RenderWindow app) throws IOException, TextureCreationException {
		Log.debug("Game");

		_app = app;
		_lastInput = 0;
		_frame = 0;
		_viewport = new Viewport(app);
		_ui = UserInterface.getInstance();
		_ui.init(app, _viewport);

		_spriteManager = SpriteManager.getInstance();
		_worldRenderer = new WorldRenderer(app, _spriteManager, _ui);
		ServiceManager.setWorldRenderer(_worldRenderer);
		_dynamicObjectManager = DynamicObjectManager.getInstance();

		_update = 0;
		_characterManager = ServiceManager.getCharacterManager();
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

	void	onUpdate() {
		_dynamicObjectManager.update();

		ServiceManager.getWorldMap().update();

		// Update item
		int w = ServiceManager.getWorldMap().getWidth();
		int h = ServiceManager.getWorldMap().getHeight();

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {


				//				// Update oxygen
				//				if (_frame % 6 == 0) {
				//					WorldArea area = ServiceManager.getWorldMap().getArea(i, j);
				//					if (area.getStructure() != null && area.getStructure().isType(BaseItem.Type.STRUCTURE_FLOOR)) {
				//						int oxygen = area.getOxygen();
				//						int count = 1;
				//
				//						WorldArea a1 = ServiceManager.getWorldMap().getArea(i+1, j);
				//						if (a1.getStructure() == null) {
				//							count++;
				//						}
				//						else if (area.getStructure().isType(BaseItem.Type.STRUCTURE_FLOOR)) {
				//							oxygen += a1.getOxygen();
				//							count++;
				//						}
				//
				//						WorldArea a2 = ServiceManager.getWorldMap().getArea(i-1, j);
				//						if (a2.getStructure() == null) {
				//							count++;
				//						}
				//						else if (a2.getStructure().isType(BaseItem.Type.STRUCTURE_FLOOR)) {
				//							oxygen += a2.getOxygen();
				//							count++;
				//						}
				//
				//						WorldArea a3 = ServiceManager.getWorldMap().getArea(i, j+1);
				//						if (a3.getStructure() == null) {
				//							count++;
				//						}
				//						else if (a3.getStructure().isType(BaseItem.Type.STRUCTURE_FLOOR)) {
				//							oxygen += a3.getOxygen();
				//							count++;
				//						}
				//			
				//						WorldArea a4 = ServiceManager.getWorldMap().getArea(i, j-1);
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
		//   ServiceManager.getWorldMap().reloadAborted();
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

		if (_update % 50 == 0) {
			ResourceManager.getInstance().update();
		}
	}

	void	onRefresh(double animProgress, int rTime) throws IOException {
		// Flush
		_app.clear(new Color(0, 0, 50));
		_frame++;
		renderTime = rTime;

		// Draw scene
		draw_surface();

		Transform transform = new Transform();
		transform = _viewport.getViewTransform(transform);
		RenderStates render = new RenderStates(transform);

		_characterManager.refresh(_app, render, animProgress);
		_FoeManager.refresh(_app, render, animProgress);

		_dynamicObjectManager.refresh(_app, render, animProgress);

		// User interface
		_ui.refresh(_frame, _update, rTime);

		if (_menu != null) {
			_menu.refresh(_app);
		}

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

	public void onEvent(Event event) throws IOException {
		if (event.type == Event.Type.MOUSE_MOVED) {
			_ui.onMouseMove(event.asMouseEvent().position.x, event.asMouseEvent().position.y);
			EventManager.getInstance().onMouseMove(event.asMouseEvent().position.x, event.asMouseEvent().position.y);
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
			_ui.checkKeyboard(event, _frame, _lastInput);
		}
	}

	void	create() {
		Log.info("Game: create");

		ResourceManager.getInstance().setMatter(Constant.RESSOURCE_MATTER_START);

		ServiceManager.getWorldMap().create();
		ServiceManager.getCharacterManager().create();
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
						if ("WATER".equals(values[0])) {
							ResourceManager.getInstance().setWater(Integer.valueOf(values[1]));
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

		ServiceManager.getWorldMap().load(filePath);
		ResourceManager.getInstance().refreshWater();

		ServiceManager.getCharacterManager().load(filePath);
		RoomManager.getInstance().load(filePath);
		JobManager.getInstance().load(filePath);
		
		//JobManager.getInstance().move(ServiceManager.getCharacterManager().getList().get(0), 25, 14);
	}

	public void	save(final String filePath) {
		Log.info("Save game: " + filePath);

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
			bw.write("BEGIN GAME\n");
			bw.write("MATTER\t" + ResourceManager.getInstance().getMatter() + "\n");
			bw.write("SPICE\t" + ResourceManager.getInstance().getSpice() + "\n");
			bw.write("WATER\t" + ResourceManager.getInstance().getWater() + "\n");
			bw.write("END GAME\n");
		} catch (FileNotFoundException e) {
			Log.error("Unable to open save file: " + filePath);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.info("Save game: " + filePath + " done");

		ServiceManager.getWorldMap().save(filePath);
		ServiceManager.getCharacterManager().save(filePath);
		RoomManager.getInstance().save(filePath);
		JobManager.getInstance().save(filePath);
	}

}
