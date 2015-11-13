package org.smallbox.faraway.core.module.lua;

import org.json.JSONObject;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.data.BindingInfo;
import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.engine.lua.LuaCrewModel;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.model.WeatherInfo;
import org.smallbox.faraway.core.game.model.planet.PlanetInfo;
import org.smallbox.faraway.core.game.module.area.model.AreaModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.model.*;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.game.module.world.model.resource.PlantModel;
import org.smallbox.faraway.core.module.ModuleInfo;
import org.smallbox.faraway.core.module.lua.data.LuaExtend;
import org.smallbox.faraway.core.module.lua.data.extend.*;
import org.smallbox.faraway.core.module.lua.luaModel.LuaEventsModel;
import org.smallbox.faraway.core.module.lua.luaModel.LuaApplicationModel;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Alex on 26/09/2015.
 */
public class LuaModuleManager implements GameObserver {
    private static final List<LuaExtend> EXTENDS = Arrays.asList(
            new LuaUIExtend(),
            new LuaItemExtend(),
            new LuaWeatherExtend(),
            new LuaPlanetExtend(),
            new LuaBindingsExtend(),
            new LuaNetworkExtend(),
            new LuaReceiptExtend(),
            new LuaCursorExtend(),
            new LuaCharacterBuffExtend(),
            new LuaCharacterDiseaseExtend(),
            new LuaLangExtend());

    private static LuaModuleManager _self;

    private List<LuaEventListener>      _luaEventListeners = new ArrayList<>();
    private List<LuaEventListener>      _luaEventInGameListeners = new ArrayList<>();
    private List<LuaRefreshListener>    _luaRefreshListeners = new ArrayList<>();
    private List<LuaLoadListener>       _luaLoadListeners = new ArrayList<>();
    private List<LuaModule>             _modules = new ArrayList<>();

    private LuaCrewModel                _luaCrew;
    private LuaEventsModel              _luaEvents;
    private LuaApplicationModel         _luaApplication;
    private LuaValue                    _luaApplicationValue;

    public LuaModuleManager() {
        Application.getInstance().addObserver(this);
    }

    public void load() {
        _luaCrew = new LuaCrewModel();
        _luaEvents = new LuaEventsModel();
        _luaApplication = new LuaApplicationModel(_luaCrew, _luaEvents, UserInterface.getInstance());
        _luaApplicationValue = CoerceJavaToLua.coerce(_luaApplication);

        reload();
    }

    private void reload() {
        // TODO: wrong emplacement
        Data.getData().bindings.clear();
        _luaApplication.bindings = new LuaTable();

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

        Data.getData().fix();

        _luaLoadListeners.forEach(LuaLoadListener::onLoad);

        if (GameManager.getInstance().isRunning()) {
            if (Game.getInstance().getSelector().getSelectedCharacter() != null) {
                Application.getInstance().notify(o -> o.onSelectCharacter(Game.getInstance().getSelector().getSelectedCharacter()));
            }
            if (Game.getInstance().getSelector().getSelectedConsumable() != null) {
                Application.getInstance().notify(o -> o.onSelectConsumable(Game.getInstance().getSelector().getSelectedConsumable()));
            }
            if (Game.getInstance().getSelector().getSelectedItem() != null) {
                Application.getInstance().notify(o -> o.onSelectItem(Game.getInstance().getSelector().getSelectedItem()));
            }
            if (Game.getInstance().getSelector().getSelectedResource() != null) {
                Application.getInstance().notify(o -> o.onSelectPlant(Game.getInstance().getSelector().getSelectedResource()));
            }
            if (Game.getInstance().getSelector().getSelectedStructure() != null) {
                Application.getInstance().notify(o -> o.onSelectStructure(Game.getInstance().getSelector().getSelectedStructure()));
            }
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
        globals.load("function main(a, d)\n application = a\ndata = d\n end", "main").call();
        globals.get("main").call(_luaApplicationValue, CoerceJavaToLua.coerce(new LuaDataModel(values -> {
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
        for (LuaExtend luaExtend: EXTENDS) {
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
        _luaApplication.update();
    }

    public void addLuaRefreshListener(LuaRefreshListener luaRefreshListener) {
        _luaRefreshListeners.add(luaRefreshListener);
    }

    public void addLuaEventListener(LuaEventListener luaEventListener, boolean inGame) {
        if (inGame) {
            _luaEventInGameListeners.add(luaEventListener);
        } else {
            _luaEventListeners.add(luaEventListener);
        }
    }

    public void addLuaLoadListener(LuaLoadListener luaLoadListener) {
        _luaLoadListeners.add(luaLoadListener);
    }

    private void broadcastToLuaModules(int eventId) {
        _luaEventListeners.forEach(listener -> listener.onEvent(eventId, LuaValue.NIL, null));
        if (GameManager.getInstance().isRunning()) {
            _luaEventInGameListeners.forEach(listener -> listener.onEvent(eventId, LuaValue.NIL, null));
        }
    }

    private void broadcastToLuaModules(int eventId, Object data) {
        LuaValue value = CoerceJavaToLua.coerce(data);
        _luaEventListeners.forEach(listener -> listener.onEvent(eventId, LuaValue.NIL, value));
        if (GameManager.getInstance().isRunning()) {
            _luaEventInGameListeners.forEach(listener -> listener.onEvent(eventId, LuaValue.NIL, value));
        }
    }

    private void broadcastToLuaModules(int eventId, Object data1, Object data2) {
        LuaValue value = new LuaTable();
        value.set(1, CoerceJavaToLua.coerce(data1));
        value.set(2, CoerceJavaToLua.coerce(data2));
        _luaEventListeners.forEach(listener -> listener.onEvent(eventId, LuaValue.NIL, value));
        if (GameManager.getInstance().isRunning()) {
            _luaEventInGameListeners.forEach(listener -> listener.onEvent(eventId, LuaValue.NIL, value));
        }
    }

    //    default void onAddCharacter(CharacterModel model){}
//    default void onAddStructure(StructureModel structure){}
//    default void onAddItem(ItemModel item){}
//    default void onAddConsumable(ConsumableModel consumable){}
//    default void onAddResource(ResourceModel resource) {}
//    default void onRemoveItem(ItemModel item){}
//    default void onRemoveConsumable(ConsumableModel consumable){}
//    default void onRemoveStructure(StructureModel structure){}
//    default void onRemovePlant(ResourceModel resource){}
//    default void onRefreshItem(ItemModel item) {}
//    default void onRefreshStructure(StructureModel structure) {}
//    default void onDayChange(int day) {}
//    default void onYearChange(int year) {}
//    //    default void onOpenQuest(QuestModel quest) {}
////    default void onCloseQuest(QuestModel quest) {}
    public void onSelectArea(AreaModel area) { broadcastToLuaModules(LuaEventsModel.on_area_selected, area); }
    public void onSelectCharacter(CharacterModel character) { broadcastToLuaModules(LuaEventsModel.on_character_selected, character); }
    public void onSelectParcel(ParcelModel parcel) { broadcastToLuaModules(LuaEventsModel.on_parcel_selected, parcel); }
    public void onSelectItem(ItemModel item) { broadcastToLuaModules(LuaEventsModel.on_item_selected, item); }
    public void onSelectRock(ItemInfo rockInfo) { broadcastToLuaModules(LuaEventsModel.on_rock_selected, rockInfo); }
    public void onSelectPlant(PlantModel plant) { broadcastToLuaModules(LuaEventsModel.on_plant_selected, plant); }
    public void onSelectConsumable(ConsumableModel consumable) { broadcastToLuaModules(LuaEventsModel.on_consumable_selected, consumable); }
    public void onSelectStructure(StructureModel structure) { broadcastToLuaModules(LuaEventsModel.on_structure_selected, structure); }
    public void onSelectNetwork(NetworkObjectModel network) { broadcastToLuaModules(LuaEventsModel.on_network_selected, network); }
    public void onSelectReceipt(ReceiptGroupInfo receipt) { broadcastToLuaModules(LuaEventsModel.on_receipt_select, receipt); }
    public void onOverParcel(ParcelModel parcel) { broadcastToLuaModules(LuaEventsModel.on_parcel_over, parcel); }
    public void onDeselect() { broadcastToLuaModules(LuaEventsModel.on_deselect, null); }
    public void onReloadUI() { reload(); }
    public void onRefreshUI() { _luaRefreshListeners.forEach(LuaRefreshListener::onRefresh); }
    public void onKeyPress(GameEventListener.Key key) { broadcastToLuaModules(LuaEventsModel.on_key_press, key.name());}
    public void onWeatherChange(WeatherInfo weather) { broadcastToLuaModules(LuaEventsModel.on_weather_change, weather);}
    public void onTemperatureChange(double temperature) { broadcastToLuaModules(LuaEventsModel.on_temperature_change, temperature);}
    public void onLightChange(double light, long color) { broadcastToLuaModules(LuaEventsModel.on_light_change, light, color);}
    public void onDayTimeChange(PlanetInfo.DayTime daytime) { broadcastToLuaModules(LuaEventsModel.on_day_time_change, daytime);}
    public void onHourChange(int hour) { broadcastToLuaModules(LuaEventsModel.on_hour_change, hour);}
    public void onDayChange(int day) { broadcastToLuaModules(LuaEventsModel.on_day_change, day);}
    public void onSpeedChange(int speed) { broadcastToLuaModules(LuaEventsModel.on_speed_change, speed);}
    public void onBindingPress(BindingInfo binding) { broadcastToLuaModules(LuaEventsModel.on_binding, binding);}
    public void onGamePaused() { broadcastToLuaModules(LuaEventsModel.on_game_paused);}
    public void onGameResume() {broadcastToLuaModules(LuaEventsModel.on_game_resume); }
    public void onFloorChange(int floor) {broadcastToLuaModules(LuaEventsModel.on_floor_change, floor); }

//    default void onGameStart() {}
//    default void onLog(String tag, String message) {}
//    default void onAddArea(AreaType type, int fromX, int fromY, int toX, int toY) {}
//    default void onRemoveArea(AreaType type, int fromX, int fromY, int toX, int toY) {}

    public void onJobCreate(JobModel job) { broadcastToLuaModules(LuaEventsModel.on_job_create, job);}
    public void onCustomEvent(String tag, Object object) {
        LuaValue luaTag = CoerceJavaToLua.coerce(tag);
        LuaValue luaValue = CoerceJavaToLua.coerce(object);
        _luaEventListeners.forEach(listener -> listener.onEvent(LuaEventsModel.on_custom_event, luaTag, luaValue));
    }

    public Collection<LuaModule> getModules() {
        return _modules;
    }

    public LuaApplicationModel getGame() {
        return _luaApplication;
    }

    public static LuaModuleManager getInstance() {
        if (_self == null) {
            _self = new LuaModuleManager();
        }
        return _self;
    }
}