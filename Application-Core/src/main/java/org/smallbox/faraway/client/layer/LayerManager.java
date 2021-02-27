package org.smallbox.faraway.client.layer;

import org.smallbox.faraway.client.renderer.MapRenderer;
import org.smallbox.faraway.client.renderer.UIRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameLayerBegin;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameStart;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.WorldModule;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@ApplicationObject
public class LayerManager {
    @Inject private UIManager uiManager;
    @Inject private GameManager gameManager;
    @Inject private MapRenderer mapRenderer;
    @Inject private UIRenderer UIRenderer;
    @Inject private DependencyManager dependencyManager;
    @Inject private ApplicationConfig applicationConfig;
    @Inject private WorldModule worldModule;
    @Inject private Viewport viewport;
    @Inject private Game game;

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

    public void render() {

        // Draw
        if (gameManager.isRunning()) {
            _animationProgress = 1 - ((double) (game.getNextUpdate() - System.currentTimeMillis()) / applicationConfig.game.tickInterval);
        }

        draw(_animationProgress, _frame);

        viewport.move();

        uiManager.onRefresh(_frame);

        _frame++;
    }

    public void draw(double animProgress, int frame) {
        if (_layers != null) {
            int fromX = Math.max(viewport.getWorldPosX(0) - 1, 0);
            int fromY = Math.max(viewport.getWorldPosY(0) - 1, 0);
            int toX = Math.min(viewport.getWorldPosX(applicationConfig.getResolutionWidth()) + 2, worldModule.getWidth());
            int toY = Math.min(viewport.getWorldPosY(applicationConfig.getResolutionHeight()) + 2, worldModule.getWidth());

            for (BaseLayer layer : _layers) {
                layer.draw(layer instanceof BaseMapLayer ? mapRenderer : UIRenderer, viewport, animProgress, frame);

                for (int x = fromX; x < toX; x++) {
                    for (int y = fromY; y < toY; y++) {
                        Parcel parcel = worldModule.getParcel(x, y, viewport.getFloor());
                        if (parcel != null) {
                            layer.drawParcel(layer instanceof BaseMapLayer ? mapRenderer : UIRenderer, parcel);
                        }
                    }
                }
            }

        }
        _frame++;
    }

    public int getFrame() {
        return _frame;
    }

    public Collection<BaseLayer> getLayers() {
        return _layers;
    }

}
