package alone.in.deepspace.engine.renderer;

import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.graphics.Transform;

import alone.in.deepspace.engine.Viewport;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.manager.SpriteManager;
import alone.in.deepspace.ui.UserInterface;
import alone.in.deepspace.ui.UserInterface.Mode;
import alone.in.deepspace.util.Settings;

public class MainRenderer {
	static MainRenderer 			_self;
	private int 					_frame;
	private int 					_renderTime;
	private WorldRenderer 			_worldRenderer;
	private LightRenderer			_lightRenderer;
	private DebugRenderer 			_debugRenderer;
	private JobRenderer				_jobRenderer;
	private SpriteManager 			_spriteManager;
	private Viewport 				_viewport;
	private RenderWindow 			_app;
	private RoomRenderer 			_roomRenderer;
	private UserInterface 			_ui;
	private CharacterRenderer 		_characterRenderer;
	private static MainRenderer 	_this;
	
	public MainRenderer(RenderWindow app, Viewport viewport, UserInterface ui) throws IOException, TextureCreationException {

		// TODO
		_this = this;
		_app = app;
		_ui = ui;
		
		_viewport = viewport;

		_spriteManager = SpriteManager.getInstance();

		_worldRenderer = new WorldRenderer(_spriteManager, ui);
		ServiceManager.setWorldRenderer(_worldRenderer);
		_lightRenderer = new LightRenderer();
		ServiceManager.setLightRenderer(_lightRenderer);
		_debugRenderer = new DebugRenderer();
		
		_characterRenderer = new CharacterRenderer(ServiceManager.getCharacterManager().getList());
		
		_jobRenderer = new JobRenderer();
		_roomRenderer = new RoomRenderer();
	}

	public void draw(RenderWindow app, double animProgress, int renderTime) {
		// Flush
		app.clear(new Color(0, 0, 50));
		_frame++;
		_renderTime = renderTime;

		Transform transform = new Transform();
		transform = _viewport.getViewTransform(transform);
		RenderStates render = new RenderStates(transform);

		_worldRenderer.onDraw(app, render, animProgress);
		_lightRenderer.onDraw(app, render, animProgress);
		if (_ui.getMode() == Mode.ROOM) {
			_roomRenderer.onDraw(app, render, animProgress);
		}
		_jobRenderer.onDraw(app, render, animProgress);
		_characterRenderer.onDraw(app, render, animProgress);
		
		// Draw debug
		if (Settings.getInstance().isDebug()) {
			_debugRenderer.onDraw(app, render, animProgress);
		}
	}
	public void init() {
		_lightRenderer.initLight();
	}

	public static MainRenderer getInstance() {
		return _this;
	}

	public int getRenderTime() {
		return _renderTime;
	}
}
