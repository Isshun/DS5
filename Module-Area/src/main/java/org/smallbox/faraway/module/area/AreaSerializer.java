package org.smallbox.faraway.module.area;

import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.data.serializer.GameSerializer;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.module.area.model.AreaModel;
import org.smallbox.faraway.core.game.module.area.model.GardenAreaModel;
import org.smallbox.faraway.core.game.module.area.model.StorageAreaModel;
import org.smallbox.faraway.core.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AreaSerializer extends GameSerializer<AreaModule> {
    @Override
    public void onSave(AreaModule module, Game game) {
        Application.sqlManager.post(db -> {
            AreaModule areaModule = (AreaModule) ModuleManager.getInstance().getModule(AreaModule.class);
            try {
                // Save areas
                db.exec("CREATE TABLE area_parcel (x INTEGER, y INTEGER, z INTEGER, area_id INTEGER)");
                SQLiteStatement stParcel = db.prepare("INSERT INTO area_parcel (x, y, z, area_id) VALUES (?, ?, ?, ?)");

                db.exec("CREATE TABLE area_storage_item (item TEXT, area_id INTEGER, priority INTEGER)");
                SQLiteStatement stItem = db.prepare("INSERT INTO area_storage_item (item, area_id, priority) VALUES (?, ?, ?)");

                try {
                    // Save garden areas
                    db.exec("CREATE TABLE area_garden (id INTEGER, plant TEXT)");
                    SQLiteStatement stGarden = db.prepare("INSERT INTO area_garden (id, plant) VALUES (?, ?)");
                    try {
                        db.exec("begin transaction");
                        areaModule.getGardens().forEach(garden -> {
                            try {
                                stGarden.bind(1, garden.getId());
                                if (garden.getCurrent() != null) {
                                    stGarden.bind(2, garden.getCurrent().name);
                                } else {
                                    stGarden.bindNull(2);
                                }
                                stGarden.step();
                                stGarden.reset(false);
                                insertAreaParcels(garden, stParcel);
                            } catch (SQLiteException e) {
                                e.printStackTrace();
                            }
                        });
                        db.exec("end transaction");
                    } finally {
                        stGarden.dispose();
                    }

                    // Save storage areas
                    db.exec("CREATE TABLE area_storage (id INTEGER, plant TEXT)");
                    SQLiteStatement stStorage = db.prepare("INSERT INTO area_storage (id) VALUES (?)");
                    try {
                        db.exec("begin transaction");
                        areaModule.getStorages().forEach(storage -> {
                            try {
                                stStorage.bind(1, storage.getId());
                                stStorage.step();
                                stStorage.reset(false);
                                insertAreaParcels(storage, stParcel);
                                insertStorageAreaItems(storage, stItem);
                            } catch (SQLiteException e) {
                                e.printStackTrace();
                            }
                        });
                        db.exec("end transaction");
                    } finally {
                        stStorage.dispose();
                    }
                } finally {
                    stParcel.dispose();
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        });
    }

    private void insertStorageAreaItems(StorageAreaModel storage, SQLiteStatement stItem) {
        storage.getItemsAccepts().entrySet().stream().filter(Map.Entry::getValue).forEach(itemEntry -> {
            try {
                stItem.bind(1, itemEntry.getKey().name).bind(2, storage.getId()).bind(3, storage.getPriority());
                stItem.step();
                stItem.reset(false);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        });
    }

    private void insertAreaParcels(AreaModel area, SQLiteStatement stParcel) {
        area.getParcels().forEach(parcel -> {
            try {
                stParcel.bind(1, parcel.x).bind(2, parcel.y).bind(3, parcel.z).bind(4, area.getId());
                stParcel.step();
                stParcel.reset(false);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        });
    }

    public void onLoad(AreaModule module, Game game) {
        Application.sqlManager.post(db -> {
            try {
                SQLiteStatement stParcel = db.prepare("SELECT x, y, z, area_id FROM area_parcel where area_id = ?");

                List<GardenAreaModel> gardenAreas = new ArrayList<>();
                SQLiteStatement stGarden = db.prepare("SELECT id, plant FROM area_garden");
                try {
                    while (stGarden.step()) {
                        GardenAreaModel garden = new GardenAreaModel();
                        garden.setAccept(Application.data.getItemInfo(stGarden.columnString(1)), true);
                        getAreaParcels(garden, stParcel, stGarden.columnInt(0));
                        gardenAreas.add(garden);
                    }
                } finally {
                    stGarden.dispose();
                }

                List<StorageAreaModel> storageAreas = new ArrayList<>();
                SQLiteStatement stStorage = db.prepare("SELECT id FROM area_storage");
                SQLiteStatement stStorageItem = db.prepare("SELECT item, area_id, priority FROM area_storage_item where area_id = ?");
                try {
                    while (stStorage.step()) {
                        StorageAreaModel storage = new StorageAreaModel();
                        getAreaParcels(storage, stParcel, stStorage.columnInt(0));
                        getAreaStorageItems(storage, stStorageItem, stStorage.columnInt(0));
                        storageAreas.add(storage);
                    }
                } finally {
                    stStorage.dispose();
                }

                AreaModule areaModule = (AreaModule) ModuleManager.getInstance().getModule(AreaModule.class);
                areaModule.init(storageAreas, gardenAreas);
            } catch (SQLiteException e) {
                Log.warning("Unable to read area_parcel or area_storage table: " + e.getMessage());
            }
        });
    }

    private void getAreaStorageItems(StorageAreaModel storage, SQLiteStatement stItem, int areaId) throws SQLiteException {
        stItem.bind(1, areaId);
        while (stItem.step()) {
            storage.setAccept(Application.data.getItemInfo(stItem.columnString(0)), true);
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