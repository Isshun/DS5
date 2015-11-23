//package org.smallbox.faraway.core.engine.renderer;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.graphics.g2d.Sprite;
//import com.badlogic.gdx.math.Rectangle;
//import org.smallbox.faraway.core.data.GraphicInfo;
//import org.smallbox.faraway.core.data.ItemInfo;
//import org.smallbox.faraway.core.engine.module.java.ModuleHelper;
//import org.smallbox.faraway.core.game.Game;
//import org.smallbox.faraway.core.game.helper.WorldHelper;
//import org.smallbox.faraway.core.game.module.world.model.*;
//import org.smallbox.faraway.core.game.module.world.model.item.ItemFactoryReceiptModel;
//import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
//import org.smallbox.faraway.core.util.Constant;
//
//public abstract class OldWorldRenderer extends BaseRenderer {
//    protected static final int  CACHE_SIZE = 16;
//
//    protected SpriteManager     _spriteManager;
//    protected MapObjectModel    _itemSelected;
//    protected boolean           _firstRefresh;
//    protected LayerGrid         _layerGrid;
//    private int                 _floor;
//
//    @Override
//    protected void onLoad(Game game) {
//        _spriteManager = SpriteManager.getInstance();
//        _firstRefresh = true;
//        _layerGrid = new LayerGrid(game.getInfo().worldWidth / CACHE_SIZE, game.getInfo().worldHeight / CACHE_SIZE);
//        _layerGrid.setOnRefreshLayer((layer, fromX, fromY, toX, toY) -> {
//            ModuleHelper.getWorldModule().getParcels(fromX, toX-1, fromY, toY-1, _floor, _floor, parcelsDo ->
//                    Gdx.app.postRunnable(() -> {
//                        layer.begin();
//                        layer.setRefresh();
//                        for (ParcelModel parcel: parcelsDo) {
//                            refreshStructure(layer, parcel.getStructure(), parcel.x, parcel.y);
//                            refreshPlant(layer, parcel, parcel.getPlant(), parcel.x, parcel.y);
//                            refreshItems(layer, parcel.getItem(), parcel.x, parcel.y);
//                        }
//
//                        for (ParcelModel parcel: parcelsDo) {
//                            refreshConsumable(layer, parcel.getConsumable(), parcel.x, parcel.y);
//                        }
//                        layer.end();
//                    }));
//        });
//    }
//
//    public int getLevel() {
//        return MainRenderer.WORLD_RENDERER_LEVEL;
//    }
//
//    public void onRefresh(int frame) {
//        if (_firstRefresh) {
//            _firstRefresh = false;
//            _layerGrid.refresh();
//        }
//    }
//
//    @Override
//    public void onFloorChange(int floor) {
//        if (_layerGrid != null) {
//            _layerGrid.refreshAll();
//        }
//        _floor = floor;
//    }
//
//    @Override
//    protected void onUpdate() {
//        ModuleHelper.getWorldModule().getPlants().forEach(plant -> {
//            if (plant.getInfo().graphics != null && plant.getInfo().graphics.get(0).type == GraphicInfo.Type.TERRAIN) {
//            }
//        });
//
//        ModuleHelper.getWorldModule().getStructures().forEach(structure -> {
//            ParcelModel parcel = structure.getParcel();
//
//            if ((structure.isWall() || structure.isDoor()) && structure.getInfo().graphics != null) {
//                int tile = 0;
//                if (WorldHelper.hasRock(parcel.x - 1, parcel.y - 1, parcel.z) || WorldHelper.hasWallOrDoor(parcel.x - 1, parcel.y - 1, parcel.z)) { tile |= 0b10000000; }
//                if (WorldHelper.hasRock(parcel.x,     parcel.y - 1, parcel.z) || WorldHelper.hasWallOrDoor(parcel.x,     parcel.y - 1, parcel.z)) { tile |= 0b01000000; }
//                if (WorldHelper.hasRock(parcel.x + 1, parcel.y - 1, parcel.z) || WorldHelper.hasWallOrDoor(parcel.x + 1, parcel.y - 1, parcel.z)) { tile |= 0b00100000; }
//                if (WorldHelper.hasRock(parcel.x - 1, parcel.y,     parcel.z) || WorldHelper.hasWallOrDoor(parcel.x - 1, parcel.y,     parcel.z)) { tile |= 0b00010000; }
//                if (WorldHelper.hasRock(parcel.x + 1, parcel.y,     parcel.z) || WorldHelper.hasWallOrDoor(parcel.x + 1, parcel.y,     parcel.z)) { tile |= 0b00001000; }
//                if (WorldHelper.hasRock(parcel.x - 1, parcel.y + 1, parcel.z) || WorldHelper.hasWallOrDoor(parcel.x - 1, parcel.y + 1, parcel.z)) { tile |= 0b00000100; }
//                if (WorldHelper.hasRock(parcel.x,     parcel.y + 1, parcel.z) || WorldHelper.hasWallOrDoor(parcel.x,     parcel.y + 1, parcel.z)) { tile |= 0b00000010; }
//                if (WorldHelper.hasRock(parcel.x + 1, parcel.y + 1, parcel.z) || WorldHelper.hasWallOrDoor(parcel.x + 1, parcel.y + 1, parcel.z)) { tile |= 0b00000001; }
//                parcel.setTile(tile);
//            }
//        });
//
//        ModuleHelper.getWorldModule().getPlants().forEach(plant -> {
//            if (plant.getInfo().graphics != null) {
//                ParcelModel parcel = plant.getParcel();
//                ItemInfo plantInfo = plant.getInfo();
//
//                int tile = 0;
//                if (WorldHelper.getPlantInfo(parcel.x - 1, parcel.y - 1, parcel.z) == plantInfo) { tile |= 0b10000000; }
//                if (WorldHelper.getPlantInfo(parcel.x,     parcel.y - 1, parcel.z) == plantInfo) { tile |= 0b01000000; }
//                if (WorldHelper.getPlantInfo(parcel.x + 1, parcel.y - 1, parcel.z) == plantInfo) { tile |= 0b00100000; }
//                if (WorldHelper.getPlantInfo(parcel.x - 1, parcel.y,     parcel.z) == plantInfo) { tile |= 0b00010000; }
//                if (WorldHelper.getPlantInfo(parcel.x + 1, parcel.y,     parcel.z) == plantInfo) { tile |= 0b00001000; }
//                if (WorldHelper.getPlantInfo(parcel.x - 1, parcel.y + 1, parcel.z) == plantInfo) { tile |= 0b00000100; }
//                if (WorldHelper.getPlantInfo(parcel.x,     parcel.y + 1, parcel.z) == plantInfo) { tile |= 0b00000010; }
//                if (WorldHelper.getPlantInfo(parcel.x + 1, parcel.y + 1, parcel.z) == plantInfo) { tile |= 0b00000001; }
//                parcel.setTile(tile);
//            }
//        });
//    }
//
//    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
//        int fromX = (int) ((-viewport.getPosX() / Constant.TILE_WIDTH) * viewport.getScale());
//        int fromY = (int) ((-viewport.getPosY() / Constant.TILE_HEIGHT) * viewport.getScale());
//        int toX = fromX + 50;
//        int toY = fromY + 40;
//
//        ParcelModel[][][] parcels = ModuleHelper.getWorldModule().getParcels();
//        for (int x = toX-1; x >= fromX; x--) {
//            for (int y = toY-1; y >= fromY; y--) {
//                ParcelModel parcel = parcels[x][y][_floor];
//                if (parcel.hasPlant()) {
//                    renderer.draw(_spriteManager.getItem(parcel.getPlant().getGraphic(), parcel.getTile(), parcel.getPlant().getTile()), (x * Constant.TILE_WIDTH) + viewportX, (y * Constant.TILE_HEIGHT) + viewportY);
//                }
//                if (parcel.getStructure() != null) {
//                    renderer.draw(_spriteManager.getItem(parcel.getStructure()), (x * Constant.TILE_WIDTH) + viewportX, (y * Constant.TILE_HEIGHT) + viewportY);
//                }
//                if (parcel.getNetworkObjects() != null) {
//                    for (NetworkObjectModel networkObject: parcel.getNetworkObjects()) {
//                        renderer.draw(_spriteManager.getItem(networkObject), (x * Constant.TILE_WIDTH) + viewportX, (y * Constant.TILE_HEIGHT) + viewportY);
//                    }
//                }
//                if (parcel.getItem() != null && parcel == parcel.getItem().getParcel()) {
//                    renderer.draw(_spriteManager.getItem(parcel.getItem()), (x * Constant.TILE_WIDTH) + viewportX, (y * Constant.TILE_HEIGHT) + viewportY);
//                }
//                if (parcel.getConsumable() != null) {
//                    renderer.draw(_spriteManager.getItem(parcel.getConsumable()), (x * Constant.TILE_WIDTH) + viewportX, (y * Constant.TILE_HEIGHT) + viewportY);
//                }
//            }
//        }
//
//        if (_layerGrid != null) {
//            _layerGrid.draw(renderer);
//        }
//    }
//
//    private void refreshResource(RenderLayer layer, ParcelModel parcel, PlantModel resource, int x, int y) {
//        if (parcel != null && resource != null) {
//            Sprite sprite = _spriteManager.getItem(parcel.getPlant().getGraphic(), parcel.getPlant().getTile(), parcel.getPlant().getTile());
//            layer.draw(sprite, (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
//        }
//    }
//
//    // TODO: random
//    void    refreshFloor(RenderLayer layer, int type, int x, int y) {
////        // Draw ground
////        if (type != 0) {
////            SpriteModel sprite = _spriteManager.getGround(type);
////            layer.draw(sprite, (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
////        }
//    }
//
//    private void refreshPlant(RenderLayer layer, ParcelModel parcel, PlantModel plant, int x, int y) {
//        if (parcel != null && plant != null) {
//            Sprite sprite = _spriteManager.getItem(plant.getGraphic(), parcel.getTile(), plant.getTile());
//            layer.draw(sprite, (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
//        }
//    }
//
//    //TODO: random
//    void    refreshStructure(RenderLayer layer, StructureModel structure, int x, int y) {
//        int offsetWall = (Constant.TILE_WIDTH / 2 * 3) - Constant.TILE_HEIGHT;
//
//        if (structure != null) {
//            // Door
//            if (structure.isDoor()) {
//                layer.draw(_spriteManager.getItem(structure), (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT - offsetWall);
//            }
//
//            // Floor
//            else if (structure.isFloor()) {
//                layer.draw(_spriteManager.getItem(structure), (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
//            }
//
//            else {
//                layer.draw(SpriteManager.getInstance().getItem(structure), (structure.getParcel().x % CACHE_SIZE) * Constant.TILE_WIDTH, (structure.getParcel().y % CACHE_SIZE) * Constant.TILE_HEIGHT);
//            }
//        }
//    }
//
//    void    refreshItems(RenderLayer layer, ItemModel item, int x, int y) {
//        if (item != null && item.getParcel().x == x && item.getParcel().y == y) {
//
//            // Display item
//            layer.draw(_spriteManager.getItem(item, item.getCurrentFrame()), (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
//
//            // Display components
//            if (item.getFactory() != null && item.getFactory().getActiveReceipt() != null && item.getFactory().getActiveReceipt().getShoppingList() != null) {
//                for (ItemFactoryReceiptModel.FactoryShoppingItemModel component : item.getFactory().getActiveReceipt().getShoppingList()) {
//                    Sprite sprite = _spriteManager.getItem(component.consumable.getInfo());
//                    if (sprite != null) {
//                        if (item.getInfo().factory != null && item.getInfo().factory.inputSlots != null) {
//                            layer.draw(sprite,
//                                    (x + item.getInfo().factory.inputSlots[0]) * Constant.TILE_WIDTH,
//                                    (y + item.getInfo().factory.inputSlots[1]) * Constant.TILE_HEIGHT);
//                        } else {
//                            layer.draw(sprite, (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
//                        }
//                    }
//                }
//            }
//
//            // Display selection
//            if (!item.isFunctional()) {
//                layer.draw(_spriteManager.getIcon("data/res/ic_power.png"), (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
//            }
//        }
//    }
//
//    void    refreshConsumable(RenderLayer layer, ConsumableModel consumable, int x, int y) {
//        if (consumable != null) {
//
//            // Regular item
//            Sprite sprite = _spriteManager.getItem(consumable, consumable.getCurrentFrame());
//            if (sprite != null) {
//                layer.draw(sprite, (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
//            }
//
//            // Selection
//            if (consumable.isSelected()) {
//                _itemSelected = consumable;
//            }
//        }
//    }
//
//    public void onDrawSelected(GDXRenderer renderer, Viewport viewport, double animProgress) {
//        if (_itemSelected != null) {
//            int offset = 0;
////        switch (frame / 10 % 5) {
////            case 1: offset = 1; break;
////            case 2: offset = 2; break;
////            case 3: offset = 3; break;
////            case 4: offset = 2; break;
////            case 5: offset = 1; break;
////        }
//
//            int fromX = 0;
//            int fromY = 0;
//            int toX = 32;
//            int toY = 32;
//            if (_itemSelected.getGraphic() != null && _itemSelected.getGraphic().textureRect != null) {
//                Rectangle rect = _itemSelected.getGraphic().textureRect;
//                fromX = (int)(rect.getX()/2);
//                fromY = (int)(rect.getY()/2);
//                toX = (int)(rect.getX() + rect.getWidth()) - 8;
//                toY = (int)(rect.getY() + rect.getHeight()) - 8;
//            }
//
//            int x = _itemSelected.getParcel().x * Constant.TILE_WIDTH + viewport.getPosX();
//            int y = _itemSelected.getParcel().y * Constant.TILE_HEIGHT + viewport.getPosY();
//            renderer.draw(_spriteManager.getSelectorCorner(0), x - offset + fromX, y - offset + fromY);
//            renderer.draw(_spriteManager.getSelectorCorner(1), x + offset + toX, y - offset + fromY);
//            renderer.draw(_spriteManager.getSelectorCorner(2), x - offset + fromX, y + offset + toY);
//            renderer.draw(_spriteManager.getSelectorCorner(3), x + offset + toX, y + offset + toY);
//
//            if (_itemSelected.getInfo().slots != null) {
//                for (int[] slot: _itemSelected.getInfo().slots) {
//                    renderer.draw(_spriteManager.getIcon("data/res/ic_slot.png"),
//                            (_itemSelected.getParcel().x + slot[0])* Constant.TILE_WIDTH + viewport.getPosX(),
//                            (_itemSelected.getParcel().y + slot[1])* Constant.TILE_HEIGHT + viewport.getPosY());
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onAddStructure(StructureModel structure){
//        if (_layerGrid != null) {
//            _layerGrid.planRefresh(structure.getParcel().x / CACHE_SIZE, structure.getParcel().y / CACHE_SIZE);
//        }
//    }
//
//    @Override
//    public void onAddItem(ItemModel item){
//        if (_layerGrid != null) {
//            _layerGrid.planRefresh(item.getParcel().x / CACHE_SIZE, item.getParcel().y / CACHE_SIZE);
//        }
//    }
//
//    @Override
//    public void onAddConsumable(ConsumableModel consumable){
//        if (_layerGrid != null) {
//            _layerGrid.planRefresh(consumable.getParcel().x / CACHE_SIZE, consumable.getParcel().y / CACHE_SIZE);
//        }
//    }
//
//    @Override
//    public void onAddPlant(PlantModel resource) {
//        if (_layerGrid != null) {
//            _layerGrid.planRefresh(resource.getParcel().x / CACHE_SIZE, resource.getParcel().y / CACHE_SIZE);
//        }
//    }
//
//    @Override
//    public void onRemoveItem(ItemModel item){
//        if (_layerGrid != null) {
//            _layerGrid.planRefresh(item.getParcel().x / CACHE_SIZE, item.getParcel().y / CACHE_SIZE);
//        }
//    }
//
//    @Override
//    public void onRemoveConsumable(ConsumableModel consumable){
//        if (_layerGrid != null) {
//            _layerGrid.planRefresh(consumable.getParcel().x / CACHE_SIZE, consumable.getParcel().y / CACHE_SIZE);
//        }
//    }
//
//    @Override
//    public void onRemoveStructure(StructureModel structure){
//        if (_layerGrid != null) {
//            _layerGrid.planRefresh(structure.getParcel().x / CACHE_SIZE, structure.getParcel().y / CACHE_SIZE);
//        }
//    }
//
//    @Override
//    public void onRemovePlant(PlantModel plant){
//        if (_layerGrid != null) {
//            _layerGrid.planRefresh(plant.getParcel().x / CACHE_SIZE, plant.getParcel().y / CACHE_SIZE);
//        }
//    }
//
//    @Override
//    public void onRefreshItem(ItemModel item) {
//        if (_layerGrid != null) {
//            _layerGrid.planRefresh(item.getParcel().x / CACHE_SIZE, item.getParcel().y / CACHE_SIZE);
//        }
//    }
//
//    @Override
//    public void onRefreshStructure(StructureModel structure) {
//        if (_layerGrid != null) {
//            _layerGrid.planRefresh(structure.getParcel().x / CACHE_SIZE, structure.getParcel().y / CACHE_SIZE);
//        }
//    }
//
//    @Override
//    public void onSelectItem(ItemModel item) {
//        _itemSelected = item;
//    }
//
//    @Override
//    public void onSelectPlant(PlantModel resource) {
//        _itemSelected = resource;
//    }
//
//    @Override
//    public void onSelectConsumable(ConsumableModel consumable) {
//        _itemSelected = consumable;
//    }
//
//    @Override
//    public void onSelectStructure(StructureModel structure) {
//        _itemSelected = structure;
//    }
//
//    @Override
//    public void onDeselect() {
//        _itemSelected = null;
//    }
//
//    @Override
//    public void onFloorChange(int floor) {
//        if (_layerGrid != null) {
//            _layerGrid.refreshAll();
//        }
//    }
//}