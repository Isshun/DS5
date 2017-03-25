package org.smallbox.faraway.client.render;

import com.badlogic.gdx.Input;
import org.reflections.Reflections;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.GameClientObserver;
import org.smallbox.faraway.client.render.layer.BaseLayer;
import org.smallbox.faraway.client.render.layer.GDXRenderer;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.util.Log;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

public class LayerManager implements GameClientObserver {
    public static final int                 MINI_MAP_LEVEL = 100;
    public static final int                 PARTICLE_LAYER_LEVEL = -100;
    public static final int                 CHARACTER_LAYER_LEVEL = -101;
    public static final int                 CONSUMABLE_LAYER_LEVEL = -102;
    public static final int                 PLANT_LAYER_LEVEL = -103;
    public static final int                 ITEM_LAYER_LEVEL = -103;
    public static final int                 STRUCTURE_LAYER_LEVEL = -104;
    public static final int                 AREA_LAYER_LEVEL = -105;
    public static final int                 ROOM_LAYER_LEVEL = -105;
    public static final int                 WORLD_GROUND_LAYER_LEVEL = -106;
    public static final int                 WORLD_TOP_LAYER_LEVEL = -107;
//    public static final int                 JOB_LAYER_LEVEL = -96;

    private long                            _renderTime;
    private int                             _frame;

    // Render
//    private int                             _frame;
    private double                          _animationProgress;

    private Collection<BaseLayer>        _renders;
    private Viewport                        _viewport;

    public Viewport getViewport() { return _viewport; }

    @Override
    public void onGameCreateObserver(Game game) {
        // Find GameLayer annotated class
        _renders = new Reflections("org.smallbox.faraway").getSubTypesOf(BaseLayer.class).stream()
                .filter(cls -> !Modifier.isAbstract(cls.getModifiers()))
                .map(cls -> {
                    Log.info("Find game layer: " + cls.getSimpleName());

                    try {
                        BaseLayer layer = cls.newInstance();
                        Application.dependencyInjector.register(layer);
                        Application.addObserver(layer);
                        return layer;
                    } catch ( IllegalAccessException | InstantiationException e) {
                        throw new GameException(LayerManager.class, e);
                    }

                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(BaseLayer::getLevel))
                .collect(Collectors.toList());

        _renders.forEach(render -> render.onGameCreateObserver(game));
    }

    @Override
    public void onGameStart(Game game) {
        _frame = 0;

//        // Sort renders by level and addSubJob them to observers
//        _renders = game.getModules().stream()
//                .filter(module -> module.getClass().isAnnotationPresent(ModuleLayer.class))
//                .flatMap(module -> BaseLayer.createLayer(module).stream())
//                .sorted(Comparator.comparingInt(BaseLayer::getLevel))
//                .peek(Application::addObserver)
//                .collect(Collectors.toList());

        _renders.forEach(render -> render.gameStart(game));

        _viewport = new Viewport(400, 300);
        _viewport.setPosition(0, 0, game.getInfo().groundFloor);

        ApplicationClient.dependencyInjector.register(_viewport);

        ApplicationClient.notify(observer -> observer.onFloorChange(game.getInfo().groundFloor));
    }

    @Override
    public void onGameRender(Game game) {

        // Draw
        if (Application.gameManager.isRunning()) {
            _animationProgress = 1 - ((double) (game.getNextUpdate() - System.currentTimeMillis()) / game.getTickInterval());
        }

        ApplicationClient.LAYER_MANAGER.draw(ApplicationClient.GDX_LAYER, _viewport, _animationProgress, _frame);

        if (game.isRunning()) {
            if (ApplicationClient.inputManager.getDirection()[0]) { _viewport.move(20, 0); }
            if (ApplicationClient.inputManager.getDirection()[1]) { _viewport.move(0, 20); }
            if (ApplicationClient.inputManager.getDirection()[2]) { _viewport.move(-20, 0); }
            if (ApplicationClient.inputManager.getDirection()[3]) { _viewport.move(0, -20); }
        }

        ApplicationClient.uiManager.onRefresh(_frame);

//        _gameAction.drawPixel(layer);
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

        _frame++;
        _renderTime += System.currentTimeMillis() - time;
    }

    public int getFrame() { return _frame; }

    public long getRenderTime() { return _frame > 0 ? _renderTime / _frame : 0; }

    public Collection<BaseLayer> getRenders() {
        return _renders;
    }

    public void toggleRender(BaseLayer render) {
        if (render.isLoaded()) {
            render.unload();
        } else {
            render.gameStart(Application.gameManager.getGame());
        }
    }

    @GameShortcut(key = Input.Keys.PAGE_UP)
    public void onFloorUp() {
        _viewport.setFloor(_viewport.getFloor() + 1);
    }

    @GameShortcut(key = Input.Keys.PAGE_DOWN)
    public void onFloorDown() {
        _viewport.setFloor(_viewport.getFloor() - 1);
    }

}
