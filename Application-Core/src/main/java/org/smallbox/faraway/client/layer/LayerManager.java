package org.smallbox.faraway.client.layer;

import org.smallbox.faraway.client.input.GameClientObserver;
import org.smallbox.faraway.client.input.InputManager;
import org.smallbox.faraway.client.input.WorldInputManager;
import org.smallbox.faraway.client.renderer.MapRenderer;
import org.smallbox.faraway.client.renderer.UIRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.shortcut.GameShortcut;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnGameLayerInit;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnGameStart;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@ApplicationObject
public class LayerManager implements GameClientObserver {
    public static final int MOVE_OFFSET = 30;

    @Inject private LayerManager layerManager;
    @Inject private InputManager inputManager;
    @Inject private UIManager uiManager;
    @Inject private Viewport viewport;
    @Inject private GameManager gameManager;
    @Inject private MapRenderer mapRenderer;
    @Inject private UIRenderer UIRenderer;
    @Inject private WorldInputManager worldInputManager;
    @Inject private DependencyManager dependencyManager;
    @Inject private ApplicationConfig applicationConfig;
    @Inject private Game game;

    private long _renderTime;
    private int _frame;

    // Render
    private double _animationProgress;

    private Collection<BaseLayer> _layers;

    public Viewport getViewport() {
        return viewport;
    }

    @OnGameLayerInit
    public void onGameInitLayers() {
        // Find GameLayer annotated class
        _layers = dependencyManager.getSubTypesOf(BaseLayer.class).stream()
//                new Reflections("org.smallbox.faraway").getSubTypesOf(BaseLayer.class).stream()
//                .filter(cls -> !Modifier.isAbstract(cls.getModifiers()))
//                .map(cls -> {
//                    Log.info("Find game layer: " + cls.getSimpleName());
//
//                    BaseLayer layer = ;
//                    Application.addObserver(layer);
//                    return layer;
//
//                })
//                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(BaseLayer::getLevel))
                .collect(Collectors.toList());

        // Create viewport
        //_viewport.setPosition(0, 0, gameManager.getGame().getInfo().groundFloor);
        //DependencyInjector.getInstance().register(_viewport);
    }

    @OnGameStart
    public void onGameStart() {
        _frame = 0;

        // Call gameStart on each layer
        _layers.forEach(render -> render.gameStart(game));
    }

    public void render(Game game) {

        // Draw
        if (gameManager.isRunning()) {
            _animationProgress = 1 - ((double) (game.getNextUpdate() - System.currentTimeMillis()) / applicationConfig.game.tickInterval);
        }

        layerManager.draw(viewport, _animationProgress, _frame);

        // Move viewport
        if (game.isRunning()) {
            if (worldInputManager.getDirection()[0]) {
                viewport.move(MOVE_OFFSET, 0);
            }
            if (worldInputManager.getDirection()[1]) {
                viewport.move(0, MOVE_OFFSET);
            }
            if (worldInputManager.getDirection()[2]) {
                viewport.move(-MOVE_OFFSET, 0);
            }
            if (worldInputManager.getDirection()[3]) {
                viewport.move(0, -MOVE_OFFSET);
            }
        }

        uiManager.onRefresh(_frame);

        _frame++;
    }

    public void draw(Viewport viewport, double animProgress, int frame) {
        long time = System.currentTimeMillis();

        //noinspection Convert2streamapi
        if (_layers != null) {
            _layers.forEach(render -> render.draw(render instanceof BaseMapLayer ? mapRenderer : UIRenderer, viewport, animProgress, frame));
        }

        _frame++;
        _renderTime += System.currentTimeMillis() - time;
    }

    public int getFrame() {
        return _frame;
    }

    public long getRenderTime() {
        return _frame > 0 ? _renderTime / _frame : 0;
    }

    public Collection<BaseLayer> getLayers() {
        return _layers;
    }

    public void toggleRender(BaseLayer render) {
        if (render.isLoaded()) {
            render.unload();
        } else {
            render.gameStart(game);
        }
    }

    @GameShortcut("map/floor_up")
    public void onFloorUp() {
        viewport.setFloor(viewport.getFloor() + 1);
    }

    @GameShortcut("map/floor_down")
    public void onFloorDown() {
        viewport.setFloor(viewport.getFloor() - 1);
    }

}
