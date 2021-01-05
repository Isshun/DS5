package org.smallbox.faraway.modules.consumable;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GenericGameSerializer;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.util.Constant;

import java.util.Collection;

@GameObject
public class ConsumableSerializer extends GenericGameSerializer<ConsumableItem> {

    @Inject
    private Game game;

    @Inject
    private Data data;

    @Inject
    private ConsumableModule consumableModule;

    @Override
    public int getModulePriority() { return Constant.MODULE_ITEM_PRIORITY; }

    @Override
    public void onCreateTable(SQLiteConnection db) throws SQLiteException {
        db.exec("CREATE TABLE WorldModule_consumable (_id INTEGER, x INTEGER, y INTEGER, z INTEGER, name TEXT, quantity INTEGER)");
    }

    @Override
    public void onSaveEntry(SQLiteConnection db, ConsumableItem consumable) throws SQLiteException {
        SQLiteStatement stItem = db.prepare("INSERT INTO WorldModule_consumable (_id, x, y, z, name, quantity) VALUES (?, ?, ?, ?, ?, ?)");
        try {
            if (consumable.getParcel() != null) {
                stItem.bind(1, consumable.getId());
                stItem.bind(2, consumable.getParcel().x);
                stItem.bind(3, consumable.getParcel().y);
                stItem.bind(4, consumable.getParcel().z);
                stItem.bind(5, consumable.getInfo().name);
                stItem.bind(6, consumable.getFreeQuantity());
                stItem.step();
            }
        } finally {
            stItem.dispose();
        }
    }

    @Override
    public void onLoadEntry(SQLiteConnection db) throws SQLiteException {
        SQLiteStatement stItem = db.prepare("SELECT _id, x, y, z, name, quantity FROM WorldModule_consumable");
        try {
            while (stItem.step()) {
                ItemInfo itemInfo = data.getItemInfo(stItem.columnString(4));
                if (itemInfo != null) {
                    consumableModule.create(itemInfo, stItem.columnInt(5), stItem.columnInt(1), stItem.columnInt(2), stItem.columnInt(3));
                }
            }
        } finally {
            stItem.dispose();
        }
    }

    @Override
    public Collection<ConsumableItem> getEntries() {
        return consumableModule.getAll();
    }

}
