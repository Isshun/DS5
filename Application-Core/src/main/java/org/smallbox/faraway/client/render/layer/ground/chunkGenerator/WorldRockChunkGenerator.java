package org.smallbox.faraway.client.render.layer.ground.chunkGenerator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.render.terrain.TerrainManager;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnGameLayerInit;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.model.MovableModel;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
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
    public static final int CHUNK_SIZE = 16;

    @Inject private WorldModule worldModule;
    @Inject private Viewport viewport;
    @Inject private Game game;
    @Inject private Data data;
    @Inject private TerrainManager terrainManager;

    private Map<ItemInfo, Pixmap> _pxRocks;
    public Texture[][] _rockLayers;
    public List<Rule> rules;

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

        public void draw(WorldRockChunkGenerator worldRockChunkGenerator, Pixmap pxRockOut, ParcelModel parcel, int fromX, int fromY) {
            worldRockChunkGenerator.drawPixmap(pxRockOut, parcel, fromX, fromY, key, position);
        }

    }

    @OnGameLayerInit
    public void onGameLayerInit() {
        Application.runOnMainThread(() -> {
            _pxRocks = new HashMap<>();

            data.items.stream().filter(itemInfo -> itemInfo.isRock).forEach(itemInfo -> {
//                Texture textureIn = new Texture(new FileHandle(SpriteManager.getFile(itemInfo, itemInfo.graphics.get(0))));
                Texture textureIn = new Texture("data/graphics/texture/g2.png");
                textureIn.getTextureData().prepare();
                _pxRocks.put(itemInfo, textureIn.getTextureData().consumePixmap());
                textureIn.dispose();
            });

            int _cols = game.getInfo().worldWidth / CHUNK_SIZE + 1;
            int _rows = game.getInfo().worldHeight / CHUNK_SIZE + 1;

            _rockLayers = new Texture[_cols][_rows];
        });
    }

    public void createGround(int col, int row) {
        final int fromX = Math.max(col * CHUNK_SIZE, 0);
        final int fromY = Math.max(row * CHUNK_SIZE, 0);
        final int toX = Math.min(col * CHUNK_SIZE + CHUNK_SIZE, game.getInfo().worldWidth);
        final int toY = Math.min(row * CHUNK_SIZE + CHUNK_SIZE, game.getInfo().worldHeight);

        worldModule.getParcels(fromX, fromX + CHUNK_SIZE - 1, fromY, fromY + CHUNK_SIZE - 1, viewport.getFloor(), viewport.getFloor(), parcels -> {
            Pixmap pxRockOut = new Pixmap(CHUNK_SIZE * Constant.TILE_SIZE, CHUNK_SIZE * Constant.TILE_SIZE, Pixmap.Format.RGBA8888);

            for (ParcelModel parcel : parcels) {

                // Draw rock
                if (parcel.hasRock() && parcel.getRockInfo().hasGraphics()) {
                    Pixmap pxRock = _pxRocks.get(parcel.getRockInfo());
                    if (pxRock != null) {

                        rules = Arrays.asList(
                                new Rule(0, TERRAIN_FULL, ParcelModel::hasRock, TOP, LEFT, TOP_LEFT),
                                new Rule(0, TERRAIN_INNER_BOTTOM_RIGHT, TOP_LEFT, TOP, LEFT),
                                new Rule(0, TERRAIN_TOP_LEFT, p -> !p.hasRock(), TOP, LEFT),
                                new Rule(0, TERRAIN_TOP, p -> !p.hasRock(), TOP),
                                new Rule(0, TERRAIN_LEFT, p -> !p.hasRock(), LEFT),
                                new Rule(1, TERRAIN_FULL, ParcelModel::hasRock, TOP, RIGHT, TOP_RIGHT),
                                new Rule(1, TERRAIN_INNER_BOTTOM_LEFT, TOP_RIGHT, TOP, RIGHT),
                                new Rule(1, TERRAIN_TOP_RIGHT, p -> !p.hasRock(), TOP, RIGHT),
                                new Rule(1, TERRAIN_TOP, p -> !p.hasRock(), TOP),
                                new Rule(1, TERRAIN_RIGHT, p -> !p.hasRock(), RIGHT),
                                new Rule(2, TERRAIN_FULL, ParcelModel::hasRock, BOTTOM, LEFT, BOTTOM_LEFT),
                                new Rule(2, TERRAIN_INNER_TOP_RIGHT, BOTTOM_LEFT, BOTTOM, LEFT),
                                new Rule(2, TERRAIN_BOTTOM_LEFT, p -> !p.hasRock(), BOTTOM, LEFT),
                                new Rule(2, TERRAIN_BOTTOM, p -> !p.hasRock(), BOTTOM),
                                new Rule(2, TERRAIN_LEFT, p -> !p.hasRock(), LEFT),
                                new Rule(3, TERRAIN_FULL, ParcelModel::hasRock, BOTTOM, RIGHT, BOTTOM_RIGHT),
                                new Rule(3, TERRAIN_INNER_TOP_LEFT, BOTTOM_RIGHT, BOTTOM, RIGHT),
                                new Rule(3, TERRAIN_BOTTOM_RIGHT, p -> !p.hasRock(), BOTTOM, RIGHT),
                                new Rule(3, TERRAIN_BOTTOM, p -> !p.hasRock(), BOTTOM),
                                new Rule(3, TERRAIN_RIGHT, p -> !p.hasRock(), RIGHT)
                        );

                        rules.stream().filter(rule -> rule.position == 0).filter(rule -> rule.check(worldModule, parcel)).findFirst().ifPresent(rule -> rule.draw(this, pxRockOut, parcel, fromX, fromY));
                        rules.stream().filter(rule -> rule.position == 1).filter(rule -> rule.check(worldModule, parcel)).findFirst().ifPresent(rule -> rule.draw(this, pxRockOut, parcel, fromX, fromY));
                        rules.stream().filter(rule -> rule.position == 2).filter(rule -> rule.check(worldModule, parcel)).findFirst().ifPresent(rule -> rule.draw(this, pxRockOut, parcel, fromX, fromY));
                        rules.stream().filter(rule -> rule.position == 3).filter(rule -> rule.check(worldModule, parcel)).findFirst().ifPresent(rule -> rule.draw(this, pxRockOut, parcel, fromX, fromY));
                    }

                }

//                    Pixmap.setBlending(Pixmap.Blending.None);
            }

            Gdx.app.postRunnable(() -> {
                if (_rockLayers[col][row] != null) {
                    _rockLayers[col][row].dispose();
                }
                _rockLayers[col][row] = new Texture(pxRockOut);
                pxRockOut.dispose();
            });
        });
    }

    private void drawPixmap(Pixmap pxRockOut, ParcelModel parcel, int fromX, int fromY, String key, int position) {
        int outX = (parcel.x % CHUNK_SIZE) * Constant.TILE_SIZE + (position == 1 || position == 3 ? Constant.HALF_TILE_SIZE : 0);
        int outY = (parcel.y % CHUNK_SIZE) * Constant.TILE_SIZE + (position == 2 || position == 3 ? Constant.HALF_TILE_SIZE : 0);
        Pixmap pxRock = _pxRocks.get(parcel.getRockInfo());
//        for (int x = 0; x < HALF_TILE_SIZE; x++) {
//            for (int y = 0; y < HALF_TILE_SIZE; y++) {
//                pxRockOut.drawPixel(outX + x, outY + y, 0xff0000ff);
//            }
//        }

        terrainManager.generate(pxRock, pxRockOut, key, position, outX, outY);

//        pxRockOut.drawPixmap(pixmapSource,
//                (parcel.x - fromX) * Constant.TILE_SIZE + (position == 1 || position == 3 ? Constant.HALF_TILE_SIZE : 0),
//                (parcel.y - fromY) * Constant.TILE_SIZE + (position == 2 || position == 3 ? Constant.HALF_TILE_SIZE : 0));

    }

}
