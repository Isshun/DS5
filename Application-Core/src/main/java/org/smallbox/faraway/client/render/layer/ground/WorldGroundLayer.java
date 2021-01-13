package org.smallbox.faraway.client.render.layer.ground;

import com.badlogic.gdx.graphics.Pixmap;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.terrain.TerrainManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.render.layer.BaseLayer;
import org.smallbox.faraway.client.render.layer.ground.chunkGenerator.WorldGroundChunkGenerator;
import org.smallbox.faraway.client.render.layer.ground.chunkGenerator.WorldRockChunkGenerator;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnGameLayerInit;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfig;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.world.WorldModule;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.log.Log;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@GameObject
@GameLayer(level = LayerManager.WORLD_GROUND_LAYER_LEVEL, visible = true)
public class WorldGroundLayer extends BaseLayer {
    public static final int CHUNK_SIZE = 16;
    private static final int TOP_LEFT = 0b10000000;
    private static final int TOP = 0b01000000;
    private static final int TOP_RIGHT = 0b00100000;
    private static final int LEFT = 0b00010000;
    private static final int RIGHT = 0b00001000;
    private static final int BOTTOM_LEFT = 0b00000100;
    private static final int BOTTOM = 0b00000010;
    private static final int BOTTOM_RIGHT = 0b00000001;

    private static final Random random = new Random(42);

    @Inject private WorldModule _worldModule;
    @Inject private WorldGroundChunkGenerator worldGroundChunkGenerator;
    @Inject private WorldRockChunkGenerator worldRockChunkGenerator;
    @Inject private Viewport viewport;
    @Inject private Game game;
    @Inject private Data data;
    @Inject private SpriteManager spriteManager;
    @Inject private ApplicationConfig applicationConfig;
    @Inject private TerrainManager terrainManager;

    private final ExecutorService         _executor = Executors.newSingleThreadExecutor();
    private boolean[][]             _rockLayersUpToDate;
    private int                     _rows;
    private int                     _cols;
    private Map<ItemInfo, Pixmap>   _pxLiquids;
    private Map<ItemInfo, Pixmap>   _pxGroundBorders;

    @OnGameLayerInit
    public void onGameLayerInit() {
        Application.runOnMainThread(() -> {
//            _pxLiquids = new HashMap<>();
//
//            _pxGroundBorders = new HashMap<>();
//
//            data.items.stream().filter(itemInfo -> itemInfo.isGround).forEach(itemInfo -> {
//                Texture textureIn = new Texture(new FileHandle(SpriteManager.getFile(itemInfo, itemInfo.graphics.get(0))));
//                textureIn.getTextureData().prepare();
//                _pxGrounds.put(itemInfo, textureIn.getTextureData().consumePixmap());
//
//                if (itemInfo.graphics.size() >= 2) {
//                    Texture textureDecoration = new Texture(new FileHandle(SpriteManager.getFile(itemInfo, itemInfo.graphics.get(1))));
//                    textureDecoration.getTextureData().prepare();
//                    _pxGroundDecorations.put(itemInfo, textureDecoration.getTextureData().consumePixmap());
//                }
//
////                if (itemInfo.graphics.size() == 3) {
////                    Texture textureBorders = new Texture(new FileHandle(SpriteManager.getFile(itemInfo, itemInfo.graphics.get(2))));
////                    textureBorders.getTextureData().prepare();
////                    _pxGroundBorders.put(itemInfo, textureBorders.getTextureData().consumePixmap());
////                }
//            });
//
//            data.items.stream().filter(itemInfo -> itemInfo.isLiquid).forEach(itemInfo -> {
//                Texture textureIn = new Texture(new FileHandle(SpriteManager.getFile(itemInfo, itemInfo.graphics.get(0))));
//                textureIn.getTextureData().prepare();
//                _pxLiquids.put(itemInfo, textureIn.getTextureData().consumePixmap());
//            });

            _cols = game.getInfo().worldWidth / CHUNK_SIZE + 1;
            _rows = game.getInfo().worldHeight / CHUNK_SIZE + 1;

            _rockLayersUpToDate = new boolean[_cols][_rows];
        });
    }

    @Override
    public void onRemoveRock(ParcelModel parcel) {
        _rockLayersUpToDate[parcel.x / CHUNK_SIZE][parcel.y / CHUNK_SIZE] = false;
    }

    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        int fromX = Math.max((int) ((-viewport.getPosX() / Constant.TILE_SIZE) * viewport.getScale()), 0);
        int fromY = Math.max((int) ((-viewport.getPosY() / Constant.TILE_SIZE) * viewport.getScale()), 0);

        // TODO: take right panel in consideration
        int tileWidthCount = (int) (applicationConfig.getResolutionWidth() / (Constant.TILE_SIZE * viewport.getScale()));
        int tileHeightCount = (int) (applicationConfig.getResolutionHeight() / (Constant.TILE_SIZE * viewport.getScale()));
        int toX = Math.min(fromX + tileWidthCount, game.getInfo().worldWidth);
        int toY = Math.min(fromY + tileHeightCount, game.getInfo().worldHeight);

        int fromCol = Math.max(fromX / CHUNK_SIZE, 0);
        int fromRow = Math.max(fromY / CHUNK_SIZE, 0);
        int toCol = Math.min(toX / CHUNK_SIZE + 1, _cols);
        int toRow = Math.min(toY / CHUNK_SIZE + 1, _rows);

        int viewportX = viewport.getPosX();
        int viewportY = viewport.getPosY();

        // Draw chunks
        for (int col = 0; col < _cols; col++) {
            for (int row = 0; row < _rows; row++) {
                if (!_rockLayersUpToDate[col][row]) {
                    _rockLayersUpToDate[col][row] = true;
                    createGround(col, row);
                }
                renderer.drawChunk(viewportX + (col * CHUNK_SIZE * Constant.TILE_SIZE), viewportY + (row * CHUNK_SIZE * Constant.TILE_SIZE), worldGroundChunkGenerator._groundLayers[col][row]);
                renderer.drawChunk(viewportX + (col * CHUNK_SIZE * Constant.TILE_SIZE), viewportY + (row * CHUNK_SIZE * Constant.TILE_SIZE), worldRockChunkGenerator._rockLayers[col][row]);
            }
        }

//        renderer.draw(perlinGenerator.render());
    }

    private void createGround(int col, int row) {
        _executor.submit(() -> {
            try {
                worldGroundChunkGenerator.createGround(col, row);
                worldRockChunkGenerator.createGround(col, row);
            } catch (Exception e) {
                Log.warning(e.getMessage());
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onFloorChange(int floor) {
        _rockLayersUpToDate = new boolean[_cols][_rows];
    }
}