package org.smallbox.faraway.core.engine.module.lua.data.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.engine.module.lua.DataExtendException;
import org.smallbox.faraway.core.engine.module.lua.LuaModule;
import org.smallbox.faraway.core.engine.module.lua.LuaModuleManager;
import org.smallbox.faraway.core.engine.module.lua.data.LuaExtend;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.model.NetworkInfo;

import java.util.ArrayList;

/**
 * Created by Alex on 04/11/2015.
 */
public class LuaNetworkExtend extends LuaExtend {
    @Override
    public boolean accept(String type) {
        return "network".equals(type);
    }

    @Override
    public void extend(LuaModuleManager luaModuleManager, LuaModule module, Globals globals, LuaValue value) throws DataExtendException {
        String name = getString(value, "name", null);

        NetworkInfo networkInfo;
        if (Data.getData().hasNetwork(name)) {
            networkInfo = Data.getData().getNetwork(name);
        } else {
            networkInfo = new NetworkInfo();
            Data.getData().networks.add(networkInfo);
        }

        networkInfo.name = getString(value, "name", null);
        networkInfo.label = getString(value, "label", null);

        if (!value.get("items").isnil()) {
            networkInfo.itemNames = new ArrayList<>();
            for (int i = 1; i <= value.get("items").length(); i++) {
                networkInfo.itemNames.add(value.get("items").get(i).toString());
            }
        }
    }
}