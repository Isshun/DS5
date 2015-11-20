package org.smallbox.faraway.core.game.module.area;

import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.smallbox.faraway.core.data.serializer.SerializerInterface;
import org.smallbox.faraway.core.engine.module.java.ModuleHelper;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.module.area.model.AreaModel;
import org.smallbox.faraway.core.game.module.area.model.GardenAreaModel;
import org.smallbox.faraway.core.game.module.area.model.StorageAreaModel;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.world.SQLHelper;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.PlantModel;
import org.smallbox.faraway.core.game.module.world.model.StructureModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.util.Constant;

import java.util.ArrayList;
import java.util.List;

public class AreaSerializer extends SerializerInterface {

    @Override
    public void save() {
        SQLHelper.getInstance().post(db -> {
            AreaModule areaModule = (AreaModule) ModuleManager.getInstance().getModule(AreaModule.class);
            try {
                // Save areas
                db.exec("CREATE TABLE area_parcel (x INTEGER, y INTEGER, z INTEGER, area_id INTEGER)");
                SQLiteStatement stParcel = db.prepare("INSERT INTO area_parcel (x, y, z, area_id) VALUES (?, ?, ?, ?)");
                try {
                    // Save garden areas
                    db.exec("CREATE TABLE area_garden (id INTEGER, plant TEXT)");
                    SQLiteStatement stGarden = db.prepare("INSERT INTO area_garden (id, plant) VALUES (?, ?)");
                    try {
                        db.exec("begin transaction");
                        areaModule.getGardens().forEach(garden -> {
                            try {
                                stGarden.bind(1, garden.getId());
                                if (garden.getAccepted() != null) {
                                    stGarden.bind(2, garden.getAccepted().name);
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

    public void load(Game game) {
        SQLHelper.getInstance().post(db -> {
            try {
                SQLiteStatement stParcel = db.prepare("SELECT x, y, z, area_id FROM area_parcel where area_id = ?");

                List<GardenAreaModel> gardenAreas = new ArrayList<>();
                SQLiteStatement stGarden = db.prepare("SELECT id, plant FROM area_garden");
                try {
                    while (stGarden.step()) {
                        GardenAreaModel garden = new GardenAreaModel();
                        garden.setAccept(Data.getData().getItemInfo(stGarden.columnString(1)), true);
                        getAreaParcels(garden, stParcel, stGarden.columnInt(0));
                        gardenAreas.add(garden);
                    }
                } finally {
                    stGarden.dispose();
                }

                List<StorageAreaModel> storageAreas = new ArrayList<>();
                SQLiteStatement stStorage = db.prepare("SELECT id FROM area_storage");
                try {
                    while (stStorage.step()) {
                        StorageAreaModel storage = new StorageAreaModel();
                        getAreaParcels(storage, stParcel, stStorage.columnInt(0));
                        storageAreas.add(storage);
                    }
                } finally {
                    stStorage.dispose();
                }

                AreaModule areaModule = (AreaModule) ModuleManager.getInstance().getModule(AreaModule.class);
                areaModule.init(storageAreas, gardenAreas);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        });
    }

    private void getAreaParcels(AreaModel area, SQLiteStatement stParcel, int areaId) throws SQLiteException {
        stParcel.bind(1, areaId);
        while (stParcel.step()) {
            area.addParcel(WorldHelper.getParcel(stParcel.columnInt(0), stParcel.columnInt(1), stParcel.columnInt(2)));
        }
        stParcel.reset(false);
    }
}