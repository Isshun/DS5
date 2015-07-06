package org.smallbox.faraway.engine.renderer;

import org.smallbox.faraway.engine.GFXRenderer;
import org.smallbox.faraway.engine.RenderEffect;
import org.smallbox.faraway.engine.SpriteManager;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.GameConfig;

import java.util.ArrayList;
import java.util.List;

public class MainRenderer {
	private static MainRenderer         _self;
	private static long 				_renderTime;
	private static int 					_frame;
	private SpriteManager 			    _spriteManager;
	private CharacterRenderer 		    _characterRenderer;
	private WorldRenderer 			    _worldRenderer;
	private final List<BaseRenderer>    _renders;

	public MainRenderer(GFXRenderer renderer, GameConfig config) {
		_self = this;
		_spriteManager = SpriteManager.getInstance();
		_renders = new ArrayList<>();
	}

	public void onRefresh(int frame) {
        for (BaseRenderer render: _renders) {
            render.onRefresh(frame);
        }
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

		if (config.manager.fauna) {
			_renders.add(renderer.createFaunaRenderer());
		}

		if (config.render.debug) {
			_renders.add(new DebugRenderer());
		}
	}

	public static MainRenderer getInstance() { return _self; }

	public static int getFrame() { return _frame; }

	public static long getRenderTime() { return _frame > 0 ? _renderTime / _frame : 0; }

	public List<BaseRenderer> getRenders() {
		return _renders;
	}

	public WorldRenderer getWorldRenderer() {
		return _worldRenderer;
	}
}
