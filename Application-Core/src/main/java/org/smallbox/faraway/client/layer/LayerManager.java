package org.smallbox.faraway.client.layer;

import org.smallbox.faraway.client.input.InputManager;
import org.smallbox.faraway.client.input.CameraMoveInputManager;
import org.smallbox.faraway.client.renderer.MapRenderer;
import org.smallbox.faraway.client.renderer.UIRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.shortcut.GameShortcut;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameLayerBegin;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameStart;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@ApplicationObject
public class LayerManager {
    public static final int MOVE_OFFSET = 30;

    @Inject private LayerManager layerManager;
    @Inject private InputManager inputManager;
    @Inject private UIManager uiManager;
    @Inject private GameManager gameManager;
    @Inject private MapRenderer mapRenderer;
    @Inject private UIRenderer UIRenderer;
    @Inject private CameraMoveInputManager cameraMoveInputManager;
    @Inject private DependencyManager dependencyManager;
    @Inject private ApplicationConfig applicationConfig;
    @Inject private Viewport viewport;
    @Inject private Game game;

    private long _renderTime;
    private double _animationProgress;
    private Collection<BaseLayer> _layers;
    private int _frame;

    @OnGameLayerBegin
    public void onGameInitLayers() {
        _layers = dependencyManager.getSubTypesOf(BaseLayer.class).stream()
                .sorted(Comparator.comparingInt(BaseLayer::getLevel))
                .collect(Collectors.toList());
    }

    @OnGameStart
    public void onGameStart() {
        _frame = 0;
    }

    public void render(Game game) {

        // Draw
        if (gameManager.isRunning()) {
            _animationProgress = 1 - ((double) (game.getNextUpdate() - System.currentTimeMillis()) / applicationConfig.game.tickInterval);
        }

        layerManager.draw(viewport, _animationProgress, _frame);

        // Move viewport
        if (game.isRunning()) {
            if (cameraMoveInputManager.isMovingLeft()) {
                viewport.move(MOVE_OFFSET, 0);
            }
            if (cameraMoveInputManager.isMovingUp()) {
                viewport.move(0, MOVE_OFFSET);
            }
            if (cameraMoveInputManager.isMovingRight()) {
                viewport.move(-MOVE_OFFSET, 0);
            }
            if (cameraMoveInputManager.isMovingDown()) {
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

    @GameShortcut("map/floor_up")
    public void onFloorUp() {
        viewport.setFloor(viewport.getFloor() + 1);
    }

    @GameShortcut("map/floor_down")
    public void onFloorDown() {
        viewport.setFloor(viewport.getFloor() - 1);
    }

}
