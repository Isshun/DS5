package org.smallbox.faraway.core;

import com.badlogic.gdx.Gdx;
import org.reflections.Reflections;
import org.smallbox.faraway.GameTaskManager;
import org.smallbox.faraway.core.dependencyInjector.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.engine.module.lua.LuaModuleManager;
import org.smallbox.faraway.core.game.*;
import org.smallbox.faraway.core.groovy.GroovyManager;
import org.smallbox.faraway.core.module.world.SQLManager;
import org.smallbox.faraway.core.task.TaskManager;

import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.Consumer;

public class Application {
    private static Queue<GameObserver> _observers = new PriorityBlockingQueue<>(200, (o1, o2) -> {
        GameObserverPriority.Priority p1 = o1.getClass().isAnnotationPresent(GameObserverPriority.class)
                ? o1.getClass().getAnnotation(GameObserverPriority.class).value()
                : GameObserverPriority.Priority.REGULAR;
        GameObserverPriority.Priority p2 = o2.getClass().isAnnotationPresent(GameObserverPriority.class)
                ? o2.getClass().getAnnotation(GameObserverPriority.class).value()
                : GameObserverPriority.Priority.REGULAR;
        return p1.compareTo(p2);
    });

    public static DependencyInjector dependencyInjector;

    // Server
    public static GameManager           gameManager;
    public static ModuleManager         moduleManager;
    public static LuaModuleManager      luaModuleManager;
    public static TaskManager           taskManager;
    public static SQLManager            sqlManager;
    public static Data                  data;
    public static GroovyManager         groovyManager;

    public static boolean isLoaded = false;
    public static ApplicationClientListener clientListener;
    public static GameServerKyro gameServer;
    public static long id;

    public Application() {
        dependencyInjector = DependencyInjector.getInstance();
        gameManager = dependencyInjector.create(GameManager.class);
        taskManager = dependencyInjector.create(TaskManager.class);
        sqlManager = dependencyInjector.create(SQLManager.class);
        moduleManager = dependencyInjector.create(ModuleManager.class);
        luaModuleManager = dependencyInjector.create(ServerLuaModuleManager.class);
        groovyManager = dependencyInjector.create(GroovyManager.class);
        gameServer = dependencyInjector.create(GameServerKyro.class);
        data = dependencyInjector.create(Data.class);

        dependencyInjector.create(GameTaskManager.class);
        dependencyInjector.create(GameSaveManager.class);

        findAndCreateApplicationObjects();
    }

    /**
     * Automatically create object annotated with @ApplicationObject
     */
    private void findAndCreateApplicationObjects() {
        new Reflections("org.smallbox").getTypesAnnotatedWith(ApplicationObject.class).stream()
                .filter(cls -> dependencyInjector.getDependency(cls) == null)
                .forEach(cls -> dependencyInjector.create(cls));
    }

    private static boolean                          _isRunning = true;
    private long                                    _nextDataUpdate;
//    private long                                    _dataLastModified = Utils.getLastDataModified();

    public static void          addTask(Runnable runnable) { Gdx.app.postRunnable(runnable); }
    public static void          setRunning(boolean isRunning) {
        _isRunning = isRunning;
        if (!isRunning && Gdx.app != null) {
            Gdx.app.exit();
        }
    }
    public boolean              isRunning() { return _isRunning; }

    public static void          addObserver(GameObserver observer) {
        assert observer != null;

        _observers.add(observer);
    }

    public static void                 removeObserver(GameObserver observer) { assert observer != null; _observers.remove(observer); }

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

        action.getClass();

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

    public static void runOnMainThread(Runnable runnable) {

        if (Gdx.app != null) {
            Gdx.app.postRunnable(runnable);
        } else {
            runnable.run();
        }

    }
}