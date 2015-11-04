package org.smallbox.faraway.core.module.lua.data.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.GraphicInfo;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.model.NetworkInfo;
import org.smallbox.faraway.core.module.lua.DataExtendException;
import org.smallbox.faraway.core.module.lua.LuaModule;
import org.smallbox.faraway.core.module.lua.LuaModuleManager;
import org.smallbox.faraway.core.module.lua.data.LuaExtend;

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
        NetworkInfo networkInfo = Data.getData().getNetwork(getString(value, "name", null));

        if (networkInfo == null) {
            networkInfo = new NetworkInfo();
            Data.getData().networks.add(networkInfo);
        }

        networkInfo.name = getString(value, "name", null);
        networkInfo.label = getString(value, "label", null);

        LuaValue luaGraphics = value.get("graphics");
        if (!luaGraphics.isnil()) {
            networkInfo.graphics = readGraphic(luaGraphics);
        } else {
            networkInfo.graphics = new GraphicInfo("base", "/graphics/missing.png");
        }
    }

    private GraphicInfo readGraphic(LuaValue luaGraphic) throws DataExtendException {
        GraphicInfo graphicInfo;
        if (!luaGraphic.get("path").isnil()) {
            String path = luaGraphic.get("path").toString();
            graphicInfo = new GraphicInfo(
                    path.substring(1, path.indexOf(']')),
                    path.substring(path.indexOf(']') + 1, path.length())
            );
        } else {
            throw new DataExtendException(DataExtendException.Type.MANDATORY, "graphics.path");
        }
        if (!luaGraphic.get("type").isnil()) {
            graphicInfo.type = GraphicInfo.Type.valueOf(luaGraphic.get("type").toString().toUpperCase());
        }
        if (!luaGraphic.get("x").isnil()) {
            graphicInfo.x = luaGraphic.get("x").toint();
        }
        if (!luaGraphic.get("y").isnil()) {
            graphicInfo.y = luaGraphic.get("y").toint();
        }
        return graphicInfo;
    }
}