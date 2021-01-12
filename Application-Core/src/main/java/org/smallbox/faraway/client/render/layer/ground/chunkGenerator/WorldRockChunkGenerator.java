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
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.world.WorldModule;
import org.smallbox.faraway.util.Constant;

import java.util.HashMap;
import java.util.Map;

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
//                        int tile = 0;
//                        if (repeatTile(parcel.x - 1, parcel.y - 1, parcel.z)) { tile |= 0b10000000; }
//                        if (repeatTile(parcel.x,     parcel.y - 1, parcel.z)) { tile |= 0b01000000; }
//                        if (repeatTile(parcel.x + 1, parcel.y - 1, parcel.z)) { tile |= 0b00100000; }
//                        if (repeatTile(parcel.x - 1, parcel.y,     parcel.z)) { tile |= 0b00010000; }
//                        if (repeatTile(parcel.x + 1, parcel.y,     parcel.z)) { tile |= 0b00001000; }
//                        if (repeatTile(parcel.x - 1, parcel.y + 1, parcel.z)) { tile |= 0b00000100; }
//                        if (repeatTile(parcel.x,     parcel.y + 1, parcel.z)) { tile |= 0b00000010; }
//                        if (repeatTile(parcel.x + 1, parcel.y + 1, parcel.z)) { tile |= 0b00000001; }
//                        parcel.setTile(tile);

//                        Pixmap pxRock = _pxGrounds.entrySet().stream().filter(entry -> StringUtils.equals(entry.getKey().name, parcel.getRockInfo().name)).map(Map.Entry::getValue).findFirst().orElse(null);
                    Pixmap pxRock = _pxRocks.get(parcel.getRockInfo());
                    if (pxRock != null) {

                        // Position 0
                        if (worldModule.check(parcel, ParcelModel::hasRock, TOP, LEFT, TOP_LEFT)) {
                            drawPixmap(pxRockOut, parcel, fromX, fromY, terrainManager.getPixmap(parcel, TERRAIN_FULL, 0), 0);
                        }
                        else if (worldModule.check(parcel, ParcelModel::hasRock, TOP, LEFT) && worldModule.check(parcel, p -> !p.hasRock(), TOP_LEFT)) {
                            drawPixmap(pxRockOut, parcel, fromX, fromY, terrainManager.getPixmap(parcel, TERRAIN_INNER_BOTTOM_RIGHT, 0), 0);
                        }
                        else if (worldModule.check(parcel, p -> !p.hasRock(), TOP, LEFT, TOP_LEFT)) {
                            drawPixmap(pxRockOut, parcel, fromX, fromY, terrainManager.getPixmap(parcel, TERRAIN_TOP_LEFT, 0), 0);
                        }
                        else if (worldModule.check(parcel, p -> !p.hasRock(), TOP)) {
                            drawPixmap(pxRockOut, parcel, fromX, fromY, terrainManager.getPixmap(parcel, TERRAIN_TOP, 0), 0);
                        }
                        else if (worldModule.check(parcel, p -> !p.hasRock(), LEFT)) {
                            drawPixmap(pxRockOut, parcel, fromX, fromY, terrainManager.getPixmap(parcel, TERRAIN_LEFT, 0), 0);
                        }

                        // Position 1
                        if (worldModule.check(parcel, ParcelModel::hasRock, TOP, RIGHT, TOP_RIGHT)) {
                            drawPixmap(pxRockOut, parcel, fromX, fromY, terrainManager.getPixmap(parcel, TERRAIN_FULL, 1), 1);
                        }
                        else if (worldModule.check(parcel, ParcelModel::hasRock, TOP, RIGHT) && worldModule.check(parcel, p -> !p.hasRock(), TOP_RIGHT)) {
                            drawPixmap(pxRockOut, parcel, fromX, fromY, terrainManager.getPixmap(parcel, TERRAIN_INNER_BOTTOM_LEFT, 1), 1);
                        }
                        else if (worldModule.check(parcel, p -> !p.hasRock(), TOP, RIGHT, TOP_RIGHT)) {
                            drawPixmap(pxRockOut, parcel, fromX, fromY, terrainManager.getPixmap(parcel, TERRAIN_TOP_RIGHT, 1), 1);
                        }
                        else if (worldModule.check(parcel, p -> !p.hasRock(), TOP)) {
                            drawPixmap(pxRockOut, parcel, fromX, fromY, terrainManager.getPixmap(parcel, TERRAIN_TOP, 1), 1);
                        }
                        else if (worldModule.check(parcel, p -> !p.hasRock(), RIGHT)) {
                            drawPixmap(pxRockOut, parcel, fromX, fromY, terrainManager.getPixmap(parcel, TERRAIN_RIGHT, 1), 1);
                        }

                        // Position 2
                        if (worldModule.check(parcel, ParcelModel::hasRock, BOTTOM, LEFT, BOTTOM_LEFT)) {
                            drawPixmap(pxRockOut, parcel, fromX, fromY, terrainManager.getPixmap(parcel, TERRAIN_FULL, 2), 2);
                        }
                        else if (worldModule.check(parcel, ParcelModel::hasRock, BOTTOM, LEFT) && worldModule.check(parcel, p -> !p.hasRock(), BOTTOM_LEFT)) {
                            drawPixmap(pxRockOut, parcel, fromX, fromY, terrainManager.getPixmap(parcel, TERRAIN_INNER_TOP_RIGHT, 2), 2);
                        }
                        else if (worldModule.check(parcel, p -> !p.hasRock(), BOTTOM, LEFT, BOTTOM_LEFT)) {
                            drawPixmap(pxRockOut, parcel, fromX, fromY, terrainManager.getPixmap(parcel, TERRAIN_BOTTOM_LEFT, 2), 2);
                        }
                        else if (worldModule.check(parcel, p -> !p.hasRock(), BOTTOM)) {
                            drawPixmap(pxRockOut, parcel, fromX, fromY, terrainManager.getPixmap(parcel, TERRAIN_BOTTOM, 2), 2);
                        }
                        else if (worldModule.check(parcel, p -> !p.hasRock(), LEFT)) {
                            drawPixmap(pxRockOut, parcel, fromX, fromY, terrainManager.getPixmap(parcel, TERRAIN_LEFT, 2), 2);
                        }

                        // Position 3
                        if (worldModule.check(parcel, ParcelModel::hasRock, BOTTOM, RIGHT, BOTTOM_RIGHT)) {
                            drawPixmap(pxRockOut, parcel, fromX, fromY, terrainManager.getPixmap(parcel, TERRAIN_FULL, 3), 3);
                        }
                        else if (worldModule.check(parcel, ParcelModel::hasRock, BOTTOM, RIGHT) && worldModule.check(parcel, p -> !p.hasRock(), BOTTOM_RIGHT)) {
                            drawPixmap(pxRockOut, parcel, fromX, fromY, terrainManager.getPixmap(parcel, TERRAIN_INNER_TOP_LEFT, 3), 3);
                        }
                        else if (worldModule.check(parcel, p -> !p.hasRock(), BOTTOM, RIGHT, BOTTOM_RIGHT)) {
                            drawPixmap(pxRockOut, parcel, fromX, fromY, terrainManager.getPixmap(parcel, TERRAIN_BOTTOM_RIGHT, 3), 3);
                        }
                        else if (worldModule.check(parcel, p -> !p.hasRock(), BOTTOM)) {
                            drawPixmap(pxRockOut, parcel, fromX, fromY, terrainManager.getPixmap(parcel, TERRAIN_BOTTOM, 3), 3);
                        }
                        else if (worldModule.check(parcel, p -> !p.hasRock(), RIGHT)) {
                            drawPixmap(pxRockOut, parcel, fromX, fromY, terrainManager.getPixmap(parcel, TERRAIN_RIGHT, 3), 3);
                        }

//                        if (!worldModule.check(parcel, TOP_LEFT, ParcelModel::hasRock) && worldModule.check(parcel, TOP, ParcelModel::hasRock) && worldModule.check(parcel, LEFT, ParcelModel::hasRock)) {
//                            drawPixmap(pxRockOut, parcel, fromX, fromY, terrainManager.getPixmap(parcel, TERRAIN_INNER_BOTTOM_RIGHT));
//                        } else if (!worldModule.check(parcel, TOP_RIGHT, ParcelModel::hasRock) && worldModule.check(parcel, TOP, ParcelModel::hasRock) && worldModule.check(parcel, RIGHT, ParcelModel::hasRock)) {
//                            drawPixmap(pxRockOut, parcel, fromX, fromY, terrainManager.getPixmap(parcel, TERRAIN_INNER_BOTTOM_LEFT));
//                        } else if (!worldModule.check(parcel, BOTTOM_LEFT, ParcelModel::hasRock) && worldModule.check(parcel, BOTTOM, ParcelModel::hasRock) && worldModule.check(parcel, LEFT, ParcelModel::hasRock)) {
//                            drawPixmap(pxRockOut, parcel, fromX, fromY, terrainManager.getPixmap(parcel, TERRAIN_INNER_TOP_RIGHT));
//                        } else if (!worldModule.check(parcel, BOTTOM_RIGHT, ParcelModel::hasRock) && worldModule.check(parcel, BOTTOM, ParcelModel::hasRock) && worldModule.check(parcel, RIGHT, ParcelModel::hasRock)) {
//                            drawPixmap(pxRockOut, parcel, fromX, fromY, terrainManager.getPixmap(parcel, TERRAIN_INNER_TOP_LEFT));
//                        } else if (!worldModule.check(parcel, TOP, ParcelModel::hasRock) && !worldModule.check(parcel, LEFT, ParcelModel::hasRock)) {
//                            drawPixmap(pxRockOut, parcel, fromX, fromY, terrainManager.getPixmap(parcel, TERRAIN_TOP_LEFT));
//                        } else if (!worldModule.check(parcel, TOP, ParcelModel::hasRock) && !worldModule.check(parcel, RIGHT, ParcelModel::hasRock)) {
//                            drawPixmap(pxRockOut, parcel, fromX, fromY, terrainManager.getPixmap(parcel, TERRAIN_TOP_RIGHT));
//                        } else if (!worldModule.check(parcel, BOTTOM, ParcelModel::hasRock) && !worldModule.check(parcel, LEFT, ParcelModel::hasRock)) {
//                            drawPixmap(pxRockOut, parcel, fromX, fromY, terrainManager.getPixmap(parcel, TERRAIN_BOTTOM_LEFT));
//                        } else if (!worldModule.check(parcel, BOTTOM, ParcelModel::hasRock) && !worldModule.check(parcel, RIGHT, ParcelModel::hasRock)) {
//                            drawPixmap(pxRockOut, parcel, fromX, fromY, terrainManager.getPixmap(parcel, TERRAIN_BOTTOM_RIGHT));
//                        } else if (!worldModule.check(parcel, TOP, ParcelModel::hasRock)) {
//                            drawPixmap(pxRockOut, parcel, fromX, fromY, terrainManager.getPixmap(parcel, TERRAIN_TOP));
//                        } else if (!worldModule.check(parcel, BOTTOM, ParcelModel::hasRock)) {
//                            drawPixmap(pxRockOut, parcel, fromX, fromY, terrainManager.getPixmap(parcel, TERRAIN_BOTTOM));
//                        } else if (!worldModule.check(parcel, LEFT, ParcelModel::hasRock)) {
//                            drawPixmap(pxRockOut, parcel, fromX, fromY, terrainManager.getPixmap(parcel, TERRAIN_LEFT));
//                        } else if (!worldModule.check(parcel, RIGHT, ParcelModel::hasRock)) {
//                            drawPixmap(pxRockOut, parcel, fromX, fromY, terrainManager.getPixmap(parcel, TERRAIN_RIGHT));
//                        } else {
//                            pxRockOut.drawPixmap(pxRock,
//                                    (parcel.x - fromX) * Constant.TILE_SIZE,
//                                    (parcel.y - fromY) * Constant.TILE_SIZE,
//                                    ((parcel.x - fromX) * Constant.TILE_SIZE) % 512,
//                                    ((parcel.y - fromY) * Constant.TILE_SIZE) % 512,
//                                    Constant.TILE_SIZE,
//                                    Constant.TILE_SIZE);
//                        }

                    }
//                        } else {
//                        Gdx.app.postRunnable(() -> {
//                            if (parcel.hasRock() && parcel.getRockInfo().hasGraphics()) {
//                                Sprite sprite = spriteManager.getRock(parcel.getRockInfo().graphics.get(0), parcel.getTile());
//                                if (sprite != null) {
//                                    TextureData textureData = sprite.getTexture().getTextureData();
//                                    if (!textureData.isPrepared()) {
//                                        textureData.prepare();
//                                    }
//                                    Pixmap pxRockDo = textureData.consumePixmap();
//                                    _pxRocks.put(parcel.getRockInfo(), pxRockDo);
//                                    textureData.disposePixmap();
//                                    pxOut.drawPixmap(pxRockDo, (parcel.x - fromX) * 32, (parcel.y - fromY) * 32, 0, 0, 32, 32);
//                                }
//                            }
//                        });
//                        }
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

    private void drawPixmap(Pixmap pxRockOut, ParcelModel parcel, int fromX, int fromY, Pixmap pixmapSource, int position) {
        pxRockOut.drawPixmap(pixmapSource,
                (parcel.x - fromX) * Constant.TILE_SIZE + (position == 1 || position == 3 ? Constant.HALF_TILE_SIZE : 0),
                (parcel.y - fromY) * Constant.TILE_SIZE + (position == 2 || position == 3 ? Constant.HALF_TILE_SIZE : 0));
    }

}
