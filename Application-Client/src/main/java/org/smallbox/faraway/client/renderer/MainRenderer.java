package org.smallbox.faraway.client.renderer;

import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.ModuleRenderer;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

public class MainRenderer implements GameObserver {
    public static final int                 WORLD_GROUND_RENDERER_LEVEL = -100;
    public static final int                 MINI_MAP_LEVEL = 100;
    public static final int                 PARTICLE_RENDERER_LEVEL = -99;
    public static final int                 WORLD_TOP_RENDERER_LEVEL = -98;
    public static final int                 CHARACTER_RENDERER_LEVEL = -97;
    public static final int                 JOB_RENDERER_LEVEL = -96;

    private static long                     _renderTime;
    private static int                      _frame;

    // Render
//    private int                             _frame;
    private double                          _animationProgress;

    private Collection<BaseRenderer>        _renders;
    private Viewport                        _viewport;

    public void onRefresh(int frame) {
        for (BaseRenderer render: _renders) {
            render.onRefresh(frame);
        }
    }

    public Viewport getViewport() { return _viewport; }

    @Override
    public void onGameStart(Game game) {
        _frame = 0;

        // Sort renders by level and addSubJob them to observers
        _renders = game.getModules().stream()
                .filter(module -> module.getClass().isAnnotationPresent(ModuleRenderer.class))
                .flatMap(module -> BaseRenderer.createRenderer(module).stream())
                .sorted(Comparator.comparingInt(BaseRenderer::getLevel))
                .peek(Application::addObserver)
                .collect(Collectors.toList());

        _renders.forEach(render -> render.gameStart(game));

        _viewport = new Viewport(400, 300);
        _viewport.setPosition(-500, -3800, 7);
    }

    @Override
    public void onGameUpdate(Game game) {
        _renders.stream().filter(BaseRenderer::isLoaded).forEach(BaseRenderer::gameUpdate);
    }

    @Override
    public void onGameRender(Game game) {
        // Draw
        if (!Application.gameManager.isRunning()) {
            _animationProgress = 1 - ((double) (game.getNextUpdate() - System.currentTimeMillis()) / game.getTickInterval());
        }

        ApplicationClient.mainRenderer.onDraw(ApplicationClient.gdxRenderer, _viewport, _animationProgress);

        if (game.isRunning()) {
            if (ApplicationClient.inputManager.getDirection()[0]) { _viewport.move(20, 0); }
            if (ApplicationClient.inputManager.getDirection()[1]) { _viewport.move(0, 20); }
            if (ApplicationClient.inputManager.getDirection()[2]) { _viewport.move(-20, 0); }
            if (ApplicationClient.inputManager.getDirection()[3]) { _viewport.move(0, -20); }
        }

        ApplicationClient.mainRenderer.onRefresh(_frame);

        // TODO
        try {
            ApplicationClient.uiManager.onRefresh(_frame);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        _gameAction.draw(renderer);
        _frame++;
    }

//    @Override
//    public void onReloadUI() {
//        ApplicationClient.uiEventManager.clear();
//        ApplicationClient.uiManager.clearViews();
//    }

    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        long time = System.currentTimeMillis();

        Game game = Application.gameManager.getGame();

        //noinspection Convert2streamapi
        _renders.forEach(render -> render.draw(renderer, viewport, animProgress));

        _frame++;
        _renderTime += System.currentTimeMillis() - time;
    }

    public static int getFrame() { return _frame; }

    public static long getRenderTime() { return _frame > 0 ? _renderTime / _frame : 0; }

    public Collection<BaseRenderer> getRenders() {
        return _renders;
    }

    public void toggleRender(BaseRenderer render) {
        if (render.isLoaded()) {
            render.unload();
        } else {
            render.gameStart(Application.gameManager.getGame());
        }
    }
}
