package org.smallbox.faraway.core.engine.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import org.smallbox.faraway.core.GraphicInfo;
import org.smallbox.faraway.core.RenderLayer;
import org.smallbox.faraway.core.SpriteManager;
import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.core.engine.SpriteModel;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.GameConfig;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.module.world.model.*;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.core.util.Log;

public class WorldRenderer extends BaseRenderer {
    private int[][][] TEMPLATES = new int[][][] {
            new int[][] {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}},
            new int[][] {{1, 1, 1}, {1, 1, 1}, {1, 1, 0}},
            new int[][] {{1, 1, 1}, {1, 1, 1}, {0, 1, 1}},
            new int[][] {{1, 1, 0}, {1, 1, 1}, {1, 1, 1}},
            new int[][] {{0, 1, 1}, {1, 1, 1}, {1, 1, 1}},

            // Inner without corners
            new int[][] {{0, 1, 0}, {1, 1, 1}, {0, 1, 0}},
            new int[][] {{0, 0, 0}, {1, 1, 1}, {0, 1, 0}},
            new int[][] {{0, 1, 0}, {1, 1, 1}, {0, 0, 0}},
            new int[][] {{0, 1, 0}, {0, 1, 1}, {0, 1, 0}},
            new int[][] {{0, 1, 0}, {1, 1, 0}, {0, 1, 0}},

            // T
            new int[][] {{2, 1, 2}, {0, 1, 0}, {2, 0, 2}},
            new int[][] {{2, 0, 2}, {0, 1, 0}, {2, 1, 2}},
            new int[][] {{2, 0, 2}, {1, 1, 0}, {2, 0, 2}},
            new int[][] {{2, 0, 2}, {0, 1, 1}, {2, 0, 2}},

            // =
            new int[][] {{2, 1, 2}, {0, 1, 0}, {2, 1, 2}},
            new int[][] {{2, 0, 2}, {1, 1, 1}, {2, 0, 2}},

            // Corners
            new int[][] {{2, 2, 2}, {2, 2, 1}, {2, 1, 1}},
            new int[][] {{2, 1, 1}, {2, 2, 1}, {2, 2, 2}},
            new int[][] {{2, 2, 2}, {1, 2, 2}, {1, 1, 2}},
            new int[][] {{1, 1, 2}, {1, 2, 2}, {2, 2, 2}},

            new int[][] {{0, 0, 0}, {0, 0, 1}, {2, 1, 0}},
            new int[][] {{2, 1, 2}, {1, 0, 0}, {0, 0, 2}},
            new int[][] {{2, 0, 2}, {1, 0, 0}, {2, 1, 2}},
            new int[][] {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}},
    };

    private static final int    CACHE_SIZE = 25;

    private int                 _cacheCols;
    private boolean             _needRefresh;
    private SpriteManager       _spriteManager;
    private RenderLayer[][]     _layers;
    //    private RenderLayer[][]     _layersLight;
    private MapObjectModel _itemSelected;
    private Viewport            _viewport;
    private boolean             _firstRefresh;

    @Override
    public void init() {
        _spriteManager = SpriteManager.getInstance();
        _viewport = Game.getInstance().getViewport();
        _needRefresh = true;
        _firstRefresh = true;
        _cacheCols = (250 / CACHE_SIZE);
        {
            _layers = new RenderLayer[_cacheCols][_cacheCols];
            int index = 0;
            for (int i = 0; i < _cacheCols; i++) {
                for (int j = 0; j < _cacheCols; j++) {
                    _layers[i][j] = new RenderLayer(index++,
                            i * CACHE_SIZE * Constant.TILE_WIDTH,
                            j * CACHE_SIZE * Constant.TILE_HEIGHT,
                            CACHE_SIZE * Constant.TILE_WIDTH,
                            CACHE_SIZE * Constant.TILE_HEIGHT);
                }
            }
        }
//        {
//            _layersLight = new RenderLayer[_cacheCols][_cacheCols];
//            int index = 0;
//            for (int i = 0; i < _cacheCols; i++) {
//                for (int j = 0; j < _cacheCols; j++) {
//                    _layersLight[i][j] = new RenderLayer(index++,
//                            i * CACHE_SIZE * Constant.TILE_WIDTH,
//                            j * CACHE_SIZE * Constant.TILE_HEIGHT,
//                            CACHE_SIZE * Constant.TILE_WIDTH,
//                            CACHE_SIZE * Constant.TILE_HEIGHT);
//                }
//            }
//        }
    }

    public int getLevel() {
        return -100;
    }

    public void onRefresh(int frame) {
        if (_firstRefresh) {
            _firstRefresh = false;
            refreshLayers();
        }
    }

    @Override
    public boolean isActive(GameConfig config) {
        return true;
    }

    private void refreshLayers() {
        Log.info("Refresh layers");

        for (int i = 0; i < _cacheCols; i++) {
            for (int j = 0; j < _cacheCols; j++) {
                if (_layers[i][j].isVisible(_viewport) && _layers[i][j].needRefresh()) {
                    _layers[i][j].refresh();
                    refreshLayer(_layers[i][j], i * CACHE_SIZE, j * CACHE_SIZE, (i + 1) * CACHE_SIZE, (j + 1) * CACHE_SIZE);
//                    refreshLayerLight(_layersLight[i][j], i * CACHE_SIZE, j * CACHE_SIZE, (i + 1) * CACHE_SIZE, (j + 1) * CACHE_SIZE);
                }
            }
        }
    }

    public void refreshAll() {
        for (int i = 0; i < _cacheCols; i++) {
            for (int j = 0; j < _cacheCols; j++) {
                _layers[i][j].planRefresh();
//                _layersLight[i][j].planRefresh();
            }
        }
        _needRefresh = true;
    }

    private void refreshLayerLight(RenderLayer layer, int fromX, int fromY, int toX, int toY) {
        Log.info("Refresh layer: " + layer.getIndex());

//        int mb = 1024 * 1024;
//        Runtime runtime = Runtime.getRuntime();
//        int used = (int) ((runtime.totalMemory() - runtime.freeMemory()) / mb);
//        int total = (int) (runtime.totalMemory() / mb);
//        System.out.println("RefreshLayer: " + used + "/" + total);

        Color[] colors = new Color[] {
                new Color(0, 0, 0, .9f),
                new Color(0, 0, 0, .8f),
                new Color(0, 0, 0, .7f),
                new Color(0, 0, 0, .6f),
                new Color(0, 0, 0, .5f),
                new Color(0, 0, 0, .4f),
                new Color(0, 0, 0, .3f),
                new Color(0, 0, 0, .2f),
                new Color(0, 0, 0, .1f),
                new Color(0, 0, 0, .0f),
                new Color(0, 0, 0, .0f),
        };

        layer.begin();
        layer.setRefresh();
        for (int x = toX - 1; x >= fromX; x--) {
            for (int y = toY - 1; y >= fromY; y--) {
                ParcelModel parcel = ModuleHelper.getWorldModule().getParcel(x, y);
                if (parcel != null) {
                    layer.draw(colors[Math.min(10, (int)(parcel.getLight() * 10))], (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
                }
            }
        }
        layer.end();
    }

    private void refreshLayer(RenderLayer layer, int fromX, int fromY, int toX, int toY) {
        Log.info("Refresh layer: " + layer.getIndex());

//        int mb = 1024 * 1024;
//        Runtime runtime = Runtime.getRuntime();
//        int used = (int) ((runtime.totalMemory() - runtime.freeMemory()) / mb);
//        int total = (int) (runtime.totalMemory() / mb);
//        System.out.println("RefreshLayer: " + used + "/" + total);

        layer.begin();
        layer.setRefresh();
        for (int x = toX - 1; x >= fromX; x--) {
            for (int y = toY - 1; y >= fromY; y--) {
//                if (cacheOnScreen(x / CACHE_SIZE, y / CACHE_SIZE)) {
//                }
                ParcelModel parcel = ModuleHelper.getWorldModule().getParcel(x, y);
                if (parcel != null) {
                    if (GameData.config.render.floor) {
                        refreshFloor(layer, parcel.getType(), x, y);
                    }
                    if (GameData.config.render.structure) {
                        refreshStructure(layer, parcel.getStructure(), x, y);
                    }
                    if (GameData.config.render.resource) {
                        refreshResource(layer, parcel, parcel.getResource(), x, y);
                    }
                    if (GameData.config.render.item) {
                        refreshItems(layer, parcel.getItem(), x, y);
                    }
                }
            }
        }
        for (int x = toX - 1; x >= fromX; x--) {
            for (int y = toY - 1; y >= fromY; y--) {
//                if (cacheOnScreen(x / CACHE_SIZE, y / CACHE_SIZE)) {
//                }
                ParcelModel parcel = ModuleHelper.getWorldModule().getParcel(x, y);
                if (parcel != null) {
                    if (GameData.config.render.consumable) {
                        refreshConsumable(layer, parcel.getConsumable(), x, y);
                    }
                }
            }
        }
        layer.end();
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
//            SpriteModel sprite = resource.isRock() && WorldHelper.isSurroundedByRock(parcel) ? _spriteManager.getGround(11) : _spriteManager.getItem(resource, tile, tile);
//            if (sprite != null) {
//                layer.draw(sprite, (x % CACHE_SIZE) * Constant.TILE_WIDTH + offsetX, (y % CACHE_SIZE) * Constant.TILE_HEIGHT + offsetX);
//            }
        });
    }

    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        int fromX = (int) ((-viewport.getPosX() / Constant.TILE_WIDTH) * viewport.getScale());
        int fromY = (int) ((-viewport.getPosY() / Constant.TILE_HEIGHT) * viewport.getScale());
        int toX = fromX + 50;
        int toY = fromY + 40;

        int offsetX = viewport.getPosX();
        int offsetY = viewport.getPosY();

        for (int x = toX; x >= fromX; x--) {
            for (int y = toY; y >= fromY; y--) {
                ParcelModel parcel = WorldHelper.getParcel(x, y);
                if (parcel != null) {
                    renderer.draw(_spriteManager.getGround(1), (x * Constant.TILE_WIDTH) + offsetX, (y * Constant.TILE_HEIGHT) + offsetY);
                }
            }
        }

        for (int x = toX; x >= fromX; x--) {
            for (int y = toY; y >= fromY; y--) {
                ParcelModel parcel = WorldHelper.getParcel(x, y);
                if (parcel != null) {
                    if (parcel.getResource() != null) {
                        if (parcel.getResource().getTile() == 42) {
                            renderer.draw(_spriteManager.getItem(parcel.getResource(), parcel.getResource().getTile(), parcel.getResource().getTile()), (x * Constant.TILE_WIDTH) + offsetX - 16, (y * Constant.TILE_HEIGHT) + offsetY - 16);
                        } else {
                            renderer.draw(_spriteManager.getItem(parcel.getResource(), parcel.getResource().getTile(), parcel.getResource().getTile()), (x * Constant.TILE_WIDTH) + offsetX, (y * Constant.TILE_HEIGHT) + offsetY);
                        }
                    }
                    if (parcel.getStructure() != null) {
                        renderer.draw(_spriteManager.getItem(parcel.getStructure()), (x * Constant.TILE_WIDTH) + offsetX, (y * Constant.TILE_HEIGHT) + offsetY);
                    }
                    if (parcel.getItem() != null && parcel == parcel.getItem().getParcel()) {
                        renderer.draw(_spriteManager.getItem(parcel.getItem()), (x * Constant.TILE_WIDTH) + offsetX, (y * Constant.TILE_HEIGHT) + offsetY);
                    }
                    if (parcel.getConsumable() != null) {
                        renderer.draw(_spriteManager.getItem(parcel.getConsumable()), (x * Constant.TILE_WIDTH) + offsetX, (y * Constant.TILE_HEIGHT) + offsetY);
                    }
                }
            }
        }

//        System.out.println("from " + fromX + "x" + fromY);
//
//        if (_needRefresh) {
//            _needRefresh = false;
//            refreshLayers();
//        }
//
//        if (_layers != null) {
//            for (int i = _cacheCols - 1; i >= 0; i--) {
//                for (int j = _cacheCols - 1; j >= 0; j--) {
//                    // Draw up to date layer
//                    if (_layers[i][j].isVisible(viewport) && !_layers[i][j].needRefresh() && _layers[i][j].isDrawable()) {
//                        _layers[i][j].onDraw(renderer, viewport, i * CACHE_SIZE * Constant.TILE_WIDTH, j * CACHE_SIZE * Constant.TILE_HEIGHT);
//                    }
//
//                    // Refresh needed layer
//                    if (_layers[i][j].isVisible(viewport) && _layers[i][j].needRefresh()) {
//                        Log.info("refresh layer: " + _layers[i][j].getIndex());
//                        _layers[i][j].refresh();
//                        refreshLayer(_layers[i][j], i * CACHE_SIZE, j * CACHE_SIZE, (i + 1) * CACHE_SIZE, (j + 1) * CACHE_SIZE);
//                    }
//
//                    // Clear out of screen layers
//                    if (!_layers[i][j].isVisible(viewport) && _layers[i][j].isDrawable()) {
//                        Log.info("clear layer: " + _layers[i][j].getIndex());
//                        _layers[i][j].clear();
//                    }
//
////                    // Draw up to date layer
////                    if (_layersLight[i][j].isVisible(viewport) && !_layersLight[i][j].needRefresh() && _layersLight[i][j].isDrawable()) {
////                        _layersLight[i][j].onDraw(renderer, viewport, i * CACHE_SIZE * Constant.TILE_WIDTH, j * CACHE_SIZE * Constant.TILE_HEIGHT);
////                    }
////
////                    // Refresh needed layer
////                    if (_layersLight[i][j].isVisible(viewport) && _layersLight[i][j].needRefresh()) {
////                        Log.info("refresh layer: " + _layersLight[i][j].getIndex());
////                        _layersLight[i][j].refresh();
////                        refreshLayerLight(_layersLight[i][j], i * CACHE_SIZE, j * CACHE_SIZE, (i + 1) * CACHE_SIZE, (j + 1) * CACHE_SIZE);
////                    }
////
////                    // Clear out of screen layers
////                    if (!_layersLight[i][j].isVisible(viewport) && _layersLight[i][j].isDrawable()) {
////                        Log.info("clear layer: " + _layersLight[i][j].getIndex());
////                        _layersLight[i][j].clear();
////                    }
//                }
//            }
//
//            onDrawSelected(renderer, viewport, animProgress);
//        }
    }

    private void refreshResource(RenderLayer layer, ParcelModel parcel, ResourceModel resource, int x, int y) {
        if (parcel != null && resource != null) {
            boolean topLeft = !(WorldHelper.getResource(parcel.x - 1, parcel.y - 1) == null || WorldHelper.getResource(parcel.x - 1, parcel.y - 1).getInfo() != resource.getInfo());
            boolean top = !(WorldHelper.getResource(parcel.x, parcel.y - 1) == null || WorldHelper.getResource(parcel.x, parcel.y - 1).getInfo() != resource.getInfo());
            boolean topRight = !(WorldHelper.getResource(parcel.x + 1, parcel.y - 1) == null || WorldHelper.getResource(parcel.x + 1, parcel.y - 1).getInfo() != resource.getInfo());

            boolean left = !(WorldHelper.getResource(parcel.x - 1, parcel.y) == null || WorldHelper.getResource(parcel.x - 1, parcel.y).getInfo() != resource.getInfo());
            boolean right = !(WorldHelper.getResource(parcel.x + 1, parcel.y) == null || WorldHelper.getResource(parcel.x + 1, parcel.y).getInfo() != resource.getInfo());

            boolean bottomLeft = !(WorldHelper.getResource(parcel.x - 1, parcel.y + 1) == null || WorldHelper.getResource(parcel.x - 1, parcel.y + 1).getInfo() != resource.getInfo());
            boolean bottom = !(WorldHelper.getResource(parcel.x, parcel.y + 1) == null || WorldHelper.getResource(parcel.x, parcel.y + 1).getInfo() != resource.getInfo());
            boolean bottomRight = !(WorldHelper.getResource(parcel.x + 1, parcel.y + 1) == null || WorldHelper.getResource(parcel.x + 1, parcel.y + 1).getInfo() != resource.getInfo());

            int tile = 0;
            int offsetX = 0;
            int offsetY = 0;

            if (!top && !bottom && !right && !left) {
                tile = 42;
                offsetX = -16;
                offsetY = -16;
            }

            SpriteModel sprite = resource.isRock() && WorldHelper.isSurroundedByRock(parcel) ? _spriteManager.getGround(11) : _spriteManager.getItem(resource, tile, tile);
            if (sprite != null) {
                layer.draw(sprite, (x % CACHE_SIZE) * Constant.TILE_WIDTH + offsetX, (y % CACHE_SIZE) * Constant.TILE_HEIGHT + offsetX);
            }
        }
    }

    // TODO: random
    void    refreshFloor(RenderLayer layer, int type, int x, int y) {
        // Draw ground
        if (type != 0) {
            SpriteModel sprite = _spriteManager.getGround(type);
            layer.draw(sprite, (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
        }
    }

    //TODO: random
    void    refreshStructure(RenderLayer layer, StructureModel structure, int x, int y) {
        int offsetWall = (Constant.TILE_WIDTH / 2 * 3) - Constant.TILE_HEIGHT;

        if (structure != null) {
            // Door
            if (structure.isDoor()) {
                layer.draw(_spriteManager.getItem(structure), (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT - offsetWall);
            }

            // Floor
            else if (structure.isFloor()) {
                layer.draw(_spriteManager.getItem(structure), (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
            }

//            // Wall
//            else if (structure.isWall()) {
//                layer.draw(drawWall(structure, x, y, offsetWall), (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
//            }
//
//            // Hull
//            else if (structure.isHull()) {
//                layer.draw(drawWall(structure, x, y, offsetWall), (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
//            }

            else {
                layer.draw(SpriteManager.getInstance().getItem(structure), (structure.getX() % CACHE_SIZE) * Constant.TILE_WIDTH, (structure.getY() % CACHE_SIZE) * Constant.TILE_HEIGHT);
            }
        }
    }

//    private SpriteModel drawWall(StructureModel structure, int x, int y, int offsetWall) {
//        return _spriteManager.getItem(structure, structure.isComplete() ? 0 : 1);
//    }

    void    refreshItems(RenderLayer layer, ItemModel item, int x, int y) {
        if (item != null && item.getX() == x && item.getY() == y) {

            // Display item
            layer.draw(_spriteManager.getItem(item, item.getCurrentFrame()), (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);

            // Display components
            if (item.getFactory() != null && item.getFactory().getShoppingList() != null) {
                for (ItemFactoryModel.FactoryShoppingItemModel component : item.getFactory().getShoppingList()) {
                    SpriteModel sprite = _spriteManager.getItem(component.consumable.getInfo());
                    if (sprite != null) {
                        if (item.getInfo().factory != null && item.getInfo().factory.inputSlots != null) {
                            layer.draw(sprite,
                                    (x + item.getInfo().factory.inputSlots[0]) * Constant.TILE_WIDTH,
                                    (y + item.getInfo().factory.inputSlots[1]) * Constant.TILE_HEIGHT);
                        } else {
                            layer.draw(sprite, (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
                        }
                    }
                }
            }

//            // Display crafts
//            if (item.getFactory() != null && item.getFactory().getOutputs() != null) {
//                for (ItemFactoryModel.FactoryOutputModel product : item.getFactory().getOutputs()) {
//                    SpriteModel sprite = _spriteManager.getItem(product.itemInfo);
//                    if (sprite != null) {
//                        if (item.getInfo().factory != null && item.getInfo().factory.outputSlots != null) {
//                            layer.draw(sprite,
//                                    (x + item.getInfo().factory.outputSlots[0]) * Constant.TILE_WIDTH,
//                                    (y + item.getInfo().factory.outputSlots[1]) * Constant.TILE_HEIGHT);
//                        } else {
//                            layer.draw(sprite, (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
//                        }
//                    }
//                }
//            }

//            // Display selection
//            if (item.isSelected()) {
//                _itemSelected = item;
//            }

            // Display selection
            if (!item.isFunctional()) {
                layer.draw(_spriteManager.getIcon("data/res/ic_power.png"), (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
            }
        }
    }

    void    refreshConsumable(RenderLayer layer, ConsumableModel consumable, int x, int y) {
        if (consumable != null) {

            // Regular item
            SpriteModel sprite = _spriteManager.getItem(consumable, consumable.getCurrentFrame());
            if (sprite != null) {
                layer.draw(sprite, (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
            }

            // Selection
            if (consumable.isSelected()) {
                _itemSelected = consumable;
            }
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

            int x = _itemSelected.getX() * Constant.TILE_WIDTH + viewport.getPosX();
            int y = _itemSelected.getY() * Constant.TILE_HEIGHT + viewport.getPosY();
            renderer.draw(_spriteManager.getSelectorCorner(0), x - offset + fromX, y - offset + fromY);
            renderer.draw(_spriteManager.getSelectorCorner(1), x + offset + toX, y - offset + fromY);
            renderer.draw(_spriteManager.getSelectorCorner(2), x - offset + fromX, y + offset + toY);
            renderer.draw(_spriteManager.getSelectorCorner(3), x + offset + toX, y + offset + toY);

            if (_itemSelected.getInfo().slots != null) {
                for (int[] slot: _itemSelected.getInfo().slots) {
                    renderer.draw(_spriteManager.getIcon("data/res/ic_slot.png"),
                            (_itemSelected.getX() + slot[0])* Constant.TILE_WIDTH + viewport.getPosX(),
                            (_itemSelected.getY() + slot[1])* Constant.TILE_HEIGHT + viewport.getPosY());
                }
            }
        }
    }

    @Override
    public void onAddStructure(StructureModel structure){
        if (_layers != null) {
            _layers[structure.getX() / CACHE_SIZE][structure.getY() / CACHE_SIZE].planRefresh();
            _needRefresh = true;
        }
    }

    @Override
    public void onAddItem(ItemModel item){
        if (_layers != null) {
            _layers[item.getX() / CACHE_SIZE][item.getY() / CACHE_SIZE].planRefresh();
            _needRefresh = true;
        }
    }

    @Override
    public void onAddConsumable(ConsumableModel consumable){
        if (_layers != null) {
            _layers[consumable.getX() / CACHE_SIZE][consumable.getY() / CACHE_SIZE].planRefresh();
            _needRefresh = true;
        }
    }

    @Override
    public void onAddResource(ResourceModel resource) {
        if (_layers != null) {
            _layers[resource.getX() / CACHE_SIZE][resource.getY() / CACHE_SIZE].planRefresh();
            _needRefresh = true;
        }
    }

    @Override
    public void onRemoveItem(ItemModel item){
        _layers[item.getX() / CACHE_SIZE][item.getY() / CACHE_SIZE].planRefresh();
        _needRefresh = true;
    }

    @Override
    public void onRemoveConsumable(ConsumableModel consumable){
        _layers[consumable.getX() / CACHE_SIZE][consumable.getY() / CACHE_SIZE].planRefresh();
        _needRefresh = true;
    }

    @Override
    public void onRemoveStructure(StructureModel structure){
        _layers[structure.getX() / CACHE_SIZE][structure.getY() / CACHE_SIZE].planRefresh();
        _needRefresh = true;
    }

    @Override
    public void onRemoveResource(ResourceModel resource){
        _layers[resource.getX() / CACHE_SIZE][resource.getY() / CACHE_SIZE].planRefresh();
        _needRefresh = true;
    }

    @Override
    public void onRefreshItem(ItemModel item) {
        _layers[item.getX() / CACHE_SIZE][item.getY() / CACHE_SIZE].planRefresh();
        _needRefresh = true;
    }

    @Override
    public void onRefreshStructure(StructureModel structure) {
        _layers[structure.getX() / CACHE_SIZE][structure.getY() / CACHE_SIZE].planRefresh();
        _needRefresh = true;
    }

    @Override
    public void onSelectItem(ItemModel item) {
        _itemSelected = item;
    }

    @Override
    public void onSelectResource(ResourceModel resource) {
        _itemSelected = resource;
    }

    @Override
    public void onSelectConsumable(ConsumableModel consumable) {
        _itemSelected = consumable;
    }

    @Override
    public void onSelectStructure(StructureModel structure) {
        _itemSelected = structure;
    }

    @Override
    public void onDeselect() {
        _itemSelected = null;
    }

}
