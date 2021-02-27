package org.smallbox.faraway.client.layer.ground.impl;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import org.smallbox.faraway.client.asset.AssetManager;
import org.smallbox.faraway.client.asset.terrain.TerrainManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.game.world.Parcel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888;
import static org.smallbox.faraway.core.game.modelInfo.GraphicInfo.Type.TERRAIN;
import static org.smallbox.faraway.util.Constant.TILE_SIZE;

@GameObject
public class GroundTileGenerator {
    @Inject private TerrainManager terrainManager;
    @Inject private AssetManager assetManager;

    private final Map<Integer, Texture> cachedTextures = new HashMap<>();

    public Texture getTexture(Parcel parcel) {
        int neighborhood = computeNeighborhood(parcel);

        if (!cachedTextures.containsKey(neighborhood)) {
            Optional.ofNullable(parcel.getGroundInfo().getGraphicInfo(TERRAIN)).ifPresent(
                    terrainGraphicInfo -> assetManager.temporaryPixmap(terrainGraphicInfo.absolutePath,
                            pixmapIn -> cachedTextures.put(neighborhood, buildTexture(pixmapIn, parcel))
                    )
            );
        }

        return cachedTextures.get(neighborhood);
    }

    private Texture buildTexture(Pixmap pixmapIn, Parcel parcel) {
        return assetManager.createTextureFromPixmap(TILE_SIZE, TILE_SIZE, RGBA8888, pixmap -> draw(pixmap, pixmapIn, parcel));
    }

    private void draw(Pixmap pixmapOut, Pixmap pixmapIn, Parcel parcel) {
        terrainManager.generate(pixmapOut, pixmapIn, 0, 0, parcel);

//        assetManager.temporaryPixmap("data/graphics/texture/g2_decoration_3.png", pixmap -> {
//            pixmapOut.drawPixmap(pixmap, 0, 0, new Random().nextInt(4) * 128, new Random().nextInt(4) * 128, 128, 128);
//        });
    }
//
//    @OnGameStop
//    private void gameStop() {
//        cachedTextures.values().forEach(Texture::dispose);
//    }

    private int computeNeighborhood(Parcel parcel) {
        int neighborhood = 0;
        neighborhood |= parcel.y % 4 == 3 ? 0b10000000 : 0;
        neighborhood |= parcel.y % 4 == 2 ? 0b01000000 : 0;
        neighborhood |= parcel.y % 4 == 1 ? 0b00100000 : 0;
        neighborhood |= parcel.y % 4 == 0 ? 0b00010000 : 0;
        neighborhood |= parcel.x % 4 == 3 ? 0b00001000 : 0;
        neighborhood |= parcel.x % 4 == 2 ? 0b00000100 : 0;
        neighborhood |= parcel.x % 4 == 1 ? 0b00000010 : 0;
        neighborhood |= parcel.x % 4 == 0 ? 0b00000001 : 0;
        return neighborhood;
    }

}
