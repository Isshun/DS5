package org.smallbox.faraway.core.engine.renderer;

import com.badlogic.gdx.math.Rectangle;
import org.smallbox.faraway.core.*;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.model.GameConfig;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.StructureModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemFactoryReceiptModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.game.module.world.model.resource.ResourceModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.core.util.Log;

public abstract class WorldRenderer extends BaseRenderer {
    protected static final int    CACHE_SIZE = 25;

    protected SpriteManager       _spriteManager;
    protected MapObjectModel      _itemSelected;
    protected boolean             _firstRefresh;
    protected LayerGrid           _layerGrid;

    @Override
    protected void onLoad(Game game) {
        _spriteManager = SpriteManager.getInstance();
        _firstRefresh = true;
        _layerGrid = new LayerGrid(game.getInfo().worldWidth / CACHE_SIZE, game.getInfo().worldHeight / CACHE_SIZE);
    }

    public int getLevel() {
        return MainRenderer.WORLD_RENDERER_LEVEL;
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
        _layerGrid.refresh();
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
//
//        int offsetX = viewport.getPosX();
//        int offsetY = viewport.getPosY();
//
//        for (int x = toX; x >= fromX; x--) {
//            for (int y = toY; y >= fromY; y--) {
//                ParcelModel parcel = WorldHelper.getParcel(x, y);
//                if (parcel != null) {
//                    renderer.draw(_spriteManager.getGround(1), (x * Constant.TILE_WIDTH) + offsetX, (y * Constant.TILE_HEIGHT) + offsetY);
//                }
//            }
//        }
//
//        for (int x = toX; x >= fromX; x--) {
//            for (int y = toY; y >= fromY; y--) {
//                ParcelModel parcel = WorldHelper.getParcel(x, y);
//                if (parcel != null) {
//                    if (parcel.getResource() != null) {
//                        if (parcel.getResource().getTile() == 42) {
//                            renderer.draw(_spriteManager.getItem(parcel.getResource(), parcel.getResource().getTile(), parcel.getResource().getTile()), (x * Constant.TILE_WIDTH) + offsetX - 16, (y * Constant.TILE_HEIGHT) + offsetY - 16);
//                        } else {
//                            renderer.draw(_spriteManager.getItem(parcel.getResource(), parcel.getResource().getTile(), parcel.getResource().getTile()), (x * Constant.TILE_WIDTH) + offsetX, (y * Constant.TILE_HEIGHT) + offsetY);
//                        }
//                    }
//                    if (parcel.getStructure() != null) {
//                        renderer.draw(_spriteManager.getItem(parcel.getStructure()), (x * Constant.TILE_WIDTH) + offsetX, (y * Constant.TILE_HEIGHT) + offsetY);
//                    }
//                    if (parcel.getNetworkObjects() != null) {
//                        for (NetworkObjectModel networkObject: parcel.getNetworkObjects()) {
//                            renderer.draw(_spriteManager.getItem(networkObject), (x * Constant.TILE_WIDTH) + offsetX, (y * Constant.TILE_HEIGHT) + offsetY);
//                        }
//                    }
//                    if (parcel.getItem() != null && parcel == parcel.getItem().getParcel()) {
//                        renderer.draw(_spriteManager.getItem(parcel.getItem()), (x * Constant.TILE_WIDTH) + offsetX, (y * Constant.TILE_HEIGHT) + offsetY);
//                    }
//                    if (parcel.getConsumable() != null) {
//                        renderer.draw(_spriteManager.getItem(parcel.getConsumable()), (x * Constant.TILE_WIDTH) + offsetX, (y * Constant.TILE_HEIGHT) + offsetY);
//                    }
//                }
//            }
//        }

        if (_layerGrid != null) {
            _layerGrid.draw(renderer);
        }
    }

    private void refreshResource(RenderLayer layer, ParcelModel parcel, ResourceModel resource, int x, int y) {
        if (parcel != null && resource != null) {
            SpriteModel sprite = _spriteManager.getItem(parcel.getResource(), parcel.getResource().getTile(), parcel.getResource().getTile());
            layer.draw(sprite, (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
        }
    }

    // TODO: random
    void    refreshFloor(RenderLayer layer, int type, int x, int y) {
//        // Draw ground
//        if (type != 0) {
//            SpriteModel sprite = _spriteManager.getGround(type);
//            layer.draw(sprite, (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
//        }
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

            else {
                layer.draw(SpriteManager.getInstance().getItem(structure), (structure.getParcel().x % CACHE_SIZE) * Constant.TILE_WIDTH, (structure.getParcel().y % CACHE_SIZE) * Constant.TILE_HEIGHT);
            }
        }
    }

    void    refreshItems(RenderLayer layer, ItemModel item, int x, int y) {
        if (item != null && item.getParcel().x == x && item.getParcel().y == y) {

            // Display item
            layer.draw(_spriteManager.getItem(item, item.getCurrentFrame()), (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);

            // Display components
            if (item.getFactory() != null && item.getFactory().getActiveReceipt() != null && item.getFactory().getActiveReceipt().getShoppingList() != null) {
                for (ItemFactoryReceiptModel.FactoryShoppingItemModel component : item.getFactory().getActiveReceipt().getShoppingList()) {
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

    @Override
    public void onAddStructure(StructureModel structure){
        if (_layerGrid != null) {
            _layerGrid.planRefresh(structure.getParcel().x / CACHE_SIZE, structure.getParcel().y / CACHE_SIZE);
        }
    }

    @Override
    public void onAddItem(ItemModel item){
        if (_layerGrid != null) {
            _layerGrid.planRefresh(item.getParcel().x / CACHE_SIZE, item.getParcel().y / CACHE_SIZE);
        }
    }

    @Override
    public void onAddConsumable(ConsumableModel consumable){
        if (_layerGrid != null) {
            _layerGrid.planRefresh(consumable.getParcel().x / CACHE_SIZE, consumable.getParcel().y / CACHE_SIZE);
        }
    }

    @Override
    public void onAddResource(ResourceModel resource) {
        if (_layerGrid != null) {
            _layerGrid.planRefresh(resource.getParcel().x / CACHE_SIZE, resource.getParcel().y / CACHE_SIZE);
        }
    }

    @Override
    public void onRemoveItem(ItemModel item){
        if (_layerGrid != null) {
            _layerGrid.planRefresh(item.getParcel().x / CACHE_SIZE, item.getParcel().y / CACHE_SIZE);
        }
    }

    @Override
    public void onRemoveConsumable(ConsumableModel consumable){
        if (_layerGrid != null) {
            _layerGrid.planRefresh(consumable.getParcel().x / CACHE_SIZE, consumable.getParcel().y / CACHE_SIZE);
        }
    }

    @Override
    public void onRemoveStructure(StructureModel structure){
        if (_layerGrid != null) {
            _layerGrid.planRefresh(structure.getParcel().x / CACHE_SIZE, structure.getParcel().y / CACHE_SIZE);
        }
    }

    @Override
    public void onRemoveResource(ResourceModel resource){
        if (_layerGrid != null) {
            _layerGrid.planRefresh(resource.getParcel().x / CACHE_SIZE, resource.getParcel().y / CACHE_SIZE);
        }
    }

    @Override
    public void onRefreshItem(ItemModel item) {
        if (_layerGrid != null) {
            _layerGrid.planRefresh(item.getParcel().x / CACHE_SIZE, item.getParcel().y / CACHE_SIZE);
        }
    }

    @Override
    public void onRefreshStructure(StructureModel structure) {
        if (_layerGrid != null) {
            _layerGrid.planRefresh(structure.getParcel().x / CACHE_SIZE, structure.getParcel().y / CACHE_SIZE);
        }
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

    @Override
    public void onFloorChange(int floor) {
        if (_layerGrid != null) {
            _layerGrid.refreshAll();
        }
    }
}
