package org.smallbox.faraway.client.layer.ground.impl;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import org.smallbox.faraway.client.asset.AssetManager;
import org.smallbox.faraway.client.asset.terrain.TerrainManager;
import org.smallbox.faraway.client.layer.ground.TileGeneratorRule;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameStop;
import org.smallbox.faraway.core.game.model.MovableModel;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.WorldModule;

import java.util.*;

import static com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888;
import static org.smallbox.faraway.client.asset.terrain.TerrainManager.*;
import static org.smallbox.faraway.client.layer.ground.TileGeneratorRule.has;
import static org.smallbox.faraway.client.layer.ground.TileGeneratorRule.hasNot;
import static org.smallbox.faraway.core.game.model.MovableModel.Direction.*;
import static org.smallbox.faraway.core.game.modelInfo.GraphicInfo.Type.TERRAIN;
import static org.smallbox.faraway.util.Constant.HALF_TILE_SIZE;
import static org.smallbox.faraway.util.Constant.TILE_SIZE;

@GameObject
public class RockTileGenerator {
    @Inject private TerrainManager terrainManager;
    @Inject private AssetManager assetManager;
    @Inject private WorldModule worldModule;

    public List<TileGeneratorRule> rules = Arrays.asList(
            new TileGeneratorRule(0, TERRAIN_FULL, has(TOP), has(LEFT), has(TOP_LEFT)),
            new TileGeneratorRule(0, TERRAIN_INNER_BOTTOM_RIGHT, hasNot(TOP_LEFT), has(TOP), has(LEFT)),
            new TileGeneratorRule(0, TERRAIN_CORNER_TOP_LEFT, hasNot(TOP), hasNot(LEFT)),
            new TileGeneratorRule(0, TERRAIN_TOP, hasNot(TOP)),
            new TileGeneratorRule(0, TERRAIN_LEFT, hasNot(LEFT)),
            new TileGeneratorRule(1, TERRAIN_FULL, has(TOP), has(RIGHT), has(TOP_RIGHT)),
            new TileGeneratorRule(1, TERRAIN_INNER_BOTTOM_LEFT, hasNot(TOP_RIGHT), has(TOP), has(RIGHT)),
            new TileGeneratorRule(1, TERRAIN_CORNER_TOP_RIGHT, hasNot(TOP), hasNot(RIGHT)),
            new TileGeneratorRule(1, TERRAIN_TOP, hasNot(TOP)),
            new TileGeneratorRule(1, TERRAIN_RIGHT, hasNot(RIGHT)),
            new TileGeneratorRule(2, TERRAIN_FULL, has(BOTTOM), has(LEFT), has(BOTTOM_LEFT)),
            new TileGeneratorRule(2, TERRAIN_INNER_TOP_RIGHT, hasNot(BOTTOM_LEFT), has(BOTTOM), has(LEFT)),
            new TileGeneratorRule(2, TERRAIN_CORNER_BOTTOM_LEFT, hasNot(BOTTOM), hasNot(LEFT)),
            new TileGeneratorRule(2, TERRAIN_BOTTOM, hasNot(BOTTOM)),
            new TileGeneratorRule(2, TERRAIN_LEFT, hasNot(LEFT)),
            new TileGeneratorRule(3, TERRAIN_FULL, has(BOTTOM), has(RIGHT), has(BOTTOM_RIGHT)),
            new TileGeneratorRule(3, TERRAIN_INNER_TOP_LEFT, hasNot(BOTTOM_RIGHT), has(BOTTOM), has(RIGHT)),
            new TileGeneratorRule(3, TERRAIN_CORNER_BOTTOM_RIGHT, hasNot(BOTTOM), hasNot(RIGHT)),
            new TileGeneratorRule(3, TERRAIN_BOTTOM, hasNot(BOTTOM)),
            new TileGeneratorRule(3, TERRAIN_RIGHT, hasNot(RIGHT))
    );

    private final Map<Integer, Texture> cachedTextures = new HashMap<>();

    public Texture getTexture(Parcel parcel) {
        int neighborhood = computeNeighborhood(parcel);

        if (!cachedTextures.containsKey(neighborhood)) {
            Optional.ofNullable(parcel.getRockInfo().getGraphicInfo(TERRAIN)).ifPresent(
                    terrainGraphicInfo -> assetManager.temporaryPixmap(terrainGraphicInfo.absolutePath,
                            pixmapIn -> cachedTextures.put(neighborhood, buildTexture(pixmapIn, parcel))
                    )
            );
        }

        return cachedTextures.get(neighborhood);
    }

    private Texture buildTexture(Pixmap pixmapIn, Parcel parcel) {
        return assetManager.createTextureFromPixmap(TILE_SIZE, TILE_SIZE, RGBA8888, pixmap -> {
            rules.stream().filter(rule -> rule.position == 0).filter(rule -> rule.check(worldModule, parcel)).findFirst().ifPresent(rule -> draw(rule, pixmap, pixmapIn, parcel));
            rules.stream().filter(rule -> rule.position == 1).filter(rule -> rule.check(worldModule, parcel)).findFirst().ifPresent(rule -> draw(rule, pixmap, pixmapIn, parcel));
            rules.stream().filter(rule -> rule.position == 2).filter(rule -> rule.check(worldModule, parcel)).findFirst().ifPresent(rule -> draw(rule, pixmap, pixmapIn, parcel));
            rules.stream().filter(rule -> rule.position == 3).filter(rule -> rule.check(worldModule, parcel)).findFirst().ifPresent(rule -> draw(rule, pixmap, pixmapIn, parcel));
        });
    }

    @OnGameStop
    private void gameStop() {
        cachedTextures.values().forEach(Texture::dispose);
    }

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

        neighborhood |= worldModule.checkOrNull(parcel, Parcel::hasRock, MovableModel.Direction.TOP_LEFT) ? 0b100000000 << 8 : 0;
        neighborhood |= worldModule.checkOrNull(parcel, Parcel::hasRock, MovableModel.Direction.TOP) ? 0b010000000 << 8 : 0;
        neighborhood |= worldModule.checkOrNull(parcel, Parcel::hasRock, MovableModel.Direction.TOP_RIGHT) ? 0b001000000 << 8 : 0;
        neighborhood |= worldModule.checkOrNull(parcel, Parcel::hasRock, MovableModel.Direction.LEFT) ? 0b000100000 << 8 : 0;
        neighborhood |= worldModule.checkOrNull(parcel, Parcel::hasRock, MovableModel.Direction.NONE) ? 0b000010000 << 8 : 0;
        neighborhood |= worldModule.checkOrNull(parcel, Parcel::hasRock, MovableModel.Direction.RIGHT) ? 0b000001000 << 8 : 0;
        neighborhood |= worldModule.checkOrNull(parcel, Parcel::hasRock, MovableModel.Direction.BOTTOM_LEFT) ? 0b000000100 << 8 : 0;
        neighborhood |= worldModule.checkOrNull(parcel, Parcel::hasRock, MovableModel.Direction.BOTTOM) ? 0b000000010 << 8 : 0;
        neighborhood |= worldModule.checkOrNull(parcel, Parcel::hasRock, MovableModel.Direction.BOTTOM_RIGHT) ? 0b000000001 << 8 : 0;

        return neighborhood;
    }

    private void draw(TileGeneratorRule rule, Pixmap pixmapOut, Pixmap pixmapIn, Parcel parcel) {
        int outX = rule.position == 1 || rule.position == 3 ? HALF_TILE_SIZE : 0;
        int outY = rule.position == 2 || rule.position == 3 ? HALF_TILE_SIZE : 0;
        terrainManager.generate(pixmapOut, pixmapIn, rule.key, rule.position, outX, outY, parcel);
    }

}
