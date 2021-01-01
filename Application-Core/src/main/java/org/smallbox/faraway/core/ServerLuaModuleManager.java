package org.smallbox.faraway.core;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.lua.LuaDataModel;
import org.smallbox.faraway.core.engine.module.lua.LuaExtendInterface;
import org.smallbox.faraway.core.engine.module.lua.LuaModuleManager;
import org.smallbox.faraway.core.engine.module.lua.luaModel.LuaApplicationModel;
import org.smallbox.faraway.core.engine.module.lua.luaModel.LuaEventsModel;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfig;

import java.io.File;

@ApplicationObject
public class ServerLuaModuleManager extends LuaModuleManager {

    @Inject
    private ApplicationConfig applicationConfig;

    @Inject
    private Data data;

    @Override
    protected Globals createGlobals(ModuleBase module, File dataDirectory) {
        Globals globals = JsePlatform.standardGlobals();
        globals.load("function main(a, u, d)\n application = a\n data = d\n ui = u\n math.round = function(num, idp)\n local mult = 10^(idp or 0)\n return math.floor(num * mult + 0.5) / mult\n end end", "main").call();
        globals.get("main").call(
                CoerceJavaToLua.coerce(new LuaApplicationModel(null, new LuaEventsModel(), applicationConfig.screen)),
                CoerceJavaToLua.coerce((LuaExtendInterface) values -> {}),
                CoerceJavaToLua.coerce(new LuaDataModel(data) {
                    @Override
                    public void extend(LuaValue values) {
//                        Log.debug("Load lua data: " + values.get("name").toString());
                        if (!values.get("type").isnil()) {
                            extendLuaValue(module, values, globals, dataDirectory);
                        } else {
                            for (int i = 1; i <= values.length(); i++) {
                                extendLuaValue(module, values.get(i), globals, dataDirectory);
                            }
                        }
                    }
                }));

        return globals;
    }
}
