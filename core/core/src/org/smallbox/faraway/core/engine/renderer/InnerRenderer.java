//package org.smallbox.faraway.core.engine.renderer;
//
//import com.badlogic.gdx.math.Rectangle;
//import org.smallbox.faraway.core.RenderLayer;
//import org.smallbox.faraway.core.SpriteManager;
//import org.smallbox.faraway.core.SpriteModel;
//import org.smallbox.faraway.core.Viewport;
//import org.smallbox.faraway.core.game.Game;
//import org.smallbox.faraway.core.game.model.Data;
//import org.smallbox.faraway.core.game.model.GameConfig;
//import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
//import org.smallbox.faraway.core.game.module.world.model.MapObjectModel;
//import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
//import org.smallbox.faraway.core.game.module.world.model.StructureModel;
//import org.smallbox.faraway.core.game.module.world.model.item.ItemFactoryReceiptModel;
//import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
//import org.smallbox.faraway.core.game.module.world.model.resource.ResourceModel;
//import org.smallbox.faraway.core.module.java.ModuleHelper;
//import org.smallbox.faraway.core.util.Constant;
//import org.smallbox.faraway.core.util.Log;
//
//public class InnerRenderer extends WorldRenderer {
//    private MapObjectModel      _itemSelected;
//    private boolean             _firstRefresh;
//
//    @Override
//    protected void onLoad(Game game) {
//        super.onLoad(game);
//
//        _layerGrid.setOnRefreshLayer((layer, fromX, fromY, toX, toY) -> {
//            Log.info("Refresh layer: " + layer.getIndex());
//
//            layer.begin();
//            layer.setRefresh();
//            for (int x = toX - 1; x >= fromX; x--) {
//                for (int y = toY - 1; y >= fromY; y--) {
//                    ParcelModel parcel = ModuleHelper.getWorldModule().getParcel(x, y);
//                    if (parcel != null && !parcel.isExterior()) {
//                        if (Data.config.render.floor) {
//                            refreshFloor(layer, parcel.getType(), x, y);
//                        }
//                        if (Data.config.render.structure) {
//                            refreshStructure(layer, parcel.getStructure(), x, y);
//                        }
//                        if (Data.config.render.resource) {
//                            refreshResource(layer, parcel, parcel.getResource(), x, y);
//                        }
//                        if (Data.config.render.item) {
//                            refreshItems(layer, parcel.getItem(), x, y);
//                        }
//                    }
//                }
//            }
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
//            layer.end();
//        });
//    }
//
//    public int getLevel() {
//        return MainRenderer.INNER_RENDERER_LEVEL;
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
//    public boolean isActive(GameConfig config) {
//        return true;
//    }
//
//    private void refreshLayer(RenderLayer layer, int fromX, int fromY, int toX, int toY) {
//        Log.info("Refresh layer: " + layer.getIndex());
//
//        layer.begin();
//        layer.setRefresh();
//        for (int x = toX - 1; x >= fromX; x--) {
//            for (int y = toY - 1; y >= fromY; y--) {
////                if (cacheOnScreen(x / CACHE_SIZE, y / CACHE_SIZE)) {
////                }
//                ParcelModel parcel = ModuleHelper.getWorldModule().getParcel(x, y);
//                if (parcel != null) {
//                    if (Data.config.render.floor) {
//                        refreshFloor(layer, parcel.getType(), x, y);
//                    }
//                    if (Data.config.render.structure) {
//                        refreshStructure(layer, parcel.getStructure(), x, y);
//                    }
//                    if (Data.config.render.resource) {
//                        refreshResource(layer, parcel, parcel.getResource(), x, y);
//                    }
//                    if (Data.config.render.item) {
//                        refreshItems(layer, parcel.getItem(), x, y);
//                    }
//                }
//            }
//        }
//        for (int x = toX - 1; x >= fromX; x--) {
//            for (int y = toY - 1; y >= fromY; y--) {
////                if (cacheOnScreen(x / CACHE_SIZE, y / CACHE_SIZE)) {
////                }
//                ParcelModel parcel = ModuleHelper.getWorldModule().getParcel(x, y);
//                if (parcel != null) {
//                    if (Data.config.render.consumable) {
//                        refreshConsumable(layer, parcel.getConsumable(), x, y);
//                    }
//                }
//            }
//        }
//        layer.end();
//    }
//
//    @Override
//    protected void onUpdate() {
//    }
//
//    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
//        if (_layerGrid != null) {
//            _layerGrid.draw(renderer);
//        }
//    }
//
//    private void refreshResource(RenderLayer layer, ParcelModel parcel, ResourceModel resource, int x, int y) {
//        if (parcel != null && resource != null) {
//            SpriteModel sprite = _spriteManager.getItem(parcel.getResource(), parcel.getResource().getTile(), parcel.getResource().getTile());
//            layer.draw(sprite, (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
//        }
//    }
//
//    // TODO: random
//    void    refreshFloor(RenderLayer layer, int type, int x, int y) {
//        // Draw ground
//        if (type != 0) {
//            SpriteModel sprite = _spriteManager.getGround(type);
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
////            // Wall
////            else if (structure.isWall()) {
////                layer.draw(drawWall(structure, x, y, offsetWall), (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
////            }
////
////            // Hull
////            else if (structure.isHull()) {
////                layer.draw(drawWall(structure, x, y, offsetWall), (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
////            }
//
//            else {
//                layer.draw(SpriteManager.getInstance().getItem(structure), (structure.getParcel().x % CACHE_SIZE) * Constant.TILE_WIDTH, (structure.getParcel().y % CACHE_SIZE) * Constant.TILE_HEIGHT);
//            }
//        }
//    }
//
////    private SpriteModel drawWall(StructureModel structure, int x, int y, int offsetWall) {
////        return _spriteManager.getItem(structure, structure.isComplete() ? 0 : 1);
////    }
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
//                    SpriteModel sprite = _spriteManager.getItem(component.consumable.getInfo());
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
////            // Display crafts
////            if (item.getFactory() != null && item.getFactory().getOutputs() != null) {
////                for (ItemFactoryModel.FactoryOutputModel product : item.getFactory().getOutputs()) {
////                    SpriteModel sprite = _spriteManager.getItem(product.itemInfo);
////                    if (sprite != null) {
////                        if (item.getInfo().factory != null && item.getInfo().factory.outputSlots != null) {
////                            layer.draw(sprite,
////                                    (x + item.getInfo().factory.outputSlots[0]) * Constant.TILE_WIDTH,
////                                    (y + item.getInfo().factory.outputSlots[1]) * Constant.TILE_HEIGHT);
////                        } else {
////                            layer.draw(sprite, (x % CACHE_SIZE) * Constant.TILE_WIDTH, (y % CACHE_SIZE) * Constant.TILE_HEIGHT);
////                        }
////                    }
////                }
////            }
//
////            // Display selection
////            if (item.isSelected()) {
////                _itemSelected = item;
////            }
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
//            SpriteModel sprite = _spriteManager.getItem(consumable, consumable.getCurrentFrame());
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
//}
