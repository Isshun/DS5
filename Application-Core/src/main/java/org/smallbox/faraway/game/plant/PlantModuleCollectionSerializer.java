package org.smallbox.faraway.game.plant;

import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.GameSerializerPriority;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.save.GenericGameCollectionSerializer;
import org.smallbox.faraway.game.plant.model.PlantItem;
import org.smallbox.faraway.game.world.WorldModule;

import java.util.Collection;

@GameObject
public class PlantModuleCollectionSerializer extends GenericGameCollectionSerializer<PlantItem> {
    private final static String CREATE_TABLE_CMD = "CREATE TABLE WorldModule_plant (_id INTEGER, x INTEGER, y INTEGER, z INTEGER, grid_position INTEGER, name TEXT)";
    private final static String INSERT_CMD = "INSERT INTO WorldModule_plant (_id, x, y, z, grid_position, name) VALUES (?, ?, ?, ?, ?, ?)";
    private final static String SELECT_CMD = "SELECT _id, x, y, z, grid_position, name FROM WorldModule_plant";

    @Inject private DataManager dataManager;
    @Inject private WorldModule worldModule;
    @Inject private PlantModule plantModule;

    public PlantModuleCollectionSerializer() {
        super(CREATE_TABLE_CMD, INSERT_CMD, SELECT_CMD);
    }

    @Override
    public GameSerializerPriority getPriority() {
        return GameSerializerPriority.MODULE_ITEM_PRIORITY;
    }

    @Override
    public void onSaveEntry(SQLiteStatement statement, PlantItem item) throws SQLiteException {
        if (item.getParcel() != null) {
            statement.bind(1, item.getId());
            statement.bind(2, item.getParcel().x);
            statement.bind(3, item.getParcel().y);
            statement.bind(4, item.getParcel().z);
            statement.bind(5, item.getGridPosition());
            statement.bind(6, item.getInfo().name);
            statement.step();
        }
    }

    @Override
    public void onLoadEntry(SQLiteStatement statement) throws SQLiteException {
        ItemInfo itemInfo = dataManager.getItemInfo(statement.columnString(5));
        if (itemInfo != null) {
            PlantItem item = new PlantItem(itemInfo, statement.columnInt(0));
            item.setGridPosition(statement.columnInt(4));
            item.setParcel(worldModule.getParcel(statement.columnInt(1), statement.columnInt(2), statement.columnInt(3)));
            plantModule.add(item);
        }
    }

    @Override
    public Collection<PlantItem> getEntries() {
        return plantModule.getAll();
    }

}
