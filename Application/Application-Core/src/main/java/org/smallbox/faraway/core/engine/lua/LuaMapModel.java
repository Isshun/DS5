//package org.smallbox.faraway.core.engine.lua;
//
//import org.luaj.vm2.LuaTable;
//import org.luaj.vm2.LuaValue;
//import org.smallbox.faraway.core.game.Game;
//import org.smallbox.faraway.core.module.world.model.ParcelModel;
//import org.smallbox.faraway.core.engine.module.java.ModuleHelper;
//
///**
// * Created by Alex on 20/06/2015.
// */
//public class LuaMapModel {
//    public LuaValue getDropLocation() {
//        ParcelModel[][][] parcels = ModuleHelper.getWorldModule().getParcelsByType();
//        int width = Application.gameManager.getGame().getInfo().worldWidth;
//        int height = Application.gameManager.getGame().getInfo().worldHeight;
//
//        int startX = (int) (Math.random() * width);
//        int startY = (int) (Math.random() * height);
//        for (int x = 0; x < width; x++) {
//            for (int y = 0; y < height; y++) {
//                ParcelModel parcel = parcels[(x + startX) % width][(y + startY) % height][0];
//                if (parcel.getRoom() != null && !parcel.getRoom().isExterior()) {
//                    break;
//                }
//                if (parcel.getStructure() != null && parcel.getStructure().isSolid()) {
//                    break;
//                }
//                if (parcel.hasResource() && parcel.getResource().isSolid()) {
//                    break;
//                }
//                if (parcel.getItem() != null) {
//                    break;
//                }
//                if (parcel.getConsumable() != null) {
//                    break;
//                }
//
//                // Return free space
//                LuaValue ret = new LuaTable();
//                ret.set("x", LuaValue.valueOf(parcel.x));
//                ret.set("y", LuaValue.valueOf(parcel.y));
//                return ret;
//            }
//        }
//        return null;
//    }
//}
