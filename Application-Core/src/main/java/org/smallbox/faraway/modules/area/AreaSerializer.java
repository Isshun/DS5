package org.smallbox.faraway.modules.area;

import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameSerializer;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.module.world.SQLManager;
import org.smallbox.faraway.modules.storage.StorageArea;

@GameObject
public class AreaSerializer extends GameSerializer {
    @Inject private Game game;
    @Inject private Data data;
    @Inject private AreaModule areaModule;
    @Inject private SQLManager sqlManager;

    @Override
    public void onSave(SQLManager sqlManager) {
        sqlManager.post(db -> {
            try {
                // Save areas
                db.exec("CREATE TABLE area_parcel (x INTEGER, y INTEGER, z INTEGER, area_id INTEGER)");
                SQLiteStatement stParcel = db.prepare("INSERT INTO area_parcel (x, y, z, area_id) VALUES (?, ?, ?, ?)");

//                try {
//                    // Save garden areas
//                    db.exec("CREATE TABLE area_garden (id INTEGER, plant TEXT)");
//                    SQLiteStatement stGarden = db.prepare("INSERT INTO area_garden (id, plant) VALUES (?, ?)");
//                    try {
//                        db.exec("begin transaction");
//                        areaModule.getGardens().forEach(garden -> {
//                            try {
//                                stGarden.bind(1, garden.getId());
//                                if (garden.getCurrent() != null) {
//                                    stGarden.bind(2, garden.getCurrent().name);
//                                } else {
//                                    stGarden.bindNull(2);
//                                }
//                                stGarden.step();
//                                stGarden.reset(false);
//                                insertAreaParcels(garden, stParcel);
//                            } catch (SQLiteException e) {
//                                e.printStackTrace();
//                            }
//                        });
//                        db.exec("end transaction");
//                    } finally {
//                        stGarden.dispose();
//                    }
//
//                } finally {
//                    stParcel.dispose();
//                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        });
    }

    public void onLoad(SQLManager sqlManager) {
//        Application.sqlManager.post(db -> {
//            try {
//                SQLiteStatement stParcel = db.prepare("SELECT x, y, z, area_id FROM area_parcel where area_id = ?");
//
//                List<GardenArea> gardenAreas = new ArrayList<>();
//                SQLiteStatement stGarden = db.prepare("SELECT id, plant FROM area_garden");
//                try {
//                    while (stGarden.step()) {
//                        GardenArea garden = new GardenArea();
//                        garden.setAccept(Application.data.getItemInfo(stGarden.columnString(1)), true);
//                        getAreaParcels(garden, stParcel, stGarden.columnInt(0));
//                        gardenAreas.add(garden);
//                    }
//                } finally {
//                    stGarden.dispose();
//                }
//
//            } catch (SQLiteException e) {
//                Log.warning("Unable to read area_parcel or area_storage table: " + e.getMessage());
//            }
//        });
    }

    private void getAreaStorageItems(StorageArea storage, SQLiteStatement stItem, int areaId, Data data) throws SQLiteException {
        stItem.bind(1, areaId);
        while (stItem.step()) {
            storage.setAccept(data.getItemInfo(stItem.columnString(0)), true);
            storage.setPriority(stItem.columnInt(2));
        }
        stItem.reset(false);
    }

    private void getAreaParcels(AreaModel area, SQLiteStatement stParcel, int areaId) throws SQLiteException {
        stParcel.bind(1, areaId);
        while (stParcel.step()) {
            area.addParcel(WorldHelper.getParcel(stParcel.columnInt(0), stParcel.columnInt(1), stParcel.columnInt(2)));
        }
        stParcel.reset(false);
    }
}