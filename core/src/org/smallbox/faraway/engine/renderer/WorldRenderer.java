package org.smallbox.faraway.engine.renderer;

import com.badlogic.gdx.math.Rectangle;
import org.smallbox.faraway.core.RenderLayer;
import org.smallbox.faraway.core.SpriteManager;
import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.engine.SpriteModel;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.helper.WorldHelper;
import org.smallbox.faraway.game.model.GameConfig;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.item.*;
import org.smallbox.faraway.game.module.ModuleHelper;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.Log;

public class WorldRenderer extends BaseRenderer {
    private static final int    CACHE_SIZE = 25;

    private int                 _cacheCols;
    private boolean             _needRefresh;
    private SpriteManager       _spriteManager;
    private RenderLayer[][]     _layers;
    private MapObjectModel      _itemSelected;
    private Viewport            _viewport;
    private boolean             _firstRefresh;

    @Override
    public void init() {
        _spriteManager = SpriteManager.getInstance();
        _viewport = Game.getInstance().getViewport();
        _needRefresh = true;
        _firstRefresh = true;
        _cacheCols = (250 / CACHE_SIZE);
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
                }
            }
        }
    }

    public void refreshAll() {
        for (int i = 0; i < _cacheCols; i++) {
            for (int j = 0; j < _cacheCols; j++) {
                _layers[i][j].planRefresh();
            }
        }
        _needRefresh = true;
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
                    if (GameData.config.render.consumable) {
                        refreshConsumable(layer, parcel.getConsumable(), x, y);
                    }
                }
            }
        }
        layer.end();
    }

    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        if (_needRefresh) {
            _needRefresh = false;
            refreshLayers();
        }

        if (_layers != null) {
            for (int i = _cacheCols - 1; i >= 0; i--) {
                for (int j = _cacheCols - 1; j >= 0; j--) {
                    // Draw up to date layer
                    if (_layers[i][j].isVisible(viewport) && !_layers[i][j].needRefresh() && _layers[i][j].isDrawable()) {
                        _layers[i][j].onDraw(renderer, viewport, i * CACHE_SIZE * Constant.TILE_WIDTH, j * CACHE_SIZE * Constant.TILE_HEIGHT);
                    }

                    // Refresh needed layer
                    if (_layers[i][j].isVisible(viewport) && _layers[i][j].needRefresh()) {
                        Log.info("refresh layer: " + _layers[i][j].getIndex());
                        _layers[i][j].refresh();
                        refreshLayer(_layers[i][j], i * CACHE_SIZE, j * CACHE_SIZE, (i + 1) * CACHE_SIZE, (j + 1) * CACHE_SIZE);
                    }

                    // Clear out of screen layers
                    if (!_layers[i][j].isVisible(viewport) && _layers[i][j].isDrawable()) {
                        Log.info("clear layer: " + _layers[i][j].getIndex());
                        _layers[i][j].clear();
                    }
                }
            }

            onDrawSelected(renderer, viewport, animProgress);
        }
    }

    private void refreshResource(RenderLayer layer, ParcelModel parcel, ResourceModel resource, int x, int y) {
        if (parcel != null && resource != null) {
            SpriteModel sprite = resource.isRock() && WorldHelper.isSurroundedByRock(parcel) ? _spriteManager.getGround(11) : _spriteManager.getResource(resource);
            if (sprite != null) {
                layer.draw(sprite, (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
            }
        }
    }

    // TODO: random
    void	refreshFloor(RenderLayer layer, int type, int x, int y) {
        // Draw ground
        if (type != 0) {
            SpriteModel sprite = _spriteManager.getGround(type);
            layer.draw(sprite, (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
        }
    }

    //TODO: random
    void	refreshStructure(RenderLayer layer, StructureModel structure, int x, int y) {
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

            // Wall
            else if (structure.isWall()) {
                layer.draw(drawWall(structure, x, y, offsetWall), (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
            }

            // Hull
            else if (structure.isHull()) {
                layer.draw(drawWall(structure, x, y, offsetWall), (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
            }

            else {
                layer.draw(SpriteManager.getInstance().getItem(structure), (structure.getX() % CACHE_SIZE) * Constant.TILE_WIDTH, (structure.getY() % CACHE_SIZE) * Constant.TILE_HEIGHT);
            }
        }
    }

    private SpriteModel drawWall(StructureModel structure, int x, int y, int offsetWall) {
        return _spriteManager.getItem(structure);
    }

    void	refreshItems(RenderLayer layer, ItemModel item, int x, int y) {
        if (item != null && item.getX() == x && item.getY() == y) {

            // Display components
            for (BuildableMapObject.ComponentModel component: item.getComponents()) {
                SpriteModel sprite = _spriteManager.getItem(component.info, 0);
                if (sprite != null) {
                    if (item.getInfo().storage != null && item.getInfo().storage.components != null) {
                        layer.draw(sprite,
                                (x + item.getInfo().storage.components[0]) * Constant.TILE_WIDTH,
                                (y + item.getInfo().storage.components[1]) * Constant.TILE_HEIGHT);
                    } else {
                        layer.draw(sprite, (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
                    }
                }
            }

            // Display crafts
            for (BuildableMapObject.ComponentModel component: item.getComponents()) {
                SpriteModel sprite = _spriteManager.getItem(component.info, 0);
                if (sprite != null) {
                    if (item.getInfo().storage != null && item.getInfo().storage.crafts != null) {
                        layer.draw(sprite,
                                (x + item.getInfo().storage.crafts[0]) * Constant.TILE_WIDTH,
                                (y + item.getInfo().storage.crafts[1]) * Constant.TILE_HEIGHT);
                    } else {
                        layer.draw(sprite, (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
                    }
                }
            }

            // Display item
            layer.draw(_spriteManager.getItem(item, item.getCurrentFrame()), (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);

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

    void	refreshConsumable(RenderLayer layer, ConsumableModel consumable, int x, int y) {
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

            Rectangle rect = _itemSelected.getInfo().textureRect;
            int fromX = (int)(rect.getX()/2);
            int fromY = (int)(rect.getY()/2);
            int toX = (int)(rect.getX() + rect.getWidth()) - 8;
            int toY = (int)(rect.getY() + rect.getHeight()) - 8;

            int x = _itemSelected.getX() * Constant.TILE_WIDTH + viewport.getPosX();
            int y = _itemSelected.getY() * Constant.TILE_HEIGHT + viewport.getPosY();
            renderer.draw(_spriteManager.getSelectorCorner(0), x - offset + fromX, y - offset + fromY);
            renderer.draw(_spriteManager.getSelectorCorner(1), x + offset + toX, y - offset + fromY);
            renderer.draw(_spriteManager.getSelectorCorner(2), x - offset + fromX, y + offset + toY);
            renderer.draw(_spriteManager.getSelectorCorner(3), x + offset + toX, y + offset + toY);
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
