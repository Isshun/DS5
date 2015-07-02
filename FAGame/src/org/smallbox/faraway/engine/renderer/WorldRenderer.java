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

    private SpriteManager 			_spriteManager;
    private RenderLayer[][] 		_layerStructure;
    private boolean 				_hasChanged;

    private WorldManager 			_worldMap;
    private MapObjectModel          _itemSelected;
    private int 					_frame;

    public WorldRenderer(SpriteManager spriteManager) {
        _spriteManager = spriteManager;

//		_layerItem = ViewFactory.getInstance().createRenderLayer(Constant.WORLD_WIDTH * Constant.TILE_WIDTH, Constant.WORLD_HEIGHT * Constant.TILE_HEIGHT);

        _hasChanged = true;
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
                    refreshLayer(_layerStructure[i][j], i * CACHE_SIZE, j * CACHE_SIZE, (i + 1) * CACHE_SIZE, (j + 1) * CACHE_SIZE);
                }
            }
        }
    }

    private void refreshLayer(RenderLayer layer, int fromX, int fromY, int toX, int toY) {
        Log.info("Refresh layer: " + layer.getIndex());

        layer.begin();
        layer.setRefresh();
        for (int x = toX; x >= fromX; x--) {
            for (int y = toY; y >= fromY; y--) {
//                if (onScreen(x / CACHE_SIZE, y / CACHE_SIZE)) {
//                }
                ParcelModel area = Game.getWorldManager().getParcel(x, y);
                if (area != null) {
                    if (GameData.config.render.floor) {
                        refreshFloor(layer, area, x, y);
                    }
                    if (GameData.config.render.structure) {
                        refreshStructure(layer, area, x, y);
                    }
                    if (GameData.config.render.resource) {
                        refreshResource(layer, area, x, y);
                    }
                    if (GameData.config.render.item) {
                        refreshItems(layer, area, x, y);
                    }
                    if (GameData.config.render.consumable) {
                        refreshConsumable(layer, area, x, y);
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
                    if (onScreen(i, j) && !_layerStructure[i][j].needRefresh()) {
                        _layerStructure[i][j].onDraw(renderer, effect, i * CACHE_SIZE * Constant.TILE_WIDTH, j * CACHE_SIZE * Constant.TILE_HEIGHT);
                    }
                }
            }

            onDrawSelected(renderer, effect, animProgress);
        }
        //_layerItem.onDraw(renderer, buffEffect);
    }

    private boolean onScreen(int i, int j) {
        Viewport viewport = Game.getInstance().getViewport();
        int posX = (int) ((i * CACHE_SIZE * Constant.TILE_WIDTH + viewport.getPosX()) * viewport.getScale());
        int posY = (int) ((j * CACHE_SIZE * Constant.TILE_HEIGHT + viewport.getPosY()) * viewport.getScale());
        int width = (int) (CACHE_SIZE * Constant.TILE_WIDTH * viewport.getScale());
        int height = (int) (CACHE_SIZE * Constant.TILE_HEIGHT * viewport.getScale());
        return (posX < 1500 && posY < 1200 && posX + width > 0 && posY + height > 0);
    }

    private void refreshResource(RenderLayer layer, ParcelModel area, int x, int y) {
        ResourceModel resource = area.getResource();
        if (resource != null) {
            SpriteModel sprite = _spriteManager.getResource(resource);
            if (sprite != null) {
                sprite.setPosition((x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
                layer.draw(sprite);
            }
        }
    }

    // TODO: random
    void	refreshFloor(RenderLayer layer, ParcelModel parcel, int x, int y) {
//        StructureModel structure = parcel.getStructure();
//
//        if (structure != null && structure.isFloor()) {
//            if (structure.getName().equals("base.greenhouse")) {
//                int index = 2;
//                SpriteModel sprite = _spriteManager.getGreenHouse(index + (structure.isWorking() ? 1 : 0));
//                sprite.setPosition((x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
//                layer.draw(sprite);
//
//                ResourceModel resource = _worldMap.getResource(x, y);
//                if (resource != null) {
//                    sprite = _spriteManager.getResource(resource);
//                    sprite.setPosition((x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
//                    layer.draw(sprite);
//                }
//            }
//
//            // Floor
//            else {
//                int roomId = 0;
//                SpriteModel sprite = _spriteManager.getFloor(structure, roomId, 0);
//                if (sprite != null) {
//                    sprite.setPosition((x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
//                    layer.draw(sprite);
//                }
//
//                ResourceModel ressource = _worldMap.getResource(x, y);
//                if (ressource != null) {
//                    sprite = _spriteManager.getResource(ressource);
//                    sprite.setPosition((x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
//                    layer.draw(sprite);
//                }
//
//
////							RectangleShape shape = new RectangleShape(new Vector2f(32, 32));
////							shape.setFillColor(new Color(0, 0, 0, 155 * (12 - area.getLight()) / 12));
////							shape.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
////							_texture.draw(shape);
//            }
//
//        // No floor
//        } else if (parcel.getType() != 0) {
//            SpriteModel sprite = _spriteManager.getGround(parcel.getType());
//            sprite.setPosition((x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
//            layer.draw(sprite);
//        }

        // Draw ground
        if (parcel.getType() != 0) {
            SpriteModel sprite = _spriteManager.getGround(parcel.getType());
            sprite.setPosition((x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
            layer.draw(sprite);
        }

        // Draw floor
        if (parcel.getStructure() != null && parcel.getStructure().isFloor()) {
            SpriteModel sprite = _spriteManager.getFloor(parcel.getStructure(), 0, 0);
            sprite.setPosition((x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
            layer.draw(sprite);
        }

//
//        else {
//            SpriteModel sprite = _spriteManager.getExterior(x + y * 42, 0);
//            sprite.setPosition(x * Constant.TILE_WIDTH, y * Constant.TILE_HEIGHT);
//            _layerStructure.draw(sprite);
//        }
    }

    //TODO: random
    void	refreshStructure(RenderLayer layer, ParcelModel area, int x, int y) {
        int offsetWall = (Constant.TILE_WIDTH / 2 * 3) - Constant.TILE_HEIGHT;

        StructureModel structure = area.getStructure();
        if (structure != null) {

            // Door
            if (structure.isDoor()) {
                SpriteModel sprite = _spriteManager.getSimpleWall(0);
                if (sprite != null) {
                    sprite.setPosition((x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT - offsetWall);
                    layer.draw(sprite);
                }

                sprite = _spriteManager.getItem(structure);
                if (sprite != null) {
                    sprite.setPosition((x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT - 4);
                    layer.draw(sprite);
                }
            }

            // Floor
            else if (structure.isFloor()) {
            }

            // Wall
            else if (structure.isWall()) {
                SpriteModel sprite = drawWall(structure, x, y, offsetWall);
                layer.draw(sprite);
            }

            // Hull
            else if (structure.isHull()) {
                SpriteModel sprite = drawWall(structure, x, y, offsetWall);
                layer.draw(sprite);
            }

            else {
                SpriteModel sprite = SpriteManager.getInstance().getItem(structure);
                sprite.setPosition(structure.getX() * Constant.TILE_WIDTH, structure.getY() * Constant.TILE_HEIGHT);
                layer.draw(sprite);
            }
        }
    }

    private SpriteModel drawWall(StructureModel item, int i, int j, int offsetWall) {
        SpriteModel sprite = null;
        sprite = _spriteManager.getWall(item, 1, 1, 0);
        sprite.setPosition((i % CACHE_SIZE) * Constant.TILE_WIDTH, (j % CACHE_SIZE) * Constant.TILE_HEIGHT);
        return sprite;
//
//        StructureModel bellow = _worldMap.getStructure(i, j+1);
//        StructureModel right = _worldMap.getStructure(i+1, j);
//        StructureModel left = _worldMap.getStructure(i-1, j);
//
//        int zone = 0;
//        // bellow is a wall
//        if (bellow != null && (bellow.isWall() || bellow.isDoor())) {
//            StructureModel bellowBellow = _worldMap.getStructure(i, j+2);
//            if (bellow.isDoor() || bellowBellow == null || (!bellowBellow.isWall() && !bellowBellow.isDoor())) {
//                StructureModel bellowRight = _worldMap.getStructure(i+1, j+1);
//                StructureModel bellowLeft = _worldMap.getStructure(i-1, j+1);
//                boolean wallOnRight = bellowRight != null && (bellowRight.isWall() || bellowRight.isDoor());
//                boolean wallOnLeft = bellowLeft != null && (bellowLeft.isWall() || bellowLeft.isDoor());
//
//                if (wallOnRight && wallOnLeft) {
//                    sprite = _spriteManager.getWall(item, 5, 0, zone);
//                } else if (wallOnLeft) {
//                    boolean wallOnSupRight = right != null && (right.isWall() || right.isDoor());
//                    if (wallOnSupRight) {
//                        sprite = _spriteManager.getWall(item, 1, 5, zone);
//                    } else {
//                        sprite = _spriteManager.getWall(item, 5, 2, zone);
//                    }
//                } else if (wallOnRight) {
//                    boolean wallOnSupLeft = left != null && (left.isWall() || left.isDoor());
//                    if (wallOnSupLeft) {
//                        sprite = _spriteManager.getWall(item, 1, 4, zone);
//                    } else {
//                        sprite = _spriteManager.getWall(item, 5, 1, zone);
//                    }
//                } else {
//                    sprite = _spriteManager.getWall(item, 5, 3, zone);
//                }
//            } else {
//                boolean wallOnRight = right != null && (right.isWall() || right.isDoor());
//                boolean wallOnLeft = left != null && (left.isWall() || left.isDoor());
//                if (wallOnRight && wallOnLeft) {
//                    sprite = _spriteManager.getWall(item, 1, 0, zone);
//                } else if (wallOnLeft) {
//                    sprite = _spriteManager.getWall(item, 1, 2, zone);
//                } else if (wallOnRight) {
//                    sprite = _spriteManager.getWall(item, 1, 1, zone);
//                } else {
//                    sprite = _spriteManager.getWall(item, 1, 3, zone);
//                }
//            }
//
//            if (sprite != null) {
//                sprite.setPosition((i % CACHE_SIZE) * Constant.TILE_WIDTH, (j % CACHE_SIZE) * Constant.TILE_HEIGHT);
//            }
//        }
//
//        // No wall above or bellow
//        else if (bellow == null || bellow.isWall() == false) {
//
//            // Check double wall
//            boolean doubleWall = false;
//            if (right != null && right.isComplete() && right.isWall() &&
//                    (_lastSpecialY != j || _lastSpecialX != i+1)) {
//                StructureModel aboveRight = _worldMap.getStructure(i+1, j-1);
//                StructureModel bellowRight = _worldMap.getStructure(i+1, j+1);
//                if ((aboveRight == null || aboveRight.isWall() == false) &&
//                        (bellowRight == null || bellowRight.isWall() == false)) {
//                    doubleWall = true;
//                }
//            }
//
//            // Normal
//            if (bellow == null) {
//                // Double wall
//                if (doubleWall) {
//                    sprite = _spriteManager.getWall(item, 4, i+j, zone);
//                    _lastSpecialX = i;
//                    _lastSpecialY = j;
//                }
//                // Single wall
//                else {
//                    sprite = _spriteManager.getWall(item, 0, 0, zone);
//                }
//            }
//            // Special
//            else {
//                // Double wall
//                if (doubleWall) {
//                    sprite = _spriteManager.getWall(item, 2, i+j, zone);
//                    _lastSpecialX = i;
//                    _lastSpecialY = j;
//                }
//                // Single wall
//                else {
//                    sprite = _spriteManager.getWall(item, 3, i+j, zone);
//                }
//            }
//            if (sprite != null) {
//                sprite.setPosition((i % CACHE_SIZE) * Constant.TILE_WIDTH, (j % CACHE_SIZE) * Constant.TILE_HEIGHT);
////                sprite.setPosition(i * Constant.TILE_WIDTH, j * Constant.TILE_HEIGHT - offsetWall);
//            }
//        }
//
//        // // left is a wall
//        // else if (left != null && left.type == BaseItem.STRUCTURE_WALL) {
//        // 	_spriteManager.getWall(item, 2, &sprite);
//        // 	sprite.setPosition(i * TILE_SIZE - TILE_SIZE, j * TILE_SIZE - offset);
//        // }
//
//        // single wall
//        else {
//            sprite = _spriteManager.getWall(item, 0, 0, 0);
//            sprite.setPosition((i % CACHE_SIZE) * Constant.TILE_WIDTH, (j % CACHE_SIZE) * Constant.TILE_HEIGHT);
////            sprite.setPosition(i * Constant.TILE_WIDTH, j * Constant.TILE_HEIGHT - offsetWall);
//        }
//
//        return sprite;
    }

    void	refreshItems(RenderLayer layer, ParcelModel area, int x, int y) {
        ItemModel item = area.getItem();
        if (item != null && item.getX() == x && item.getY() == y) {

            // Display components
            for (ConsumableModel component: item.getComponents()) {
                SpriteModel sprite = _spriteManager.getItem(component, component.getCurrentFrame());
                if (sprite != null) {
                    if (item.getInfo().storage != null && item.getInfo().storage.components != null) {
                        sprite.setPosition(
                                (x + item.getInfo().storage.components[0]) * Constant.TILE_WIDTH,
                                (y + item.getInfo().storage.components[1]) * Constant.TILE_HEIGHT);
                    } else {
                        sprite.setPosition((x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
                    }
                    layer.draw(sprite);
                }
            }

            // Display crafts
            for (ConsumableModel component: item.getComponents()) {
                SpriteModel sprite = _spriteManager.getItem(component, component.getCurrentFrame());
                if (sprite != null) {
                    if (item.getInfo().storage != null && item.getInfo().storage.crafts != null) {
                        sprite.setPosition(
                                (x + item.getInfo().storage.crafts[0]) * Constant.TILE_WIDTH,
                                (y + item.getInfo().storage.crafts[1]) * Constant.TILE_HEIGHT);
                    } else {
                        sprite.setPosition((x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
                    }
                    layer.draw(sprite);
                }
            }

            // Display item
            SpriteModel sprite = _spriteManager.getItem(item, item.getCurrentFrame());
            if (sprite != null) {
                sprite.setPosition((x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
                layer.draw(sprite);
            }

            // Display selection
            if (item.isSelected()) {
                _itemSelected = item;
            }

            // Display selection
            if (!item.isFunctional()) {
                sprite = _spriteManager.getIcon("data/res/ic_power.png");
                sprite.setPosition((x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
                layer.draw(sprite);
            }
        }
    }

    void	refreshConsumable(RenderLayer layer, ParcelModel area, int x, int y) {
        ConsumableModel consumable = area.getConsumable();
        if (consumable != null) {

            // Regular item
            SpriteModel sprite = _spriteManager.getItem(consumable, consumable.getCurrentFrame());
            if (sprite != null) {
                sprite.setPosition((x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
                layer.draw(sprite);
            }

            // Selection
            if (consumable.isSelected()) {
                _itemSelected = consumable;
            }
        }
    }

    private void refreshSelected(GFXRenderer renderer, RenderEffect effect, int frame, MapObjectModel item) {
        int x = item.getX();
        int y = item.getY();
        int offset = 0;
        switch (frame % 5) {
            case 1: offset = 1; break;
            case 2: offset = 2; break;
            case 3: offset = 3; break;
            case 4: offset = 2; break;
            case 5: offset = 1; break;
        }

        SpriteModel sprite = _spriteManager.getSelectorCorner(0);
        sprite.setPosition(x * Constant.TILE_WIDTH - offset, y * Constant.TILE_HEIGHT - offset);
        renderer.draw(sprite, effect);

        sprite = _spriteManager.getSelectorCorner(1);
        sprite.setPosition((x + item.getWidth()) * Constant.TILE_WIDTH - 6 + offset, y * Constant.TILE_HEIGHT - offset);
        renderer.draw(sprite, effect);

        sprite = _spriteManager.getSelectorCorner(2);
        sprite.setPosition(x * Constant.TILE_WIDTH - offset, (y + item.getHeight()) * Constant.TILE_HEIGHT - 6 + offset);
        renderer.draw(sprite, effect);

        sprite = _spriteManager.getSelectorCorner(3);
        sprite.setPosition((x + item.getWidth()) * Constant.TILE_WIDTH - 6 + offset, (y + item.getHeight()) * Constant.TILE_HEIGHT - 6 + offset);
        renderer.draw(sprite, effect);
    }

    public void invalidate(int x, int y) {
//		_changed.add(new Vector2i(x, y));
        _hasChanged = true;
    }

    public void invalidate() {
        _hasChanged = true;
    }

    public void onDrawSelected(GFXRenderer renderer, RenderEffect effect, double animProgress) {
        if (_itemSelected != null) {
            refreshSelected(renderer, effect, _frame, _itemSelected);
        }
    }

    @Override
    public void onAddStructure(StructureModel structure){
        _layerStructure[structure.getX() / CACHE_SIZE][structure.getY() / CACHE_SIZE].clear();
        _needRefresh = true;
    }

    @Override
    public void onAddItem(ItemModel item){
        _layerStructure[item.getX() / CACHE_SIZE][item.getY() / CACHE_SIZE].clear();
        _needRefresh = true;
    }

    @Override
    public void onAddConsumable(ConsumableModel consumable){
        _layerStructure[consumable.getX() / CACHE_SIZE][consumable.getY() / CACHE_SIZE].clear();
        _needRefresh = true;
    }

    @Override
    public void onAddResource(ResourceModel resource) {
        _layerStructure[resource.getX() / CACHE_SIZE][resource.getY() / CACHE_SIZE].clear();
        _needRefresh = true;
    }

    @Override
    public void onRemoveItem(ItemModel item){
        _layerStructure[item.getX() / CACHE_SIZE][item.getY() / CACHE_SIZE].clear();
        _needRefresh = true;
    }

    @Override
    public void onRemoveConsumable(ConsumableModel consumable){
        _layerStructure[consumable.getX() / CACHE_SIZE][consumable.getY() / CACHE_SIZE].clear();
        _needRefresh = true;
    }

    @Override
    public void onRemoveStructure(StructureModel structure){
        _layerStructure[structure.getX() / CACHE_SIZE][structure.getY() / CACHE_SIZE].clear();
        _needRefresh = true;
    }

    @Override
    public void onRemoveResource(ResourceModel resource){
        _layerStructure[resource.getX() / CACHE_SIZE][resource.getY() / CACHE_SIZE].clear();
        _needRefresh = true;
    }


}
