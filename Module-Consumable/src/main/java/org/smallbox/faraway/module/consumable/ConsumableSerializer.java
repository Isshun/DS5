package org.smallbox.faraway.module.consumable;

import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.smallbox.faraway.core.data.serializer.GameSerializer;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.module.world.SQLHelper;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.module.world.WorldModule;

/**
 * Created by Alex on 21/07/2016.
 */
public class ConsumableSerializer extends GameSerializer {
    private final ConsumableModule _consumableModule;
    private final WorldModule _world;

    public ConsumableSerializer(ConsumableModule consumableModule, WorldModule world) {
        _consumableModule = consumableModule;
        _world = world;
    }

    @Override
    public int getModulePriority() { return Constant.MODULE_WORLD_PRIORITY; }

    @Override
    public void onSave(Game game) {
        SQLHelper.getInstance().post(db -> {
            try {
                db.exec("CREATE TABLE WorldModule_consumable (id INTEGER, x INTEGER, y INTEGER, z INTEGER, name TEXT, quantity INTEGER)");

                SQLiteStatement stItem = db.prepare("INSERT INTO WorldModule_consumable (id, x, y, z, name, quantity) VALUES (?, ?, ?, ?, ?, ?)");
                try {
                    db.exec("begin transaction");
                    _consumableModule.getConsumables().forEach(consumable -> {
                        try {
                            if (consumable.getParcel() != null) {
                                stItem.bind(1, consumable.getId());
                                stItem.bind(2, consumable.getParcel().x);
                                stItem.bind(3, consumable.getParcel().y);
                                stItem.bind(4, consumable.getParcel().z);
                                stItem.bind(5, consumable.getInfo().name);
                                stItem.bind(6, consumable.getQuantity());
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
                SQLiteStatement stItem = db.prepare("SELECT id, x, y, z, name, quantity FROM WorldModule_consumable");
                try {
                    while (stItem.step()) {
                        ParcelModel parcel = _world.getParcel(stItem.columnInt(1), stItem.columnInt(2), stItem.columnInt(3));
                        if (parcel != null) {
                            ItemInfo itemInfo = Data.getData().getItemInfo(stItem.columnString(4));
                            if (itemInfo != null) {
                                _consumableModule.create(itemInfo, stItem.columnInt(5), parcel);
                            }
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
