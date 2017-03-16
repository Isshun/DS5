package org.smallbox.faraway.core.engine.module.lua;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.reflections.Reflections;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.ModuleInfo;
import org.smallbox.faraway.core.engine.module.lua.data.DataExtendException;
import org.smallbox.faraway.core.engine.module.lua.data.LuaExtend;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.util.FileUtils;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.util.Utils;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * Created by Alex on 26/09/2015.
 */
public abstract class LuaModuleManager implements GameObserver {
    private Collection<LuaEventListener>        _luaEventListeners = new LinkedBlockingQueue<>();
    private Collection<LuaEventListener>        _luaEventInGameListeners = new LinkedBlockingQueue<>();
    private Collection<LuaRefreshListener>      _luaRefreshListeners = new LinkedBlockingQueue<>();
    private Collection<LuaLoadListener>         _luaLoadListeners = new LinkedBlockingQueue<>();
    private Collection<LuaModule>               _luaModules = new LinkedBlockingQueue<>();
    private List<LuaExtend>                     _extends;
    private Queue<Runnable>                     _runAfterList = new ConcurrentLinkedQueue<>();

    public LuaModuleManager() {
        Application.addObserver(new GameObserver() {
//            public void onSelectArea(AreaModel area) { broadcastToLuaModules(LuaEventsModel.on_area_selected, area); }
//            public boolean onSelectCharacter(CharacterModel character) { broadcastToLuaModules(LuaEventsModel.on_character_selected, character); return true; }
//            public boolean onSelectParcel(ParcelModel parcel) { broadcastToLuaModules(LuaEventsModel.on_parcel_selected, parcel); return true; }
//            public void onSelectRock(ItemInfo rockInfo) { broadcastToLuaModules(LuaEventsModel.on_rock_selected, rockInfo); }
//            public void onSelectPlant(PlantItem plant) { broadcastToLuaModules(LuaEventsModel.on_plant_selected, plant); }
//            public void onSelectConsumable(ConsumableItem consumable) { broadcastToLuaModules(LuaEventsModel.on_consumable_selected, consumable); }
//            public void onSelectStructure(StructureItem structure) { broadcastToLuaModules(LuaEventsModel.on_structure_selected, structure); }
//            public void onSelectNetwork(NetworkItem network) { broadcastToLuaModules(LuaEventsModel.on_network_selected, network); }
//            public void onSelectReceipt(ReceiptGroupInfo receipt) { broadcastToLuaModules(LuaEventsModel.on_receipt_select, receipt); }
//            public void onOverParcel(ParcelModel parcel) { broadcastToLuaModules(LuaEventsModel.on_parcel_over, parcel); }
//            public void onDeselect() { broadcastToLuaModules(LuaEventsModel.on_deselect, null); }
//            public void onRefreshUI(int frame) { _luaRefreshListeners.forEach(listener -> listener.onRefresh(frame)); }
//            public boolean onKeyPress(GameEventListener.Key key) { broadcastToLuaModules(LuaEventsModel.on_key_press, key.name()); return false; }
//            public void onWeatherChange(WeatherInfo weather) { broadcastToLuaModules(LuaEventsModel.on_weather_change, weather);}
//            public void onTemperatureChange(double temperature) { broadcastToLuaModules(LuaEventsModel.on_temperature_change, temperature);}
//            public void onLightChange(double light, long color) { broadcastToLuaModules(LuaEventsModel.on_light_change, light, color);}
//            public void onDayTimeChange(PlanetInfo.DayTime daytime) { broadcastToLuaModules(LuaEventsModel.on_day_time_change, daytime);}
//            public void onHourChange(int hour) { broadcastToLuaModules(LuaEventsModel.on_hour_change, hour);}
//            public void onDayChange(int day) { broadcastToLuaModules(LuaEventsModel.on_day_change, day);}
//            public void onSpeedChange(int speed) { broadcastToLuaModules(LuaEventsModel.on_speed_change, speed);}
//            public void onBindingPress(BindingInfo binding) { broadcastToLuaModules(LuaEventsModel.on_binding, binding);}
//            public void onGamePaused() { broadcastToLuaModules(LuaEventsModel.on_game_paused);}
//            public void onGameResume() {
//                //broadcastToLuaModules(LuaEventsModel.on_game_resume);
//            }
//            public void onFloorChange(int floor) {broadcastToLuaModules(LuaEventsModel.on_floor_change, floor); }
//            public void onDisplayChange(String displayName, boolean isVisible) {broadcastToLuaModules(LuaEventsModel.on_display_change, displayName, isVisible); }
//            public void onLog(String tag, String message) { broadcastToLuaModules(LuaEventsModel.on_log, message); }
//            public void onJobCreate(JobModel job) { broadcastToLuaModules(LuaEventsModel.on_job_create, job);}
//            public void onCustomEvent(String tag, Object object) {
//                LuaValue luaTag = CoerceJavaToLua.coerce(tag);
//                LuaValue luaValue = CoerceJavaToLua.coerce(object);
//                _luaEventListeners.forEach(listener -> listener.onEvent(LuaEventsModel.on_custom_event, luaTag, luaValue));
//            }
        });
    }

    public Collection<LuaModule> getModules() {
        return _luaModules;
    }

    public List<LuaExtend> getExtends() {
        return _extends;
    }

    // TODO: start twice ?
    @Override
    public void onGameStart(Game game) {
        _luaModules.forEach(module -> module.startGame(game));
    }

    public void init(boolean initGui) {
        // Invoke extenders
        _extends = new Reflections("org.smallbox.faraway").getSubTypesOf(LuaExtend.class).stream()
                .filter(cls -> !Modifier.isAbstract(cls.getModifiers()))
                .filter(cls -> initGui || !cls.getSimpleName().equals("LuaUIExtend"))
                .map(cls -> {
                    try {
                        Log.info("Find extend class: " + cls.getSimpleName());
                        return cls.getConstructor().newInstance();
                    } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .collect(Collectors.toList());

        // TODO: wrong emplacement
        Application.data.bindings.clear();
//        _luaApplication.bindings = new LuaTable();

        _luaEventListeners.clear();
        _luaEventInGameListeners.clear();
        _luaLoadListeners.clear();
        _luaRefreshListeners.clear();

        // Load modules info
        _luaModules.clear();
        FileUtils.list(new File(Application.BASE_PATH, "data/modules/")).forEach(file -> {
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

        // TODO: load all lua files
        loadLuaFiles(null, new File(Application.BASE_PATH));

        Globals globals = createGlobals(null, null);

        com.google.common.io.Files.fileTreeTraverser().preOrderTraversal(new File("."))
                .filter(file -> file.getAbsolutePath().replace('\\', '/').contains("src/main/resources"))
                .filter(file -> file.getName().endsWith(".lua"))
                .forEach(file -> {
                    try {
                        Log.debug(LuaModuleManager.class, "Load lua file: %s", file.getAbsolutePath());
                        globals.load(new FileReader(file), file.getName()).call();
                    } catch (FileNotFoundException | LuaError e) {
                        e.printStackTrace();
                    }
                });

//        org.apache.commons.io.FileUtils.listFilesAndDirs(new File("."), null, TrueFileFilter.TRUE).stream()
//                .filter(file -> )

//        // TODO
//        // Load lua from java modules
//        org.apache.commons.io.FileUtils.listFiles(new File("."), new String[] {"lua"}, true)
//                .forEach(f -> {
//                    try {
//                        Log.info("Load lua file: %s", f.getAbsolutePath());
//                        globals.load(new FileReader(f), f.getName()).call();
//                    } catch (FileNotFoundException | LuaError e) {
//                        e.printStackTrace();
//                    }
////                    try {
////                        File dataDirectory = new File(moduleDirectory.getCanonicalPath(), "src/main/resources/");
////                        if (dataDirectory.exists()) {
////                            loadLuaFiles(null, dataDirectory);
////                        }
////                    } catch (IOException e) {
////                        throw new GameException(e);
////                    }
//                });

        _runAfterList.forEach(Runnable::run);

        Application.data.fix();

        Log.info("LOAD LUA !!!");
        _luaLoadListeners.forEach(LuaLoadListener::onLoad);
    }

    public void loadLuaFiles(ModuleBase module, File dataDirectory) {
        Globals globals = createGlobals(module, dataDirectory);

        // Load lua files
        FileUtils.listRecursively(dataDirectory).stream()
                .filter(f -> f.getName().endsWith(".lua"))
                .forEach(f -> {
                    Log.debug(LuaModuleManager.class, "Load lua file: %s", f.getAbsolutePath());
                    try (FileReader fileReader = new FileReader(f)) {
                        globals.load(fileReader, f.getName()).call();
                    } catch (LuaError | IOException e) {
                        e.printStackTrace();
                    }
                });

        // TODO
//        // Load css files
//        FileUtils.listRecursively(dataDirectory.getAbsolutePath()).stream().filter(f -> f.getName().endsWith(".css")).forEach(f -> {
//            Log.info("Found css file: %s", f.getName());
//
//            try {
//                InputSource source = new InputSource(new FileReader(f));
//                CSSOMParser parser = new CSSOMParser(new SACParserCSS3());
//                CSSStyleSheet sheet = parser.parseStyleSheet(source, null, null);
//                CSSRuleList rules = sheet.getCssRules();
//                for (int i = 0; i < rules.getLength(); i++) {
//                    final CSSRule rule = rules.item(i);
//                    if (rule instanceof CSSStyleRuleImpl) {
//                        LuaStyleManager.getInstance().addRule(((CSSStyleRuleImpl)rule).getSelectorText(), ((CSSStyleRuleImpl)rule).getStyle());
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });

        _runAfterList.forEach(Runnable::run);
    }

    public void runAfter(Runnable runnable) {
        _runAfterList.add(runnable);
    }

    protected abstract Globals createGlobals(ModuleBase module, File dataDirectory);

    // TODO, cree dans le client un luamodulemanager qui extend celui-ci et override cette methode pour renseigner le ui bridge
    // creer un call a data:extend et ui:extend dans les lua
    private void callMain(Globals globals, ModuleBase module, File dataDirectory) {
    }

    protected void extendLuaValue(ModuleBase module, LuaValue value, Globals globals, File dataDirectory) {
        String type = value.get("type").toString();

        Optional<LuaExtend> optional = _extends.stream()
                .filter(extend -> extend.accept(type))
                .findAny();

        if (optional.isPresent()) {
            Log.debug(LuaModuleManager.class, "Found lua extend: %s", optional.get().getClass());
            try {
                optional.get().extend(module, globals, value, dataDirectory);
            } catch (DataExtendException e) {
                if (!value.get("name").isnil()) {
                    Log.info("Error during extend " + value.get("name").toString());
                }
                e.printStackTrace();
            }
        } else {
            Log.warning(LuaModuleManager.class, "No extend for type: %s", type);
        }
    }

    private void loadModule(LuaModule luaModule) {
        ModuleInfo info = luaModule.getInfo();

        if (!hasRequiredModules(info)) {
            Log.info("Unable to onLoadModule lua module: " + info.id + " (" + info.name + ")");
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
        if (Application.gameManager.isLoaded()) {
            broadcast(_luaEventInGameListeners, eventId, LuaValue.NIL, null);
        }
    }

    private void broadcastToLuaModules(int eventId, Object data) {
        LuaValue value = CoerceJavaToLua.coerce(data);
        if (broadcast(_luaEventListeners, eventId, LuaValue.NIL, value)) {
            return;
        }
        if (Application.gameManager.isLoaded()) {
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
        if (Application.gameManager.isLoaded()) {
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
}