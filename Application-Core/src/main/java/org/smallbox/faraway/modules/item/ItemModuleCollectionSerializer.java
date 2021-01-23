package org.smallbox.faraway.modules.item;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.GenericGameCollectionSerializer;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.util.Constant;

import java.util.Collection;

@GameObject
public class ItemModuleCollectionSerializer extends GenericGameCollectionSerializer<UsableItem> {
    @Inject private Data data;
    @Inject private ItemModule itemModule;

    @Override
    public int getModulePriority() { return Constant.MODULE_ITEM_PRIORITY; }

    @Override
    public void onCreateTable(SQLiteConnection db) throws SQLiteException {
        db.exec("CREATE TABLE WorldModule_item (_id INTEGER, x INTEGER, y INTEGER, z INTEGER, name TEXT, buildProgress INTEGER, health REAL)");
    }

    @Override
    public void onSaveEntry(SQLiteConnection db, UsableItem item) throws SQLiteException {
        SQLiteStatement insertStatement = db.prepare("INSERT INTO WorldModule_item (_id, x, y, z, name, buildProgress, health) VALUES (?, ?, ?, ?, ?, ?, ?)");
        try {
            if (item.getParcel() != null) {
                insertStatement.bind(1, item.getId());
                insertStatement.bind(2, item.getParcel().x);
                insertStatement.bind(3, item.getParcel().y);
                insertStatement.bind(4, item.getParcel().z);
                insertStatement.bind(5, item.getInfo().name);
                insertStatement.bind(6, item.getBuildValue());
                insertStatement.bind(7, item.getHealth());
                insertStatement.step();
            }
        } finally {
            insertStatement.dispose();
        }
    }

    @Override
    public void onLoadEntry(SQLiteConnection db) throws SQLiteException {
        SQLiteStatement selectStatement = db.prepare("SELECT _id, x, y, z, name, buildProgress, health FROM WorldModule_item");
        try {
            while (selectStatement.step()) {
                ItemInfo itemInfo = data.getItemInfo(selectStatement.columnString(4));
                if (itemInfo != null) {
                    UsableItem item = new UsableItem(itemInfo, selectStatement.columnInt(0));
                    item.setParcel(WorldHelper.getParcel(selectStatement.columnInt(1), selectStatement.columnInt(2), selectStatement.columnInt(3)));
                    item.setBuildProgress(selectStatement.columnInt(5));
                    item.setHealth(selectStatement.columnInt(6));
                    item.init();
                    itemModule.addItem(item);
                }
            }
        } finally {
            selectStatement.dispose();
        }
    }

    @Override
    public Collection<UsableItem> getEntries() {
        return itemModule.getAll();
    }

}
