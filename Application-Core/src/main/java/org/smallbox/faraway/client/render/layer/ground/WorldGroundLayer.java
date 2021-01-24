package org.smallbox.faraway.client.render.layer.ground;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.client.render.GDXRendererBase;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.render.layer.BaseMapLayer;
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
public class WorldGroundLayer extends BaseMapLayer {
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
    public void onDraw(GDXRendererBase renderer, Viewport viewport, double animProgress, int frame) {
        int fromX = Math.max(viewport.getWorldPosX(0) - 1, 0);
        int fromY = Math.max(viewport.getWorldPosY(0) - 1, 0);
        int toX = Math.min(viewport.getWorldPosX(applicationConfig.getResolutionWidth()) + 2, worldModule.getWidth());
        int toY = Math.min(viewport.getWorldPosY(applicationConfig.getResolutionHeight()) + 2, worldModule.getWidth());

        for (int x = fromX; x < toX; x++) {
            for (int y = fromY; y < toY; y++) {
                Parcel parcel = worldModule.getParcel(x, y, viewport.getFloor());

                if (parcel != null) {

                    if (parcel.hasGround()) {
                        renderer.drawTextureOnMap(cachedGrounds.computeIfAbsent(parcel, p -> groundTileGenerator.getTexture(p)), parcel);
                    } else {
                        for (int z = 0; z < 100; z++) {
                            Parcel bottomParcel = worldModule.getParcel(x, y, parcel.z - z);
                            if (bottomParcel == null || bottomParcel.hasRock() || bottomParcel.hasGround()) {

                                // Draw bottom parcel
                                if (bottomParcel != null && (bottomParcel.hasRock() || bottomParcel.hasGround())) {
                                    renderer.drawTextureOnMap(cachedGrounds.computeIfAbsent(bottomParcel, p -> groundTileGenerator.getTexture(p)), bottomParcel);
                                }

                                // Draw shadow
                                renderer.drawRectangleOnMap(parcel.x, parcel.y, Constant.TILE_SIZE, Constant.TILE_SIZE, new Color((int)((1 - Math.exp(z * -0.5)) * 255)), 0, 0);

                                break;
                            }
                        }
                    }

                    if (parcel.hasRock()) {
                        renderer.drawTextureOnMap(cachedRocks.computeIfAbsent(parcel, p -> rockTileGenerator.getTexture(p)), parcel);
                    }

                    if (gameSelectionManager.getSelected().contains(parcel)) {
                        renderer.drawCadreOnMap(parcel.x, parcel.y, Constant.TILE_SIZE - 8, Constant.TILE_SIZE - 8, Color.WHITE, 4, 4, 4);
                    }

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
                    gdxRenderer.drawRectangleOnMap(x, y, Constant.TILE_SIZE, Constant.TILE_SIZE, Color.BLACK, 0, 0);
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