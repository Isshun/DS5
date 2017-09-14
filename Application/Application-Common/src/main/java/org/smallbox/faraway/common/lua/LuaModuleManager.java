package org.smallbox.faraway.common.lua;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.reflections.Reflections;
import org.smallbox.faraway.common.GameException;
import org.smallbox.faraway.common.GameObserver;
import org.smallbox.faraway.common.ModuleBase;
import org.smallbox.faraway.common.ModuleInfo;
import org.smallbox.faraway.common.lua.data.DataExtendException;
import org.smallbox.faraway.common.lua.data.LuaExtend;
import org.smallbox.faraway.common.util.FileUtils;
import org.smallbox.faraway.common.util.Log;
import org.smallbox.faraway.common.util.Utils;

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

    public Collection<LuaModule> getModules() {
        return _luaModules;
    }

    public List<LuaExtend> getExtends() {
        return _extends;
    }

    // TODO: start twice ?
    @Override
    public void onGameStart() {
        _luaModules.forEach(module -> module.startGame());
    }

    public void init(boolean initGui) {
        // Invoke extenders
        _extends = new Reflections("org.smallbox.faraway").getSubTypesOf(LuaExtend.class).stream()
                .filter(cls -> !Modifier.isAbstract(cls.getModifiers()))
                .filter(cls -> initGui || !cls.getSimpleName().equals("LuaUIExtend"))
                .map(cls -> {
                    Log.info("Find extend class: " + cls.getSimpleName());
                    try {
                        return cls.getConstructor().newInstance();
                    } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                        throw new GameException(LuaModuleManager.class, "Unable to create extend cls: " + cls.getSimpleName());
                    }
                })
                .collect(Collectors.toList());

        // TODO: wrong emplacement
//        Application.data.bindings.clear();
//        _luaApplication.bindings = new LuaTable();

        _luaEventListeners.clear();
        _luaEventInGameListeners.clear();
        _luaLoadListeners.clear();
        _luaRefreshListeners.clear();

        // Load modules info
        _luaModules.clear();
        FileUtils.list(new File(FileUtils.BASE_PATH, "data/modules/")).forEach(file -> {
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
        loadLuaFiles(null, new File(FileUtils.BASE_PATH));

        Globals globals = createGlobals(null, null);

        com.google.common.io.Files.fileTreeTraverser().preOrderTraversal(new File("."))
                .filter(file -> file.getAbsolutePath().replace('\\', '/').contains("src/main/resources"))
                .filter(file -> file.getName().endsWith(".lua"))
                .forEach(file -> {
                    try {
                        Log.debug(LuaModuleManager.class, "Load lua file: %s", file.getAbsolutePath());
                        globals.load(new FileReader(file), file.getName()).call();
                    } catch (FileNotFoundException | LuaError e) {
                        throw new GameException(LuaModuleManager.class, "Error for reading lua file: " + file.getAbsolutePath());
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

//        Application.data.fix();

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
    private void callMain(Globals globals, Object module, File dataDirectory) {
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

    private boolean broadcast(Collection<LuaEventListener> listeners, int eventId, LuaValue tag, LuaValue value) {
        for (LuaEventListener listener: listeners) {
            if (listener.onEvent(eventId, tag, value)) {
                return true;
            }
        }
        return false;
    }
}