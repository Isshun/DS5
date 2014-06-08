package alone.in.deepspace.engine.renderer;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;

import alone.in.deepspace.Game;
import alone.in.deepspace.engine.Viewport;
import alone.in.deepspace.manager.SpriteManager;
import alone.in.deepspace.model.item.ItemBase;
import alone.in.deepspace.ui.UserInterface.Mode;
import alone.in.deepspace.util.Settings;

public class MainRenderer {
	private static MainRenderer 	_self;
	
	private SpriteManager 			_spriteManager;
	private CharacterRenderer 		_characterRenderer;
	private WorldRenderer 			_worldRenderer;
	private LightRenderer			_lightRenderer;
	private DebugRenderer 			_debugRenderer;
	private JobRenderer				_jobRenderer;
	private Viewport 				_viewport;
	private RoomRenderer 			_roomRenderer;
	private Mode 					_mode;

	private int _lastSavedFrame;

	private static int 				_fps;
	private static long 			_renderTime;
	private static int 				_frame;
	
	public static int getFrame() { return _frame; }
	public static long getRenderTime() { return _renderTime; }

	public MainRenderer(RenderWindow app) {
		_self = this;
		_spriteManager = SpriteManager.getInstance();

		_worldRenderer = new WorldRenderer(_spriteManager);
		_lightRenderer = new LightRenderer();
		_debugRenderer = new DebugRenderer();
		
		_jobRenderer = new JobRenderer();
		_roomRenderer = new RoomRenderer();
	}

	public void refresh(int frame) {
		_worldRenderer.onRefresh(frame);
		_characterRenderer.onRefresh(frame);
	}
	
	public void draw(RenderWindow app, double animProgress, long renderTime) {
		_renderTime = renderTime;

		app.clear(new Color(0, 0, 50));
		
		RenderStates render = _viewport.getRender();

		_worldRenderer.onDraw(app, render, animProgress);
		_lightRenderer.onDraw(app, render, animProgress);

		_worldRenderer.onDrawSelected(app, render, animProgress);
		
		if (_mode == Mode.ROOM) {
			_roomRenderer.onDraw(app, render, animProgress);
		}
		_jobRenderer.onDraw(app, render, animProgress);
		_characterRenderer.onDraw(app, render, animProgress);
		
		// Draw debug
		if (Settings.getInstance().isDebug()) {
			_debugRenderer.onDraw(app, render, animProgress);
		}
		
		_frame++;
	}

	public void init(Game game) {
		_frame = 0;
		_viewport = game.getViewport();
		_characterRenderer = new CharacterRenderer(Game.getCharacterManager().getList());
		_lightRenderer.initLight();
	}

	public static MainRenderer getInstance() {
		return _self;
	}

	public void setMode(Mode mode) {
		_mode = mode;
	}
	public void initLight() {
		if (_lightRenderer != null) {
			_lightRenderer.initLight();
		}
	}

	public void invalidate(int x, int y) {
		if (_worldRenderer != null) {
			_worldRenderer.invalidate(x, y);
		}
	}

	public void invalidate() {
		if (_worldRenderer != null) {
			_worldRenderer.invalidate();
		}
		if (_lightRenderer != null) {
			_lightRenderer.initLight();
		}
	}

	public void refreshLight(ItemBase item) {
		if (_lightRenderer != null) {
			_lightRenderer.refresh(item);
		}
	}
	public void setFPS(int frame, int interval) {
		_fps = (frame - _lastSavedFrame) / (interval / 1000);
		_lastSavedFrame = frame;
	}
	public static int getFPS() {
		return _fps;
	}
}
