package org.smallbox.faraway.game.item;

import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.GameSerializerPriority;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.save.GenericGameCollectionSerializer;
import org.smallbox.faraway.game.world.WorldHelper;

import java.util.Collection;

@GameObject
public class ItemModuleCollectionSerializer extends GenericGameCollectionSerializer<UsableItem> {
    private final static String CREATE_TABLE_CMD = "CREATE TABLE WorldModule_item (_id INTEGER, x INTEGER, y INTEGER, z INTEGER, name TEXT, buildProgress INTEGER, health REAL)";
    private final static String INSERT_CMD = "INSERT INTO WorldModule_item (_id, x, y, z, name, buildProgress, health) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private final static String SELECT_CMD = "SELECT _id, x, y, z, name, buildProgress, health FROM WorldModule_item";

    @Inject private DataManager dataManager;
    @Inject private ItemModule itemModule;

    public ItemModuleCollectionSerializer() {
        super(CREATE_TABLE_CMD, INSERT_CMD, SELECT_CMD);
    }

    @Override
    public GameSerializerPriority getPriority() {
        return GameSerializerPriority.MODULE_ITEM_PRIORITY;
    }

    @Override
    public void onSaveEntry(SQLiteStatement statement, UsableItem item) throws SQLiteException {
        if (item.getParcel() != null) {
            statement.bind(1, item.getId());
            statement.bind(2, item.getParcel().x);
            statement.bind(3, item.getParcel().y);
            statement.bind(4, item.getParcel().z);
            statement.bind(5, item.getInfo().name);
            statement.bind(6, item.getBuildValue());
            statement.bind(7, item.getHealth());
        }
    }

    @Override
    public void onLoadEntry(SQLiteStatement statement) throws SQLiteException {
        ItemInfo itemInfo = dataManager.getItemInfo(statement.columnString(4));
        if (itemInfo != null) {
            UsableItem item = new UsableItem(itemInfo, statement.columnInt(0));
            item.setParcel(WorldHelper.getParcel(statement.columnInt(1), statement.columnInt(2), statement.columnInt(3)));
            item.setBuildProgress(statement.columnInt(5));
            item.setHealth(statement.columnInt(6));
            item.init();
            itemModule.addItem(item);
        }
    }

    @Override
    public Collection<UsableItem> getEntries() {
        return itemModule.getAll();
    }

}
