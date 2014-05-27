package alone.in.deepspace.engine.renderer;

import java.io.File;
import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.graphics.Transform;

import alone.in.deepspace.engine.Viewport;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.manager.SpriteManager;
import alone.in.deepspace.ui.UserInterface;
import alone.in.deepspace.ui.UserInterface.Mode;
import alone.in.deepspace.util.Log;
import alone.in.deepspace.util.Settings;

public class MainRenderer {
	public void draw(Drawable sprite, RenderStates render) {
		if (render != null) {
			_app.draw(sprite, render);
		} else {
			_app.draw(sprite);
		}
	}

	static MainRenderer 			_self;
	private int 					_frame;
	private int 					_renderTime;
	private WorldRenderer 			_worldRenderer;
	private LightRenderer			_lightRenderer;
	private DebugRenderer 			_debugRenderer;
	private JobRenderer				_jobRenderer;
	private SpriteManager 			_spriteManager;
	private Viewport 				_viewport;
	private Texture 				_backgroundTexture;
	private Sprite 					_background;
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
		
		// Background
		Log.debug("Game background");
		_backgroundTexture = new Texture();
		_backgroundTexture.loadFromFile((new File("res/background.png")).toPath());
		_background = new Sprite();
		_background.setTexture(_backgroundTexture);
		_background.setTextureRect(new IntRect(0, 0, 1920, 1080));

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

		// Draw surface
		{
			// Background
			Transform transform2 = new Transform();
			RenderStates render2 = new RenderStates(_viewport.getViewTransformBackground(transform2));
			app.draw(_background, render2);
	
			// Render transformation for viewport
			Transform transform = new Transform();
			RenderStates render = new RenderStates(_viewport.getViewTransform(transform));
			_worldRenderer.onDraw(app, render, animProgress);
		}
		
		Transform transform = new Transform();
		transform = _viewport.getViewTransform(transform);
		RenderStates render = new RenderStates(transform);

		_lightRenderer.onDraw(app, render, animProgress);

		if (_ui.getMode() == Mode.ROOM) {
			_roomRenderer.onDraw(app, render, animProgress);
		}
		
		_jobRenderer.onDraw(app, render, animProgress);

		_characterRenderer.onDraw(_app, render, animProgress);
		
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
