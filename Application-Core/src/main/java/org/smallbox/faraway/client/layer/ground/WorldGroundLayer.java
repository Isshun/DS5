package org.smallbox.faraway.client.layer.ground;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import org.smallbox.faraway.client.asset.AssetManager;
import org.smallbox.faraway.client.renderer.MapRenderer;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.layer.LayerManager;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.layer.BaseMapLayer;
import org.smallbox.faraway.client.layer.ground.impl.GroundTileGenerator;
import org.smallbox.faraway.client.layer.ground.impl.RockTileGenerator;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.client.layer.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnGameStart;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.WorldModule;
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
    @Inject private AssetManager assetManager;
    @Inject private GameSelectionManager gameSelectionManager;
    @Inject private Viewport viewport;
    @Inject private MapRenderer mapRenderer;

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
    public void onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {
        int fromX = Math.max(viewport.getWorldPosX(0) - 1, 0);
        int fromY = Math.max(viewport.getWorldPosY(0) - 1, 0);
        int toX = Math.min(viewport.getWorldPosX(applicationConfig.getResolutionWidth()) + 2, worldModule.getWidth());
        int toY = Math.min(viewport.getWorldPosY(applicationConfig.getResolutionHeight()) + 2, worldModule.getWidth());

        for (int x = fromX; x < toX; x++) {
            for (int y = fromY; y < toY; y++) {
                Parcel parcel = worldModule.getParcel(x, y, viewport.getFloor());

                if (parcel != null) {

                    if (parcel.hasGround()) {
                        renderer.drawTextureOnMap(parcel, cachedGrounds.computeIfAbsent(parcel, p -> groundTileGenerator.getTexture(p)));
                    } else {
                        for (int z = 0; z < 100; z++) {
                            Parcel bottomParcel = worldModule.getParcel(x, y, parcel.z - z);
                            if (bottomParcel == null || bottomParcel.hasRock() || bottomParcel.hasGround()) {

                                // Draw bottom parcel
                                if (bottomParcel != null && (bottomParcel.hasRock() || bottomParcel.hasGround())) {
                                    renderer.drawTextureOnMap(bottomParcel, cachedGrounds.computeIfAbsent(bottomParcel, p -> groundTileGenerator.getTexture(p)));
                                }

                                // Draw shadow
                                renderer.drawRectangleOnMap(parcel, Constant.TILE_SIZE, Constant.TILE_SIZE, new Color((int)((1 - Math.exp(z * -0.5)) * 255)), 0, 0);

                                break;
                            }
                        }
                    }

                    if (parcel.hasRock()) {

                        if (parcel.getRampDirection() != null) {
                            renderer.drawTextOnMap(parcel, "RAMP", Color.BLACK, 32, 0, 0);
                        } else {
                            renderer.drawTextureOnMap(parcel, cachedRocks.computeIfAbsent(parcel, p -> rockTileGenerator.getTexture(p)));
                        }
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
                    mapRenderer.drawRectangleOnMap(x, y, Constant.TILE_SIZE, Constant.TILE_SIZE, Color.BLACK, 0, 0);
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