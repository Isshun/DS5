package org.smallbox.faraway.client;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.smallbox.faraway.client.lua.LuaUIBridge;
import org.smallbox.faraway.client.ui.engine.RawColors;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.lua.LuaDataModel;
import org.smallbox.faraway.core.engine.module.lua.LuaModuleManager;
import org.smallbox.faraway.core.engine.module.lua.luaModel.LuaApplicationModel;
import org.smallbox.faraway.core.engine.module.lua.luaModel.LuaEventsModel;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfig;

import java.io.File;

@ApplicationObject
public class ClientLuaModuleManager extends LuaModuleManager {

    @Inject
    private ApplicationConfig applicationConfig;

    @Inject
    private Data data;

    @Override
    protected Globals createGlobals(ModuleBase module, File dataDirectory) {
        Globals globals = JsePlatform.standardGlobals();

        globals.load("function main(a, u, d)\n" +
                        "application = a\n" +
                        "data = d\n" +
                        "ui = u\n" +

                        "blue_light_1 = " + RawColors.RAW_BLUE_LIGHT_1 + "\n" +
                        "blue_light_2 = " + RawColors.RAW_BLUE_LIGHT_2 + "\n" +
                        "blue_light_3 = " + RawColors.RAW_BLUE_LIGHT_3 + "\n" +
                        "blue_light_4 = " + RawColors.RAW_BLUE_LIGHT_4 + "\n" +
                        "blue_light_5 = " + RawColors.RAW_BLUE_LIGHT_5 + "\n" +

                        "blue_dark_1 = " + RawColors.RAW_BLUE_DARK_1 + "\n" +
                        "blue_dark_2 = " + RawColors.RAW_BLUE_DARK_2 + "\n" +
                        "blue_dark_3 = " + RawColors.RAW_BLUE_DARK_3 + "\n" +
                        "blue_dark_4 = " + RawColors.RAW_BLUE_DARK_4 + "\n" +
                        "blue_dark_5 = " + RawColors.RAW_BLUE_DARK_5 + "\n" +

                        "red_light_1 = " + RawColors.RAW_RED_LIGHT_1 + "\n" +
                        "red_light_2 = " + RawColors.RAW_RED_LIGHT_2 + "\n" +
                        "red_light_3 = " + RawColors.RAW_RED_LIGHT_3 + "\n" +
                        "red_light_4 = " + RawColors.RAW_RED_LIGHT_4 + "\n" +
                        "red_light_5 = " + RawColors.RAW_RED_LIGHT_5 + "\n" +

                        "red_dark_1 = " + RawColors.RAW_RED_DARK_1 + "\n" +
                        "red_dark_2 = " + RawColors.RAW_RED_DARK_2 + "\n" +
                        "red_dark_3 = " + RawColors.RAW_RED_DARK_3 + "\n" +
                        "red_dark_4 = " + RawColors.RAW_RED_DARK_4 + "\n" +
                        "red_dark_5 = " + RawColors.RAW_RED_DARK_5 + "\n" +

                        "math.round = function(num, idp)\n" +
                        "local mult = 10^(idp or 0)\n" +
                        "return math.floor(num * mult + 0.5) / mult\n" +
                        "end end",
                "main").call();

        globals.get("main").call(
                CoerceJavaToLua.coerce(new LuaApplicationModel(null, new LuaEventsModel(), applicationConfig.screen)),
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
                CoerceJavaToLua.coerce(new LuaDataModel(data) {
                    @Override
                    public void extend(LuaValue values) {
                    }
                }));

        return globals;
    }

}
