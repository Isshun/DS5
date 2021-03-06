package org.smallbox.faraway.game.world;

import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameSerializer;
import org.smallbox.faraway.core.game.GameSerializerPriority;
import org.smallbox.faraway.core.game.model.MovableModel;
import org.smallbox.faraway.core.save.SQLManager;
import org.smallbox.faraway.util.GameException;

import java.util.ArrayList;
import java.util.List;

@GameObject
public class WorldModuleSerializer extends GameSerializer {
    @Inject private WorldModule worldModule;
    @Inject private DataManager dataManager;
    @Inject private Game game;

    @Override
    public GameSerializerPriority getPriority() { return GameSerializerPriority.MODULE_WORLD_PRIORITY; }

    @Override
    public void onSave(SQLManager sqlManager) {
        sqlManager.post(db -> {
            try {
                db.exec("CREATE TABLE WorldModule_parcel (x INTEGER, y INTEGER, z INTEGER, ground TEXT, rock TEXT, plant INTEGER, item INTEGER, structure INTEGER, consumable INTEGER, liquid TEXT, liquid_value REAL, ramp INTEGER)");
//                db.exec("CREATE TABLE WorldModule_structure (id INTEGER, name TEXT, buildProgress INTEGER)");
//                db.exec("CREATE TABLE WorldModule_consumable (id INTEGER, name TEXT, quantity INTEGER)");
//                db.exec("CREATE TABLE WorldModule_network (x INTEGER, y INTEGER, z INTEGER, ground TEXT, rock TEXT, plant TEXT, item TEXT, structure TEXT)");
                SQLiteStatement st = db.prepare("INSERT INTO WorldModule_parcel (x, y, z, ground, rock, plant, item, structure, consumable, liquid, liquid_value, ramp) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
//                SQLiteStatement stStructure = db.prepare("INSERT INTO WorldModule_structure (id, name, buildProgress) VALUES (?, ?, ?)");
//                SQLiteStatement stConsumable = db.prepare("INSERT INTO WorldModule_consumable (id, name, quantity) VALUES (?, ?, ?)");
                try {
                    db.exec("begin transaction");
                    for (Parcel parcel: worldModule.getAll()) {
                        saveParcel(st, parcel);
                    }
                    db.exec("end transaction");
                } finally {
                    st.dispose();
                }
            } catch (SQLiteException e) {
                throw new GameException(WorldModuleSerializer.class, "Error during save", e);
            }
        });
    }

    private void saveParcel(SQLiteStatement st, Parcel parcel) throws SQLiteException {
        st.bind(1, parcel.x);
        st.bind(2, parcel.y);
        st.bind(3, parcel.z);

        // Ground
        st.bind(4, parcel.hasGround() ? parcel.getGroundInfo().name : null);

        // Rock
        st.bind(5, parcel.hasRock() ? parcel.getRockInfo().name : null);

//                                // Plant
//                                if (parcel.hasPlant()) {
//                                    PlantItem plant = parcel.getPlant();
//                                    st.bind(6, plant.getId());
//                                    stPlant.bind(1, plant.getId());
//                                    stPlant.bind(2, plant.getInfo().name);
//                                    stPlant.bind(3, plant.getMaturity());
//                                    stPlant.bind(4, plant.getNourish());
//                                    stPlant.bind(5, plant.hasSeed() ? 1 : 0);
//                                    stPlant.step();
//                                    stPlant.reset(false);
//                                } else {
//                                    st.bindNull(6);
//                                }

//                                // Structure
//                                if (parcel.hasStructure()) {
//                                    StructureItem structure = parcel.getStructure();
//                                    st.bind(8, structure.getId());
//                                    stStructure.bind(1, structure.getId()).bind(2, structure.getInfo().name).bind(3, structure.isComplete() ? 1 : 0);
//                                    stStructure.step();
//                                    stStructure.reset(false);
//                                } else {
//                                    st.bindNull(8);
//                                }

//                                // Consumable
//                                if (parcel.hasConsumable()) {
//                                    ConsumableItem consumable = parcel.getConsumable();
//                                    st.bind(9, consumable.getId());
//                                    stConsumable.bind(1, consumable.getId()).bind(2, consumable.getInfo().name).bind(3, consumable.getQuantity());
//                                    stConsumable.step();
//                                    stConsumable.reset(false);
//                                } else {
//                                    st.bindNull(9);
//                                }


        // Liquid
        if (parcel.hasLiquid()) {
            st.bind(10, parcel.getLiquidInfo().name);
            st.bind(11, parcel.getLiquidValue());
        } else {
            st.bindNull(10);
        }

        st.bind(12, parcel.hasRamp() ? 1 : 0);

        st.step();
        st.reset(false);
    }

    public void onLoad(SQLManager sqlManager) {
        sqlManager.post(db -> {
            int width = game.getInfo().worldWidth;
            int height = game.getInfo().worldHeight;
            List<Parcel> parcelsList = new ArrayList<>();

            try {
                SQLiteStatement st = db.prepare("SELECT x, y, z, ground, rock, plant, item, structure, consumable, liquid, liquid_value, ramp FROM WorldModule_parcel");
                try {
                    while (st.step()) {
                        int x = st.columnInt(0);
                        int y = st.columnInt(1);
                        int z = st.columnInt(2);

                        Parcel parcel = new Parcel(x + (y * width) + (z * width * height), x, y, z);
                        parcelsList.add(parcel);

                        // Ground
                        if (!st.columnNull(3)) {
                            parcel.setGroundInfo(dataManager.getItemInfo(st.columnString(3)));
                        }

                        // Rock
                        if (!st.columnNull(4)) {
                            parcel.setRockInfo(dataManager.getItemInfo(st.columnString(4)));
                        }

//                        // Plant
//                        if (!st.columnNull(5)) {
//                            int plantId = st.columnInt(5);
//                            stPlant.bind(1, plantId);
//                            if (stPlant.step()) {
//                                PlantItem plant = new PlantItem(Application.data.getItemInfo(stPlant.columnString(1)), plantId);
//                                plant.setSeed(stPlant.columnInt(4) > 0);
//                                plant.setMaturity(stPlant.columnDouble(2));
//                                plant.setNourish(stPlant.columnDouble(3));
//                                plant.setParcel(parcel);
//                                parcel.setPlant(plant);
//                            }
//                            stPlant.reset(false);
//                        }

                        // Liquid
                        if (!st.columnNull(9)) {
                            parcel.setLiquidInfo(dataManager.getItemInfo(st.columnString(9)), st.columnDouble(10));
                        }

                        // Ramp
                        if (st.columnInt(11) == 1) {
                            parcel.setRamp(MovableModel.Direction.LEFT);
                        }

                    }
                } finally {
                    st.dispose();
//                    stConsumable.dispose();
//                    stStructure.dispose();
                }

                worldModule.init(parcelsList);
            } catch (SQLiteException e) {
                throw new GameException(WorldModuleSerializer.class, "Error during load");
            }
        });
    }
}