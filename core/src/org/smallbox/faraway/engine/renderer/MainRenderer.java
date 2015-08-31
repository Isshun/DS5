package org.smallbox.faraway.engine.renderer;

import org.reflections.Reflections;
import org.smallbox.faraway.core.SpriteManager;
import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.GameConfig;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
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

	public MainRenderer(GDXRenderer renderer, GameConfig config) {
		_self = this;
		_spriteManager = SpriteManager.getInstance();
		_renders = new ArrayList<>();
	}

	public void onRefresh(int frame) {
        for (BaseRenderer render: _renders) {
            render.onRefresh(frame);
        }
	}
	
	public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
		long time = System.currentTimeMillis();

        _renders.stream().filter(BaseRenderer::isLoaded).forEach(render -> render.draw(renderer, viewport, animProgress));

        _frame++;
		_renderTime += System.currentTimeMillis() - time;
	}

	public void init(GameConfig config, Game game) {
		_frame = 0;

        new Reflections("org.smallbox.faraway").getSubTypesOf(BaseRenderer.class).stream().filter(cls -> !Modifier.isAbstract(cls.getModifiers())).forEach(cls -> {
            try {
                Log.info("Load render: " + cls.getSimpleName());
                BaseRenderer render = cls.getConstructor().newInstance();
                if (render.isActive(config)) {
                    render.load();
                } else {
                    render.unload();
                }
                _renders.add(render);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        _renders.sort((r1, r2) -> r1.getLevel() - r2.getLevel());

		_worldRenderer = (WorldRenderer)getRender(WorldRenderer.class);
		game.addObserver(_worldRenderer);

        _characterRenderer = (CharacterRenderer)getRender(CharacterRenderer.class);

        _renders.stream().filter(BaseRenderer::isLoaded).forEach(BaseRenderer::init);
	}

    private BaseRenderer getRender(Class<? extends BaseRenderer> cls) {
        for (BaseRenderer renderer: _renders) {
            if (renderer.getClass() == cls) {
                return renderer;
            }
        }
        return null;
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

    public void toggleRender(BaseRenderer render) {
        if (render.isLoaded()) {
            render.unload();
        } else {
            render.load();
        }
    }
}
