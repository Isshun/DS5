package org.smallbox.faraway.game.plant;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.save.GenericGameCollectionSerializer;
import org.smallbox.faraway.game.plant.model.PlantItem;
import org.smallbox.faraway.game.world.WorldModule;
import org.smallbox.faraway.util.Constant;

import java.util.Collection;

@GameObject
public class PlantModuleCollectionSerializer extends GenericGameCollectionSerializer<PlantItem> {
    @Inject private DataManager dataManager;
    @Inject private WorldModule worldModule;
    @Inject private PlantModule plantModule;

    @Override
    public int getModulePriority() { return Constant.MODULE_ITEM_PRIORITY; }

    @Override
    public void onCreateTable(SQLiteConnection db) throws SQLiteException {
        db.exec("CREATE TABLE WorldModule_plant (_id INTEGER, x INTEGER, y INTEGER, z INTEGER, grid_position INTEGER, name TEXT)");
    }

    @Override
    public void onSaveEntry(SQLiteConnection db, PlantItem item) throws SQLiteException {
        SQLiteStatement insertStatement = db.prepare("INSERT INTO WorldModule_plant (_id, x, y, z, grid_position, name) VALUES (?, ?, ?, ?, ?, ?)");
        try {
            if (item.getParcel() != null) {
                insertStatement.bind(1, item.getId());
                insertStatement.bind(2, item.getParcel().x);
                insertStatement.bind(3, item.getParcel().y);
                insertStatement.bind(4, item.getParcel().z);
                insertStatement.bind(5, item.getGridPosition());
                insertStatement.bind(6, item.getInfo().name);
                insertStatement.step();
            }
        } finally {
            insertStatement.dispose();
        }
    }

    @Override
    public void onLoadEntry(SQLiteConnection db) throws SQLiteException {
        SQLiteStatement selectStatement = db.prepare("SELECT _id, x, y, z, grid_position, name FROM WorldModule_plant");
        try {
            while (selectStatement.step()) {
                ItemInfo itemInfo = dataManager.getItemInfo(selectStatement.columnString(5));
                if (itemInfo != null) {
                    PlantItem item = new PlantItem(itemInfo, selectStatement.columnInt(0));
                    item.setGridPosition(selectStatement.columnInt(4));
                    item.setParcel(worldModule.getParcel(selectStatement.columnInt(1), selectStatement.columnInt(2), selectStatement.columnInt(3)));
                    plantModule.add(item);
                }
            }
        } finally {
            selectStatement.dispose();
        }
    }

    @Override
    public Collection<PlantItem> getEntries() {
        return plantModule.getAll();
    }

}
