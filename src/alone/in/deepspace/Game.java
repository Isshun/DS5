package alone.in.deepspace;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.graphics.Transform;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Mouse.Button;
import org.jsfml.window.event.Event;
import org.jsfml.window.event.MouseButtonEvent;

import alone.in.deepspace.UserInterface.UserInterface;
import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.Utils.Log;
import alone.in.deepspace.engine.ISavable;
import alone.in.deepspace.engine.Viewport;
import alone.in.deepspace.engine.renderer.MainRenderer;
import alone.in.deepspace.manager.CharacterManager;
import alone.in.deepspace.manager.DynamicObjectManager;
import alone.in.deepspace.manager.FoeManager;
import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.PathManager;
import alone.in.deepspace.manager.ResourceManager;
import alone.in.deepspace.manager.RoomManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.manager.UIEventManager;

public class Game implements ISavable {
	private int 					_lastInput;
	private static int 				_frame;
	private int 					_update;
	private Viewport 				_viewport;
	private UserInterface 			_ui;
	private CharacterManager		_characterManager;
	private FoeManager 				_FoeManager;
	private DynamicObjectManager	_dynamicObjectManager;
	private int 					_lastLeftClick;
	private MainRenderer 			_mainRenderer;
	private RenderWindow 			_app;
	private boolean					_isMenuOpen;
	private boolean 				_isRunning;

	public static int getFrame() { return _frame; }
	
	static {
		System.loadLibrary("JNILight");
	}

	public Game(RenderWindow app) throws IOException, TextureCreationException {
		Log.debug("Game");

		_lastInput = 0;
		_frame = 0;
		_app = app;
		_isRunning = true;
		
		_viewport = new Viewport(app);
		
		_ui = UserInterface.getInstance();
		_ui.onCreate(this, app, _viewport);

		_dynamicObjectManager = DynamicObjectManager.getInstance();

		_update = 0;
		_characterManager = ServiceManager.getCharacterManager();
		_FoeManager = FoeManager.getInstance();

		_mainRenderer = new MainRenderer(app, _viewport, _ui);
		
		app.setKeyRepeatEnabled(true);
		
		Log.info("Game:\tdone");
	}

	public void	onUpdate() {
		if (_isMenuOpen) {
			return;
		}
		
		_dynamicObjectManager.update();

		ServiceManager.getWorldMap().update();

		// Path complete
		List<Runnable> jobsDone = PathManager.getInstance().getJobs();
		for (Runnable job: jobsDone) {
			job.run();
		}
		jobsDone.clear();
		
		// Characters
		_characterManager.onUpdate(_update);

		// Foes
		_FoeManager.checkSurroundings();

		_update++;
	}

	public void onLongUpdate() {
		ResourceManager.getInstance().update();
		_characterManager.onLongUpdate();
	}

	public void onEvent(Event event) throws IOException {
		if (event.type == Event.Type.MOUSE_MOVED) {
			_ui.onMouseMove(event.asMouseEvent().position.x, event.asMouseEvent().position.y);
			UIEventManager.getInstance().onMouseMove(event.asMouseEvent().position.x, event.asMouseEvent().position.y);
		}

		if (event.type == Event.Type.MOUSE_BUTTON_PRESSED || event.type == Event.Type.MOUSE_BUTTON_RELEASED) {
			MouseButtonEvent mouseButtonEvent = event.asMouseButtonEvent();
			if (mouseButtonEvent.button == Button.LEFT) {
				if (event.type == Event.Type.MOUSE_BUTTON_PRESSED) {
					_ui.onLeftPress(mouseButtonEvent.position.x, mouseButtonEvent.position.y);
				} else {
					// Is consume by EventManager
					if (UIEventManager.getInstance().leftClick(mouseButtonEvent.position.x, mouseButtonEvent.position.y)) {
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
			if (_ui.checkKeyboard(event, _frame, _lastInput)) {
				return;
			}
			
			if (event.asKeyEvent().key == Keyboard.Key.ESCAPE) {
				_isMenuOpen = !_isMenuOpen;
			}
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
		
		_mainRenderer.init();

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

	public void onDraw(double animProgress, int renderTime) throws IOException {
		_frame++;
		
		_mainRenderer.draw(_app, animProgress, renderTime);
		
		Transform transform = new Transform();
		transform = _viewport.getViewTransform(transform);
		RenderStates render = new RenderStates(transform);

		_characterManager.onDraw(_app, render, animProgress);
		_FoeManager.onDraw(_app, render, animProgress);
		_dynamicObjectManager.refresh(_app, render, animProgress);

		// User interface
		_ui.refresh(_frame, _update, renderTime);

//		if (_menu != null) {
//			_menu.refresh(_app);
//		}
	}

	public boolean isRunning() {
		return _isRunning;
	}

	public void setRunning(boolean running) {
		_isRunning = running;		
	}
}
