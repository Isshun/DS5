package org.smallbox.faraway.module.world;

import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.smallbox.faraway.core.data.serializer.SerializerInterface;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.world.SQLHelper;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.PlantModel;
import org.smallbox.faraway.core.game.module.world.model.StructureModel;
import org.smallbox.faraway.core.util.Constant;

import java.util.ArrayList;
import java.util.List;

public class WorldModuleSerializer extends SerializerInterface {
    private final WorldModule _worldModule;

    public WorldModuleSerializer(WorldModule worldModule) {
        _worldModule = worldModule;
    }

    @Override
    public int getModulePriority() { return Constant.MODULE_WORLD_PRIORITY; }

    @Override
    public void save() {
        SQLHelper.getInstance().post(db -> {
            int width = Game.getInstance().getInfo().worldWidth;
            int height = Game.getInstance().getInfo().worldHeight;
            int floors = Game.getInstance().getInfo().worldFloors;
            ParcelModel[][][] parcels = _worldModule.getParcels();

            try {
                db.exec("CREATE TABLE WorldModule_parcel (x INTEGER, y INTEGER, z INTEGER, ground TEXT, rock TEXT, plant INTEGER, item INTEGER, structure INTEGER, consumable INTEGER, liquid TEXT, liquid_value REAL)");
                db.exec("CREATE TABLE WorldModule_structure (id INTEGER, name TEXT, complete INTEGER)");
                db.exec("CREATE TABLE WorldModule_item (id INTEGER, name TEXT, complete INTEGER)");
                db.exec("CREATE TABLE WorldModule_plant (id INTEGER, name TEXT, maturity REAL, nourish REAL, seed INTEGER)");
                db.exec("CREATE TABLE WorldModule_consumable (id INTEGER, name TEXT, quantity INTEGER)");
//                db.exec("CREATE TABLE WorldModule_network (x INTEGER, y INTEGER, z INTEGER, ground TEXT, rock TEXT, plant TEXT, item TEXT, structure TEXT)");
                SQLiteStatement st = db.prepare("INSERT INTO WorldModule_parcel (x, y, z, ground, rock, plant, item, structure, consumable, liquid, liquid_value) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                SQLiteStatement stItem = db.prepare("INSERT INTO WorldModule_item (id, name, complete) VALUES (?, ?, ?)");
                SQLiteStatement stStructure = db.prepare("INSERT INTO WorldModule_structure (id, name, complete) VALUES (?, ?, ?)");
                SQLiteStatement stPlant = db.prepare("INSERT INTO WorldModule_plant (id, name, maturity, nourish, seed) VALUES (?, ?, ?, ?, ?)");
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

                                // Ground
                                st.bind(4, parcel.hasGround() ? parcel.getGroundInfo().name : null);

                                // Rock
                                st.bind(5, parcel.hasRock() ? parcel.getRockInfo().name : null);

                                // Plant
                                if (parcel.hasPlant()) {
                                    PlantModel plant = parcel.getPlant();
                                    st.bind(6, plant.getId());
                                    stPlant.bind(1, plant.getId());
                                    stPlant.bind(2, plant.getInfo().name);
                                    stPlant.bind(3, plant.getMaturity());
                                    stPlant.bind(4, plant.getNourish());
                                    stPlant.bind(5, plant.hasSeed() ? 1 : 0);
                                    stPlant.step();
                                    stPlant.reset(false);
                                } else {
                                    st.bindNull(6);
                                }

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
                                    st.bind(9, consumable.getId());
                                    stConsumable.bind(1, consumable.getId()).bind(2, consumable.getInfo().name).bind(3, consumable.getQuantity());
                                    stConsumable.step();
                                    stConsumable.reset(false);
                                } else {
                                    st.bindNull(9);
                                }


                                // Liquid
                                if (parcel.hasLiquid()) {
                                    st.bind(10, parcel.getLiquidInfo().name);
                                    st.bind(11, parcel.getLiquidValue());
                                } else {
                                    st.bindNull(10);
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

    public void load(Game game) {
        SQLHelper.getInstance().post(db -> {
            int width = game.getInfo().worldWidth;
            int height = game.getInfo().worldHeight;
            int floors = game.getInfo().worldFloors;
            ParcelModel[][][] parcels = new ParcelModel[width][height][floors];
            List<ParcelModel> parcelsList = new ArrayList<>();

            try {
                SQLiteStatement st = db.prepare("SELECT x, y, z, ground, rock, plant, item, structure, consumable, liquid, liquid_value FROM WorldModule_parcel");
                SQLiteStatement stPlant = db.prepare("SELECT id, name, maturity, nourish, seed FROM WorldModule_plant WHERE id = ?");
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

                        // Ground
                        if (!st.columnNull(3)) {
                            parcel.setGroundInfo(Data.getData().getItemInfo(st.columnString(3)));
                        }

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
                                plant.setSeed(stPlant.columnInt(4) > 0);
                                plant.setMaturity(stPlant.columnDouble(2));
                                plant.setNourish(stPlant.columnDouble(3));
                                plant.setParcel(parcel);
                                parcel.setPlant(plant);
                            }
                            stPlant.reset(false);
                        }

                        // Structure
                        if (!st.columnNull(7)) {
                            int structureId = st.columnInt(7);
                            stStructure.bind(1, structureId);
                            if (stStructure.step()) {
                                StructureModel structure = new StructureModel(Data.getData().getItemInfo(stStructure.columnString(1)), structureId);
                                structure.setComplete(stStructure.columnInt(2) > 0);
                                structure.setParcel(parcel);
                                parcel.setStructure(structure);
                            }
                            stStructure.reset(false);
                        }

                        // Consumable
                        if (!st.columnNull(8)) {
                            int consumableId = st.columnInt(8);
                            stConsumable.bind(1, consumableId);
                            if (stConsumable.step()) {
                                ConsumableModel consumable = new ConsumableModel(Data.getData().getItemInfo(stConsumable.columnString(1)));
                                consumable.setId(consumableId);
                                consumable.setQuantity(stConsumable.columnInt(2));
                                consumable.setParcel(parcel);
                                parcel.setConsumable(consumable);
                            }
                            stConsumable.reset(false);
                        }

                        // Liquid
                        if (!st.columnNull(9)) {
                            parcel.setLiquidInfo(Data.getData().getItemInfo(st.columnString(9)), st.columnDouble(10));
                        }
                    }
                } finally {
                    st.dispose();
                    stConsumable.dispose();
                    stPlant.dispose();
                    stStructure.dispose();
                }

                WorldHelper.init(game.getInfo(), parcels);

                _worldModule.init(game, parcels, parcelsList);

                PathManager.getInstance().init(parcelsList);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        });
    }
}