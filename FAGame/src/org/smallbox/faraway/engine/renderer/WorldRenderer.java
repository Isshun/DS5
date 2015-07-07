package org.smallbox.faraway.engine.renderer;

import org.smallbox.faraway.engine.*;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.GameObserver;
import org.smallbox.faraway.game.manager.WorldManager;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.item.*;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.Log;

public class WorldRenderer extends BaseRenderer implements GameObserver {
    private static final int    CACHE_SIZE = 25;

    private int                 _cacheCols;
    private boolean             _needRefresh;
    private SpriteManager 	    _spriteManager;
    private RenderLayer[][] 	_layerStructure;
    private WorldManager 		_worldMap;
    private MapObjectModel      _itemSelected;
    private int 				_frame;

    public WorldRenderer(SpriteManager spriteManager) {
        _spriteManager = spriteManager;
        _needRefresh = true;
    }

    public void onRefresh(int frame) {
        _frame = frame;

        if (_worldMap == null) {
            _worldMap = Game.getWorldManager();
        }

        if (_layerStructure == null) {
            _cacheCols = (250 / CACHE_SIZE);
            _layerStructure = new RenderLayer[_cacheCols][_cacheCols];
            int index = 0;
            for (int i = 0; i < _cacheCols; i++) {
                for (int j = 0; j < _cacheCols; j++) {
                    _layerStructure[i][j] = ViewFactory.getInstance().createRenderLayer(index++, CACHE_SIZE * Constant.TILE_WIDTH, CACHE_SIZE * Constant.TILE_HEIGHT);
                }
            }
            refreshLayers();
        }
    }

    private void refreshLayers() {
        Log.info("Refresh layers");

        for (int i = 0; i < _cacheCols; i++) {
            for (int j = 0; j < _cacheCols; j++) {
                if (_layerStructure[i][j].needRefresh()) {
                    _layerStructure[i][j].refresh();
                    refreshLayer(_layerStructure[i][j], i * CACHE_SIZE, j * CACHE_SIZE, (i + 1) * CACHE_SIZE, (j + 1) * CACHE_SIZE);
                }
            }
        }
    }

    public void refreshAll() {
        for (int i = 0; i < _cacheCols; i++) {
            for (int j = 0; j < _cacheCols; j++) {
                _layerStructure[i][j].planRefresh();
            }
        }
        _needRefresh = true;
    }

    private void refreshLayer(RenderLayer layer, int fromX, int fromY, int toX, int toY) {
        Log.info("Refresh layer: " + layer.getIndex());

        int mb = 1024 * 1024;
        Runtime runtime = Runtime.getRuntime();
        int used = (int) ((runtime.totalMemory() - runtime.freeMemory()) / mb);
        int total = (int) (runtime.totalMemory() / mb);
        System.out.println("RefreshLayer: " + used + "/" + total);

        layer.begin();
        layer.setRefresh();
        for (int x = toX - 1; x >= fromX; x--) {
            for (int y = toY - 1; y >= fromY; y--) {
//                if (onScreen(x / CACHE_SIZE, y / CACHE_SIZE)) {
//                }
                ParcelModel parcel = Game.getWorldManager().getParcel(x, y);
                if (parcel != null) {
                    if (GameData.config.render.floor) {
//                        if (x % 2 == 0 && y % 2 == 0) {
                            refreshFloor(layer, parcel.getType(), x, y);
//                        }
                    }
                    if (GameData.config.render.structure) {
                        refreshStructure(layer, parcel.getStructure(), x, y);
                    }
                    if (GameData.config.render.resource) {
                        refreshResource(layer, parcel.getResource(), x, y);
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

    public void onDraw(GFXRenderer renderer, RenderEffect effect, double animProgress) {
        if (_needRefresh) {
            _needRefresh = false;
            refreshLayers();
        }

        if (_layerStructure != null) {
            for (int i = _cacheCols - 1; i >= 0; i--) {
                for (int j = _cacheCols - 1; j >= 0; j--) {
                    if (onScreen(i, j) && !_layerStructure[i][j].needRefresh() && _layerStructure[i][j].isDrawable()) {
                        _layerStructure[i][j].onDraw(renderer, effect, i * CACHE_SIZE * Constant.TILE_WIDTH, j * CACHE_SIZE * Constant.TILE_HEIGHT);
                    }
                }
            }

            onDrawSelected(renderer, effect, animProgress);
        }

        // Draw live item
        SpriteModel sprite;
        int offsetX = effect.getViewport().getPosX();
        int offsetY = effect.getViewport().getPosY();
        for (ResourceModel resource: Game.getWorldManager().getResources()) {
            if (resource.getInfo().isLive) {
                sprite = _spriteManager.getResource(resource);
                if (sprite != null) {
                    renderer.draw(sprite, resource.getX() * Constant.TILE_WIDTH + offsetX, resource.getY() * Constant.TILE_HEIGHT + offsetY);
                }
            }
        }
    }

    private boolean onScreen(int i, int j) {
        Viewport viewport = Game.getInstance().getViewport();
        int posX = (int) ((i * CACHE_SIZE * Constant.TILE_WIDTH + viewport.getPosX()) * viewport.getScale());
        int posY = (int) ((j * CACHE_SIZE * Constant.TILE_HEIGHT + viewport.getPosY()) * viewport.getScale());
        int width = (int) (CACHE_SIZE * Constant.TILE_WIDTH * viewport.getScale());
        int height = (int) (CACHE_SIZE * Constant.TILE_HEIGHT * viewport.getScale());
        return (posX < 1500 && posY < 1200 && posX + width > 0 && posY + height > 0);
    }

    private void refreshResource(RenderLayer layer, ResourceModel resource, int x, int y) {
        if (resource != null && !resource.getInfo().isLive) {
            SpriteModel sprite = _spriteManager.getResource(resource);
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
            for (ConsumableModel component: item.getComponents()) {
                SpriteModel sprite = _spriteManager.getItem(component, component.getCurrentFrame());
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
            for (ConsumableModel component: item.getComponents()) {
                SpriteModel sprite = _spriteManager.getItem(component, component.getCurrentFrame());
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

            // Display selection
            if (item.isSelected()) {
                _itemSelected = item;
            }

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

    private void refreshSelected(GFXRenderer renderer, RenderEffect effect, int frame, MapObjectModel item) {
        int offset = 0;
        switch (frame % 5) {
            case 1: offset = 1; break;
            case 2: offset = 2; break;
            case 3: offset = 3; break;
            case 4: offset = 2; break;
            case 5: offset = 1; break;
        }

        int x = item.getX();
        int y = item.getY();
        renderer.draw(_spriteManager.getSelectorCorner(0), x * Constant.TILE_WIDTH - offset, y * Constant.TILE_HEIGHT - offset);
        renderer.draw(_spriteManager.getSelectorCorner(1), (x + item.getWidth()) * Constant.TILE_WIDTH - 6 + offset, y * Constant.TILE_HEIGHT - offset);
        renderer.draw(_spriteManager.getSelectorCorner(2), x * Constant.TILE_WIDTH - offset, (y + item.getHeight()) * Constant.TILE_HEIGHT - 6 + offset);
        renderer.draw(_spriteManager.getSelectorCorner(3), (x + item.getWidth()) * Constant.TILE_WIDTH - 6 + offset, (y + item.getHeight()) * Constant.TILE_HEIGHT - 6 + offset);
    }

    public void onDrawSelected(GFXRenderer renderer, RenderEffect effect, double animProgress) {
        if (_itemSelected != null) {
            refreshSelected(renderer, effect, _frame, _itemSelected);
        }
    }

    @Override
    public void onAddStructure(StructureModel structure){
        _layerStructure[structure.getX() / CACHE_SIZE][structure.getY() / CACHE_SIZE].planRefresh();
        _needRefresh = true;
    }

    @Override
    public void onAddItem(ItemModel item){
        _layerStructure[item.getX() / CACHE_SIZE][item.getY() / CACHE_SIZE].planRefresh();
        _needRefresh = true;
    }

    @Override
    public void onAddConsumable(ConsumableModel consumable){
        _layerStructure[consumable.getX() / CACHE_SIZE][consumable.getY() / CACHE_SIZE].planRefresh();
        _needRefresh = true;
    }

    @Override
    public void onAddResource(ResourceModel resource) {
        _layerStructure[resource.getX() / CACHE_SIZE][resource.getY() / CACHE_SIZE].planRefresh();
        _needRefresh = true;
    }

    @Override
    public void onRemoveItem(ItemModel item){
        _layerStructure[item.getX() / CACHE_SIZE][item.getY() / CACHE_SIZE].planRefresh();
        _needRefresh = true;
    }

    @Override
    public void onRemoveConsumable(ConsumableModel consumable){
        _layerStructure[consumable.getX() / CACHE_SIZE][consumable.getY() / CACHE_SIZE].planRefresh();
        _needRefresh = true;
    }

    @Override
    public void onRemoveStructure(StructureModel structure){
        _layerStructure[structure.getX() / CACHE_SIZE][structure.getY() / CACHE_SIZE].planRefresh();
        _needRefresh = true;
    }

    @Override
    public void onRemoveResource(ResourceModel resource){
        _layerStructure[resource.getX() / CACHE_SIZE][resource.getY() / CACHE_SIZE].planRefresh();
        _needRefresh = true;
    }

    @Override
    public void onRefreshItem(ItemModel item) {
        _layerStructure[item.getX() / CACHE_SIZE][item.getY() / CACHE_SIZE].planRefresh();
        _needRefresh = true;
    }

    @Override
    public void onRefreshStructure(StructureModel structure) {
        _layerStructure[structure.getX() / CACHE_SIZE][structure.getY() / CACHE_SIZE].planRefresh();
        _needRefresh = true;
    }

}
