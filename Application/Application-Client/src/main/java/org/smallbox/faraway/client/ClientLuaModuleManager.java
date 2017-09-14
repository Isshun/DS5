package org.smallbox.faraway.client;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.smallbox.faraway.client.lua.LuaUIBridge;
import org.smallbox.faraway.common.ModuleBase;
import org.smallbox.faraway.common.dependencyInjector.ApplicationObject;
import org.smallbox.faraway.common.lua.LuaDataModel;
import org.smallbox.faraway.common.lua.LuaModuleManager;

import java.io.File;

/**
 * Created by Alex on 29/11/2016.
 */
@ApplicationObject
public class ClientLuaModuleManager extends LuaModuleManager {

    @Override
    protected Globals createGlobals(ModuleBase module, File dataDirectory) {
        Globals globals = JsePlatform.standardGlobals();
        globals.load("function main(a, u, d)\n" +
                        "application = a\n" +
                        "data = d\n" +
                        "ui = u\n" +
                        "color1 = 0x2ab8baff\n" +
                        "color2 = 0x9afbffff\n" +
                        "color3 = 0x132733ff\n" +
                        "math.round = function(num, idp)\n" +
                        "local mult = 10^(idp or 0)\n" +
                        "return math.floor(num * mult + 0.5) / mult\n" +
                        "end end",
                "main").call();
        globals.get("main").call(
//                CoerceJavaToLua.coerce(new LuaApplicationModel(null, new LuaEventsModel())),
                null,
                CoerceJavaToLua.coerce(new LuaUIBridge(null) {
                    @Override
                    public void extend(LuaValue values) {
                        if (!values.get("type").isnil()) {
                            extendLuaValue(module, values, globals, dataDirectory);
                        } else {
                            for (int i = 1; i <= values.length(); i++) {
                                extendLuaValue(module, values.get(i), globals, dataDirectory);
                            }
                        }
                    }
                }),
                CoerceJavaToLua.coerce(new LuaDataModel() {
                    @Override
                    public void extend(LuaValue values) {
                    }
                }));

        return globals;
    }

}
