package org.smallbox.faraway.module.item;

import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.smallbox.faraway.core.data.serializer.GameSerializer;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.module.world.SQLHelper;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.module.item.item.ItemModel;
import org.smallbox.faraway.module.world.WorldModule;

/**
 * Created by Alex on 21/07/2016.
 */
public class ItemModuleSerializer extends GameSerializer {
    private final ItemModule _itemModule;
    private final WorldModule _world;

    public ItemModuleSerializer(ItemModule itemModule, WorldModule world) {
        _itemModule = itemModule;
        _world = world;
    }

    @Override
    public int getModulePriority() { return Constant.MODULE_WORLD_PRIORITY; }

    @Override
    public void onSave(Game game) {
        SQLHelper.getInstance().post(db -> {
            try {
                db.exec("CREATE TABLE WorldModule_item (id INTEGER, x INTEGER, y INTEGER, z INTEGER, name TEXT, buildProgress INTEGER)");

                SQLiteStatement stItem = db.prepare("INSERT INTO WorldModule_item (id, x, y, z, name, buildProgress) VALUES (?, ?, ?, ?, ?, ?)");
                try {
                    db.exec("begin transaction");
                    _itemModule.getItems().forEach(item -> {
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

    public void onLoad(Game game) {
        SQLHelper.getInstance().post(db -> {
            try {
                SQLiteStatement stItem = db.prepare("SELECT id, x, y, z, name, buildProgress FROM WorldModule_item");
                try {
                    while (stItem.step()) {
                        ParcelModel parcel = _world.getParcel(stItem.columnInt(1), stItem.columnInt(2), stItem.columnInt(3));
                        if (parcel != null) {
                            ItemModel item = new ItemModel(Data.getData().getItemInfo(stItem.columnString(4)), parcel, stItem.columnInt(0));
                            item.setBuildProgress(stItem.columnInt(5));
                            item.setParcel(parcel);
                            _itemModule.getItems().add(item);
                        }
                    }
                } finally {
                    stItem.dispose();
                }
            } catch (SQLiteException e) {
                Log.error(e);
            }
        });
    }
}
