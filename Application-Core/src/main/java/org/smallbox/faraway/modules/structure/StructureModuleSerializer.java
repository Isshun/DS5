package org.smallbox.faraway.modules.structure;

import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameSerializer;
import org.smallbox.faraway.core.module.world.SQLManager;
import org.smallbox.faraway.core.module.world.model.StructureItem;
import org.smallbox.faraway.util.Constant;

/**
 * Created by Alex on 21/07/2016.
 */
public class StructureModuleSerializer extends GameSerializer<StructureModule> {

    @Override
    public int getModulePriority() { return Constant.MODULE_WORLD_PRIORITY; }

    @Override
    public void onSave(SQLManager sqlManager, StructureModule module, Game game) {
        sqlManager.post(db -> {
            try {
                db.exec("CREATE TABLE WorldModule_structure (_id INTEGER, x INTEGER, y INTEGER, z INTEGER, name TEXT, buildProgress INTEGER)");

                SQLiteStatement stItem = db.prepare("INSERT INTO WorldModule_structure (_id, x, y, z, name, buildProgress) VALUES (?, ?, ?, ?, ?, ?)");
                try {
                    db.exec("begin transaction");
                    for (StructureItem structure: module.getStructures()) {
                        if (structure.getParcel() != null) {
                            stItem.bind(1, structure.getId());
                            stItem.bind(2, structure.getParcel().x);
                            stItem.bind(3, structure.getParcel().y);
                            stItem.bind(4, structure.getParcel().z);
                            stItem.bind(5, structure.getInfo().name);
                            stItem.bind(6, structure.getBuildValue());
                            stItem.step();
                            stItem.reset(false);
                        }
                    }
                    db.exec("end transaction");
                } finally {
                    stItem.dispose();
                }
            } catch (SQLiteException e) {
                throw new GameException(StructureModuleSerializer.class, "Error during save");
            }
        });
    }

    public void onLoad(SQLManager sqlManager, StructureModule module, Game game, Data data) {
        sqlManager.post(db -> {
            try {
                SQLiteStatement stItem = db.prepare("SELECT _id, x, y, z, name, buildProgress FROM WorldModule_structure");
                try {
                    while (stItem.step()) {
                        StructureItem structure = new StructureItem(data.getItemInfo(stItem.columnString(4)), stItem.columnInt(0));
                        structure.setBuildProgress(stItem.columnInt(5));
                        module.addStructure(structure, stItem.columnInt(1), stItem.columnInt(2), stItem.columnInt(3));
                    }
                } finally {
                    stItem.dispose();
                }
            } catch (SQLiteException e) {
                throw new GameException(StructureModuleSerializer.class, "Error during load");
            }
        });
    }
}
