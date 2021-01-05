package org.smallbox.faraway.modules.storage;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameSerializer;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.module.world.SQLManager;
import org.smallbox.faraway.modules.area.AreaModel;
import org.smallbox.faraway.modules.area.AreaModule;
import org.smallbox.faraway.util.log.Log;

import java.util.ArrayList;
import java.util.List;

@GameObject
public class StorageModuleSerializer extends GameSerializer {

    @Inject
    private Game game;

    @Inject
    private Data data;

    @Inject
    private AreaModule areaModule;

    @Inject
    private StorageModule storageModule;

    @Inject
    private SQLManager sqlManager;

    @Override
    public void onSave(SQLManager sqlManager) {
        sqlManager.post(db -> {
            try {
                db.exec("CREATE TABLE area_storage_parcel (x INTEGER, y INTEGER, z INTEGER, area_id INTEGER)");
                db.exec("CREATE TABLE area_storage_item (item TEXT, area_id INTEGER, priority INTEGER)");
                db.exec("CREATE TABLE area_storage (id INTEGER, plant TEXT)");

                SQLiteStatement stStorage = db.prepare("INSERT INTO area_storage (id) VALUES (?)");
                try {
                    db.exec("begin transaction");
                    storageModule.getAreas().forEach(storage -> {
                        try {
                            stStorage.bind(1, storage.getId());
                            stStorage.step();
                            stStorage.reset(false);
                            insertAreaParcels(db, storage);
                            insertStorageAreaItems(db, storage);
                        } catch (SQLiteException e) {
                            e.printStackTrace();
                        }
                    });
                    db.exec("end transaction");
                } finally {
                    stStorage.dispose();
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onLoad(SQLManager sqlManager) {
        sqlManager.post(db -> {
            try {
                List<StorageArea> storageAreas = new ArrayList<>();
                SQLiteStatement stStorage = db.prepare("SELECT id FROM area_storage");
                try {
                    while (stStorage.step()) {
                        StorageArea storage = new StorageArea();
                        getAreaParcels(db, storage, stStorage.columnInt(0));
                        getAreaStorageItems(db, storage, stStorage.columnInt(0));
                        storageAreas.add(storage);
                    }

                    storageModule.getAreas().clear();
                    storageModule.getAreas().addAll(storageAreas);
                } finally {
                    stStorage.dispose();
                }

            } catch (SQLiteException e) {
                Log.warning("Unable to read area_parcel or area_storage table: " + e.getMessage());
            }
        });
    }

    private void insertStorageAreaItems(SQLiteConnection db, StorageArea storage) throws SQLiteException {
        SQLiteStatement stItem = db.prepare("INSERT INTO area_storage_item (item, area_id, priority) VALUES (?, ?, ?)");
        try {
            storage.getItemsAccepts().forEach(itemEntry -> {
                try {
                    stItem.bind(1, itemEntry.name).bind(2, storage.getId()).bind(3, storage.getPriority());
                    stItem.step();
                    stItem.reset(false);
                } catch (SQLiteException e) {
                    e.printStackTrace();
                }
            });
        } finally {
            stItem.dispose();
        }
    }

    private void insertAreaParcels(SQLiteConnection db, StorageArea area) throws SQLiteException {
        SQLiteStatement stParcel = db.prepare("INSERT INTO area_storage_parcel (x, y, z, area_id) VALUES (?, ?, ?, ?)");
        try {
            area.getParcels().forEach(parcel -> {
                try {
                    stParcel.bind(1, parcel.x).bind(2, parcel.y).bind(3, parcel.z).bind(4, area.getId());
                    stParcel.step();
                    stParcel.reset(false);
                } catch (SQLiteException e) {
                    e.printStackTrace();
                }
            });
        } finally {
            stParcel.dispose();
        }
    }

    private void getAreaStorageItems(SQLiteConnection db, StorageArea storage, int areaId) throws SQLiteException {
        SQLiteStatement stStorageItem = db.prepare("SELECT item, area_id, priority FROM area_storage_item where area_id = ?");
        try {
            stStorageItem.bind(1, areaId);
            while (stStorageItem.step()) {
                storage.setAccept(data.getItemInfo(stStorageItem.columnString(0)), true);
                storage.setPriority(stStorageItem.columnInt(2));
            }
            stStorageItem.reset(false);
        } finally {
            stStorageItem.dispose();
        }
    }

    private void getAreaParcels(SQLiteConnection db, AreaModel area, int areaId) throws SQLiteException {
        SQLiteStatement stParcel = db.prepare("SELECT x, y, z, area_id FROM area_storage_parcel where area_id = ?");
        try {
            stParcel.bind(1, areaId);
            while (stParcel.step()) {
                area.addParcel(WorldHelper.getParcel(stParcel.columnInt(0), stParcel.columnInt(1), stParcel.columnInt(2)));
            }
            stParcel.reset(false);
        } finally {
            stParcel.dispose();
        }
    }
}