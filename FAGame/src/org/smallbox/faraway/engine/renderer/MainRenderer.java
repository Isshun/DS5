package org.smallbox.faraway.engine.renderer;

import org.smallbox.faraway.GFXRenderer;
import org.smallbox.faraway.Game;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.manager.SpriteManager;
import org.smallbox.faraway.model.GameConfig;
import org.smallbox.faraway.ui.UserInterface;

import java.util.ArrayList;
import java.util.List;

public class MainRenderer implements IRenderer {
	private static IRenderer 		_self;

	private SpriteManager 			_spriteManager;
	private CharacterRenderer 		_characterRenderer;
	private WorldRenderer 			_worldRenderer;
	private final List<IRenderer> 	_hudRenders;

	private int _lastSavedFrame;

	private static int 				_fps;
	private static long 			_renderTime;
	private static int 				_frame;
    private UserInterface.Mode      _mode;

    public static int getFrame() { return _frame; }
	public static long getRenderTime() { return _renderTime; }

	public MainRenderer(GFXRenderer renderer, GameConfig config) {
		_self = this;
		_spriteManager = SpriteManager.getInstance();

		_worldRenderer = new WorldRenderer(_spriteManager);

		_hudRenders = new ArrayList<>();

		if (config.render.debug) {
			_hudRenders.add(new DebugRenderer());
		}
		if (config.render.job) {
			_hudRenders.add(new JobRenderer());
		}
		if (config.render.area) {
			_hudRenders.add(renderer.createAreaRenderer());
		}
		if (config.render.temperature) {
			_hudRenders.add(renderer.createTemperatureRenderer());
		}
	}

	public void onRefresh(int frame) {
		if (_worldRenderer != null) {
			_worldRenderer.onRefresh(frame);
		}

		if (_characterRenderer != null) {
			_characterRenderer.onRefresh(frame);
		}
	}
	
	public void onDraw(GFXRenderer renderer, RenderEffect effect, double animProgress) {
//		_renderTime = renderTime;

		_worldRenderer.onDraw(renderer, effect, animProgress);
		_worldRenderer.onDrawSelected(renderer, effect, animProgress);

        _characterRenderer.onDraw(renderer, effect, animProgress);

        _frame++;
	}

	public void onDrawHUD(GFXRenderer renderer, RenderEffect effect, double animProgress) {
		for (IRenderer render: _hudRenders) {
			render.onDraw(renderer, effect, animProgress);
		}
	}

	public void init(Game game) {
		_frame = 0;
		_characterRenderer = new CharacterRenderer(game.getCharacterManager().getList());
	}

	public static IRenderer getInstance() {
		return _self;
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
	}

	public void setFPS(int frame, int interval) {
		_fps = (frame - _lastSavedFrame) / (interval / 1000);
		_lastSavedFrame = frame;
	}

	public static int getFPS() {
		return _fps;
	}

    public void setMode(UserInterface.Mode mode) {
        _mode = mode;
    }
}
