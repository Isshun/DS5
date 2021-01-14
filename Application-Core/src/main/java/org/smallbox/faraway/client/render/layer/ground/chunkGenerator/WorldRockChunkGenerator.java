package org.smallbox.faraway.client.render.layer.ground.chunkGenerator;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.render.terrain.TerrainManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnGameLayerInit;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.model.MovableModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.world.WorldModule;
import org.smallbox.faraway.util.Constant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static org.smallbox.faraway.client.render.terrain.TerrainManager.*;
import static org.smallbox.faraway.core.game.model.MovableModel.Direction.*;

@GameObject
public class WorldRockChunkGenerator {
    @Inject private WorldModule worldModule;
    @Inject private Viewport viewport;
    @Inject private Game game;
    @Inject private Data data;
    @Inject private TerrainManager terrainManager;

    public List<Rule> rules = Arrays.asList(
            new Rule(0, TERRAIN_FULL, ParcelModel::hasRock, TOP, LEFT, TOP_LEFT),
            new Rule(0, TERRAIN_INNER_BOTTOM_RIGHT, TOP_LEFT, TOP, LEFT),
            new Rule(0, TERRAIN_CORNER_TOP_LEFT, p -> !p.hasRock(), TOP, LEFT),
            new Rule(0, TERRAIN_TOP, p -> !p.hasRock(), TOP),
            new Rule(0, TERRAIN_LEFT, p -> !p.hasRock(), LEFT),
            new Rule(1, TERRAIN_FULL, ParcelModel::hasRock, TOP, RIGHT, TOP_RIGHT),
            new Rule(1, TERRAIN_INNER_BOTTOM_LEFT, TOP_RIGHT, TOP, RIGHT),
            new Rule(1, TERRAIN_CORNER_TOP_RIGHT, p -> !p.hasRock(), TOP, RIGHT),
            new Rule(1, TERRAIN_TOP, p -> !p.hasRock(), TOP),
            new Rule(1, TERRAIN_RIGHT, p -> !p.hasRock(), RIGHT),
            new Rule(2, TERRAIN_FULL, ParcelModel::hasRock, BOTTOM, LEFT, BOTTOM_LEFT),
            new Rule(2, TERRAIN_INNER_TOP_RIGHT, BOTTOM_LEFT, BOTTOM, LEFT),
            new Rule(2, TERRAIN_CORNER_BOTTOM_LEFT, p -> !p.hasRock(), BOTTOM, LEFT),
            new Rule(2, TERRAIN_BOTTOM, p -> !p.hasRock(), BOTTOM),
            new Rule(2, TERRAIN_LEFT, p -> !p.hasRock(), LEFT),
            new Rule(3, TERRAIN_FULL, ParcelModel::hasRock, BOTTOM, RIGHT, BOTTOM_RIGHT),
            new Rule(3, TERRAIN_INNER_TOP_LEFT, BOTTOM_RIGHT, BOTTOM, RIGHT),
            new Rule(3, TERRAIN_CORNER_BOTTOM_RIGHT, p -> !p.hasRock(), BOTTOM, RIGHT),
            new Rule(3, TERRAIN_BOTTOM, p -> !p.hasRock(), BOTTOM),
            new Rule(3, TERRAIN_RIGHT, p -> !p.hasRock(), RIGHT)
    );

    private final Map<Integer, Texture> cachedTextures = new HashMap<>();

    public Texture getTexture(ParcelModel parcel, int neighborhood, Texture textureRock) {

        if (!cachedTextures.containsKey(neighborhood)) {
            Pixmap pixmap = new Pixmap(Constant.TILE_SIZE, Constant.TILE_SIZE, Pixmap.Format.RGBA8888);

            // Draw rock
            if (parcel.hasRock() && parcel.getRockInfo().hasGraphics()) {
                rules.stream().filter(rule -> rule.position == 0).filter(rule -> rule.check(worldModule, parcel)).findFirst().ifPresent(rule -> rule.draw(this, pixmap, parcel));
                rules.stream().filter(rule -> rule.position == 1).filter(rule -> rule.check(worldModule, parcel)).findFirst().ifPresent(rule -> rule.draw(this, pixmap, parcel));
                rules.stream().filter(rule -> rule.position == 2).filter(rule -> rule.check(worldModule, parcel)).findFirst().ifPresent(rule -> rule.draw(this, pixmap, parcel));
                rules.stream().filter(rule -> rule.position == 3).filter(rule -> rule.check(worldModule, parcel)).findFirst().ifPresent(rule -> rule.draw(this, pixmap, parcel));
            }

            cachedTextures.put(neighborhood, new Texture(pixmap));
            pixmap.dispose();
        }

        return cachedTextures.get(neighborhood);
    }

    private static class Rule {
        public final int position;
        public final String key;
        public final Predicate<ParcelModel> predicate;
        public final MovableModel.Direction[] directions;
        public final MovableModel.Direction directionEx;

        public Rule(int position, String key, Predicate<ParcelModel> predicate, MovableModel.Direction... directions) {
            this.position = position;
            this.key = key;
            this.predicate = predicate;
            this.directionEx = null;
            this.directions = directions;
        }

        public Rule(int position, String key, MovableModel.Direction directionEx, MovableModel.Direction... directions) {
            this.position = position;
            this.key = key;
            this.predicate = null;
            this.directionEx = directionEx;
            this.directions = directions;
        }

        public boolean check(WorldModule worldModule, ParcelModel parcel) {
            if (predicate != null) {
                return worldModule.check(parcel, predicate, directions);
            }
            return worldModule.check(parcel, ParcelModel::hasRock, directions) && worldModule.check(parcel, p -> !p.hasRock(), directionEx);
        }

        public void draw(WorldRockChunkGenerator worldRockChunkGenerator, Pixmap pxRockOut, ParcelModel parcel) {
            worldRockChunkGenerator.drawPixmap(pxRockOut, parcel, key, position);
        }

    }

    @OnGameLayerInit
    public void onGameLayerInit() {
//        Application.runOnMainThread(() -> {
//            _pxRocks = new HashMap<>();
//
//            data.items.stream().filter(itemInfo -> itemInfo.isRock).forEach(itemInfo -> {
////                Texture textureIn = new Texture(new FileHandle(SpriteManager.getFile(itemInfo, itemInfo.graphics.get(0))));
//                Texture textureIn = new Texture("data/graphics/texture/g2.png");
//                textureIn.getTextureData().prepare();
//                _pxRocks.put(itemInfo, textureIn.getTextureData().consumePixmap());
//                textureIn.dispose();
//            });
//
//        });
    }

    private void drawPixmap(Pixmap pxRockOut, ParcelModel parcel, String key, int position) {
        int outX = position == 1 || position == 3 ? Constant.HALF_TILE_SIZE : 0;
        int outY = position == 2 || position == 3 ? Constant.HALF_TILE_SIZE : 0;

        terrainManager.generate(pxRockOut, key, position, outX, outY);
    }

}
