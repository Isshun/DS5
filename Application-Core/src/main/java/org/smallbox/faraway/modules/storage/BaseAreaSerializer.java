//package org.smallbox.faraway.modules.storage;
//
//import com.almworks.sqlite4java.SQLiteConnection;
//import com.almworks.sqlite4java.SQLiteException;
//import com.almworks.sqlite4java.SQLiteStatement;
//import org.smallbox.faraway.core.game.GameSerializer;
//import org.smallbox.faraway.core.game.helper.WorldHelper;
//import org.smallbox.faraway.modules.area.AreaModel;
//
//public abstract class BaseAreaSerializer<T> extends GameSerializer {
//
//    protected abstract String getPrefix();
//
//    private void insertAreaParcels(SQLiteConnection db, T area) throws SQLiteException {
//        SQLiteStatement stParcel = db.prepare("INSERT INTO " + getPrefix() + "_parcel (x, y, z, area_id) VALUES (?, ?, ?, ?)");
//        try {
//            area.getParcels().forEach(parcel -> {
//                try {
//                    stParcel.bind(1, parcel.x).bind(2, parcel.y).bind(3, parcel.z).bind(4, area.getId());
//                    stParcel.step();
//                    stParcel.reset(false);
//                } catch (SQLiteException e) {
//                    e.printStackTrace();
//                }
//            });
//        } finally {
//            stParcel.dispose();
//        }
//    }
//
//    private void getAreaParcels(SQLiteConnection db, AreaModel area, int areaId) throws SQLiteException {
//        SQLiteStatement stParcel = db.prepare("SELECT x, y, z, area_id FROM " + getPrefix() + "_parcel where area_id = ?");
//        try {
//            stParcel.bind(1, areaId);
//            while (stParcel.step()) {
//                area.addParcel(WorldHelper.getParcel(stParcel.columnInt(0), stParcel.columnInt(1), stParcel.columnInt(2)));
//            }
//            stParcel.reset(false);
//        } finally {
//            stParcel.dispose();
//        }
//    }
//
//}
