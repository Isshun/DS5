package org.smallbox.faraway.engine.renderer;

import org.smallbox.faraway.engine.GFXRenderer;
import org.smallbox.faraway.engine.RenderEffect;
import org.smallbox.faraway.engine.SpriteManager;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.GameConfig;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.ui.UserInterface;

import java.util.ArrayList;
import java.util.List;

public class MainRenderer {
	private static MainRenderer         _self;

	private SpriteManager 			    _spriteManager;
	private CharacterRenderer 		    _characterRenderer;
	private WorldRenderer 			    _worldRenderer;
	private final List<BaseRenderer>    _renders;

	private int _lastSavedFrame;

	private static int 				_fps;
	private static long 			_renderTime;
	private static int 				_frame;
    private UserInterface.Mode      _mode;

    public static int getFrame() { return _frame; }
	public static long getRenderTime() { return _frame > 0 ? _renderTime / _frame : 0; }

	public MainRenderer(GFXRenderer renderer, GameConfig config) {
		_self = this;
		_spriteManager = SpriteManager.getInstance();

		_renders = new ArrayList<>();
	}

	public void onRefresh(int frame) {

        for (BaseRenderer render: _renders) {
            render.onRefresh(frame);
        }

//        if (_worldRenderer != null) {
//			_worldRenderer.onRefresh(frame);
//		}
//
//		if (_characterRenderer != null) {
//			_characterRenderer.onRefresh(frame);
//		}
	}
	
	public void onDraw(GFXRenderer renderer, RenderEffect effect, double animProgress) {
		long time = System.currentTimeMillis();

		for (BaseRenderer render: _renders) {
			render.draw(renderer, effect, animProgress);
		}

        _frame++;
		_renderTime += System.currentTimeMillis() - time;
	}

	public void init(GFXRenderer renderer, GameConfig config, Game game, LightRenderer lightRenderer, ParticleRenderer particleRenderer) {
		_frame = 0;

		_worldRenderer = new WorldRenderer(_spriteManager);
		_renders.add(_worldRenderer);
		game.addObserver(_worldRenderer);

		_characterRenderer = new CharacterRenderer();
		_renders.add(_characterRenderer);

		if (lightRenderer != null) {
			_renders.add(lightRenderer);
		}

		if (particleRenderer != null) {
			_renders.add(particleRenderer);
		}

		if (config.render.job) {
			_renders.add(new JobRenderer());
		}

		if (config.render.area) {
			_renders.add(renderer.createAreaRenderer());
		}

		if (config.render.temperature) {
			_renders.add(renderer.createTemperatureRenderer());
		}

		if (config.render.room) {
			_renders.add(renderer.createRoomRenderer());
		}

		if (GameData.config.manager.fauna) {
			_renders.add(renderer.createFaunaRenderer());
		}

		if (config.render.debug) {
			_renders.add(new DebugRenderer());
		}
	}

	public static MainRenderer getInstance() {
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
//		_fps = (frame - _lastSavedFrame) / (interval / 1000);
		_lastSavedFrame = frame;
	}

	public static int getFPS() {
		return _fps;
	}

    public void setMode(UserInterface.Mode mode) {
        _mode = mode;
    }
}
