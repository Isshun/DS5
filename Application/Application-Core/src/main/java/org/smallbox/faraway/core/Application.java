package org.smallbox.faraway.core;

import com.badlogic.gdx.Gdx;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.engine.module.lua.LuaModuleManager;
import org.smallbox.faraway.core.game.*;
import org.smallbox.faraway.core.groovy.GroovyManager;
import org.smallbox.faraway.core.module.path.PathManager;
import org.smallbox.faraway.core.module.world.SQLManager;
import org.smallbox.faraway.core.task.TaskManager;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.util.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.Consumer;

public class Application {
    public static final DependencyInjector dependencyInjector;
    private static Queue<GameObserver> _observers = new PriorityBlockingQueue<>(200, (o1, o2) -> {
        GameObserverPriority.Priority p1 = o1.getClass().isAnnotationPresent(GameObserverPriority.class)
                ? o1.getClass().getAnnotation(GameObserverPriority.class).value()
                : GameObserverPriority.Priority.REGULAR;
        GameObserverPriority.Priority p2 = o2.getClass().isAnnotationPresent(GameObserverPriority.class)
                ? o2.getClass().getAnnotation(GameObserverPriority.class).value()
                : GameObserverPriority.Priority.REGULAR;
        return p1.compareTo(p2);
    });

    // Server
    public static final GameManager             gameManager;
    public static final ModuleManager           moduleManager;
    public static final LuaModuleManager        luaModuleManager;
    public static final PathManager             pathManager;
    public static final TaskManager             taskManager;
    public static final SQLManager              sqlManager;
    public static final Data                    data;
    public static final GroovyManager           groovyManager;
    public static final GameSaveManager         gameSaveManager;

    // Both
    public static final ConfigurationManager    configurationManager;

    public static boolean isLoaded = false;

    static {
        dependencyInjector = DependencyInjector.getInstance();
        gameManager = dependencyInjector.create(GameManager.class);
        pathManager = dependencyInjector.create(PathManager.class);
        taskManager = dependencyInjector.create(TaskManager.class);
        sqlManager = dependencyInjector.create(SQLManager.class);
        moduleManager = dependencyInjector.create(ModuleManager.class);
        luaModuleManager = dependencyInjector.create(ServerLuaModuleManager.class);
        groovyManager = dependencyInjector.create(GroovyManager.class);
        gameSaveManager = dependencyInjector.create(GameSaveManager.class);
        data = dependencyInjector.create(Data.class);

        // Create configurationManager
        configurationManager = loadConfig();
    }

    private static ConfigurationManager loadConfig() {
        Log.info("Load application configurationManager");
        try (FileInputStream fis = new FileInputStream(new File("data/config.json"))) {
            return ConfigurationManager.fromJSON(Utils.toJSON(fis));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean                          _isRunning = true;
    private long                                    _nextDataUpdate;
    private long                                    _dataLastModified = Utils.getLastDataModified();

    public static void          addTask(Runnable runnable) { Gdx.app.postRunnable(runnable); }
    public static void          setRunning(boolean isRunning) { _isRunning = isRunning; if (!isRunning) Gdx.app.exit(); }
    public boolean              isRunning() { return _isRunning; }

    public static void          addObserver(GameObserver observer) {
        assert observer != null;

        _observers.add(observer);
    }

    public static void                 removeObserver(GameObserver observer) { assert observer != null; _observers.remove(observer); }
    public ConfigurationManager getConfig() { return configurationManager; }

//    public void update() {
//        if (Application.gameManager.isLoaded()) {
//            Application.gameManager.getGame().update();
//        }
//
//        // Reload data
//        if (_nextDataUpdate < System.currentTimeMillis()) {
//            _nextDataUpdate = System.currentTimeMillis() + Constant.RELOAD_DATA_INTERVAL;
////            Application.addTask(() -> {
////                long lastResModified = Utils.getLastDataModified();
////                if (Application.data.needUIRefresh || lastResModified > _dataLastModified) {
////                    Application.data.needUIRefresh = false;
////                    _dataLastModified = lastResModified;
////                    ApplicationClient.uiManager.reload();
////                    ApplicationClient.spriteManager.reload();
////                    Application.notify(GameObserver::onReloadUI);
////                    Log.info("Data reloaded");
////                    ApplicationClient.uiManager.restore();
////                }
////            });
//        }
//    }

    public static void notify(Consumer<GameObserver> action) {
//        ApplicationClient.getObservers().forEach(action);

        try {
            _observers.forEach(action);
        } catch (Error | RuntimeException e) {
            setRunning(false);
            e.printStackTrace();
        }
    }

    public static void exitWithError() {
        _isRunning = false;
        Gdx.app.exit();
    }

    public static Queue<GameObserver> getObservers() {
        return _observers;
    }
}