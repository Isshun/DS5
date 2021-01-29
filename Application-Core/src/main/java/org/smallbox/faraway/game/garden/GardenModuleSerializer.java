package org.smallbox.faraway.game.garden;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameSerializer;
import org.smallbox.faraway.core.save.SQLManager;
import org.smallbox.faraway.game.area.AreaModel;
import org.smallbox.faraway.game.area.AreaModule;
import org.smallbox.faraway.game.plant.GardenArea;
import org.smallbox.faraway.game.plant.GardenModule;
import org.smallbox.faraway.game.world.WorldHelper;
import org.smallbox.faraway.util.log.Log;

import java.util.ArrayList;
import java.util.List;

@GameObject
public class GardenModuleSerializer extends GameSerializer {
    @Inject private Game game;
    @Inject private DataManager dataManager;
    @Inject private AreaModule areaModule;
    @Inject private GardenModule gardenModule;
    @Inject private SQLManager sqlManager;

    @Override
    public void onSave(SQLManager sqlManager) {
        sqlManager.post(db -> {
            try {
                db.exec("CREATE TABLE area_garden_parcel (x INTEGER, y INTEGER, z INTEGER, area_id INTEGER)");
                db.exec("CREATE TABLE area_garden (id INTEGER, plant TEXT)");

                SQLiteStatement stStorage = db.prepare("INSERT INTO area_garden (id) VALUES (?)");
                try {
                    db.exec("begin transaction");
                    gardenModule.getAreas().forEach(storage -> {
                        try {
                            stStorage.bind(1, storage.getId());
                            stStorage.step();
                            stStorage.reset(false);
                            insertAreaParcels(db, storage);
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
                List<GardenArea> gardenAreas = new ArrayList<>();
                SQLiteStatement stGarden = db.prepare("SELECT id FROM area_garden");
                try {
                    while (stGarden.step()) {
                        GardenArea gardenArea = new GardenArea();
                        getAreaParcels(db, gardenArea, stGarden.columnInt(0));
                        gardenAreas.add(gardenArea);
                    }

                    gardenModule.getAreas().clear();
                    gardenModule.getAreas().addAll(gardenAreas);
                } finally {
                    stGarden.dispose();
                }

            } catch (SQLiteException e) {
                Log.warning("Unable to read area_garden or area_garden table: " + e.getMessage());
            }
        });
    }

    private void insertAreaParcels(SQLiteConnection db, GardenArea area) throws SQLiteException {
        SQLiteStatement stParcel = db.prepare("INSERT INTO area_garden_parcel (x, y, z, area_id) VALUES (?, ?, ?, ?)");
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

    private void getAreaParcels(SQLiteConnection db, AreaModel area, int areaId) throws SQLiteException {
        SQLiteStatement stParcel = db.prepare("SELECT x, y, z, area_id FROM area_garden_parcel where area_id = ?");
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