package org.smallbox.faraway.game.structure;

import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.GameSerializerPriority;
import org.smallbox.faraway.core.save.GenericGameCollectionSerializer;

import java.util.Collection;

@GameObject
public class StructureModuleCollectionSerializer extends GenericGameCollectionSerializer<StructureItem> {
    private final static String CREATE_TABLE_CMD = "CREATE TABLE WorldModule_structure (_id INTEGER, x INTEGER, y INTEGER, z INTEGER, name TEXT, buildProgress INTEGER)";
    private final static String INSERT_CMD = "INSERT INTO WorldModule_structure (_id, x, y, z, name, buildProgress) VALUES (?, ?, ?, ?, ?, ?)";
    private final static String SELECT_CMD = "SELECT _id, x, y, z, name, buildProgress FROM WorldModule_structure";

    @Inject private StructureModule structureModule;
    @Inject private DataManager dataManager;

    public StructureModuleCollectionSerializer() {
        super(CREATE_TABLE_CMD, INSERT_CMD, SELECT_CMD);
    }

    @Override
    public GameSerializerPriority getPriority() {
        return GameSerializerPriority.MODULE_ITEM_PRIORITY;
    }

    @Override
    public void onSaveEntry(SQLiteStatement statement, StructureItem structure) throws SQLiteException {
        if (structure.getParcel() != null) {
            statement.bind(1, structure.getId());
            statement.bind(2, structure.getParcel().x);
            statement.bind(3, structure.getParcel().y);
            statement.bind(4, structure.getParcel().z);
            statement.bind(5, structure.getInfo().name);
            statement.bind(6, structure.getBuildValue());
            statement.step();
            statement.reset(false);
        }
    }

    @Override
    public void onLoadEntry(SQLiteStatement statement) throws SQLiteException {
        StructureItem structure = new StructureItem(dataManager.getItemInfo(statement.columnString(4)), statement.columnInt(0));
        structure.setBuildProgress(statement.columnInt(5));
        structureModule.addStructure(structure, statement.columnInt(1), statement.columnInt(2), statement.columnInt(3));
    }

    @Override
    public Collection<StructureItem> getEntries() {
        return structureModule.getAll();
    }

}
