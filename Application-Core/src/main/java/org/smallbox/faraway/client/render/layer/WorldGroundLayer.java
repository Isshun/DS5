package org.smallbox.faraway.client.render.layer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnGameLayerInit;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfig;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.world.WorldModule;
import org.smallbox.faraway.util.Constant;

import java.util.HashMap;
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

    @Inject
    private WorldModule _worldModule;

    @Inject
    private Game game;

    @Inject
    private Viewport viewport;

    @Inject
    private Data data;

    @Inject
    private SpriteManager spriteManager;

    @Inject
    private ApplicationConfig applicationConfig;

    private ExecutorService         _executor = Executors.newSingleThreadExecutor();
    private Texture[][]             _groundLayers;
    private Texture[][]             _rockLayers;
    private boolean[][]             _rockLayersUpToDate;
    private int                     _rows;
    private int                     _cols;
    private Map<ItemInfo, Pixmap>   _pxRocks;
    private Map<ItemInfo, Pixmap>   _pxLiquids;
    private Map<ItemInfo, Pixmap>   _pxGrounds;
    private Map<ItemInfo, Pixmap>   _pxGroundBorders;
    private Map<ItemInfo, Pixmap>   _pxGroundDecorations;

    @OnGameLayerInit
    public void onGameLayerInit() {
        Application.runOnMainThread(() -> {
            _pxLiquids = new HashMap<>();

            _pxRocks = new HashMap<>();
            _pxGrounds = new HashMap<>();
            _pxGroundBorders = new HashMap<>();
            _pxGroundDecorations = new HashMap<>();

            data.items.stream().filter(itemInfo -> itemInfo.isGround).forEach(itemInfo -> {
                Texture textureIn = new Texture(new FileHandle(SpriteManager.getFile(itemInfo, itemInfo.graphics.get(0))));
                textureIn.getTextureData().prepare();
                _pxGrounds.put(itemInfo, textureIn.getTextureData().consumePixmap());

                if (itemInfo.graphics.size() >= 2) {
                    Texture textureDecoration = new Texture(new FileHandle(SpriteManager.getFile(itemInfo, itemInfo.graphics.get(1))));
                    textureDecoration.getTextureData().prepare();
                    _pxGroundDecorations.put(itemInfo, textureDecoration.getTextureData().consumePixmap());
                }

                if (itemInfo.graphics.size() == 3) {
                    Texture textureBorders = new Texture(new FileHandle(SpriteManager.getFile(itemInfo, itemInfo.graphics.get(2))));
                    textureBorders.getTextureData().prepare();
                    _pxGroundBorders.put(itemInfo, textureBorders.getTextureData().consumePixmap());
                }
            });

            data.items.stream().filter(itemInfo -> itemInfo.isRock).forEach(itemInfo -> {
                Texture textureIn = new Texture(new FileHandle(SpriteManager.getFile(itemInfo, itemInfo.graphics.get(0))));
                textureIn.getTextureData().prepare();
                _pxRocks.put(itemInfo, textureIn.getTextureData().consumePixmap());
            });

            data.items.stream().filter(itemInfo -> itemInfo.isLiquid).forEach(itemInfo -> {
                Texture textureIn = new Texture(new FileHandle(SpriteManager.getFile(itemInfo, itemInfo.graphics.get(0))));
                textureIn.getTextureData().prepare();
                _pxLiquids.put(itemInfo, textureIn.getTextureData().consumePixmap());
            });

            _cols = game.getInfo().worldWidth / CHUNK_SIZE + 1;
            _rows = game.getInfo().worldHeight / CHUNK_SIZE + 1;

            _groundLayers = new Texture[_cols][_rows];
            _rockLayers = new Texture[_cols][_rows];
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
        for (int col = fromCol; col < toCol; col++) {
            for (int row = fromRow; row < toRow; row++) {
                if (!_rockLayersUpToDate[col][row]) {
                    _rockLayersUpToDate[col][row] = true;
                    createGround(col, row);
                }
                renderer.drawChunk(viewportX + (col * CHUNK_SIZE * Constant.TILE_SIZE), viewportY + (row * CHUNK_SIZE * Constant.TILE_SIZE), _groundLayers[col][row]);
                renderer.drawChunk(viewportX + (col * CHUNK_SIZE * Constant.TILE_SIZE), viewportY + (row * CHUNK_SIZE * Constant.TILE_SIZE), _rockLayers[col][row]);
            }
        }
    }

    private void createGround(int col, int row) {
        _executor.submit(() -> {
            final int fromX = Math.max(col * CHUNK_SIZE, 0);
            final int fromY = Math.max(row * CHUNK_SIZE, 0);
            final int toX = Math.min(col * CHUNK_SIZE + CHUNK_SIZE, game.getInfo().worldWidth);
            final int toY = Math.min(row * CHUNK_SIZE + CHUNK_SIZE, game.getInfo().worldHeight);

            _worldModule.getParcels(fromX, fromX + CHUNK_SIZE - 1, fromY, fromY + CHUNK_SIZE - 1, viewport.getFloor(), viewport.getFloor(), parcels -> {
                Pixmap pxGroundOut = new Pixmap(CHUNK_SIZE * Constant.TILE_SIZE, CHUNK_SIZE * Constant.TILE_SIZE, Pixmap.Format.RGBA8888);
                Pixmap pxRockOut = new Pixmap(CHUNK_SIZE * Constant.TILE_SIZE, CHUNK_SIZE * Constant.TILE_SIZE, Pixmap.Format.RGBA8888);

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

                    // Draw liquid
                    if (parcel.hasLiquid()) {
                        int offsetX = (parcel.x % parcel.getLiquidInfo().width) * Constant.TILE_SIZE;
                        int offsetY = (parcel.y % parcel.getLiquidInfo().height) * Constant.TILE_SIZE;
                        pxGroundOut.drawPixmap(_pxLiquids.get(parcel.getLiquidInfo()), (parcel.x - fromX) * Constant.TILE_SIZE, (parcel.y - fromY) * Constant.TILE_SIZE, offsetX, offsetY, Constant.TILE_SIZE, Constant.TILE_SIZE);
                    } else if (WorldHelper.hasLiquid(parcel.x, parcel.y, parcel.z - 1)) {
//                        int offsetX = (parcel.x % parcel.getLiquidInfo().width) * 32;
//                        int offsetY = (parcel.y % parcel.getLiquidInfo().height) * 32;
//                        pxGroundOut.drawPixmap(_pxLiquids.get(parcel.getLiquidInfo()), (parcel.x - fromX) * 32, (parcel.y - fromY) * 32, offsetX, offsetY, 32, 32);
//                        pxGroundOut.drawPixmap(_pxLiquids.get(WorldHelper.getLiquidInfo(parcel.x, parcel.y, parcel.z - 1)), (parcel.x - fromX) * 32, (parcel.y - fromY) * 32, 0, 0, 32, 32);
                    }

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
                            pxRockOut.drawPixmap(pxRock, (parcel.x - fromX) * Constant.TILE_SIZE, (parcel.y - fromY) * Constant.TILE_SIZE, 0, 0, Constant.TILE_SIZE, Constant.TILE_SIZE);
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
                    if (_groundLayers[col][row] != null) {
                        _groundLayers[col][row].dispose();
                    }
                    _groundLayers[col][row] = new Texture(pxGroundOut);
                    if (_rockLayers[col][row] != null) {
                        _rockLayers[col][row].dispose();
                    }
                    _rockLayers[col][row] = new Texture(pxRockOut);
                });
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

    @Override
    public void onFloorChange(int floor) {
        _rockLayersUpToDate = new boolean[_cols][_rows];
    }
}