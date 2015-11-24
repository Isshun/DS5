package org.smallbox.faraway.core.engine.renderer;

import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.game.Game;

import java.util.Collection;

public class MainRenderer {
    public static final int                 WORLD_GROUND_RENDERER_LEVEL = -100;
    public static final int                 PARTICLE_RENDERER_LEVEL = -99;
    public static final int                 WORLD_TOP_RENDERER_LEVEL = -98;
    public static final int                 CHARACTER_RENDERER_LEVEL = -97;
    public static final int                 JOB_RENDERER_LEVEL = -96;

    private static MainRenderer             _self;
    private static long                     _renderTime;
    private static int                      _frame;
    private final Collection<BaseRenderer>  _renders;
    private final BaseRenderer              _minimapRender;

    public MainRenderer(GDXRenderer renderer) {
        _self = this;
        _renders = ModuleManager.getInstance().getRenders();
        _minimapRender = ModuleManager.getInstance().getMinimapRender();
    }

    public void onRefresh(int frame) {
        for (BaseRenderer render: _renders) {
            render.onRefresh(frame);
        }
    }

    public void onUpdate() {
        _renders.stream().filter(BaseRenderer::isLoaded).forEach(BaseRenderer::update);
    }

    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        long time = System.currentTimeMillis();

        Game game = Game.getInstance();
        _renders.stream().filter(BaseRenderer::isLoaded)
                .filter(render -> !(render instanceof GameDisplay) || (game.hasDisplay(((GameDisplay)render).getName())))
                .forEach(render -> render.draw(renderer, viewport, animProgress));

        _frame++;
        _renderTime += System.currentTimeMillis() - time;
    }

    public void init(Game game) {
        _frame = 0;
        _renders.forEach(render -> render.load(game));
        _minimapRender.load(game);
    }

    private BaseRenderer getRender(Class<? extends BaseRenderer> cls) {
        for (BaseRenderer renderer: _renders) {
            if (renderer.getClass() == cls) {
                return renderer;
            }
        }
        return null;
    }

    public static MainRenderer getInstance() {
        if (_self == null) {
            _self = new MainRenderer(GDXRenderer.getInstance());
        }
        return _self; }

    public static int getFrame() { return _frame; }

    public static long getRenderTime() { return _frame > 0 ? _renderTime / _frame : 0; }

    public Collection<BaseRenderer> getRenders() {
        return _renders;
    }

    public void toggleRender(BaseRenderer render) {
        if (render.isLoaded()) {
            render.unload();
        } else {
            render.load(Game.getInstance());
        }
    }

    public BaseRenderer getMinimapRender() {
        return _minimapRender;
    }
}
