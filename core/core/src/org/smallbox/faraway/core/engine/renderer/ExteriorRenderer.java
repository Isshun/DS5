package org.smallbox.faraway.core.engine.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import org.smallbox.faraway.core.*;
import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.model.GameConfig;
import org.smallbox.faraway.core.game.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.resource.PlantModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.core.util.Log;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ExteriorRenderer extends WorldRenderer {
    private static final int    CHUNK_SIZE = 16;

    private ExecutorService     _executor = Executors.newSingleThreadExecutor();
    private MapObjectModel      _itemSelected;
    private boolean             _firstRefresh;
    private Texture[][]         _rocks;
    private Texture[][]         _grounds;
    private boolean[][]         _groundsUpToDate;
    private int                 _rows;
    private int                 _cols;
    private Texture             _textureIn;
    private Texture             _textureRock;
    private int                 _width;
    private int                 _height;
    private int                 _floor;

    @Override
    protected void onLoad(Game game) {
        super.onLoad(game);

        _textureIn = new Texture("data/graphics/items/ground.png");
        _textureRock = new Texture("data/graphics/items/resources/granite.png");

        _cols = game.getInfo().worldWidth / CHUNK_SIZE;
        _rows = game.getInfo().worldHeight / CHUNK_SIZE;
        _width = game.getInfo().worldWidth;
        _height = game.getInfo().worldHeight;

        _floor = 9;
        _grounds = new Texture[_cols][_rows];
        _groundsUpToDate = new boolean[_cols][_rows];

//        long time = System.currentTimeMillis();
//        {
//            _grounds = new Texture[_cols][_rows];
//            Texture textureIn = new Texture("data/graphics/items/ground.png");
//            textureIn.getTextureData().prepare();
//            Pixmap pixmapIn = textureIn.getTextureData().consumePixmap();
//            for (int x = 0; x < _rows; x++) {
//                for (int y = 0; y < _cols; y++) {
//                    Pixmap pixmapOut = new Pixmap(CHUNK_SIZE * 32, CHUNK_SIZE * 32, Pixmap.Format.RGB888);
//                    for (int x2 = 0; x2 < CHUNK_SIZE; x2++) {
//                        for (int y2 = 0; y2 < CHUNK_SIZE; y2++) {
//                            pixmapOut.drawPixmap(pixmapIn, x2 * 32, y2 * 32, 0, 32, 32, 32);
//                        }
//                    }
//                    _grounds[x][y] = new Texture(pixmapOut);
//                    PixmapIO.writePNG(new FileHandle("cache/ground_" + x + "_" + y + ".png"), pixmapOut);
//                }
//            }
//            pixmapIn.dispose();
//        }
//        System.out.println("ground: " + (System.currentTimeMillis() - time));
//
//        time = System.currentTimeMillis();
//        {
//            _grounds = new Texture[_cols][_rows];
//            Texture textureIn = new Texture("data/graphics/items/ground.png");
//            textureIn.getTextureData().prepare();
//            Pixmap pixmapIn = textureIn.getTextureData().consumePixmap();
//            for (int x = 0; x < _rows; x++) {
//                for (int y = 0; y < _cols; y++) {
//                    Pixmap pixmapOut = new Pixmap(CHUNK_SIZE * 32, CHUNK_SIZE * 32, Pixmap.Format.RGB888);
//                    for (int x2 = 0; x2 < CHUNK_SIZE; x2++) {
//                        for (int y2 = 0; y2 < CHUNK_SIZE; y2++) {
//                            pixmapOut.drawPixmap(pixmapIn, x2 * 32, y2 * 32, 0, 32, 32, 32);
//                        }
//                    }
//                    _grounds[x][y] = new Texture(pixmapOut);
//                }
//            }
//            pixmapIn.dispose();
//        }
//        System.out.println("ground: " + (System.currentTimeMillis() - time));
//
//        time = System.currentTimeMillis();
//        for (int x = 0; x < _rows; x++) {
//            for (int y = 0; y < _cols; y++) {
//                Texture texture = new Texture("cache/ground_" + x + "_" + y + ".png");
//            }
//        }
//        System.out.println("ground: " + (System.currentTimeMillis() - time));
//        System.exit(0);
//
//        {
//            _rocks = new Texture[_cols][_rows];
//            Texture textureIn = new Texture("data/graphics/items/granite.png");
//            textureIn.getTextureData().prepare();
//            Pixmap pixmapIn = textureIn.getTextureData().consumePixmap();
//            for (int x = 0; x < _rows; x++) {
//                for (int y = 0; y < _cols; y++) {
//                    Pixmap pixmapOut = new Pixmap(CHUNK_SIZE * 32, CHUNK_SIZE * 32, Pixmap.Format.RGB888);
//                    for (int x2 = 0; x2 < CHUNK_SIZE; x2++) {
//                        for (int y2 = 0; y2 < CHUNK_SIZE; y2++) {
//                            pixmapOut.drawPixmap(pixmapIn, x2 * 32, y2 * 32, 0, 32, 32, 32);
//                        }
//                    }
//                    _rocks[x][y] = new Texture(pixmapOut);
//                }
//            }
//            pixmapIn.dispose();
//        }

        _layerGrid.setOnRefreshLayer((layer, fromX, fromY, toX, toY) -> {
//            Log.info("Refresh layer: " + layer.getIndex());

            ModuleHelper.getWorldModule().getParcels(fromX, toX-1, fromY, toY-1, _floor, _floor, new GetParcelListener() {
                @Override
                public void onGetParcel(Collection<ParcelModel> parcelsDo) {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            layer.begin();
                            layer.setRefresh();
                            for (ParcelModel parcel: parcelsDo) {
                                if (Data.config.render.floor) {
                                    refreshFloor(layer, parcel.getType(), parcel.x, parcel.y);
                                }
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
//            for (int x = toX - 1; x >= fromX; x--) {
//                for (int y = toY - 1; y >= fromY; y--) {
//                    ParcelModel parcel = ModuleHelper.getWorldModule().getParcel(x, y);
//                    if (parcel != null) {
//                        if (Data.config.render.floor) {
//                            refreshFloor(layer, parcel.getType(), x, y);
//                        }
//                        if (Data.config.render.structure) {
//                            refreshStructure(layer, parcel.getStructure(), x, y);
//                        }
//                        if (Data.config.render.resource) {
//                            refreshPlant(layer, parcel, parcel.getResource(), x, y);
//                        }
//                        if (Data.config.render.item) {
//                            refreshItems(layer, parcel.getItem(), x, y);
//                        }
//                    }
//                }
//            }
//
//            for (int x = toX - 1; x >= fromX; x--) {
//                for (int y = toY - 1; y >= fromY; y--) {
//                    ParcelModel parcel = ModuleHelper.getWorldModule().getParcel(x, y);
//                    if (parcel != null) {
//                        if (Data.config.render.consumable) {
//                            refreshConsumable(layer, parcel.getConsumable(), x, y);
//                        }
//                    }
//                }
//            }
                            layer.end();
                        }
                    });
                }
            });
        });

//        ModuleHelper.getWorldModule().getResources().forEach(resource -> {
//            if (resource.getInfo().graphics != null && resource.getInfo().graphics.get(0).type == GraphicInfo.Type.TERRAIN) {
//                ParcelModel parcel = resource.getParcel();
//                ItemInfo resourceInfo = resource.getInfo();
//
//            }
//        });

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
        _groundsUpToDate = new boolean[_cols][_rows];
        _floor = floor;
    }

    @Override
    public boolean isActive(GameConfig config) {
        return true;
    }

    @Override
    protected void onUpdate() {
    }

    @Override
    public void onRemoveRock(ParcelModel parcel) {
//        _grounds[parcel.x / CHUNK_SIZE][parcel.y / CHUNK_SIZE] = null;
        _groundsUpToDate[parcel.x / CHUNK_SIZE][parcel.y / CHUNK_SIZE] = false;
    }

    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        int fromX = (int) ((-viewport.getPosX() / Constant.TILE_WIDTH) * viewport.getScale());
        int fromY = (int) ((-viewport.getPosY() / Constant.TILE_HEIGHT) * viewport.getScale());
        int toX = fromX + 50;
        int toY = fromY + 40;

        int fromCol = Math.max(fromX / CHUNK_SIZE, 0);
        int fromRow = Math.max(fromY / CHUNK_SIZE, 0);
        int toCol = Math.min(toX / CHUNK_SIZE + 1, _cols);
        int toRow = Math.min(toY / CHUNK_SIZE + 1, _rows);

        int viewportX = viewport.getPosX();
        int viewportY = viewport.getPosY();

        // Draw chunks
        for (int col = fromCol; col < toCol; col++) {
            for (int row = fromRow; row < toRow; row++) {
                if (!_groundsUpToDate[col][row]) {
                    _groundsUpToDate[col][row] = true;
                    createGround(col, row);
                }
                renderer.drawChunk(_grounds[col][row], viewportX + (col * CHUNK_SIZE * Constant.TILE_WIDTH), viewportY + (row * CHUNK_SIZE * Constant.TILE_HEIGHT));
            }
        }

//        for (int x = toX; x >= fromX; x--) {
//            for (int y = toY; y >= fromY; y--) {
//                ParcelModel parcel = WorldHelper.getParcel(x, y);
//                if (parcel != null) {
//                    if (parcel.hasResource()) {
//                        renderer.draw(_spriteManager.getItem(parcel.getResource(), parcel.getResource().getTile(), parcel.getResource().getTile()), (x * Constant.TILE_WIDTH) + viewportX, (y * Constant.TILE_HEIGHT) + viewportY);
//                    }
//                    if (parcel.getStructure() != null) {
//                        renderer.draw(_spriteManager.getItem(parcel.getStructure()), (x * Constant.TILE_WIDTH) + viewportX, (y * Constant.TILE_HEIGHT) + viewportY);
//                    }
//                    if (parcel.getNetworkObjects() != null) {
//                        for (NetworkObjectModel networkObject: parcel.getNetworkObjects()) {
//                            renderer.draw(_spriteManager.getItem(networkObject), (x * Constant.TILE_WIDTH) + viewportX, (y * Constant.TILE_HEIGHT) + viewportY);
//                        }
//                    }
//                    if (parcel.getItem() != null && parcel == parcel.getItem().getParcel()) {
//                        renderer.draw(_spriteManager.getItem(parcel.getItem()), (x * Constant.TILE_WIDTH) + viewportX, (y * Constant.TILE_HEIGHT) + viewportY);
//                    }
//                    if (parcel.getConsumable() != null) {
//                        renderer.draw(_spriteManager.getItem(parcel.getConsumable()), (x * Constant.TILE_WIDTH) + viewportX, (y * Constant.TILE_HEIGHT) + viewportY);
//                    }
//                }
//            }
//        }

//        System.out.println("from " + fromX + "x" + fromY);
//
//        if (_needRefresh) {
//            _needRefresh = false;
//            refreshLayers();
//        }
//

        if (_layerGrid != null) {
            _layerGrid.draw(renderer);
        }
    }

    private void createGround(int col, int row) {
        _executor.submit((Runnable) () -> {
            final int fromX = Math.max(col * CHUNK_SIZE, 0);
            final int fromY = Math.max(row * CHUNK_SIZE, 0);
            final int toX = Math.min(col * CHUNK_SIZE + CHUNK_SIZE, Game.getInstance().getInfo().worldWidth);
            final int toY = Math.min(row * CHUNK_SIZE + CHUNK_SIZE, Game.getInstance().getInfo().worldHeight);

            ModuleHelper.getWorldModule().getParcels(fromX, fromX + CHUNK_SIZE, fromY, fromY + CHUNK_SIZE, _floor, _floor, parcels -> {
                _textureIn.getTextureData().prepare();
                Pixmap pixmapIn = _textureIn.getTextureData().consumePixmap();

                _textureRock.getTextureData().prepare();
                Pixmap pixmapRock = _textureRock.getTextureData().consumePixmap();
                Pixmap pixmapOut = new Pixmap(CHUNK_SIZE * 32, CHUNK_SIZE * 32, Pixmap.Format.RGB888);

                for (ParcelModel parcel: parcels) {
                    // Draw ground
                    pixmapOut.drawPixmap(pixmapIn, (parcel.x - fromX) * 32, (parcel.y - fromY) * 32, parcel.getTile() * 32, 32, 32, 32);

                    // Draw resource
                    if (parcel.hasRock()) {
                        pixmapOut.drawPixmap(pixmapRock, (parcel.x - fromX) * 32, (parcel.y - fromY) * 32, 0, 0, 32, 32);
                    }

                    ItemInfo resourceInfo = parcel.getRockInfo();
                    int tile = 0;
                    if (resourceInfo != null) {
                        if (WorldHelper.getResourceInfo(parcel.x - 1, parcel.y - 1, parcel.z) == resourceInfo) { tile |= 0b10000000; }
                        if (WorldHelper.getResourceInfo(parcel.x,     parcel.y - 1, parcel.z) == resourceInfo) { tile |= 0b01000000; }
                        if (WorldHelper.getResourceInfo(parcel.x + 1, parcel.y - 1, parcel.z) == resourceInfo) { tile |= 0b00100000; }
                        if (WorldHelper.getResourceInfo(parcel.x - 1, parcel.y,     parcel.z) == resourceInfo) { tile |= 0b00010000; }
                        if (WorldHelper.getResourceInfo(parcel.x + 1, parcel.y,     parcel.z) == resourceInfo) { tile |= 0b00001000; }
                        if (WorldHelper.getResourceInfo(parcel.x - 1, parcel.y + 1, parcel.z) == resourceInfo) { tile |= 0b00000100; }
                        if (WorldHelper.getResourceInfo(parcel.x,     parcel.y + 1, parcel.z) == resourceInfo) { tile |= 0b00000010; }
                        if (WorldHelper.getResourceInfo(parcel.x + 1, parcel.y + 1, parcel.z) == resourceInfo) { tile |= 0b00000001; }
                    }
                    parcel.setTile(tile);
                }

                pixmapIn.dispose();
                pixmapRock.dispose();

                Gdx.app.postRunnable(() -> {
                    _grounds[col][row] = new Texture(pixmapOut);
                    pixmapOut.dispose();
                });
            });
        });
    }

    private void refreshPlant(RenderLayer layer, ParcelModel parcel, PlantModel plant, int x, int y) {
        if (parcel != null && plant != null) {
            SpriteModel sprite = _spriteManager.getItem(plant, parcel.getTile(), plant.getTile());
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