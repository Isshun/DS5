package org.smallbox.faraway.modules.item;

import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameSerializer;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.SQLManager;
import org.smallbox.faraway.util.Constant;

@GameObject
public class ItemModuleSerializer extends GameSerializer {

    @Inject
    private Game game;

    @Inject
    private Data data;

    @Inject
    private ItemModule module;

    @Override
    public int getModulePriority() { return Constant.MODULE_ITEM_PRIORITY; }

    @Override
    public void onSave(SQLManager sqlManager) {
        sqlManager.post(db -> {
            try {
                db.exec("CREATE TABLE WorldModule_item (_id INTEGER, x INTEGER, y INTEGER, z INTEGER, name TEXT, buildProgress INTEGER, health REAL)");

                SQLiteStatement stItem = db.prepare("INSERT INTO WorldModule_item (_id, x, y, z, name, buildProgress, health) VALUES (?, ?, ?, ?, ?, ?, ?)");
                try {
                    db.exec("begin transaction");
                    for (UsableItem item: module.getAll()) {
                        if (item.getParcel() != null) {
                            stItem.bind(1, item.getId());
                            stItem.bind(2, item.getParcel().x);
                            stItem.bind(3, item.getParcel().y);
                            stItem.bind(4, item.getParcel().z);
                            stItem.bind(5, item.getInfo().name);
                            stItem.bind(6, item.getBuildValue());
                            stItem.bind(7, item.getHealth());
                            stItem.step();
                            stItem.reset(false);
                        }
                    }
                    db.exec("end transaction");
                } finally {
                    stItem.dispose();
                }
            } catch (SQLiteException e) {
                throw new GameException(ItemModuleSerializer.class, "Error during save");
            }
        });
    }

    public void onLoad(SQLManager sqlManager) {
        sqlManager.post(db -> {
            try {
                SQLiteStatement stItem = db.prepare("SELECT _id, x, y, z, name, buildProgress, health FROM WorldModule_item");
                try {
                    while (stItem.step()) {
                        ItemInfo itemInfo = data.getItemInfo(stItem.columnString(4));
                        if (itemInfo != null) {
                            UsableItem item = new UsableItem(itemInfo, stItem.columnInt(0));
                            item.setParcel(WorldHelper.getParcel(stItem.columnInt(1), stItem.columnInt(2), stItem.columnInt(3)));
                            item.setBuildProgress(stItem.columnInt(5));
                            item.setHealth(stItem.columnInt(6));
                            item.init();
                            module.addItem(item);
                        }
                    }
                } finally {
                    stItem.dispose();
                }
            } catch (SQLiteException e) {
                throw new GameException(ItemModuleSerializer.class, "Error during load");
            }
        });
    }
}
