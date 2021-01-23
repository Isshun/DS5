package org.smallbox.faraway.modules.structure;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.GenericGameCollectionSerializer;
import org.smallbox.faraway.core.module.world.model.StructureItem;
import org.smallbox.faraway.util.Constant;

import java.util.Collection;

@GameObject
public class StructureModuleCollectionSerializer extends GenericGameCollectionSerializer<StructureItem> {
    @Inject private Data data;
    @Inject private StructureModule structureModule;

    @Override
    public int getModulePriority() { return Constant.MODULE_ITEM_PRIORITY; }

    @Override
    public void onCreateTable(SQLiteConnection db) throws SQLiteException {
        db.exec("CREATE TABLE WorldModule_structure (_id INTEGER, x INTEGER, y INTEGER, z INTEGER, name TEXT, buildProgress INTEGER)");
    }

    @Override
    public void onSaveEntry(SQLiteConnection db, StructureItem structure) throws SQLiteException {
        SQLiteStatement stItem = db.prepare("INSERT INTO WorldModule_structure (_id, x, y, z, name, buildProgress) VALUES (?, ?, ?, ?, ?, ?)");
        try {
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
        } finally {
            stItem.dispose();
        }
    }

    @Override
    public void onLoadEntry(SQLiteConnection db) throws SQLiteException {
        SQLiteStatement stItem = db.prepare("SELECT _id, x, y, z, name, buildProgress FROM WorldModule_structure");
        try {
            while (stItem.step()) {
                StructureItem structure = new StructureItem(data.getItemInfo(stItem.columnString(4)), stItem.columnInt(0));
                structure.setBuildProgress(stItem.columnInt(5));
                structureModule.addStructure(structure, stItem.columnInt(1), stItem.columnInt(2), stItem.columnInt(3));
            }
        } finally {
            stItem.dispose();
        }
    }

    @Override
    public Collection<StructureItem> getEntries() {
        return structureModule.getAll();
    }

}
