//package org.smallbox.faraway.core.data.factory;
//
//import org.smallbox.faraway.core.game.module.world.WorldModule;
//import org.smallbox.faraway.core.game.module.world.model.*;
//
//public class ItemFactory {
//
//    public static MapObjectModel create(WorldModule manager, ItemInfo info, int value) {
//        return create(manager, null, info, value);
//    }
//
//    public static MapObjectModel create(WorldModule manager, ParcelModel area, ItemInfo info, int value) {
////        // Base light item
////        if ("base.light".equals(info.name)) {
////            model.setLightSource(info.light);
////            ((MainRenderer)MainRenderer.getInstance()).initLight();
////            return null;
////        }
//        // Consumable item
//        if (info.isConsumable) {
//            return createConsumable(area, info, value);
//        }
//        // World resource
//        else if (info.isResource) {
//            return createResource(area, info, value);
//        }
//        // Structure item
//        else if (info.isStructure) {
//            return createStructure(area, info, value == -1);
//        }
//        // User item
//        else {
//            return createUserItem(manager, area, info, value == -1);
//        }
//    }
//
//    public static ItemModel createUserItem(WorldModule manager, ParcelModel parcel, ItemInfo info, boolean isComplete) {
//        ItemModel item = new ItemModel(info, parcel);
//        item.addProgress(isComplete ? info.cost : 0);
//        return item;
//    }
//
//    public static ConsumableModel createConsumable(ParcelModel area, ItemInfo info, int quantity) {
//        ConsumableModel consumable = new ConsumableModel(info);
//        consumable.setQuantity(quantity);
//        return consumable;
//    }
//
//    public static StructureModel createStructure(ParcelModel area, ItemInfo info, boolean isComplete) {
//        StructureModel structure = new StructureModel(info);
//        structure.addProgress(isComplete ? info.cost : 0);
//        return structure;
//    }
//
//    public static MapObjectModel createResource(ParcelModel area, ItemInfo info, int matterSupply) {
//        ResourceModel resource = new ResourceModel(info);
//        resource.setValue(matterSupply);
//        return resource;
//    }
//}
