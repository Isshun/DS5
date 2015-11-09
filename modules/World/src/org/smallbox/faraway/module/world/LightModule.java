//package org.smallbox.faraway.module.world;
//
//import org.smallbox.faraway.core.game.Game;
//import org.smallbox.faraway.core.game.model.Data;
//import org.smallbox.faraway.core.game.module.world.model.*;
//import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
//import org.smallbox.faraway.core.game.module.world.model.resource.ResourceModel;
//import org.smallbox.faraway.core.module.GameModule;
//import org.smallbox.faraway.core.module.java.ModuleHelper;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by Alex on 03/07/2015.
// */
//public class LightModule extends GameModule {
//    private List<MapObjectModel> _items = new ArrayList<>();
//
//    @Override
//    protected void onLoaded(Game game) {
//        _updateInterval = 10;
//    }
//
//    @Override
//    protected boolean loadOnStart() {
//        return Data.config.manager.light;
//    }
//
//    @Override
//    protected void onUpdate(int tick) {
//        double light = ModuleHelper.getWorldModule().getLight();
//        ModuleHelper.getWorldModule().getParcelList().forEach(parcel -> parcel.setLight(light));
////        for (MapObjectModel item: _items) {
////            update(item.getX(), item.getY());
////        }
//    }
//
//    private void update(ParcelModel parcel) {
////        for (int i = x - 8; i <= x + 8; i++) {
////            for (int j = y - 8; j <= y + 8; j++) {
////                ParcelModel parcel = ModuleHelper.getWorldModule().getParcel(i, j);
////                if (parcel != null) {
////                    parcel.setLight(0);
////                }
////            }
////        }
//
//        if (parcel.getItem() != null && parcel.getItem().isLight()) {
//            update(parcel, parcel.getItem());
//        }
//        if (parcel.getResource() != null && parcel.getResource().isLight()) {
//            update(parcel, parcel.getResource());
//        }
//
////        for (int i = x - 8; i <= x + 8; i++) {
////            for (int j = y - 8; j <= y + 8; j++) {
////                ParcelModel parcel = ModuleHelper.getWorldModule().getParcel(i, j);
////                if (parcel != null) {
////                    if (parcel.getItem() != null && parcel.getItem().isLight()) {
////                        update(parcel, parcel.getItem(), x-8, x+8, y-8, y+8);
////                    }
////                    if (parcel.getResource() != null && parcel.getResource().isLight()) {
////                        update(parcel, parcel.getResource(), x-8, x+8, y-8, y+8);
////                    }
////                }
////            }
////        }
//    }
//
//    private void update(ParcelModel parcel, MapObjectModel item) {
//        int itemX = item.getParcel().x;
//        int itemY = item.getParcel().y;
//        int distance = item.getInfo().lightDistance;
//        for (int x = itemX - distance; x <= itemX + distance; x++) {
//            for (int y = itemY - distance; y <= itemY + distance; y++) {
////                double distance = Math.min(0, Math.max(1, Math.abs(itemX - x) + Math.abs(itemY - y)));
////                parcel.setLight(Math.min(1, parcel.getLight() + 0.4 + (16 - distance) / 20.0));
//                parcel = ModuleHelper.getWorldModule().getParcel(x, y);
//                if (parcel != null) {
//                    parcel.setLight(1);
//                }
//            }
//        }
//    }
//
//    public void onAddItem(ItemModel item) { if (item.isLight()) { _items.add(item); update(item.getParcel()); } }
//    public void onAddStructure(StructureModel structure) { if (structure.isLight()) { _items.add(structure); update(structure.getParcel()); } }
//    public void onAddResource(ResourceModel resource) { if (resource.isLight()) { _items.add(resource); update(resource.getParcel()); } }
//    public void onAddConsumable(ConsumableModel consumable) { if (consumable.isLight()) { _items.add(consumable); update(consumable.getParcel()); } }
//    public void onRemoveItem(ItemModel item) { if (item.isLight()) { _items.remove(item); update(item.getParcel()); } }
//    public void onRemoveStructure(StructureModel structure) { if (structure.isLight()) { _items.remove(structure); update(structure.getParcel()); } }
//    public void onRemoveResource(ResourceModel resource) { if (resource.isLight()) { _items.remove(resource); update(resource.getParcel()); } }
//    public void onRemoveConsumable(ConsumableModel consumable) { if (consumable.isLight()) { _items.remove(consumable); update(consumable.getParcel()); } }
//}
