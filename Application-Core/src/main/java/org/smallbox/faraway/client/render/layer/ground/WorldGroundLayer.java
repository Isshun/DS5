package org.smallbox.faraway.client.render.layer.ground;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.render.layer.BaseLayer;
import org.smallbox.faraway.client.render.layer.ground.impl.GroundTileGenerator;
import org.smallbox.faraway.client.render.layer.ground.impl.RockTileGenerator;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnGameStart;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfig;
import org.smallbox.faraway.core.module.world.model.Parcel;
import org.smallbox.faraway.modules.world.WorldModule;
import org.smallbox.faraway.util.Constant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@GameObject
@GameLayer(level = LayerManager.WORLD_GROUND_LAYER_LEVEL, visible = true)
public class WorldGroundLayer extends BaseLayer {
    @Inject private GroundTileGenerator groundTileGenerator;
    @Inject private RockTileGenerator rockTileGenerator;
    @Inject private ApplicationConfig applicationConfig;
    @Inject private WorldModule worldModule;
    @Inject private GameSelectionManager gameSelectionManager;
    @Inject private Viewport viewport;
    @Inject private GDXRenderer gdxRenderer;

    private int changeFloorTransition = -1;

    private final Map<Parcel, Texture> cachedGrounds = new ConcurrentHashMap<>();
    private final Map<Parcel, Texture> cachedRocks = new ConcurrentHashMap<>();
    private int floor;

    @OnGameStart
    private void start() {
        floor = viewport.getFloor();
    }

    @Override
    public void onGameLongUpdate(Game game) {
        cachedGrounds.clear();
        cachedRocks.clear();
    }

    @Override
    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        int fromX = Math.max(viewport.getWorldPosX(0) - 1, 0);
        int fromY = Math.max(viewport.getWorldPosY(0) - 1, 0);
        int toX = Math.min(viewport.getWorldPosX(applicationConfig.getResolutionWidth()) + 2, worldModule.getWidth());
        int toY = Math.min(viewport.getWorldPosY(applicationConfig.getResolutionHeight()) + 2, worldModule.getWidth());

        for (int x = fromX; x < toX; x++) {
            for (int y = fromY; y < toY; y++) {
                Parcel parcel = worldModule.getParcel(x, y, floor);

                if (parcel != null && parcel.hasGround()) {
                    renderer.drawTextureOnMap(cachedGrounds.computeIfAbsent(parcel, p -> groundTileGenerator.getTexture(p)), parcel);
                }

                if (parcel != null && parcel.hasRock()) {
                    renderer.drawTextureOnMap(cachedRocks.computeIfAbsent(parcel, p -> rockTileGenerator.getTexture(p)), parcel);
                }

                if (parcel != null && gameSelectionManager.getSelected().contains(parcel)) {
                    renderer.drawRectangleOnMap(parcel.x, parcel.y, Constant.TILE_SIZE - 8, Constant.TILE_SIZE - 8, Color.WHITE, false, 4, 4);
                }
            }
        }

        if (changeFloorTransition != -1) {
            drawTransition();
        }
    }

    private void drawTransition() {
        int fromX = Math.max(viewport.getWorldPosX(0) - 1, 0);
        int fromY = Math.max(viewport.getWorldPosY(0) - 1, 0);
        int toX = Math.min(viewport.getWorldPosX(applicationConfig.getResolutionWidth()) + 2, worldModule.getWidth());
        int toY = Math.min(viewport.getWorldPosY(applicationConfig.getResolutionHeight()) + 2, worldModule.getWidth());

        for (int x = fromX; x < toX; x++) {
            for (int y = fromY; y < toY; y++) {
                if (changeFloorTransition > x + y * worldModule.getWidth()) {
                    gdxRenderer.drawRectangleOnMap(x, y, Constant.TILE_SIZE, Constant.TILE_SIZE, Color.BLACK, true, 0, 0);
                }
            }
        }

        changeFloorTransition += worldModule.getWidth() * 2;

        if (changeFloorTransition >= worldModule.getWidth() * worldModule.getHeight()) {
            changeFloorTransition = -1;
            floor = viewport.getFloor();
        }
    }

    @Override
    public void onFloorChange(int floor) {
//        changeFloorTransition = 0;
        floor = viewport.getFloor();
    }

}