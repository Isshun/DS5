package org.smallbox.faraway.core.engine.renderer;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import org.smallbox.faraway.core.*;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.model.GameConfig;
import org.smallbox.faraway.core.game.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.resource.ResourceModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.core.util.Log;

public class ExteriorRenderer extends WorldRenderer {
    private static final int CHUNK_SIZE = 25;
    private MapObjectModel      _itemSelected;
    private boolean             _firstRefresh;
    private Texture[][]         _rocks;
    private Texture[][]         _grounds;
    private int                 _rows;
    private int                 _cols;
    private Texture             _textureIn;
    private Texture             _textureRock;
    private int                 _width;
    private int                 _height;
    private int                 _floor;
    private Sprite              _spriteMap = new Sprite();

    @Override
    protected void onLoad(Game game) {
        super.onLoad(game);

        _textureIn = new Texture("data/graphics/items/ground.png");
        _textureRock = new Texture("data/graphics/items/resources/granite.png");

        _rows = game.getInfo().worldWidth / CHUNK_SIZE;
        _cols = game.getInfo().worldHeight / CHUNK_SIZE;
        _width = game.getInfo().worldWidth;
        _height = game.getInfo().worldHeight;

        _floor = 9;
        _grounds = new Texture[_cols][_rows];

        ParcelModel[][][] parcels = ModuleHelper.getWorldModule().getParcels();
        Pixmap pixmap = new Pixmap(_width, _height, Pixmap.Format.RGB888);
        for (int x = 0; x < _width; x++) {
            for (int y = 0; y < _height; y++) {
                if (parcels[x][y][_floor].getStructure() != null) {
                    pixmap.drawPixel(x, y, 0x1acb51);
                } else if (parcels[x][y][_floor].getResource() != null) {
                    pixmap.drawPixel(x, y, 0x2a71c8);
                } else {
                    pixmap.drawPixel(x, y, 0x0e272f);
                }
            }
        }
        _spriteMap.setSize(_width, _height);
        _spriteMap.setTexture(new Texture(pixmap));
        _spriteMap.setRegion(0, 0, _width, _height);
        _spriteMap.flip(false, true);

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
            Log.info("Refresh layer: " + layer.getIndex());

            layer.begin();
            layer.setRefresh();
            for (int x = toX - 1; x >= fromX; x--) {
                for (int y = toY - 1; y >= fromY; y--) {
                    ParcelModel parcel = ModuleHelper.getWorldModule().getParcel(x, y);
                    if (parcel != null) {
                        if (Data.config.render.floor) {
                            refreshFloor(layer, parcel.getType(), x, y);
                        }
                        if (Data.config.render.structure) {
                            refreshStructure(layer, parcel.getStructure(), x, y);
                        }
                        if (Data.config.render.resource) {
                            refreshResource(layer, parcel, parcel.getResource(), x, y);
                        }
                        if (Data.config.render.item) {
                            refreshItems(layer, parcel.getItem(), x, y);
                        }
                    }
                }
            }

            for (int x = toX - 1; x >= fromX; x--) {
                for (int y = toY - 1; y >= fromY; y--) {
                    ParcelModel parcel = ModuleHelper.getWorldModule().getParcel(x, y);
                    if (parcel != null) {
                        if (Data.config.render.consumable) {
                            refreshConsumable(layer, parcel.getConsumable(), x, y);
                        }
                    }
                }
            }
            layer.end();
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
        _grounds = new Texture[_cols][_rows];
        _floor = floor;
    }

    @Override
    public boolean isActive(GameConfig config) {
        return true;
    }

    @Override
    protected void onUpdate() {
        ModuleHelper.getWorldModule().getResources().forEach(resource -> {
            if (resource.getInfo().graphics != null && resource.getInfo().graphics.get(0).type == GraphicInfo.Type.TERRAIN) {
                ParcelModel parcel = resource.getParcel();

                boolean topLeft = !(WorldHelper.getResource(parcel.x - 1, parcel.y - 1) == null || WorldHelper.getResource(parcel.x - 1, parcel.y - 1).getInfo() != resource.getInfo());
                boolean top = !(WorldHelper.getResource(parcel.x, parcel.y - 1) == null || WorldHelper.getResource(parcel.x, parcel.y - 1).getInfo() != resource.getInfo());
                boolean topRight = !(WorldHelper.getResource(parcel.x + 1, parcel.y - 1) == null || WorldHelper.getResource(parcel.x + 1, parcel.y - 1).getInfo() != resource.getInfo());

                boolean left = !(WorldHelper.getResource(parcel.x - 1, parcel.y) == null || WorldHelper.getResource(parcel.x - 1, parcel.y).getInfo() != resource.getInfo());
                boolean right = !(WorldHelper.getResource(parcel.x + 1, parcel.y) == null || WorldHelper.getResource(parcel.x + 1, parcel.y).getInfo() != resource.getInfo());

                boolean bottomLeft = !(WorldHelper.getResource(parcel.x - 1, parcel.y + 1) == null || WorldHelper.getResource(parcel.x - 1, parcel.y + 1).getInfo() != resource.getInfo());
                boolean bottom = !(WorldHelper.getResource(parcel.x, parcel.y + 1) == null || WorldHelper.getResource(parcel.x, parcel.y + 1).getInfo() != resource.getInfo());
                boolean bottomRight = !(WorldHelper.getResource(parcel.x + 1, parcel.y + 1) == null || WorldHelper.getResource(parcel.x + 1, parcel.y + 1).getInfo() != resource.getInfo());

                int tile = 0;

                if (topLeft)     { tile |= 0b10000000; }
                if (top)         { tile |= 0b01000000; }
                if (topRight)    { tile |= 0b00100000; }
                if (left)        { tile |= 0b00010000; }
                if (right)       { tile |= 0b00001000; }
                if (bottomLeft)  { tile |= 0b00000100; }
                if (bottom)      { tile |= 0b00000010; }
                if (bottomRight) { tile |= 0b00000001; }

                resource.setTile(tile);
            }
        });
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
                if (_grounds[col][row] == null) {
                    _grounds[col][row] = createGround(col, row);
                }
                renderer.drawChunk(_grounds[col][row], viewportX + (col * CHUNK_SIZE * Constant.TILE_WIDTH), viewportY + (row * CHUNK_SIZE * Constant.TILE_HEIGHT));
            }
        }

//        for (int x = toX; x >= fromX; x--) {
//            for (int y = toY; y >= fromY; y--) {
//                ParcelModel parcel = WorldHelper.getParcel(x, y);
//                if (parcel != null) {
//                    if (parcel.getResource() != null) {
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

    private Texture createGround(int col, int row) {
        Texture textureOut;

        _textureIn.getTextureData().prepare();
        Pixmap pixmapIn = _textureIn.getTextureData().consumePixmap();

        _textureRock.getTextureData().prepare();
        Pixmap pixmapRock = _textureRock.getTextureData().consumePixmap();

        Pixmap pixmapOut = new Pixmap(CHUNK_SIZE * 32, CHUNK_SIZE * 32, Pixmap.Format.RGB888);

        ParcelModel[][][] parcels = ModuleHelper.getWorldModule().getParcels();

        int fromX = col * CHUNK_SIZE;
        int fromY = row * CHUNK_SIZE;
        for (int x = fromX; x < fromX + CHUNK_SIZE; x++) {
            for (int y = fromY; y < fromY + CHUNK_SIZE; y++) {
                // Draw ground
                pixmapOut.drawPixmap(pixmapIn, (x - fromX) * 32, (y - fromY) * 32, 0, 32, 32, 32);

                // Draw resource
                if (parcels[x][y][_floor].getResource() != null && parcels[x][y][_floor].getResource().isRock()) {
                    pixmapOut.drawPixmap(pixmapRock, (x - fromX) * 32, (y - fromY) * 32, 0, 0, 32, 32);
                }
            }
        }
        textureOut = new Texture(pixmapOut);
        pixmapIn.dispose();
        pixmapRock.dispose();

        return textureOut;
    }

    private void refreshResource(RenderLayer layer, ParcelModel parcel, ResourceModel resource, int x, int y) {
        if (parcel != null && resource != null && !resource.isRock()) {
            SpriteModel sprite = _spriteManager.getItem(parcel.getResource(), parcel.getResource().getTile(), parcel.getResource().getTile());
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

    public Sprite getMap() {
        return _spriteMap;
    }
}