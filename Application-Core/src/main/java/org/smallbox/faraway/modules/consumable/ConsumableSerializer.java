package org.smallbox.faraway.modules.consumable;

import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameSerializer;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.SQLManager;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.util.Constant;

public class ConsumableSerializer extends GameSerializer<ConsumableModule> {

    @Override
    public int getModulePriority() { return Constant.MODULE_WORLD_PRIORITY; }

    @Override
    public void onSave(SQLManager sqlManager, ConsumableModule module, Game game) {
        sqlManager.post(db -> {
            try {
                db.exec("CREATE TABLE WorldModule_consumable (_id INTEGER, x INTEGER, y INTEGER, z INTEGER, name TEXT, quantity INTEGER)");

                SQLiteStatement stItem = db.prepare("INSERT INTO WorldModule_consumable (_id, x, y, z, name, quantity) VALUES (?, ?, ?, ?, ?, ?)");
                try {
                    db.exec("begin transaction");
                    for (ConsumableItem consumable: module.getConsumables()) {
                        if (consumable.getParcel() != null) {
                            stItem.bind(1, consumable.getId());
                            stItem.bind(2, consumable.getParcel().x);
                            stItem.bind(3, consumable.getParcel().y);
                            stItem.bind(4, consumable.getParcel().z);
                            stItem.bind(5, consumable.getInfo().name);
                            stItem.bind(6, consumable.getFreeQuantity());
                            stItem.step();
                            stItem.reset(false);
                        }
                    }
                    db.exec("end transaction");
                } finally {
                    stItem.dispose();
                }
            } catch (SQLiteException e) {
                throw new GameException(ConsumableSerializer.class, "Error during save");
            }
        });
    }

    public void onLoad(SQLManager sqlManager, ConsumableModule module, Game game, Data data) {
        sqlManager.post(db -> {
            try {
                SQLiteStatement stItem = db.prepare("SELECT _id, x, y, z, name, quantity FROM WorldModule_consumable");
                try {
                    while (stItem.step()) {
                        ItemInfo itemInfo = data.getItemInfo(stItem.columnString(4));
                        if (itemInfo != null) {
                            module.create(itemInfo, stItem.columnInt(5), stItem.columnInt(1), stItem.columnInt(2), stItem.columnInt(3));
                        }
                    }
                } finally {
                    stItem.dispose();
                }
            } catch (SQLiteException e) {
                throw new GameException(ConsumableSerializer.class, "Error during load");
            }
        });
    }
}
