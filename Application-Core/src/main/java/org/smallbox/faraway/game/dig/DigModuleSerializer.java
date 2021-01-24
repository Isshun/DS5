//package org.smallbox.faraway.modules.dig;
//
//import com.almworks.sqlite4java.SQLiteConnection;
//import com.almworks.sqlite4java.SQLiteException;
//import com.almworks.sqlite4java.SQLiteStatement;
//import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
//import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
//import org.smallbox.faraway.core.game.Data;
//import org.smallbox.faraway.core.game.Game;
//import org.smallbox.faraway.core.game.GameSerializer;
//import org.smallbox.faraway.game.world.WorldHelper;
//import org.smallbox.faraway.core.save.SQLManager;
//import org.smallbox.faraway.modules.area.AreaModel;
//import org.smallbox.faraway.util.log.Log;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@GameObject
//public class DigModuleSerializer extends GameSerializer {
//
//    @Inject
//    private Game game;
//
//    @Inject
//    private Data data;
//
//    @Inject
//    private DigModule digModule;
//
//    @Inject
//    private SQLManager sqlManager;
//
//    @Override
//    public void onSave(SQLManager sqlManager) {
//        sqlManager.post(db -> {
//            try {
//                // Save areas
//                db.exec("CREATE TABLE dig_area_parcel (x INTEGER, y INTEGER, z INTEGER, area_id INTEGER)");
//
//                SQLiteStatement stParcel = db.prepare("INSERT INTO dig_area_parcel (x, y, z, area_id) VALUES (?, ?, ?, ?)");
//                try {
//                    db.exec("begin transaction");
//                    digModule.getAreas().forEach(digArea -> {
//                        try {
//                            stParcel.bind(1, digArea.getId());
//                            stParcel.step();
//                            stParcel.reset(false);
//                            insertAreaParcels(db, digArea);
//                        } catch (SQLiteException e) {
//                            e.printStackTrace();
//                        }
//                    });
//                    db.exec("end transaction");
//                } finally {
//                    stParcel.dispose();
//                }
//            } catch (SQLiteException e) {
//                e.printStackTrace();
//            }
//        });
//    }
//
//    public void onLoad(SQLManager sqlManager) {
//        sqlManager.post(db -> {
//            try {
//                List<DigArea> storageAreas = new ArrayList<>();
//                SQLiteStatement stStorage = db.prepare("SELECT id FROM area_storage");
//                try {
//                    while (stStorage.step()) {
//                        DigArea digArea = new DigArea();
//                        getAreaParcels(db, digArea, stStorage.columnInt(0));
//                        storageAreas.add(digArea);
//                    }
//
//                    digModule.getAreas().clear();
//                    digModule.getAreas().addAll(storageAreas);
//                } finally {
//                    stStorage.dispose();
//                }
//
//            } catch (SQLiteException e) {
//                Log.warning("Unable to read area_parcel or area_storage table: " + e.getMessage());
//            }
//        });
//
//    }
//
//    private void getAreaParcels(SQLiteConnection db, AreaModel area, int areaId) throws SQLiteException {
//        SQLiteStatement stParcel = db.prepare("SELECT x, y, z, area_id FROM dig_area_parcel where area_id = ?");
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
//    private void insertAreaParcels(SQLiteConnection db, DigArea area) throws SQLiteException {
//        SQLiteStatement stParcel = db.prepare("INSERT INTO dig_area_parcel (x, y, z, area_id) VALUES (?, ?, ?, ?)");
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
//}