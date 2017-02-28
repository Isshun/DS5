package org.smallbox.faraway.client.renderer;

import org.reflections.Reflections;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.GameClientObserver;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.util.Log;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.stream.Collectors;

public class MainRenderer implements GameClientObserver {
    public static final int                 MINI_MAP_LEVEL = 100;
    public static final int                 PARTICLE_RENDERER_LEVEL = -100;
    public static final int                 CONSUMABLE_RENDERER_LEVEL = -101;
    public static final int                 CHARACTER_RENDERER_LEVEL = -102;
    public static final int                 ITEM_RENDERER_LEVEL = -103;
    public static final int                 STRUCTURE_RENDERER_LEVEL = -104;
    public static final int                 WORLD_GROUND_RENDERER_LEVEL = -105;
    public static final int                 WORLD_TOP_RENDERER_LEVEL = -106;
//    public static final int                 JOB_RENDERER_LEVEL = -96;

    private static long                     _renderTime;
    private static int                      _frame;

    // Render
//    private int                             _frame;
    private double                          _animationProgress;

    private Collection<BaseRenderer>        _renders;
    private Viewport                        _viewport;

    public Viewport getViewport() { return _viewport; }

    @Override
    public void onGameCreate(Game game) {
        // Find GameRenderer annotated class
        _renders = new Reflections("org.smallbox.faraway").getSubTypesOf(BaseRenderer.class).stream()
                .filter(cls -> !Modifier.isAbstract(cls.getModifiers()))
                .map(cls -> {
                    Log.info("Find game renderer: " + cls.getSimpleName());

                    try {
                        BaseRenderer renderer = cls.newInstance();
                        Application.dependencyInjector.register(renderer);
                        Application.addObserver(renderer);
                        return renderer;
                    } catch ( IllegalAccessException | InstantiationException e) {
                        Log.error(e);
                    }
                    return null;
                })
                .filter(renderer -> renderer != null)
                .sorted((o1, o2) -> o1.getLevel() - o2.getLevel())
                .collect(Collectors.toList());

        _renders.forEach(render -> render.onGameCreate(game));
    }

    @Override
    public void onGameStart(Game game) {
        _frame = 0;

//        // Sort renders by level and addSubJob them to observers
//        _renders = game.getModules().stream()
//                .filter(module -> module.getClass().isAnnotationPresent(ModuleRenderer.class))
//                .flatMap(module -> BaseRenderer.createRenderer(module).stream())
//                .sorted(Comparator.comparingInt(BaseRenderer::getLevel))
//                .peek(Application::addObserver)
//                .collect(Collectors.toList());

        _renders.forEach(render -> render.gameStart(game));

        _viewport = new Viewport(400, 300);
        _viewport.setPosition(0, 0, game.getInfo().groundFloor);

        ApplicationClient.dependencyInjector.register(_viewport);

        ApplicationClient.notify(observer -> observer.onFloorChange(game.getInfo().groundFloor));
    }

    @Override
    public void onGameUpdate(Game game) {
        if (_renders != null) {
            _renders.stream().filter(BaseRenderer::isLoaded).forEach(BaseRenderer::gameUpdate);
        }
    }

    @Override
    public void onGameRender(Game game) {
        // Draw
        if (Application.gameManager.isRunning()) {
            _animationProgress = 1 - ((double) (game.getNextUpdate() - System.currentTimeMillis()) / game.getTickInterval());
        }

        ApplicationClient.mainRenderer.draw(ApplicationClient.gdxRenderer, _viewport, _animationProgress, _frame);

        if (game.isRunning()) {
            if (ApplicationClient.inputManager.getDirection()[0]) { _viewport.move(20, 0); }
            if (ApplicationClient.inputManager.getDirection()[1]) { _viewport.move(0, 20); }
            if (ApplicationClient.inputManager.getDirection()[2]) { _viewport.move(-20, 0); }
            if (ApplicationClient.inputManager.getDirection()[3]) { _viewport.move(0, -20); }
        }

        // TODO
        try {
            ApplicationClient.uiManager.onRefresh(_frame);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        _gameAction.drawPixel(renderer);
        _frame++;
    }

//    @Override
//    public void onReloadUI() {
//        ApplicationClient.uiEventManager.clear();
//        ApplicationClient.uiManager.clearViews();
//    }

    public void draw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        long time = System.currentTimeMillis();

        //noinspection Convert2streamapi
        if (_renders != null) {
            _renders.forEach(render -> render.draw(renderer, viewport, animProgress, frame));
        }

        MainRenderer._frame++;
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

    @GameShortcut(key = GameEventListener.Key.PAGEUP)
    public void onFloorUp() {
        _viewport.setFloor(_viewport.getFloor() + 1);
    }

    @GameShortcut(key = GameEventListener.Key.PAGEDOWN)
    public void onFloorDown() {
        _viewport.setFloor(_viewport.getFloor() - 1);
    }

}
