package org.smallbox.faraway.core.engine.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.engine.module.java.ModuleHelper;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.model.GameConfig;
import org.smallbox.faraway.core.game.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.game.module.world.model.NetworkObjectModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.PlantModel;
import org.smallbox.faraway.core.util.Constant;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExteriorRenderer extends WorldRenderer {
    private static final int        CHUNK_SIZE = 16;

    private ExecutorService         _executor = Executors.newSingleThreadExecutor();
    private MapObjectModel          _itemSelected;
    private boolean                 _firstRefresh;
    private Texture[][]             _rocks;
    private Texture[][]             _groundLayers;
    private Texture[][]             _rockLayers;
    private boolean[][]             _rockLayersUpToDate;
    private int                     _rows;
    private int                     _cols;
    private int                     _width;
    private int                     _height;
    private int                     _floor;
    private Map<ItemInfo, Pixmap>   _pxRocks;
    private Map<ItemInfo, Pixmap>   _pxGrounds;

    @Override
    protected void onLoad(Game game) {
        super.onLoad(game);

        _pxGrounds = new HashMap<>();

        Data.getData().items.stream().filter(itemInfo -> itemInfo.isGround).forEach(itemInfo -> {
            Texture textureIn = new Texture(new FileHandle(SpriteManager.getFile(itemInfo.graphics.get(0))));
            textureIn.getTextureData().prepare();
            _pxGrounds.put(itemInfo, textureIn.getTextureData().consumePixmap());
        });

        _pxRocks = new HashMap<>();

        _cols = game.getInfo().worldWidth / CHUNK_SIZE;
        _rows = game.getInfo().worldHeight / CHUNK_SIZE;
        _width = game.getInfo().worldWidth;
        _height = game.getInfo().worldHeight;

        _floor = WorldHelper.getCurrentFloor();
        _groundLayers = new Texture[_cols][_rows];
        _rockLayers = new Texture[_cols][_rows];
        _rockLayersUpToDate = new boolean[_cols][_rows];

        _layerGrid.setOnRefreshLayer((layer, fromX, fromY, toX, toY) -> {
            ModuleHelper.getWorldModule().getParcels(fromX, toX-1, fromY, toY-1, _floor, _floor, parcelsDo ->
                    Gdx.app.postRunnable(() -> {
                        layer.begin();
                        layer.setRefresh();
                        for (ParcelModel parcel: parcelsDo) {
                            if (Data.config.render.structure) {
                                refreshStructure(layer, parcel.getStructure(), parcel.x, parcel.y);
                            }
                            if (Data.config.render.resource) {
                                refreshPlant(layer, parcel, parcel.getPlant(), parcel.x, parcel.y);
                            }
                            if (Data.config.render.item) {
                                refreshItems(layer, parcel.getItem(), parcel.x, parcel.y);
                            }
                        }

                        for (ParcelModel parcel: parcelsDo) {
                            if (Data.config.render.consumable) {
                                refreshConsumable(layer, parcel.getConsumable(), parcel.x, parcel.y);
                            }
                        }
                        layer.end();
                    }));
        });
    }

    public int getLevel() {
        return MainRenderer.WORLD_RENDERER_LEVEL;
    }

    public void onRefresh(int frame) {
        if (_firstRefresh) {
            _firstRefresh = false;
            _layerGrid.refresh();
        }
    }

    @Override
    public void onFloorChange(int floor) {
        if (_layerGrid != null) {
            _layerGrid.refreshAll();
        }
        _rockLayersUpToDate = new boolean[_cols][_rows];
        _floor = floor;
    }

    @Override
    public boolean isActive(GameConfig config) {
        return true;
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
    }

    @Override
    public void onRemoveRock(ParcelModel parcel) {
//        _grounds[parcel.x / CHUNK_SIZE][parcel.y / CHUNK_SIZE] = null;
        _rockLayersUpToDate[parcel.x / CHUNK_SIZE][parcel.y / CHUNK_SIZE] = false;
    }

    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        int fromX = Math.max((int) ((-viewport.getPosX() / Constant.TILE_WIDTH) * viewport.getScale()), 0);
        int fromY = Math.max((int) ((-viewport.getPosY() / Constant.TILE_HEIGHT) * viewport.getScale()), 0);
        int toX = Math.min(fromX + 50, Game.getInstance().getInfo().worldWidth);
        int toY = Math.min(fromY + 40, Game.getInstance().getInfo().worldHeight);

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
                renderer.drawChunk(_groundLayers[col][row], viewportX + (col * CHUNK_SIZE * Constant.TILE_WIDTH), viewportY + (row * CHUNK_SIZE * Constant.TILE_HEIGHT));
                renderer.drawChunk(_rockLayers[col][row], viewportX + (col * CHUNK_SIZE * Constant.TILE_WIDTH), viewportY + (row * CHUNK_SIZE * Constant.TILE_HEIGHT));
            }
        }

        ParcelModel[][][] parcels = ModuleHelper.getWorldModule().getParcels();
        for (int x = toX-1; x >= fromX; x--) {
            for (int y = toY-1; y >= fromY; y--) {
                ParcelModel parcel = parcels[x][y][_floor];
                if (parcel.hasPlant()) {
                    renderer.draw(_spriteManager.getItem(parcel.getPlant().getGraphic(), parcel.getTile(), parcel.getPlant().getTile()), (x * Constant.TILE_WIDTH) + viewportX, (y * Constant.TILE_HEIGHT) + viewportY);
                }
                if (parcel.getStructure() != null) {
                    renderer.draw(_spriteManager.getItem(parcel.getStructure()), (x * Constant.TILE_WIDTH) + viewportX, (y * Constant.TILE_HEIGHT) + viewportY);
                }
                if (parcel.getNetworkObjects() != null) {
                    for (NetworkObjectModel networkObject: parcel.getNetworkObjects()) {
                        renderer.draw(_spriteManager.getItem(networkObject), (x * Constant.TILE_WIDTH) + viewportX, (y * Constant.TILE_HEIGHT) + viewportY);
                    }
                }
                if (parcel.getItem() != null && parcel == parcel.getItem().getParcel()) {
                    renderer.draw(_spriteManager.getItem(parcel.getItem()), (x * Constant.TILE_WIDTH) + viewportX, (y * Constant.TILE_HEIGHT) + viewportY);
                }
                if (parcel.getConsumable() != null) {
                    renderer.draw(_spriteManager.getItem(parcel.getConsumable()), (x * Constant.TILE_WIDTH) + viewportX, (y * Constant.TILE_HEIGHT) + viewportY);
                }
            }
        }
    }

    private boolean repeatTile(int x, int y, int z) {
        return WorldHelper.getParcel(x, y, z) == null || WorldHelper.hasRock(x, y, z) || WorldHelper.hasWall(x, y, z);
    }

    private void createGround(int col, int row) {
        _executor.submit((Runnable) () -> {
            final int fromX = Math.max(col * CHUNK_SIZE, 0);
            final int fromY = Math.max(row * CHUNK_SIZE, 0);
            final int toX = Math.min(col * CHUNK_SIZE + CHUNK_SIZE, Game.getInstance().getInfo().worldWidth);
            final int toY = Math.min(row * CHUNK_SIZE + CHUNK_SIZE, Game.getInstance().getInfo().worldHeight);

            ModuleHelper.getWorldModule().getParcels(fromX, fromX + CHUNK_SIZE - 1, fromY, fromY + CHUNK_SIZE - 1, _floor, _floor, parcels -> {
                Pixmap.setBlending(Pixmap.Blending.None);
                SpriteManager spriteManager = SpriteManager.getInstance();
                Pixmap pxGroundOut = new Pixmap(CHUNK_SIZE * 32, CHUNK_SIZE * 32, Pixmap.Format.RGBA8888);
                Pixmap pxOut = new Pixmap(CHUNK_SIZE * 32, CHUNK_SIZE * 32, Pixmap.Format.RGBA8888);

                for (ParcelModel parcel: parcels) {
                    // Draw ground
                    if (parcel.hasGround()) {
                        pxGroundOut.drawPixmap(_pxGrounds.get(parcel.getGroundInfo()), (parcel.x - fromX) * 32, (parcel.y - fromY) * 32, 0, 32, 32, 32);
                    } else if (WorldHelper.hasGround(parcel.x, parcel.y, parcel.z - 1)) {
                        pxGroundOut.drawPixmap(_pxGrounds.get(WorldHelper.getGroundInfo(parcel.x, parcel.y, parcel.z - 1)), (parcel.x - fromX) * 32, (parcel.y - fromY) * 32, 32, 32, 32, 32);
                    } else if (WorldHelper.hasGround(parcel.x, parcel.y, parcel.z - 2)) {
                        pxGroundOut.drawPixmap(_pxGrounds.get(WorldHelper.getGroundInfo(parcel.x, parcel.y, parcel.z - 2)), (parcel.x - fromX) * 32, (parcel.y - fromY) * 32, 64, 32, 32, 32);
                    } else if (WorldHelper.hasGround(parcel.x, parcel.y, parcel.z - 3)) {
                        pxGroundOut.drawPixmap(_pxGrounds.get(WorldHelper.getGroundInfo(parcel.x, parcel.y, parcel.z - 3)), (parcel.x - fromX) * 32, (parcel.y - fromY) * 32, 96, 32, 32, 32);
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

    private void refreshPlant(RenderLayer layer, ParcelModel parcel, PlantModel plant, int x, int y) {
        if (parcel != null && plant != null) {
            Sprite sprite = _spriteManager.getItem(plant.getGraphic(), parcel.getTile(), plant.getTile());
            layer.draw(sprite, (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
        }
    }

    public void onDrawSelected(GDXRenderer renderer, Viewport viewport, double animProgress) {
        if (_itemSelected != null) {
            int offset = 0;
//        switch (frame / 10 % 5) {
//            case 1: offset = 1; break;
//            case 2: offset = 2; break;
//            case 3: offset = 3; break;
//            case 4: offset = 2; break;
//            case 5: offset = 1; break;
//        }

            int fromX = 0;
            int fromY = 0;
            int toX = 32;
            int toY = 32;
            if (_itemSelected.getGraphic() != null && _itemSelected.getGraphic().textureRect != null) {
                Rectangle rect = _itemSelected.getGraphic().textureRect;
                fromX = (int)(rect.getX()/2);
                fromY = (int)(rect.getY()/2);
                toX = (int)(rect.getX() + rect.getWidth()) - 8;
                toY = (int)(rect.getY() + rect.getHeight()) - 8;
            }

            int x = _itemSelected.getParcel().x * Constant.TILE_WIDTH + viewport.getPosX();
            int y = _itemSelected.getParcel().y * Constant.TILE_HEIGHT + viewport.getPosY();
            renderer.draw(_spriteManager.getSelectorCorner(0), x - offset + fromX, y - offset + fromY);
            renderer.draw(_spriteManager.getSelectorCorner(1), x + offset + toX, y - offset + fromY);
            renderer.draw(_spriteManager.getSelectorCorner(2), x - offset + fromX, y + offset + toY);
            renderer.draw(_spriteManager.getSelectorCorner(3), x + offset + toX, y + offset + toY);

            if (_itemSelected.getInfo().slots != null) {
                for (int[] slot: _itemSelected.getInfo().slots) {
                    renderer.draw(_spriteManager.getIcon("data/res/ic_slot.png"),
                            (_itemSelected.getParcel().x + slot[0])* Constant.TILE_WIDTH + viewport.getPosX(),
                            (_itemSelected.getParcel().y + slot[1])* Constant.TILE_HEIGHT + viewport.getPosY());
                }
            }
        }
    }
}