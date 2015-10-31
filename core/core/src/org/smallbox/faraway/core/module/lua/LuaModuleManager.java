package org.smallbox.faraway.core.module.lua;

import org.json.JSONObject;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.engine.lua.LuaCrewModel;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.module.area.model.AreaModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.model.*;
import org.smallbox.faraway.core.module.ModuleInfo;
import org.smallbox.faraway.core.module.lua.data.LuaExtend;
import org.smallbox.faraway.core.module.lua.data.LuaExtendManager;
import org.smallbox.faraway.core.module.lua.data.extend.*;
import org.smallbox.faraway.core.module.lua.luaModel.LuaEventsModel;
import org.smallbox.faraway.core.module.lua.luaModel.LuaGameModel;
import org.smallbox.faraway.core.util.FileUtils;
import org.smallbox.faraway.ui.LuaDataModel;
import org.smallbox.faraway.ui.UserInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Alex on 26/09/2015.
 */
public class LuaModuleManager implements GameObserver {
    private List<LuaEventListener>      _luaEventListeners = new ArrayList<>();
    private List<LuaRefreshListener>    _luaRefreshListeners = new ArrayList<>();
    private List<LuaLoadListener>       _luaLoadListeners = new ArrayList<>();

    private LuaCrewModel                _luaCrew;
    private LuaEventsModel              _luaEvents;
    private LuaGameModel                _game;
    private LuaExtendManager            _luaExtendManager;
    private List<LuaModule>             _modules = new ArrayList<>();
    private LuaValue                    _luaGame;

    public void init() {
        _luaExtendManager = new LuaExtendManager();
        _luaExtendManager.addExtendFactory(new LuaUIExtend());
        _luaExtendManager.addExtendFactory(new LuaItemExtend());
        _luaExtendManager.addExtendFactory(new LuaReceiptExtend());
        _luaExtendManager.addExtendFactory(new LuaCursorExtend());
        _luaExtendManager.addExtendFactory(new LuaCharacterBuffExtend());
        _luaExtendManager.addExtendFactory(new LuaCharacterDiseaseExtend());
        _luaExtendManager.addExtendFactory(new LuaLangExtend());

        _luaCrew = new LuaCrewModel();
        _luaEvents = new LuaEventsModel();
        _game = new LuaGameModel(_luaCrew, _luaEvents, UserInterface.getInstance());
        _luaGame = CoerceJavaToLua.coerce(_game);

//        loadUI();
    }

    private void loadUI() {
        UserInterface.getInstance()._views.clear();
        _luaEventListeners.clear();
        _luaLoadListeners.clear();
        _luaRefreshListeners.clear();

        // Load lua scripts
//        stream().filter(file -> file.getName().endsWith(".lua"))

        // Load modules info
        _modules.clear();
        FileUtils.list("data/modules/").forEach(file -> {
            try {
                ModuleInfo info = ModuleInfo.fromJSON(new JSONObject(new String(Files.readAllBytes(new File(file, "module.json").toPath()), StandardCharsets.UTF_8)));
                if ("lua".equals(info.type)) {
                    LuaModule module = new LuaModule(file);
                    module.setInfo(info);
                    _modules.add(module);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        _modules.forEach(this::loadModule);

        GameData.getData().fix();

        _luaLoadListeners.forEach(LuaLoadListener::onLoad);

        if (UserInterface.getInstance().getSelector().getSelectedCharacter() != null) {
            Game.getInstance().notify(o -> o.onSelectCharacter(UserInterface.getInstance().getSelector().getSelectedCharacter()));
        }
        if (UserInterface.getInstance().getSelector().getSelectedConsumable() != null) {
            Game.getInstance().notify(o -> o.onSelectConsumable(UserInterface.getInstance().getSelector().getSelectedConsumable()));
        }
        if (UserInterface.getInstance().getSelector().getSelectedItem() != null) {
            Game.getInstance().notify(o -> o.onSelectItem(UserInterface.getInstance().getSelector().getSelectedItem()));
        }
        if (UserInterface.getInstance().getSelector().getSelectedResource() != null) {
            Game.getInstance().notify(o -> o.onSelectResource(UserInterface.getInstance().getSelector().getSelectedResource()));
        }
        if (UserInterface.getInstance().getSelector().getSelectedStructure() != null) {
            Game.getInstance().notify(o -> o.onSelectStructure(UserInterface.getInstance().getSelector().getSelectedStructure()));
        }
    }

    private void loadModule(LuaModule luaModule) {
        ModuleInfo info = luaModule.getInfo();

        if (!hasRequiredModules(info)) {
            System.out.println("Unable to load lua module: " + info.id + " (" + info.name + ")");
            return;
        }

        System.out.println("Load lua module: " + info.id + " (" + info.name + ")");

        Globals globals = JsePlatform.standardGlobals();
        globals.load("function main(g, d)\n game = g\ndata = d\n end", "main").call();
        globals.get("main").call(_luaGame, CoerceJavaToLua.coerce(new LuaDataModel(values -> {
            if (!values.get("type").isnil()) {
                extendLuaValue(values, luaModule, globals);
            } else {
                for (int i = 1; i <= values.length(); i++) {
                    extendLuaValue(values.get(i), luaModule, globals);
                }
            }
        })));

        FileUtils.listRecursively(luaModule.getDirectory().getAbsolutePath()).stream().filter(f -> f.getName().endsWith(".lua")).forEach(f -> {
            try {
                globals.load(new FileReader(f), f.getName()).call();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (LuaError e) {
                e.printStackTrace();
            }
        });

        luaModule.setActivate(true);
    }

    private void extendLuaValue(LuaValue value, LuaModule luaModule, Globals globals) {
        for (LuaExtend luaExtend: _luaExtendManager.getExtends()) {
            if (luaExtend.accept(value.get("type").toString())) {
                try {
                    luaExtend.extend(this, luaModule, globals, value);
                } catch (DataExtendException e) {
                    if (!value.get("name").isnil()) {
                        System.out.println("Error during extend " + value.get("name").toString());
                    }
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private boolean hasRequiredModules(ModuleInfo info) {
        for (ModuleInfo.Required required: info.required) {
            boolean requiredOk = false;
            for (LuaModule module: _modules) {
                if (module.getInfo().id.equals(required.id) && module.getInfo().version >= required.minVersion) {
                    requiredOk = true;
                }
            }
            if (!requiredOk) {
                System.out.println("Missing required (" + required.id + " >= " + required.minVersion + ")");
                return false;
            }
        }
        return true;
    }

    public void update() {
        _game.update();
    }

    public void addLuaRefreshListener(LuaRefreshListener luaRefreshListener) {
        _luaRefreshListeners.add(luaRefreshListener);
    }

    public void addLuaEventListener(LuaEventListener luaEventListener) {
        _luaEventListeners.add(luaEventListener);
    }

    public void addLuaLoadListener(LuaLoadListener luaLoadListener) {
        _luaLoadListeners.add(luaLoadListener);
    }

    private void broadcastToLuaModules(int eventId, Object data) {
        LuaValue value = CoerceJavaToLua.coerce(data);
        _luaEventListeners.forEach(listener -> listener.onEvent(eventId, value, LuaValue.NIL));
    }

    //    default void onAddCharacter(CharacterModel model){}
//    default void onAddStructure(StructureModel structure){}
//    default void onAddItem(ItemModel item){}
//    default void onAddConsumable(ConsumableModel consumable){}
//    default void onAddResource(ResourceModel resource) {}
//    default void onRemoveItem(ItemModel item){}
//    default void onRemoveConsumable(ConsumableModel consumable){}
//    default void onRemoveStructure(StructureModel structure){}
//    default void onRemoveResource(ResourceModel resource){}
//    default void onRefreshItem(ItemModel item) {}
//    default void onRefreshStructure(StructureModel structure) {}
//    default void onHourChange(int hour){}
//    default void onDayChange(int day) {}
//    default void onYearChange(int year) {}
//    //    default void onOpenQuest(QuestModel quest) {}
////    default void onCloseQuest(QuestModel quest) {}
    public void onSelectArea(AreaModel area) { broadcastToLuaModules(LuaEventsModel.on_area_selected, area); }
    public void onSelectCharacter(CharacterModel character) { broadcastToLuaModules(LuaEventsModel.on_character_selected, character); }
    public void onSelectParcel(ParcelModel parcel) { broadcastToLuaModules(LuaEventsModel.on_parcel_selected, parcel); }
    public void onSelectItem(ItemModel item) { broadcastToLuaModules(LuaEventsModel.on_item_selected, item); }
    public void onSelectResource(ResourceModel resource) { broadcastToLuaModules(LuaEventsModel.on_resource_selected, resource); }
    public void onSelectConsumable(ConsumableModel consumable) { broadcastToLuaModules(LuaEventsModel.on_consumable_selected, consumable); }
    public void onSelectStructure(StructureModel structure) { broadcastToLuaModules(LuaEventsModel.on_structure_selected, structure); }
    public void onSelectReceipt(ReceiptGroupInfo receipt) { broadcastToLuaModules(LuaEventsModel.on_receipt_select, receipt); }
    public void onOverParcel(ParcelModel parcel) { broadcastToLuaModules(LuaEventsModel.on_parcel_over, parcel); }
    public void onDeselect() { broadcastToLuaModules(LuaEventsModel.on_deselect, null); }
    public void onReloadUI() {loadUI();}
    public void onRefreshUI() { _luaRefreshListeners.forEach(LuaRefreshListener::onRefresh); }
    public void onKeyPress(GameEventListener.Key key) { broadcastToLuaModules(LuaEventsModel.on_key_press, key.name());}

//    default void onStartGame() {}
//    default void onLog(String tag, String message) {}
//    default void onAddArea(AreaType type, int fromX, int fromY, int toX, int toY) {}
//    default void onRemoveArea(AreaType type, int fromX, int fromY, int toX, int toY) {}

    public void onJobCreate(JobModel job) { broadcastToLuaModules(LuaEventsModel.on_job_create, job);}
    public void onCustomEvent(String tag, Object object) {
        LuaValue luaTag = CoerceJavaToLua.coerce(tag);
        LuaValue luaValue = CoerceJavaToLua.coerce(object);
        _luaEventListeners.forEach(listener -> listener.onEvent(LuaEventsModel.on_custom_event, luaValue, luaTag));
    }

    public Collection<LuaModule> getModules() {
        return _modules;
    }
}
