package org.smallbox.faraway.client.render.layer.ground.chunkGenerator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnGameLayerInit;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.world.WorldModule;
import org.smallbox.faraway.util.Constant;

import java.util.HashMap;
import java.util.Map;

@GameObject
public class WorldGroundChunkGenerator {
    public static final int CHUNK_SIZE = 16;

    @Inject private SpriteManager spriteManager;
    @Inject private WorldModule worldModule;
    @Inject private Viewport viewport;
    @Inject private Game game;
    @Inject private Data data;

    private Map<ItemInfo, Pixmap>   _pxGrounds;
    private Map<ItemInfo, Pixmap>   _pxGroundDecorations;
    public Texture[][]             _groundLayers;

    @OnGameLayerInit
    public void onGameLayerInit() {
        Application.runOnMainThread(() -> {
            _pxGrounds = new HashMap<>();
            _pxGroundDecorations = new HashMap<>();

            data.items.stream().filter(itemInfo -> itemInfo.isGround).forEach(itemInfo -> {
//                Texture textureIn = new Texture(new FileHandle(SpriteManager.getFile(itemInfo, itemInfo.graphics.get(0))));
                Texture textureIn = new Texture("data/graphics/texture/grass.png");

                Pixmap pixmapIn = createPixmapFromTexture(textureIn);
                textureIn.dispose();

                Pixmap pixmapOut = createPixmapFromPixmap(pixmapIn, CHUNK_SIZE * Constant.TILE_SIZE);
                pixmapIn.dispose();

                _pxGrounds.put(itemInfo, pixmapOut);

                if (itemInfo.graphics.size() >= 2) {
                    Texture textureDecoration = spriteManager.getTexture(itemInfo.graphics.get(1));
                    textureDecoration.getTextureData().prepare();
                    _pxGroundDecorations.put(itemInfo, textureDecoration.getTextureData().consumePixmap());
                }

            });

            int _cols = game.getInfo().worldWidth / CHUNK_SIZE + 1;
            int _rows = game.getInfo().worldHeight / CHUNK_SIZE + 1;

            _groundLayers = new Texture[_cols][_rows];
        });
    }

    private Pixmap createPixmapFromTexture(Texture textureIn) {
        textureIn.getTextureData().prepare();
        return textureIn.getTextureData().consumePixmap();
    }

    private Pixmap createPixmapFromPixmap(Pixmap pixmapIn, int size) {
        Pixmap pixmapOut = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                    pixmapOut.drawPixel(x, y, pixmapIn.getPixel(x % pixmapIn.getWidth(), y % pixmapIn.getHeight()));
//                {
//                    int alpha = (0xffffff << 8) + (int) (perlinNoise[x][y] * 255);
//                    int color = p1.getPixel(x % 512, y % 512) & alpha;
//                    pixmap.drawPixel(x, y, color);
//                }
//                {
//                    int alpha = (0xffffff << 8) + (int) ((1 - perlinNoise[x][y]) * 255);
//                    int color = p2.getPixel(x % 512, y % 512) & alpha;
//                    pixmap.drawPixel(x, y, color);
//                }
//                {
//                    int alpha = (0xffffff << 8) + (int) ((1 - perlinNoise2[x][y]) * 255);
//                    int color = p3.getPixel(x % 512, y % 512) & alpha;
//                    pixmap.drawPixel(x, y, color);
//                }
            }
        }

        return pixmapOut;
    }

    public void createGround(int col, int row) {
        final int fromX = Math.max(col * CHUNK_SIZE, 0);
        final int fromY = Math.max(row * CHUNK_SIZE, 0);
        final int toX = Math.min(col * CHUNK_SIZE + CHUNK_SIZE, game.getInfo().worldWidth);
        final int toY = Math.min(row * CHUNK_SIZE + CHUNK_SIZE, game.getInfo().worldHeight);

        worldModule.getParcels(fromX, fromX + CHUNK_SIZE - 1, fromY, fromY + CHUNK_SIZE - 1, viewport.getFloor(), viewport.getFloor(), parcels -> {
            Pixmap pxGroundOut = new Pixmap(CHUNK_SIZE * Constant.TILE_SIZE, CHUNK_SIZE * Constant.TILE_SIZE, Pixmap.Format.RGBA8888);

            for (ParcelModel parcel: parcels) {

                // Draw ground
                if (parcel.hasGround()) {
                    addGround(pxGroundOut, parcel, fromX, fromY);
                    addDecoration(pxGroundOut, parcel, fromX, fromY);
                } else if (WorldHelper.hasGround(parcel.x, parcel.y, parcel.z - 1)) {
                    pxGroundOut.drawPixmap(_pxGrounds.get(WorldHelper.getGroundInfo(parcel.x, parcel.y, parcel.z - 1)), (parcel.x - fromX) * Constant.TILE_SIZE, (parcel.y - fromY) * Constant.TILE_SIZE, Constant.TILE_SIZE, Constant.TILE_SIZE, Constant.TILE_SIZE, Constant.TILE_SIZE);
                } else if (WorldHelper.hasGround(parcel.x, parcel.y, parcel.z - 2)) {
                    pxGroundOut.drawPixmap(_pxGrounds.get(WorldHelper.getGroundInfo(parcel.x, parcel.y, parcel.z - 2)), (parcel.x - fromX) * Constant.TILE_SIZE, (parcel.y - fromY) * Constant.TILE_SIZE, Constant.TILE_SIZE * 2, Constant.TILE_SIZE, Constant.TILE_SIZE, Constant.TILE_SIZE);
                } else if (WorldHelper.hasGround(parcel.x, parcel.y, parcel.z - 3)) {
                    pxGroundOut.drawPixmap(_pxGrounds.get(WorldHelper.getGroundInfo(parcel.x, parcel.y, parcel.z - 3)), (parcel.x - fromX) * Constant.TILE_SIZE, (parcel.y - fromY) * Constant.TILE_SIZE, Constant.TILE_SIZE * 3, Constant.TILE_SIZE, Constant.TILE_SIZE, Constant.TILE_SIZE);
                }

//                    Pixmap.setBlending(Pixmap.Blending.None);
            }

            Gdx.app.postRunnable(() -> {
                if (_groundLayers[col][row] != null) {
                    _groundLayers[col][row].dispose();
                }
                _groundLayers[col][row] = new Texture(pxGroundOut);
                pxGroundOut.dispose();
            });
        });
    }

    private void addGround(Pixmap pxGroundOut, ParcelModel parcel, int fromX, int fromY) {
        if (_pxGrounds.get(parcel.getGroundInfo()) != null) {
            int offsetX = (parcel.x % parcel.getGroundInfo().width) * Constant.TILE_SIZE;
            int offsetY = (parcel.y % parcel.getGroundInfo().height) * Constant.TILE_SIZE;
            pxGroundOut.drawPixmap(_pxGrounds.get(parcel.getGroundInfo()), (parcel.x - fromX) * Constant.TILE_SIZE, (parcel.y - fromY) * Constant.TILE_SIZE, offsetX, offsetY, Constant.TILE_SIZE, Constant.TILE_SIZE);
        }
    }

    private void addDecoration(Pixmap pxGroundOut, ParcelModel parcel, int fromX, int fromY) {
        if (MathUtils.randomBoolean(0.1f) && _pxGroundDecorations.containsKey(parcel.getGroundInfo())) {
            pxGroundOut.drawPixmap(_pxGroundDecorations.get(parcel.getGroundInfo()),
                    (parcel.x - fromX) * Constant.TILE_SIZE,
                    (parcel.y - fromY) * Constant.TILE_SIZE,
                    MathUtils.random(0, 3) * Constant.TILE_SIZE,
                    MathUtils.random(0, 5) * Constant.TILE_SIZE,
                    Constant.TILE_SIZE,
                    Constant.TILE_SIZE);
        }
    }

}
