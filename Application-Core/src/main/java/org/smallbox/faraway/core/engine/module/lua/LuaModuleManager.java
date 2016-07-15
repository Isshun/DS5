package org.smallbox.faraway.core.engine.module.lua;

import com.steadystate.css.dom.CSSStyleRuleImpl;
import com.steadystate.css.format.CSSFormat;
import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.SACParserCSS3;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.reflections.Reflections;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.LuaControllerManager;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.engine.module.ApplicationModule;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.ModuleInfo;
import org.smallbox.faraway.core.engine.module.lua.data.DataExtendException;
import org.smallbox.faraway.core.engine.module.lua.data.LuaExtend;
import org.smallbox.faraway.core.engine.module.lua.data.extend.*;
import org.smallbox.faraway.core.engine.module.lua.luaModel.LuaApplicationModel;
import org.smallbox.faraway.core.engine.module.lua.luaModel.LuaEventsModel;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.model.planet.PlanetInfo;
import org.smallbox.faraway.core.game.modelInfo.BindingInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.modelInfo.WeatherInfo;
import org.smallbox.faraway.core.game.module.area.model.AreaModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.model.*;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.util.FileUtils;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.core.util.Utils;
import org.smallbox.faraway.ui.LuaDataModel;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.UIEventManager;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleSheet;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Alex on 26/09/2015.
 */
public class LuaModuleManager implements GameObserver {
    private static LuaModuleManager _self;

    private Collection<LuaEventListener>        _luaEventListeners = new LinkedBlockingQueue<>();
    private Collection<LuaEventListener>        _luaEventInGameListeners = new LinkedBlockingQueue<>();
    private Collection<LuaRefreshListener>      _luaRefreshListeners = new LinkedBlockingQueue<>();
    private Collection<LuaLoadListener>         _luaLoadListeners = new LinkedBlockingQueue<>();
    private Collection<LuaModule>               _luaModules = new LinkedBlockingQueue<>();
    private List<LuaExtend>                     _extends = new ArrayList<>();

    public LuaModuleManager() {
        Application.getInstance().addObserver(this);
    }

    public void startGame(Game game) {
        _luaModules.forEach(module -> module.startGame(game));
    }

    public void init() {
        // TODO: wrong emplacement
        Data.getData().bindings.clear();
//        _luaApplication.bindings = new LuaTable();

        UIEventManager.getInstance().clear();
        UserInterface.getInstance().clearViews();
        _luaEventListeners.clear();
        _luaEventInGameListeners.clear();
        _luaLoadListeners.clear();
        _luaRefreshListeners.clear();

        // Load modules info
        _luaModules.clear();
        FileUtils.list("data/modules/").forEach(file -> {
            try (FileInputStream fis = new FileInputStream(new File(file, "module.json"))) {
                ModuleInfo info = ModuleInfo.fromJSON(Utils.toJSON(fis));
                if ("lua".equals(info.type)) {
                    LuaModule module = new LuaModule(file);
                    module.setInfo(info);
                    _luaModules.add(module);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        _luaModules.forEach(this::loadModule);

        // TODO
        // Load lua from java modules
        Arrays.stream(new File(".").listFiles()).filter(file -> file.getName().startsWith("Module-")).forEach(moduleDirectory -> {
            File dataDirectory = new File(moduleDirectory, "data");
            if (dataDirectory.exists()) {
                loadLuaFiles(null, dataDirectory);
            }
        });

        LuaControllerManager.getInstance().injectControllersToModules();

        Data.getData().fix();

        Log.info("LOAD LUA !!!");
        _luaLoadListeners.forEach(LuaLoadListener::onLoad);

        __devResume();
    }

    public void loadLuaFiles(ModuleBase module, File directory) {
        Globals globals = JsePlatform.standardGlobals();
        globals.load("function main(a, u, d)\n application = a\n data = d\n ui = u\n math.round = function(num, idp)\n local mult = 10^(idp or 0)\n return math.floor(num * mult + 0.5) / mult\n end end", "main").call();

        globals.get("main").call(
                CoerceJavaToLua.coerce(new LuaApplicationModel(null, new LuaEventsModel())),
                CoerceJavaToLua.coerce(new LuaUIBridge(null)),
                CoerceJavaToLua.coerce(new LuaDataModel(values -> {
                    if (!values.get("type").isnil()) {
                        extendLuaValue(module, values, globals);
                    } else {
                        for (int i = 1; i <= values.length(); i++) {
                            extendLuaValue(module, values.get(i), globals);
                        }
                    }
                })));

        FileUtils.listRecursively(directory.getAbsolutePath()).stream().filter(f -> f.getName().endsWith(".lua")).forEach(f -> {
            try {
                globals.load(new FileReader(f), f.getName()).call();
            } catch (FileNotFoundException | LuaError e) {
                e.printStackTrace();
            }
        });

        FileUtils.listRecursively(directory.getAbsolutePath()).stream().filter(f -> f.getName().endsWith(".css")).forEach(f -> {
            Log.info("Found css file: %s", f.getName());

            try {
                InputSource source = new InputSource(new FileReader(f));
                CSSOMParser parser = new CSSOMParser(new SACParserCSS3());
                CSSStyleSheet sheet = parser.parseStyleSheet(source, null, null);
                CSSRuleList rules = sheet.getCssRules();
                for (int i = 0; i < rules.getLength(); i++) {
                    final CSSRule rule = rules.item(i);

                    if (rule instanceof CSSStyleRuleImpl) {
                        LuaStyleManager.getInstance().addRule(((CSSStyleRuleImpl)rule).getSelectorText(), ((CSSStyleRuleImpl)rule).getStyle());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Inject controllers to modules
//        LuaControllerManager.getInstance().injectControllersToModules();
//        // Inject sub-controllers to controllers
//        LuaControllerManager.getInstance().injectSubControllersToControllers();
    }

    private void __devResume() {
        if (GameManager.getInstance().isLoaded()) {
            if (Game.getInstance().getSelector().getSelectedCharacter() != null) {
                Application.getInstance().notify(o -> o.onSelectCharacter(Game.getInstance().getSelector().getSelectedCharacter()));
            }
//            if (Game.getInstance().getSelector().getSelectedConsumable() != null) {
//                Application.getInstance().notify(o -> o.onSelectConsumable(Game.getInstance().getSelector().getSelectedConsumable()));
//            }
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
            Log.info("Unable to load lua module: " + info.id + " (" + info.name + ")");
            return;
        }
        Log.info("Load lua module: " + info.id + " (" + info.name + ")");

        loadLuaFiles(luaModule, luaModule.getDirectory());

        luaModule.setActivate(true);
    }

    private boolean hasRequiredModules(ModuleInfo info) {
        for (ModuleInfo.Required required: info.required) {
            boolean requiredOk = false;
            for (LuaModule module: _luaModules) {
                if (module.getInfo().id.equals(required.id) && module.getInfo().version >= required.minVersion) {
                    requiredOk = true;
                }
            }
            if (!requiredOk) {
                Log.info("Missing required (" + required.id + " >= " + required.minVersion + ")");
                return false;
            }
        }
        return true;
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
        if (broadcast(_luaEventListeners, eventId, LuaValue.NIL, null)) {
            return;
        }
        if (GameManager.getInstance().isLoaded()) {
            broadcast(_luaEventInGameListeners, eventId, LuaValue.NIL, null);
        }
    }

    private void broadcastToLuaModules(int eventId, Object data) {
        LuaValue value = CoerceJavaToLua.coerce(data);
        if (broadcast(_luaEventListeners, eventId, LuaValue.NIL, value)) {
            return;
        }
        if (GameManager.getInstance().isLoaded()) {
            broadcast(_luaEventInGameListeners, eventId, LuaValue.NIL, value);
        }
    }

    private void broadcastToLuaModules(int eventId, Object data1, Object data2) {
        LuaValue value = new LuaTable();
        value.set(1, CoerceJavaToLua.coerce(data1));
        value.set(2, CoerceJavaToLua.coerce(data2));
        if (broadcast(_luaEventListeners, eventId, LuaValue.NIL, value)) {
            return;
        }
        if (GameManager.getInstance().isLoaded()) {
            broadcast(_luaEventInGameListeners, eventId, LuaValue.NIL, value);
        }
    }

    private boolean broadcast(Collection<LuaEventListener> listeners, int eventId, LuaValue tag, LuaValue value) {
        for (LuaEventListener listener: listeners) {
            if (listener.onEvent(eventId, tag, value)) {
                return true;
            }
        }
        return false;
    }

    //    default void onAddCharacter(CharacterModel org.smallbox.faraway.core.game.module.room.model){}
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
    public boolean onSelectCharacter(CharacterModel character) { broadcastToLuaModules(LuaEventsModel.on_character_selected, character); return true; }
    public boolean onSelectParcel(ParcelModel parcel) { broadcastToLuaModules(LuaEventsModel.on_parcel_selected, parcel); return true; }
    public void onSelectItem(ItemModel item) { broadcastToLuaModules(LuaEventsModel.on_item_selected, item); }
    public void onSelectRock(ItemInfo rockInfo) { broadcastToLuaModules(LuaEventsModel.on_rock_selected, rockInfo); }
    public void onSelectPlant(PlantModel plant) { broadcastToLuaModules(LuaEventsModel.on_plant_selected, plant); }
    public void onSelectConsumable(ConsumableModel consumable) { broadcastToLuaModules(LuaEventsModel.on_consumable_selected, consumable); }
    public void onSelectStructure(StructureModel structure) { broadcastToLuaModules(LuaEventsModel.on_structure_selected, structure); }
    public void onSelectNetwork(NetworkObjectModel network) { broadcastToLuaModules(LuaEventsModel.on_network_selected, network); }
    public void onSelectReceipt(ReceiptGroupInfo receipt) { broadcastToLuaModules(LuaEventsModel.on_receipt_select, receipt); }
    public void onOverParcel(ParcelModel parcel) { broadcastToLuaModules(LuaEventsModel.on_parcel_over, parcel); }
    public void onDeselect() { broadcastToLuaModules(LuaEventsModel.on_deselect, null); }
    public void onReloadUI() { init(); }
    public void onRefreshUI(int frame) { _luaRefreshListeners.forEach(listener -> listener.onRefresh(frame)); }
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
    public void onDisplayChange(String displayName, boolean isVisible) {broadcastToLuaModules(LuaEventsModel.on_display_change, displayName, isVisible); }
//    public void onOpenQuest(QuestModel quest) {broadcastToLuaModules(LuaEventsModel.on_open_quest, quest); }
    public void onLog(String tag, String message) { broadcastToLuaModules(LuaEventsModel.on_log, message); }

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
        return _luaModules;
    }

    public static LuaModuleManager getInstance() {
        if (_self == null) {
            _self = new LuaModuleManager();
        }
        return _self;
    }

    public List<LuaExtend> getExtends() {
        return _extends;
    }

    // TODO: replace static EXTENDS by reflection
//    new Reflections("org.smallbox.faraway").getSubTypesOf(LuaExtend.class).stream()
//    .filter(cls -> !Modifier.isAbstract(cls.getModifiers()))
//            .forEach(cls -> {
//        try {
//            Log.info("Find extend class: " + cls.getSimpleName());
//        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
//            e.printStackTrace();
//        }
//    });

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

    private void extendLuaValue(ModuleBase module, LuaValue value, Globals globals) {
        String type = value.get("type").toString();

        for (LuaExtend luaExtend: EXTENDS) {
            if (luaExtend.accept(type)) {
                try {
                    luaExtend.extend(module, globals, value);
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
                    luaExtend.extend(module, globals, value);
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