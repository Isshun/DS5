package org.smallbox.faraway.client.render.layer.ground.chunkGenerator;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import org.smallbox.faraway.client.AssetManager;
import org.smallbox.faraway.client.render.terrain.TerrainManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.model.MovableModel;
import org.smallbox.faraway.core.game.modelInfo.GraphicInfo;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.world.WorldModule;
import org.smallbox.faraway.util.Constant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.smallbox.faraway.client.render.layer.ground.chunkGenerator.TileGeneratorRule.has;
import static org.smallbox.faraway.client.render.layer.ground.chunkGenerator.TileGeneratorRule.hasNot;
import static org.smallbox.faraway.client.render.terrain.TerrainManager.*;
import static org.smallbox.faraway.core.game.model.MovableModel.Direction.*;

@GameObject
public class RockTileGenerator {
    @Inject private WorldModule worldModule;
    @Inject private TerrainManager terrainManager;
    @Inject private AssetManager assetManager;

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

    public Texture getTexture(ParcelModel parcel) {
        int neighborhood = computeNeighborhood(parcel);

        if (!cachedTextures.containsKey(neighborhood)) {
            GraphicInfo graphicInfo = parcel.getRockInfo().graphics.stream().filter(g -> g.type == GraphicInfo.Type.TERRAIN).findFirst().orElse(null);
            Texture textureIn = assetManager.lazyLoad("data" + graphicInfo.path, Texture.class);
            Pixmap pixmapIn = textureToPixmap(textureIn);
            Pixmap pixmap = new Pixmap(Constant.TILE_SIZE, Constant.TILE_SIZE, Pixmap.Format.RGBA8888);

            // Draw rock
            if (parcel.hasRock() && parcel.getRockInfo().hasGraphics()) {
                rules.stream().filter(rule -> rule.position == 0).filter(rule -> rule.check(worldModule, parcel)).findFirst().ifPresent(rule -> draw(rule, pixmap, pixmapIn, parcel));
                rules.stream().filter(rule -> rule.position == 1).filter(rule -> rule.check(worldModule, parcel)).findFirst().ifPresent(rule -> draw(rule, pixmap, pixmapIn, parcel));
                rules.stream().filter(rule -> rule.position == 2).filter(rule -> rule.check(worldModule, parcel)).findFirst().ifPresent(rule -> draw(rule, pixmap, pixmapIn, parcel));
                rules.stream().filter(rule -> rule.position == 3).filter(rule -> rule.check(worldModule, parcel)).findFirst().ifPresent(rule -> draw(rule, pixmap, pixmapIn, parcel));
            }

            cachedTextures.put(neighborhood, new Texture(pixmap));
            pixmap.dispose();
        }

        return cachedTextures.get(neighborhood);
    }

    private int computeNeighborhood(ParcelModel parcel) {
        int neighborhood = 0;
        neighborhood |= worldModule.checkOrNull(parcel, ParcelModel::hasRock, MovableModel.Direction.TOP_LEFT)       ? 0b100000000 : 0b000000000;
        neighborhood |= worldModule.checkOrNull(parcel, ParcelModel::hasRock, MovableModel.Direction.TOP)            ? 0b010000000 : 0b000000000;
        neighborhood |= worldModule.checkOrNull(parcel, ParcelModel::hasRock, MovableModel.Direction.TOP_RIGHT)      ? 0b001000000 : 0b000000000;
        neighborhood |= worldModule.checkOrNull(parcel, ParcelModel::hasRock, MovableModel.Direction.LEFT)           ? 0b000100000 : 0b000000000;
        neighborhood |= worldModule.checkOrNull(parcel, ParcelModel::hasRock, MovableModel.Direction.NONE)           ? 0b000010000 : 0b000000000;
        neighborhood |= worldModule.checkOrNull(parcel, ParcelModel::hasRock, MovableModel.Direction.RIGHT)          ? 0b000001000 : 0b000000000;
        neighborhood |= worldModule.checkOrNull(parcel, ParcelModel::hasRock, MovableModel.Direction.BOTTOM_LEFT)    ? 0b000000100 : 0b000000000;
        neighborhood |= worldModule.checkOrNull(parcel, ParcelModel::hasRock, MovableModel.Direction.BOTTOM)         ? 0b000000010 : 0b000000000;
        neighborhood |= worldModule.checkOrNull(parcel, ParcelModel::hasRock, MovableModel.Direction.BOTTOM_RIGHT)   ? 0b000000001 : 0b000000000;
        return neighborhood;
    }

    private Pixmap textureToPixmap(Texture textureIn) {
        textureIn.getTextureData().prepare();
        Pixmap pixmap = textureIn.getTextureData().consumePixmap();
        textureIn.dispose();
        return pixmap;
    }

    private void draw(TileGeneratorRule rule, Pixmap pixmapOut, Pixmap pixmapIn, ParcelModel parcel) {
        int outX = rule.position == 1 || rule.position == 3 ? Constant.HALF_TILE_SIZE : 0;
        int outY = rule.position == 2 || rule.position == 3 ? Constant.HALF_TILE_SIZE : 0;
        terrainManager.generate(pixmapOut, pixmapIn, rule.key, rule.position, outX, outY);
    }

}
