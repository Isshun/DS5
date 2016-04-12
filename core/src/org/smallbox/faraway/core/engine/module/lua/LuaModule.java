package org.smallbox.faraway.core.engine.module.lua;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.lua.data.DataExtendException;
import org.smallbox.faraway.core.engine.module.lua.data.LuaExtend;
import org.smallbox.faraway.core.engine.module.lua.data.extend.*;
import org.smallbox.faraway.core.engine.module.lua.luaModel.LuaApplicationModel;
import org.smallbox.faraway.core.engine.module.lua.luaModel.LuaEventsModel;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.util.FileUtils;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.ui.LuaDataModel;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.views.widgets.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Alex on 09/10/2015.
 */
public class LuaModule extends ModuleBase {
    private class LuaUIBridge {
        public View     find(String id) { return UserInterface.getInstance().findById(id); }
        public boolean  isVisible(String id) { return UserInterface.getInstance().isVisible(id); }

        // Factories
        public View     createView() { return new UIFrame(LuaModule.this); }
        public UILabel  createLabel() { return new UILabel(LuaModule.this); }
        public UIImage  createImage() { return new UIImage(LuaModule.this); }
        public UIGrid   createGrid() { return new UIGrid(LuaModule.this); }
        public UIList   createList() { return new UIList(LuaModule.this); }
    }

    private final File          _directory;
    private boolean             _isLoaded;

    public LuaModule(File directory) {
        _directory = directory;
    }

    @Override
    protected void onGameStart(Game game) {
    }

    @Override
    protected void onUpdate(int tick) {
    }

    @Override
    public boolean loadOnStart() {
        return false;
    }

    public void setActivate(boolean isActivate) { _isLoaded = isActivate; }

    public void loadLuaFiles() {
        Globals globals = JsePlatform.standardGlobals();
        globals.load("function main(a, u, d)\n application = a\n data = d\n ui = u\n math.round = function(num, idp)\n local mult = 10^(idp or 0)\n return math.floor(num * mult + 0.5) / mult\n end end", "main").call();

        globals.get("main").call(
                CoerceJavaToLua.coerce(new LuaApplicationModel(null, new LuaEventsModel())),
                CoerceJavaToLua.coerce(new LuaUIBridge()),
                CoerceJavaToLua.coerce(new LuaDataModel(values -> {
                    if (!values.get("type").isnil()) {
                        extendLuaValue(values, globals);
                    } else {
                        for (int i = 1; i <= values.length(); i++) {
                            extendLuaValue(values.get(i), globals);
                        }
                    }
                })));

        FileUtils.listRecursively(_directory.getAbsolutePath()).stream().filter(f -> f.getName().endsWith(".lua")).forEach(f -> {
            try {
                globals.load(new FileReader(f), f.getName()).call();
            } catch (FileNotFoundException | LuaError e) {
                e.printStackTrace();
            }
        });
    }

    private static final List<LuaExtend> EXTENDS = Arrays.asList(
            new LuaUIExtend(),
            new LuaItemExtend(),
            new LuaWeatherExtend(),
            new LuaPlanetExtend(),
            new LuaBindingsExtend(),
            new LuaNetworkExtend(),
            new LuaReceiptExtend(),
            new LuaCharacterExtend(),
            new LuaCursorExtend(),
            new LuaCharacterBuffExtend(),
            new LuaCharacterDiseaseExtend(),
            new LuaLangExtend());

    private void extendLuaValue(LuaValue value, Globals globals) {
        //TODO
        String type = value.get("type").toString();
        for (LuaExtend luaExtend: EXTENDS) {
            if (luaExtend.accept(type)) {
                try {
                    luaExtend.extend(this, globals, value);
                } catch (DataExtendException e) {
                    if (!value.get("name").isnil()) {
                        Log.info("Error during extend " + value.get("name").toString());
                    }
                    e.printStackTrace();
                }
                break;
            }
        }
        for (LuaExtend luaExtend: LuaModuleManager.getInstance().getExtends()) {
            if (luaExtend.accept(type)) {
                try {
                    luaExtend.extend(this, globals, value);
                } catch (DataExtendException e) {
                    if (!value.get("name").isnil()) {
                        Log.info("Error during extend " + value.get("name").toString());
                    }
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
