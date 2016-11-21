package org.smallbox.faraway.core.engine.renderer;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.ModuleRenderer;
import org.smallbox.faraway.core.game.Game;

import java.util.Collection;
import java.util.stream.Collectors;

public class MainRenderer {
    public static final int                 WORLD_GROUND_RENDERER_LEVEL = -100;
    public static final int                 MINI_MAP_LEVEL = 100;
    public static final int                 PARTICLE_RENDERER_LEVEL = -99;
    public static final int                 WORLD_TOP_RENDERER_LEVEL = -98;
    public static final int                 CHARACTER_RENDERER_LEVEL = -97;
    public static final int                 JOB_RENDERER_LEVEL = -96;

    private static MainRenderer             _self;
    private static long                     _renderTime;
    private static int                      _frame;

    private Collection<BaseRenderer>        _renders;

    public MainRenderer(GDXRenderer renderer) {
        _self = this;
    }

    public void onRefresh(int frame) {
        for (BaseRenderer render: _renders) {
            render.onRefresh(frame);
        }
    }

    public void gameUpdate(Game game) {
        _renders.stream().filter(BaseRenderer::isLoaded).forEach(BaseRenderer::gameUpdate);
    }

    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        long time = System.currentTimeMillis();

        Game game = Game.getInstance();

        //noinspection Convert2streamapi
        for (BaseRenderer render: _renders) {
            if (!(render instanceof GameDisplay) || render.isMandatory() || (game.hasDisplay(((GameDisplay)render).getName()))) {
                render.draw(renderer, viewport, animProgress);
            }
        }
//        _renders.stream().filter(BaseRenderer::isLoaded)
//                .filter(render -> !(render instanceof GameDisplay) || render.isMandatory() || (game.hasDisplay(((GameDisplay)render).getName())))
//                .forEach(render -> render.draw(renderer, viewport, animProgress));

        _frame++;
        _renderTime += System.currentTimeMillis() - time;
    }

    public void gameStart(Game game, BaseRenderer miniMapRender) {
        _frame = 0;

        // Sort renders by level and addSubJob them to observers
        _renders = game.getModules().stream()
                .filter(module -> module.getClass().isAnnotationPresent(ModuleRenderer.class))
                .flatMap(module -> BaseRenderer.createRenderer(module).stream())
                .sorted((r1, r2) -> r1.getLevel() - r2.getLevel())
                .peek(Application::addObserver)
                .collect(Collectors.toList());

        _renders.forEach(render -> render.gameStart(game));
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
            render.gameStart(Game.getInstance());
        }
    }
}
