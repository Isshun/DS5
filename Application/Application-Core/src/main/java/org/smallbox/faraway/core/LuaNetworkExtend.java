package org.smallbox.faraway.core;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.common.DataAsyncListener;
import org.smallbox.faraway.common.ModuleBase;
import org.smallbox.faraway.common.lua.data.DataExtendException;
import org.smallbox.faraway.common.lua.data.LuaExtend;
import org.smallbox.faraway.common.modelInfo.ItemInfo;
import org.smallbox.faraway.common.modelInfo.NetworkInfo;

import java.io.File;
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
    public void extend(ModuleBase module, Globals globals, LuaValue value, File dataDirectory) throws DataExtendException {
        String name = getString(value, "name", null);

        NetworkInfo networkInfo;
        if (Application.data.hasNetwork(name)) {
            networkInfo = Application.data.getNetwork(name);
        } else {
            networkInfo = new NetworkInfo();
            Application.data.networks.add(networkInfo);
        }

        networkInfo.name = getString(value, "name", null);
        networkInfo.label = getString(value, "label", null);

        if (!value.get("items").isnil()) {
            networkInfo.items = new ArrayList<>();
            for (int i = 1; i <= value.get("items").length(); i++) {
                Application.data.getAsync(value.get("items").get(i).toString(), ItemInfo.class, itemInfo -> networkInfo.items.add(itemInfo));
            }
        }
    }

    @Override
    protected <T> void getAsync(String itemName, Class<T> cls, DataAsyncListener<T> dataAsyncListener) {
        Application.data.getAsync(itemName, cls, dataAsyncListener);
    }
}