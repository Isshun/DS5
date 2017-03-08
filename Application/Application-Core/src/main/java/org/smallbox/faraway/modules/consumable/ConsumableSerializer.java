package org.smallbox.faraway.modules.consumable;

import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameSerializer;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.Log;

/**
 * Created by Alex on 21/07/2016.
 */
public class ConsumableSerializer extends GameSerializer<ConsumableModule> {

    @Override
    public int getModulePriority() { return Constant.MODULE_WORLD_PRIORITY; }

    @Override
    public void onSave(ConsumableModule module, Game game) {
        Application.sqlManager.post(db -> {
            try {
                db.exec("CREATE TABLE WorldModule_consumable (id INTEGER, x INTEGER, y INTEGER, z INTEGER, name TEXT, quantity INTEGER)");

                SQLiteStatement stItem = db.prepare("INSERT INTO WorldModule_consumable (id, x, y, z, name, quantity) VALUES (?, ?, ?, ?, ?, ?)");
                try {
                    db.exec("begin transaction");
                    module.getConsumables().forEach(consumable -> {
                        try {
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
                        } catch (SQLiteException e) {
                            e.printStackTrace();
                        }
                    });
                    db.exec("end transaction");
                } finally {
                    stItem.dispose();
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        });
    }

    public void onLoad(ConsumableModule module, Game game) {
        Application.sqlManager.post(db -> {
            try {
                SQLiteStatement stItem = db.prepare("SELECT id, x, y, z, name, quantity FROM WorldModule_consumable");
                try {
                    while (stItem.step()) {
                        ItemInfo itemInfo = Application.data.getItemInfo(stItem.columnString(4));
                        if (itemInfo != null) {
                            module.create(itemInfo, stItem.columnInt(5), stItem.columnInt(1), stItem.columnInt(2), stItem.columnInt(3));
                        }
                    }
                } finally {
                    stItem.dispose();
                }
            } catch (SQLiteException e) {
                Log.warning("Unable to read WorldModule_consumable table: " + e.getMessage());
            }
        });
    }
}
