package org.smallbox.faraway.module.item;

import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.GameSerializer;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.Log;

/**
 * Created by Alex on 21/07/2016.
 */
public class ItemModuleSerializer extends GameSerializer<ItemModule> {

    @Override
    public int getModulePriority() { return Constant.MODULE_WORLD_PRIORITY; }

    @Override
    public void onSave(ItemModule module, Game game) {
        Application.sqlManager.post(db -> {
            try {
                db.exec("CREATE TABLE WorldModule_item (id INTEGER, x INTEGER, y INTEGER, z INTEGER, name TEXT, buildProgress INTEGER)");

                SQLiteStatement stItem = db.prepare("INSERT INTO WorldModule_item (id, x, y, z, name, buildProgress) VALUES (?, ?, ?, ?, ?, ?)");
                try {
                    db.exec("begin transaction");
                    module.getItems().forEach(item -> {
                        try {
                            if (item.getParcel() != null) {
                                stItem.bind(1, item.getId());
                                stItem.bind(2, item.getParcel().x);
                                stItem.bind(3, item.getParcel().y);
                                stItem.bind(4, item.getParcel().z);
                                stItem.bind(5, item.getInfo().name);
                                stItem.bind(6, item.getBuildProgress());
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

    public void onLoad(ItemModule itemModule, Game game) {
        Application.sqlManager.post(db -> {
            try {
                SQLiteStatement stItem = db.prepare("SELECT id, x, y, z, name, buildProgress FROM WorldModule_item");
                try {
                    while (stItem.step()) {
                        ItemInfo itemInfo = Application.data.getItemInfo(stItem.columnString(4));
                        if (itemInfo != null) {
                            ItemModel item = new ItemModel(itemInfo, stItem.columnInt(0));
                            item.setParcel(WorldHelper.getParcel(stItem.columnInt(1), stItem.columnInt(2), stItem.columnInt(3)));
                            item.setBuildProgress(stItem.columnInt(5));
                            item.init();
                            itemModule.addItem(item);
                        }
                    }
                } finally {
                    stItem.dispose();
                }
            } catch (SQLiteException e) {
                Log.warning("Unable to read WorldModule_item table: " + e.getMessage());
            }
        });
    }
}
