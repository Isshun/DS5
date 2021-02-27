package org.smallbox.faraway.game.consumable;

import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameSerializerPriority;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.save.GenericGameCollectionSerializer;

import java.util.Collection;

@GameObject
public class ConsumableCollectionSerializer extends GenericGameCollectionSerializer<Consumable> {
    private final static String CREATE_TABLE_CMD = "CREATE TABLE WorldModule_consumable (_id INTEGER, x INTEGER, y INTEGER, z INTEGER, name TEXT, quantity INTEGER, position INTEGER)";
    private final static String SELECT_CONSUMABLE_CMD = "SELECT _id, x, y, z, name, quantity, position FROM WorldModule_consumable";
    private final static String INSERT_CONSUMABLE_CMD = "INSERT INTO WorldModule_consumable (_id, x, y, z, name, quantity, position) VALUES (?, ?, ?, ?, ?, ?, ?)";

    @Inject private Game game;
    @Inject private DataManager dataManager;
    @Inject private ConsumableModule consumableModule;

    public ConsumableCollectionSerializer() {
        super(CREATE_TABLE_CMD, INSERT_CONSUMABLE_CMD, SELECT_CONSUMABLE_CMD);
    }

    @Override
    public GameSerializerPriority getPriority() { return GameSerializerPriority.MODULE_ITEM_PRIORITY; }

    @Override
    public void onSaveEntry(SQLiteStatement statement, Consumable consumable) throws SQLiteException {
        statement.bind(1, consumable.getId());
        statement.bind(2, consumable.getParcel().x);
        statement.bind(3, consumable.getParcel().y);
        statement.bind(4, consumable.getParcel().z);
        statement.bind(5, consumable.getInfo().name);
        statement.bind(6, consumable.getActualQuantity());
        statement.bind(7, consumable.getGridPosition());
    }

    @Override
    public void onLoadEntry(SQLiteStatement statement) throws SQLiteException {
        ItemInfo itemInfo = dataManager.getItemInfo(statement.columnString(4));
        if (itemInfo != null) {
            consumableModule.create(itemInfo, statement.columnInt(5), statement.columnInt(1), statement.columnInt(2), statement.columnInt(3), statement.columnInt(6));
        }
    }

    @Override
    public Collection<Consumable> getEntries() {
        return consumableModule.getAll();
    }

}
