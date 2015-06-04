package org.smallbox.faraway.engine.renderer;

import org.smallbox.faraway.*;
import org.smallbox.faraway.engine.util.Settings;
import org.smallbox.faraway.manager.SpriteManager;
import org.smallbox.faraway.model.item.ItemBase;
import org.smallbox.faraway.ui.UserInterface.Mode;

public class MainRenderer implements IRenderer {
	private static IRenderer 		_self;
	
	private SpriteManager _spriteManager;
	private CharacterRenderer 		_characterRenderer;
	private WorldRenderer 			_worldRenderer;
    // TODO
//	private LightRenderer 			_lightRenderer;
//	private RoomRenderer 			_roomRenderer;
	private DebugRenderer 			_debugRenderer;
	private JobRenderer				_jobRenderer;
	private Viewport 				_viewport;
	private Mode 					_mode;

	private int _lastSavedFrame;

	private static int 				_fps;
	private static long 			_renderTime;
	private static int 				_frame;
	
	public static int getFrame() { return _frame; }
	public static long getRenderTime() { return _renderTime; }

	public MainRenderer() {
		_self = this;
		_spriteManager = SpriteManager.getInstance();

		_worldRenderer = new WorldRenderer(_spriteManager);
        // TODO
		//_lightRenderer = new LightRenderer();
//		_roomRenderer = new RoomRenderer();
		_debugRenderer = new DebugRenderer();
		_jobRenderer = new JobRenderer();
	}

	public void onRefresh(int frame) {
        // TODO
//		_lightRenderer.onDraw(frame);
		_worldRenderer.onRefresh(frame);
		_characterRenderer.onRefresh(frame);
	}
	
	public void onDraw(GFXRenderer renderer, RenderEffect effect, double animProgress) {
//		_renderTime = renderTime;

		renderer.clear(new Color(0, 0, 50));
		
		_worldRenderer.onDraw(renderer, effect, animProgress);
		// TODO
		//_lightRenderer.onDraw(renderer, effect, animProgress);

		_worldRenderer.onDrawSelected(renderer, effect, animProgress);

        // TODO
//		if (_mode == Mode.ROOM) {
//			_roomRenderer.onDraw(renderer, effect, animProgress);
//		}
		_jobRenderer.onDraw(renderer, effect, animProgress);
		_characterRenderer.onDraw(renderer, effect, animProgress);
		
		// Draw debug
		if (Settings.getInstance().isDebug()) {
			_debugRenderer.onDraw(renderer, effect, animProgress);
		}
		
		_frame++;
	}

	public void init(Game game) {
		_frame = 0;
		_viewport = game.getViewport();
		_characterRenderer = new CharacterRenderer(Game.getCharacterManager().getList());
        // TODO
//		_lightRenderer.initLight();
	}

	public static IRenderer getInstance() {
		return _self;
	}

	public void setMode(Mode mode) {
		_mode = mode;
	}
	public void initLight() {
        // TODO
//		if (_lightRenderer != null) {
//			_lightRenderer.initLight();
//		}
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
        // TODO
//		if (_lightRenderer != null) {
//			_lightRenderer.initLight();
//		}
	}

	public void refreshLight(ItemBase item) {
        // TODO
//		if (_lightRenderer != null) {
//			_lightRenderer.refreshGame(item);
//		}
	}
	public void setFPS(int frame, int interval) {
		_fps = (frame - _lastSavedFrame) / (interval / 1000);
		_lastSavedFrame = frame;
	}
	public static int getFPS() {
		return _fps;
	}
	public static void setInstance(IRenderer renderer) {
		_self = renderer;
	}
}
