package org.smallbox.faraway.core.game.module.world;

import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import com.ximpleware.*;
import org.smallbox.faraway.core.data.serializer.GameSerializer;
import org.smallbox.faraway.core.data.serializer.SerializerInterface;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameInfo;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.world.model.BuildableMapObject;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.StructureModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.game.module.world.model.resource.PlantModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.module.java.ModuleManager;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.core.util.FileUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WorldModuleSerializer extends SerializerInterface {

    @Override
    public int getModulePriority() { return Constant.MODULE_WORLD_PRIORITY; }

    @Override
    public void save() {
        SQLHelper.getInstance().post(db -> {
            int width = Game.getInstance().getInfo().worldWidth;
            int height = Game.getInstance().getInfo().worldHeight;
            int floors = Game.getInstance().getInfo().worldFloors;
            ParcelModel[][][] parcels = ModuleHelper.getWorldModule().getParcels();

            try {
                db.exec("CREATE TABLE WorldModule_parcel (x INTEGER, y INTEGER, z INTEGER, ground TEXT, rock TEXT, plant INTEGER, item INTEGER, structure INTEGER, consumable INTEGER)");
                db.exec("CREATE TABLE WorldModule_structure (id INTEGER, name TEXT, complete INTEGER)");
                db.exec("CREATE TABLE WorldModule_item (id INTEGER, name TEXT, complete INTEGER)");
                db.exec("CREATE TABLE WorldModule_plant (id INTEGER, name TEXT, grow REAL)");
                db.exec("CREATE TABLE WorldModule_consumable (id INTEGER, name TEXT, quantity INTEGER)");
//                db.exec("CREATE TABLE WorldModule_network (x INTEGER, y INTEGER, z INTEGER, ground TEXT, rock TEXT, plant TEXT, item TEXT, structure TEXT)");
                SQLiteStatement st = db.prepare("INSERT INTO WorldModule_parcel (x, y, z, ground, rock, plant, item, structure, consumable) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                SQLiteStatement stItem = db.prepare("INSERT INTO WorldModule_item (id, name, complete) VALUES (?, ?, ?)");
                SQLiteStatement stStructure = db.prepare("INSERT INTO WorldModule_structure (id, name, complete) VALUES (?, ?, ?)");
                SQLiteStatement stPlant = db.prepare("INSERT INTO WorldModule_plant (id, name, grow) VALUES (?, ?, ?)");
                SQLiteStatement stConsumable = db.prepare("INSERT INTO WorldModule_consumable (id, name, quantity) VALUES (?, ?, ?)");
                try {
                    db.exec("begin transaction");
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            for (int z = 0; z < floors; z++) {
                                ParcelModel parcel = parcels[x][y][z];
                                st.bind(1, x);
                                st.bind(2, y);
                                st.bind(3, z);
                                st.bind(4, 1);

                                // Rock
                                st.bind(5, parcel.hasRock() ? parcel.getRockInfo().name : null);

                                // Plant
                                if (parcel.hasPlant()) {
                                    PlantModel plant = parcel.getPlant();
                                    st.bind(6, plant.getId());
                                    stPlant.bind(1, plant.getId()).bind(2, plant.getInfo().name).bind(3, plant.getGrowState() != null ? plant.getGrowState().value : 0);
                                    stPlant.step();
                                    stPlant.reset(false);
                                } else {
                                    st.bindNull(6);
                                }

                                // Item
                                if (parcel.hasItem()) {
                                    ItemModel item = parcel.getItem();
                                    st.bind(7, item.getId());
                                    stItem.bind(1, item.getId()).bind(2, item.getInfo().name).bind(3, item.isComplete() ? 1 : 0);
                                    stItem.step();
                                    stItem.reset(false);
                                } else {
                                    st.bindNull(7);
                                }
                                st.bind(7, parcel.hasItem() ? parcel.getItem().getId() : 0);

                                // Structure
                                if (parcel.hasStructure()) {
                                    StructureModel structure = parcel.getStructure();
                                    st.bind(8, structure.getId());
                                    stStructure.bind(1, structure.getId()).bind(2, structure.getInfo().name).bind(3, structure.isComplete() ? 1 : 0);
                                    stStructure.step();
                                    stStructure.reset(false);
                                } else {
                                    st.bindNull(8);
                                }

                                // Consumable
                                if (parcel.hasConsumable()) {
                                    ConsumableModel consumable = parcel.getConsumable();
                                    st.bind(8, consumable.getId());
                                    stConsumable.bind(1, consumable.getId()).bind(2, consumable.getInfo().name).bind(3, consumable.getQuantity());
                                    stConsumable.step();
                                    stConsumable.reset(false);
                                } else {
                                    st.bindNull(9);
                                }

                                st.step();
                                st.reset(false);
                            }
                        }
                    }
                    db.exec("end transaction");
                } finally {
                    st.dispose();
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        });
    }

    public void load(GameInfo gameInfo) {
        SQLHelper.getInstance().post(db -> {
            ParcelModel[][][] parcels = new ParcelModel[gameInfo.worldWidth][gameInfo.worldHeight][gameInfo.worldFloors];
            List<ParcelModel> parcelsList = new ArrayList<>();
            int width = gameInfo.worldWidth;
            int height = gameInfo.worldHeight;

            try {
                SQLiteStatement st = db.prepare("SELECT x, y, z, ground, rock, plant, item, structure, consumable FROM WorldModule_parcel");
                SQLiteStatement stPlant = db.prepare("SELECT id, name, grow FROM WorldModule_plant WHERE id = ?");
                SQLiteStatement stItem = db.prepare("SELECT id, name, complete FROM WorldModule_item WHERE id = ?");
                SQLiteStatement stStructure = db.prepare("SELECT id, name, complete FROM WorldModule_structure WHERE id = ?");
                SQLiteStatement stConsumable = db.prepare("SELECT id, name, quantity FROM WorldModule_consumable WHERE id = ?");
                try {
                    while (st.step()) {
                        int x = st.columnInt(0);
                        int y = st.columnInt(1);
                        int z = st.columnInt(2);

                        ParcelModel parcel = new ParcelModel(x + (y * width) + (z * width * height), x, y, z);
                        parcelsList.add(parcel);
                        parcels[x][y][z] = parcel;

                        // Rock
                        if (!st.columnNull(4)) {
                            parcel.setRockInfo(Data.getData().getItemInfo(st.columnString(4)));
                        }

                        // Plant
                        if (!st.columnNull(5)) {
                            int plantId = st.columnInt(5);
                            stPlant.bind(1, plantId);
                            if (stPlant.step()) {
                                PlantModel plant = new PlantModel(Data.getData().getItemInfo(stPlant.columnString(1)), plantId);
                                parcel.setPlant(plant);
                                ModuleHelper.getWorldModule().getPlant().add(plant);
                            }
                            stPlant.reset(false);
                        }

                        // Item
                        if (!st.columnNull(6)) {
                            int itemId = st.columnInt(6);
                            stItem.bind(1, itemId);
                            if (stItem.step()) {
                                ItemModel item = new ItemModel(Data.getData().getItemInfo(stPlant.columnString(1)), parcel, itemId);
                                item.setComplete(stItem.columnInt(2) > 0);
                                parcel.setItem(item);
                                ModuleHelper.getWorldModule().getItems().add(item);
                            }
                            stItem.reset(false);
                        }

                        // Structure
                        if (!st.columnNull(7)) {
                            int structureId = st.columnInt(7);
                            stStructure.bind(1, structureId);
                            if (stStructure.step()) {
                                StructureModel structure = new StructureModel(Data.getData().getItemInfo(stStructure.columnString(1)), structureId);
                                structure.setComplete(stStructure.columnInt(2) > 0);
                                parcel.setStructure(structure);
                                ModuleHelper.getWorldModule().getStructures().add(structure);
                            }
                            stStructure.reset(false);
                        }

                        // Consumable
                        if (!st.columnNull(8)) {
                            int consumableId = st.columnInt(8);
                            stConsumable.bind(1, consumableId);
                            if (stConsumable.step()) {
                                ConsumableModel consumable = new ConsumableModel(Data.getData().getItemInfo(stStructure.columnString(1)));
                                consumable.setId(consumableId);
                                consumable.setQuantity(stStructure.columnInt(2));
                                parcel.setConsumable(consumable);
                                ModuleHelper.getWorldModule().getConsumables().add(consumable);
                            }
                            stConsumable.reset(false);
                        }
                    }
                } finally {
                    st.dispose();
                }

                WorldHelper.init(parcels, gameInfo.worldFloors - 1);
                ModuleHelper.getWorldModule().setParcels(parcels, parcelsList);
                ((PathManager)ModuleManager.getInstance().getModule(PathManager.class)).init(gameInfo);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        });
    }
}