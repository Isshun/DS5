package org.smallbox.faraway.module.structure;

import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.smallbox.faraway.core.data.serializer.GameSerializer;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.module.world.SQLHelper;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.StructureModel;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.module.world.WorldModule;

/**
 * Created by Alex on 21/07/2016.
 */
public class StructureModuleSerializer extends GameSerializer {
    private final StructureModule _structureModule;
    private final WorldModule _world;

    public StructureModuleSerializer(StructureModule structureModule, WorldModule world) {
        _structureModule = structureModule;
        _world = world;
    }

    @Override
    public int getModulePriority() { return Constant.MODULE_WORLD_PRIORITY; }

    @Override
    public void onSave(Game game) {
        SQLHelper.getInstance().post(db -> {
            try {
                db.exec("CREATE TABLE WorldModule_structure (id INTEGER, x INTEGER, y INTEGER, z INTEGER, name TEXT, buildProgress INTEGER)");

                SQLiteStatement stItem = db.prepare("INSERT INTO WorldModule_structure (id, x, y, z, name, buildProgress) VALUES (?, ?, ?, ?, ?, ?)");
                try {
                    db.exec("begin transaction");
                    _structureModule.getStructures().forEach(structure -> {
                        try {
                            if (structure.getParcel() != null) {
                                stItem.bind(1, structure.getId());
                                stItem.bind(2, structure.getParcel().x);
                                stItem.bind(3, structure.getParcel().y);
                                stItem.bind(4, structure.getParcel().z);
                                stItem.bind(5, structure.getInfo().name);
                                stItem.bind(6, structure.getBuildProgress());
                                stItem.step();
                                stItem.reset(false);
                            }
                        } catch (SQLiteException e) {
                            e.printStackTrace();
                        }
                    });
                    db.exec("end transaction");
                } finally {
                    stItem.dispose();
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        });
    }

    public void onLoad(Game game) {
        SQLHelper.getInstance().post(db -> {
            try {
                SQLiteStatement stItem = db.prepare("SELECT id, x, y, z, name, buildProgress FROM WorldModule_structure");
                try {
                    while (stItem.step()) {
                        ParcelModel parcel = _world.getParcel(stItem.columnInt(1), stItem.columnInt(2), stItem.columnInt(3));
                        if (parcel != null) {
                            StructureModel structure = new StructureModel(Data.getData().getItemInfo(stItem.columnString(4)), stItem.columnInt(0));
                            structure.setBuildProgress(stItem.columnInt(5));
                            structure.setParcel(parcel);
                            _structureModule.getStructures().add(structure);
                        }
                    }
                } finally {
                    stItem.dispose();
                }
            } catch (SQLiteException e) {
                Log.error(e);
            }
        });
    }
}
