package org.smallbox.faraway.client.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameRenderer;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.world.WorldModule;
import org.smallbox.faraway.util.Constant;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@GameRenderer(level = MainRenderer.WORLD_GROUND_RENDERER_LEVEL, visible = true)
public class WorldGroundRenderer extends BaseRenderer {
    private static final int CHUNK_SIZE = 16;
    private static final int TOP_LEFT = 0b10000000;
    private static final int TOP = 0b01000000;
    private static final int TOP_RIGHT = 0b00100000;
    private static final int LEFT = 0b00010000;
    private static final int RIGHT = 0b00001000;
    private static final int BOTTOM_LEFT = 0b00000100;
    private static final int BOTTOM = 0b00000010;
    private static final int BOTTOM_RIGHT = 0b00000001;

    @BindModule
    private WorldModule _worldModule;

    private ExecutorService         _executor = Executors.newSingleThreadExecutor();
    private Texture[][]             _groundLayers;
    private Texture[][]             _rockLayers;
    private boolean[][]             _rockLayersUpToDate;
    private int                     _rows;
    private int                     _cols;
    private int                     _floor;
    private Map<ItemInfo, Pixmap>   _pxRocks;
    private Map<ItemInfo, Pixmap>   _pxLiquids;
    private Map<ItemInfo, Pixmap>   _pxGrounds;
    private Map<ItemInfo, Pixmap>   _pxGroundBorders;
    private Map<ItemInfo, Pixmap>   _pxGroundDecorations;

    @Override
    public void onGameStart(Game game) {
        Application.runOnMainThread(() -> {
            _pxLiquids = new HashMap<>();

            _pxGrounds = new HashMap<>();
            _pxGroundBorders = new HashMap<>();
            _pxGroundDecorations = new HashMap<>();

            Application.data.items.stream().filter(itemInfo -> itemInfo.isGround).forEach(itemInfo -> {
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

            Application.data.items.stream().filter(itemInfo -> itemInfo.isLiquid).forEach(itemInfo -> {
                Texture textureIn = new Texture(new FileHandle(SpriteManager.getFile(itemInfo, itemInfo.graphics.get(0))));
                textureIn.getTextureData().prepare();
                _pxLiquids.put(itemInfo, textureIn.getTextureData().consumePixmap());
            });

            _pxRocks = new HashMap<>();

            _cols = game.getInfo().worldWidth / CHUNK_SIZE;
            _rows = game.getInfo().worldHeight / CHUNK_SIZE;

            _floor = WorldHelper.getCurrentFloor();
            _groundLayers = new Texture[_cols][_rows];
            _rockLayers = new Texture[_cols][_rows];
            _rockLayersUpToDate = new boolean[_cols][_rows];
        });
    }

    @Override
    public void onRemoveRock(ParcelModel parcel) {
        _rockLayersUpToDate[parcel.x / CHUNK_SIZE][parcel.y / CHUNK_SIZE] = false;
    }

    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        int fromX = Math.max((int) ((-viewport.getPosX() / Constant.TILE_WIDTH) * viewport.getScale()), 0);
        int fromY = Math.max((int) ((-viewport.getPosY() / Constant.TILE_HEIGHT) * viewport.getScale()), 0);
        int toX = Math.min(fromX + 50, Application.gameManager.getGame().getInfo().worldWidth);
        int toY = Math.min(fromY + 40, Application.gameManager.getGame().getInfo().worldHeight);

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
                renderer.drawChunk(viewportX + (col * CHUNK_SIZE * Constant.TILE_WIDTH), viewportY + (row * CHUNK_SIZE * Constant.TILE_HEIGHT), _groundLayers[col][row]);
                renderer.drawChunk(viewportX + (col * CHUNK_SIZE * Constant.TILE_WIDTH), viewportY + (row * CHUNK_SIZE * Constant.TILE_HEIGHT), _rockLayers[col][row]);
            }
        }
    }

    @Override
    public void onRefresh(int frame) {
    }

    private boolean repeatTile(int x, int y, int z) {
        ParcelModel parcel = WorldHelper.getParcel(x, y, z);
        return parcel == null || parcel.hasRock() || parcel.hasLiquid() || WorldHelper.hasWall(parcel) || WorldHelper.hasDoor(parcel);
    }

    private void createGround(int col, int row) {
        _executor.submit(() -> {
            final int fromX = Math.max(col * CHUNK_SIZE, 0);
            final int fromY = Math.max(row * CHUNK_SIZE, 0);
            final int toX = Math.min(col * CHUNK_SIZE + CHUNK_SIZE, Application.gameManager.getGame().getInfo().worldWidth);
            final int toY = Math.min(row * CHUNK_SIZE + CHUNK_SIZE, Application.gameManager.getGame().getInfo().worldHeight);

            _worldModule.getParcels(fromX, fromX + CHUNK_SIZE - 1, fromY, fromY + CHUNK_SIZE - 1, _floor, _floor, parcels -> {
                SpriteManager spriteManager = ApplicationClient.spriteManager;
                Pixmap pxGroundOut = new Pixmap(CHUNK_SIZE * 32, CHUNK_SIZE * 32, Pixmap.Format.RGBA8888);
                Pixmap pxOut = new Pixmap(CHUNK_SIZE * 32, CHUNK_SIZE * 32, Pixmap.Format.RGBA8888);

                for (ParcelModel parcel: parcels) {
                    ItemInfo groundInfo = parcel.getGroundInfo();

                    // Draw ground
                    if (parcel.hasGround()) {
                        boolean test = false;

                        int tile = 0;
                        if (WorldHelper.getGroundInfo(parcel.x - 1, parcel.y - 1, parcel.z) != groundInfo) { tile |= 0b10000000; }
                        if (WorldHelper.getGroundInfo(parcel.x,     parcel.y - 1, parcel.z) != groundInfo) { tile |= 0b01000000; }
                        if (WorldHelper.getGroundInfo(parcel.x + 1, parcel.y - 1, parcel.z) != groundInfo) { tile |= 0b00100000; }
                        if (WorldHelper.getGroundInfo(parcel.x - 1, parcel.y,     parcel.z) != groundInfo) { tile |= 0b00010000; }
                        if (WorldHelper.getGroundInfo(parcel.x + 1, parcel.y,     parcel.z) != groundInfo) { tile |= 0b00001000; }
                        if (WorldHelper.getGroundInfo(parcel.x - 1, parcel.y + 1, parcel.z) != groundInfo) { tile |= 0b00000100; }
                        if (WorldHelper.getGroundInfo(parcel.x,     parcel.y + 1, parcel.z) != groundInfo) { tile |= 0b00000010; }
                        if (WorldHelper.getGroundInfo(parcel.x + 1, parcel.y + 1, parcel.z) != groundInfo) { tile |= 0b00000001; }

                        if (tile != 0) {
                            pxGroundOut.drawPixmap(_pxGrounds.get(Application.data.getItemInfo("base.ground.grass")), (parcel.x - fromX) * 32, (parcel.y - fromY) * 32, 0, 0, 32, 32);
                        }

                        int offsetX = (parcel.x % parcel.getGroundInfo().width) * 32;
                        int offsetY = (parcel.y % parcel.getGroundInfo().height) * 32;

//                        if (tile != 0) {
//                            if (_pxGroundBorders.containsKey(parcel.getGroundInfo())) {
//                                if ((tile & TOP) > 0 && (tile & LEFT) > 0) {
//                                    Pixmap.setBlending(Pixmap.Blending.SourceOver);
//                                    pxGroundOut.drawPixmap(_pxGroundBorders.get(parcel.getGroundInfo()), (parcel.x - fromX) * 32, (parcel.y - fromY) * 32, 0, 0, 16, 4);
//                                }
//                                if ((tile & TOP) > 0) {
//                                    Pixmap.setBlending(Pixmap.Blending.SourceOver);
//                                    pxGroundOut.drawPixmap(_pxGroundBorders.get(parcel.getGroundInfo()), (parcel.x - fromX) * 32, (parcel.y - fromY) * 32, 16, 0, 16, 4);
//                                }
//                            }
//                            pxGroundOut.drawPixmap(_pxGrounds.get(parcel.getGroundInfo()), (parcel.x - fromX) * 32, (parcel.y - fromY) * 32 + 4, offsetX, offsetY, 32, 28);
//                        } else {
                            pxGroundOut.drawPixmap(_pxGrounds.get(parcel.getGroundInfo()), (parcel.x - fromX) * 32, (parcel.y - fromY) * 32, offsetX, offsetY, 32, 32);
//                        }

                        if (MathUtils.randomBoolean(0.1f) && _pxGroundDecorations.containsKey(parcel.getGroundInfo())) {
                            Pixmap.setBlending(Pixmap.Blending.SourceOver);
                            pxGroundOut.drawPixmap(_pxGroundDecorations.get(parcel.getGroundInfo()),
                                    (parcel.x - fromX) * 32,
                                    (parcel.y - fromY) * 32,
                                    MathUtils.random(0, 3) * 32,
                                    MathUtils.random(0, 5) * 32,
                                    32,
                                    32);
                        }
                    } else if (WorldHelper.hasGround(parcel.x, parcel.y, parcel.z - 1)) {
                        pxGroundOut.drawPixmap(_pxGrounds.get(WorldHelper.getGroundInfo(parcel.x, parcel.y, parcel.z - 1)), (parcel.x - fromX) * 32, (parcel.y - fromY) * 32, 32, 32, 32, 32);
                    } else if (WorldHelper.hasGround(parcel.x, parcel.y, parcel.z - 2)) {
                        pxGroundOut.drawPixmap(_pxGrounds.get(WorldHelper.getGroundInfo(parcel.x, parcel.y, parcel.z - 2)), (parcel.x - fromX) * 32, (parcel.y - fromY) * 32, 64, 32, 32, 32);
                    } else if (WorldHelper.hasGround(parcel.x, parcel.y, parcel.z - 3)) {
                        pxGroundOut.drawPixmap(_pxGrounds.get(WorldHelper.getGroundInfo(parcel.x, parcel.y, parcel.z - 3)), (parcel.x - fromX) * 32, (parcel.y - fromY) * 32, 96, 32, 32, 32);
                    }

                    // Draw liquid
                    if (parcel.hasLiquid()) {
                        int offsetX = (parcel.x % parcel.getLiquidInfo().width) * 32;
                        int offsetY = (parcel.y % parcel.getLiquidInfo().height) * 32;
                        pxGroundOut.drawPixmap(_pxLiquids.get(parcel.getLiquidInfo()), (parcel.x - fromX) * 32, (parcel.y - fromY) * 32, offsetX, offsetY, 32, 32);
                    } else if (WorldHelper.hasLiquid(parcel.x, parcel.y, parcel.z - 1)) {
//                        int offsetX = (parcel.x % parcel.getLiquidInfo().width) * 32;
//                        int offsetY = (parcel.y % parcel.getLiquidInfo().height) * 32;
//                        pxGroundOut.drawPixmap(_pxLiquids.get(parcel.getLiquidInfo()), (parcel.x - fromX) * 32, (parcel.y - fromY) * 32, offsetX, offsetY, 32, 32);
//                        pxGroundOut.drawPixmap(_pxLiquids.get(WorldHelper.getLiquidInfo(parcel.x, parcel.y, parcel.z - 1)), (parcel.x - fromX) * 32, (parcel.y - fromY) * 32, 0, 0, 32, 32);
                    }

                    // Draw rock
                    if (parcel.hasRock() && parcel.getRockInfo().hasGraphics()) {
                        int tile = 0;
                        if (repeatTile(parcel.x - 1, parcel.y - 1, parcel.z)) { tile |= 0b10000000; }
                        if (repeatTile(parcel.x,     parcel.y - 1, parcel.z)) { tile |= 0b01000000; }
                        if (repeatTile(parcel.x + 1, parcel.y - 1, parcel.z)) { tile |= 0b00100000; }
                        if (repeatTile(parcel.x - 1, parcel.y,     parcel.z)) { tile |= 0b00010000; }
                        if (repeatTile(parcel.x + 1, parcel.y,     parcel.z)) { tile |= 0b00001000; }
                        if (repeatTile(parcel.x - 1, parcel.y + 1, parcel.z)) { tile |= 0b00000100; }
                        if (repeatTile(parcel.x,     parcel.y + 1, parcel.z)) { tile |= 0b00000010; }
                        if (repeatTile(parcel.x + 1, parcel.y + 1, parcel.z)) { tile |= 0b00000001; }
                        parcel.setTile(tile);

//                        Pixmap pxRock = _pxRocks.get(parcel.getRockInfo());
//                        if (pxRock != null) {
//                            pxOut.drawPixmap(pxRock, (parcel.x - fromX) * 32, (parcel.y - fromY) * 32, 0, 0, 32, 32);
//                        } else {
                        Gdx.app.postRunnable(() -> {
                            if (parcel.hasRock() && parcel.getRockInfo().hasGraphics()) {
                                Sprite sprite = spriteManager.getRock(parcel.getRockInfo().graphics.get(0), parcel.getTile());
                                if (sprite != null) {
                                    TextureData textureData = sprite.getTexture().getTextureData();
                                    if (!textureData.isPrepared()) {
                                        textureData.prepare();
                                    }
                                    Pixmap pxRockDo = textureData.consumePixmap();
                                    _pxRocks.put(parcel.getRockInfo(), pxRockDo);
                                    textureData.disposePixmap();
                                    pxOut.drawPixmap(pxRockDo, (parcel.x - fromX) * 32, (parcel.y - fromY) * 32, 0, 0, 32, 32);
                                }
                            }
                        });
//                        }
                    }

                    Pixmap.setBlending(Pixmap.Blending.None);
                }

                Gdx.app.postRunnable(() -> {
                    if (_groundLayers[col][row] != null) {
                        _groundLayers[col][row].dispose();
                    }
                    _groundLayers[col][row] = new Texture(pxGroundOut);
                    if (_rockLayers[col][row] != null) {
                        _rockLayers[col][row].dispose();
                    }
                    _rockLayers[col][row] = new Texture(pxOut);
                });
            });
        });
    }

    @Override
    public void onFloorChange(int floor) {
        _rockLayersUpToDate = new boolean[_cols][_rows];
        _floor = floor;
    }
}