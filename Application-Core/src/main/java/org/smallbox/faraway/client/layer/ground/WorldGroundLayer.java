package org.smallbox.faraway.client.layer.ground;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import org.smallbox.faraway.client.LayerLevel;
import org.smallbox.faraway.client.layer.BaseMapLayer;
import org.smallbox.faraway.client.layer.GameLayer;
import org.smallbox.faraway.client.layer.ground.impl.GroundTileGenerator;
import org.smallbox.faraway.client.layer.ground.impl.RockTileGenerator;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameLongUpdate;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.WorldModule;
import org.smallbox.faraway.util.Constant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@GameObject
@GameLayer(level = LayerLevel.WORLD_GROUND_LAYER_LEVEL, visible = true)
public class WorldGroundLayer extends BaseMapLayer {
    @Inject private GroundTileGenerator groundTileGenerator;
    @Inject private RockTileGenerator rockTileGenerator;
    @Inject private ApplicationConfig applicationConfig;
    @Inject private WorldModule worldModule;

    private final Map<Parcel, Texture> cachedGrounds = new ConcurrentHashMap<>();
    private final Map<Parcel, Texture> cachedRocks = new ConcurrentHashMap<>();

    @OnGameLongUpdate
    public void onGameLongUpdate() {
        cachedGrounds.clear();
        cachedRocks.clear();
    }

    @Override
    protected void onDrawParcel(BaseRenderer renderer, Parcel parcel) {
        // Draw ground
        if (parcel.hasGround()) {
            renderer.drawTextureOnMap(parcel, cachedGrounds.computeIfAbsent(parcel, p -> groundTileGenerator.getTexture(p)));
        } else {
            drawDeepParcels(renderer, parcel);
        }

        // Draw rock
        if (parcel.hasRock()) {
            if (parcel.getRampDirection() != null) {
                renderer.drawTextOnMap(parcel, "RAMP", Color.BLACK, 32, 0, 0);
            } else {
                renderer.drawTextureOnMap(parcel, cachedRocks.computeIfAbsent(parcel, p -> rockTileGenerator.getTexture(p)));
            }
        }
    }

    private void drawDeepParcels(BaseRenderer renderer, Parcel parcel) {
        for (int z = 0; z < 100; z++) {
            Parcel bottomParcel = worldModule.getParcel(parcel.x, parcel.y, parcel.z - z);
            if (bottomParcel == null || bottomParcel.hasRock() || bottomParcel.hasGround()) {

                // Draw bottom parcel
                if (bottomParcel != null && (bottomParcel.hasRock() || bottomParcel.hasGround())) {
                    renderer.drawTextureOnMap(bottomParcel, cachedGrounds.computeIfAbsent(bottomParcel, p -> groundTileGenerator.getTexture(p)));
                }

                // Draw shadow
                renderer.drawRectangleOnMap(parcel, Constant.TILE_SIZE, Constant.TILE_SIZE, new Color((int) ((1 - Math.exp(z * -0.5)) * 255)), 0, 0);

                break;
            }
        }
    }

}