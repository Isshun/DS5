package org.smallbox.faraway.core.lua;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.widgets.CompositeView;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.lua.data.DataExtendException;
import org.smallbox.faraway.core.lua.data.LuaExtend;
import org.smallbox.faraway.core.module.ModuleBase;
import org.smallbox.faraway.core.module.ModuleInfo;
import org.smallbox.faraway.util.FileUtils;
import org.smallbox.faraway.util.GameException;
import org.smallbox.faraway.util.Utils;
import org.smallbox.faraway.util.log.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class LuaModuleManager {
    private final Collection<LuaEventListener> _luaEventListeners = new LinkedBlockingQueue<>();
    private final Collection<LuaEventListener> _luaEventInGameListeners = new LinkedBlockingQueue<>();
    private final Collection<LuaRefreshListener> _luaRefreshListeners = new LinkedBlockingQueue<>();
    private final Collection<LuaLoadListener> _luaLoadListeners = new LinkedBlockingQueue<>();
    private final Collection<LuaModule> _luaModules = new LinkedBlockingQueue<>();
    protected Collection<LuaExtend> _extends;
    private final Queue<Runnable> _runAfterList = new ConcurrentLinkedQueue<>();

    @Inject private DependencyManager dependencyManager;
    @Inject private UIManager uiManager;
    @Inject private DataManager dataManager;

    private Globals globals;

    public Collection<LuaModule> getModules() {
        return _luaModules;
    }

    public Collection<LuaExtend> getExtends() {
        return _extends;
    }

    public void init(boolean initGui) {
        // Invoke extenders
        _extends = dependencyManager.getSubTypesOf(LuaExtend.class);

        // TODO: wrong emplacement
        dataManager.bindings.clear();
//        _luaApplication.bindings = new LuaTable();

        _luaEventListeners.clear();
        _luaEventInGameListeners.clear();
        _luaLoadListeners.clear();
        _luaRefreshListeners.clear();

        // Load modules info
        _luaModules.clear();
        FileUtils.list(FileUtils.getDataFile("modules")).forEach(file -> {
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

        globals = createGlobals(null, null);

        _luaModules.forEach(luaModule -> loadModule(luaModule, globals));

        // TODO: load all lua files
        loadStyles();

        loadLuaFiles(null, new File(FileUtils.BASE_PATH), globals);

        com.google.common.io.Files.fileTreeTraverser().preOrderTraversal(new File("."))
                .filter(file -> file.getAbsolutePath().replace('\\', '/').contains("src/main/resources"))
                .filter(file -> file.getName().endsWith(".lua"))
                .forEach(this::doLoadFile);

        _runAfterList.forEach(Runnable::run);

        fixUISize();

        dataManager.fix();

        _luaLoadListeners.forEach(LuaLoadListener::onLoad);
    }

    public void loadStyles() {
        loadLuaFile("styles.lua");
    }

    public void doLoadFile(File file) {
        Log.debug(LuaModuleManager.class, "Load lua file: %s", file.getAbsolutePath());

        try (FileReader fileReader = new FileReader(file)) {
            globals.set("file", file.getAbsolutePath());
            globals.load(fileReader, file.getName()).call();
        } catch (LuaError | IOException e) {
            throw new GameException(LuaModuleManager.class, "Error for reading lua file: " + file.getAbsolutePath(), e);
        }
    }

    // Calcule toutes les tailles encore set a FILL, cela arrive dans les cas des sous-controlleurs lorsque ceux-ci sont chargés avant leur controlleur parent
    private void fixUISize() {
        uiManager.getSubViews().forEach(view -> {
            Optional.ofNullable(uiManager.getSubViewParent(view)).ifPresent(parentId -> view.setParent((CompositeView) uiManager.findById(parentId)));
        });
        uiManager.getRootViews().forEach(rootView -> fixUISizeRecurse(rootView.getView()));
    }

    private void fixUISizeRecurse(View view) {
        if (view.getWidth() == View.FILL || view.getHeight() == View.FILL) {
            view.setSize(view.getWidth(), view.getHeight());
        }
        if (view instanceof CompositeView) {
            ((CompositeView)view).getViews().forEach(this::fixUISizeRecurse);
        }
    }

    public void loadLuaFiles(ModuleBase module, File dataDirectory, Globals globals) {
//        Globals globals = createGlobals(module, dataDirectory);

        // Load lua files
        FileUtils.listRecursively(dataDirectory).stream()
                .filter(f -> f.getName().endsWith(".lua"))
                .filter(f -> !f.getName().equals("styles.lua"))
                .forEach(this::doLoadFile);

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

    public void loadLuaFile(String fileName) {
        File dataDirectory = new File(FileUtils.BASE_PATH);

        // Load lua files
        FileUtils.listRecursively(dataDirectory).stream()
                .filter(file -> file.getName().endsWith(".lua"))
                .filter(file -> file.getName().equals(fileName))
                .forEach(this::doLoadFile);

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
                optional.get().extend(dataManager, module, globals, value, dataDirectory);
            } catch (DataExtendException e) {
                if (!value.get("name").isnil()) {
                    Log.info("Error during extend " + value.get("name").toString());
                } else if (!value.get("id").isnil()) {
                    Log.info("Error during extend " + value.get("id").toString());
                }
                e.printStackTrace();
            }
        } else {
            Log.warning(LuaModuleManager.class, "No extend for type: %s", type);
        }
    }

    private void loadModule(LuaModule luaModule, Globals globals) {
        ModuleInfo info = luaModule.getInfo();

        if (!hasRequiredModules(info)) {
            Log.info("Unable to onLoadModule lua module: " + info.id + " (" + info.name + ")");
            return;
        }
        Log.debug("Load lua module: " + info.id + " (" + info.name + ")");

        loadLuaFiles(luaModule, luaModule.getDirectory(), globals);

        luaModule.setActivate(true);
    }

    private boolean hasRequiredModules(ModuleInfo info) {
        for (ModuleInfo.Required required : info.required) {
            boolean requiredOk = false;
            for (LuaModule module : _luaModules) {
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
        for (LuaEventListener listener : listeners) {
            if (listener.onEvent(eventId, tag, value)) {
                return true;
            }
        }
        return false;
    }

}