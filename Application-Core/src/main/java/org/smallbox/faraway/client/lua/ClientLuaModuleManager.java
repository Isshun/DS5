package org.smallbox.faraway.client.lua;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.smallbox.faraway.client.lua.extend.LuaUIExtend;
import org.smallbox.faraway.client.ui.extra.RawColors;
import org.smallbox.faraway.client.ui.widgets.CompositeView;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.util.GameException;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.module.ModuleBase;
import org.smallbox.faraway.core.lua.LuaDataModel;
import org.smallbox.faraway.core.lua.LuaExtendInterface;
import org.smallbox.faraway.core.lua.LuaModuleManager;
import org.smallbox.faraway.core.lua.luaModel.LuaApplicationModel;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.util.log.Log;

import java.io.File;
import java.util.Optional;

@ApplicationObject
public class ClientLuaModuleManager extends LuaModuleManager {
    @Inject private ApplicationConfig applicationConfig;
    @Inject private LuaApplicationModel luaApplicationModel;
    @Inject private DataManager dataManager;

    @Override
    protected Globals createGlobals(ModuleBase module, File dataDirectory) {
        Globals globals = JsePlatform.standardGlobals();

        globals.load("function main(a, u, d)\n" +
                        "application = a\n" +
                        "data = d\n" +
                        "ui = u\n" +

                        "panel_width = " + applicationConfig.ui.panelWidth + "\n" +

                        "fill = " + View.FILL + "\n" +

                        "black = " + 0x000000ff + "\n" +
                        "white = " + 0xffffffff + "\n" +

                        "yellow = " + RawColors.RAW_YELLOW + "\n" +
                        "yellow_50 = " + RawColors.RAW_YELLOW_50 + "\n" +
                        "blue = " + RawColors.RAW_BLUE + "\n" +
                        "green = " + RawColors.RAW_GREEN + "\n" +
                        "green_50 = " + RawColors.RAW_GREEN_50 + "\n" +
                        "red = " + RawColors.RAW_RED + "\n" +

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
                        "  local mult = 10^(idp or 0)\n" +
                        "  return math.floor(num * mult + 0.5) / mult\n" +
                        "end end",
                "main").call();

        globals.get("main").call(
                CoerceJavaToLua.coerce(luaApplicationModel),
                CoerceJavaToLua.coerce((LuaExtendInterface) values -> {
//                    if (values.get("debug").isnil()) return;
                    if (!values.get("type").isnil()) {
                        extendLuaValue(module, values, globals, dataDirectory);
                    } else {
                        for (int i = 1; i <= values.length(); i++) {
                            extendLuaValue(module, values.get(i), globals, dataDirectory);
                        }
                    }
                }),
                CoerceJavaToLua.coerce(new LuaDataModel(dataManager) {
                    @Override
                    public void extend(LuaValue values) {
                    }
                }));

        return globals;
    }

    public View createView(ModuleBase module, Globals globals, LuaValue value, boolean inGame, int deep, CompositeView parent, String path, int index, boolean isGameView, boolean runAfter) {
        String type = value.get("type").toString();

        Optional<LuaUIExtend> optional = _extends.stream()
                .filter(extend -> extend instanceof LuaUIExtend)
                .filter(extend -> extend.accept(type))
                .map(extend -> (LuaUIExtend) extend)
                .findAny();

        if (optional.isPresent()) {
            Log.debug(LuaModuleManager.class, "Found lua extend: %s", optional.get().getClass());
            return optional.get().createView(module, globals, value, inGame, deep, parent, path, index, isGameView, runAfter);
        } else {
            Log.warning(LuaModuleManager.class, "No extend for type: %s", type);
        }

        throw new GameException(ClientLuaModuleManager.class, "Cannot read view type: " + type);
    }

}
