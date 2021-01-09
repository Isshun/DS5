package org.smallbox.faraway.core.engine.module.lua.data.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.lua.data.DataExtendException;
import org.smallbox.faraway.core.engine.module.lua.data.LuaExtend;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.modelInfo.NetworkInfo;

import java.io.File;
import java.util.ArrayList;

@ApplicationObject
public class LuaNetworkExtend extends LuaExtend {

    @Override
    public boolean accept(String type) {
        return "network".equals(type);
    }

    @Override
    public void extend(Data data, ModuleBase module, Globals globals, LuaValue value, File dataDirectory) throws DataExtendException {
        String id = getString(value, "id", null);

        NetworkInfo networkInfo;
        if (data.hasNetwork(id)) {
            networkInfo = data.getNetwork(id);
        } else {
            networkInfo = new NetworkInfo();
            data.networks.add(networkInfo);
        }

        networkInfo.name = getString(value, "id", null);
        networkInfo.label = getString(value, "label", null);

        if (!value.get("items").isnil()) {
            networkInfo.items = new ArrayList<>();
            for (int i = 1; i <= value.get("items").length(); i++) {
                data.getAsync(value.get("items").get(i).toString(), ItemInfo.class, itemInfo -> networkInfo.items.add(itemInfo));
            }
        }
    }
}