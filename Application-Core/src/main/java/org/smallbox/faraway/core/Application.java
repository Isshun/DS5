package org.smallbox.faraway.core;

import com.badlogic.gdx.Gdx;
import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.engine.renderer.SpriteManager;
import org.smallbox.faraway.core.game.ConfigurationManager;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.world.SQLManager;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.core.util.Utils;
import org.smallbox.faraway.ui.MouseEvent;
import org.smallbox.faraway.ui.UIManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class Application {
    public static final DependencyInjector      dependencyInjector;

    // Server
    public static final GameManager             gameManager;
    public static final PathManager             pathManager;
    public static final SpriteManager           spriteManager;
    public static final TaskManager             taskManager;
    public static final SQLManager              sqlManager;
    public static final Data                    data;

    // Both
    public static final ConfigurationManager    configurationManager;

    // Client
    public static final UIManager               uiManager;
    public static final InputManager            inputManager;

    public static boolean isLoaded = false;

    static {
        dependencyInjector = new DependencyInjector();
        gameManager = dependencyInjector.create(GameManager.class);
        pathManager = dependencyInjector.create(PathManager.class);
        uiManager = dependencyInjector.create(UIManager.class);
        spriteManager = dependencyInjector.create(SpriteManager.class);
        taskManager = dependencyInjector.create(TaskManager.class);
        sqlManager = dependencyInjector.create(SQLManager.class);
        data = dependencyInjector.create(Data.class);

        // Create configurationManager
        configurationManager = loadConfig();

        // Create input processor
        inputManager = new InputManager();
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
    private static Collection<GameObserver>         _observers = new LinkedBlockingQueue<>();

    public static void          addTask(Runnable runnable) { Gdx.app.postRunnable(runnable); }
    public static void          setRunning(boolean isRunning) { _isRunning = isRunning; if (!isRunning) Gdx.app.exit(); }
    public boolean              isRunning() { return _isRunning; }
    public static void          addObserver(GameObserver observer) { assert observer != null; _observers.add(observer); }
    public static void                 removeObserver(GameObserver observer) { assert observer != null; _observers.remove(observer); }
    public ConfigurationManager getConfig() { return configurationManager; }

    public static void onKeyEvent(GameEventListener.Action action, GameEventListener.Key key, GameEventListener.Modifier modifier) {
        ApplicationShortcutManager.onKeyPress(key, modifier);

        Application.uiManager.onKeyEvent(action, key, modifier);

        if (Application.gameManager.isLoaded()) {
            notify(observer -> observer.onKeyPress(key));
            notify(observer -> observer.onKeyEvent(action, key, modifier));
        }
    }

    public void onWindowEvent(GameEventListener.Action action) {
        Application.uiManager.onWindowEvent(action);
    }

    public static void onMouseEvent(GameEventListener.Action action, GameEventListener.MouseButton button, int x, int y, boolean rightPressed) {
        GameEvent event = new GameEvent(new MouseEvent(x, y, button, action));

        if (Application.uiManager.onMouseEvent(event, action, button, x, y, rightPressed)) {
            return;
        }

        if (Application.gameManager.isLoaded()) {
            Application.gameManager.getGame().getInteraction().onMoveEvent(event, action, button, x, y, rightPressed);
            if (ApplicationShortcutManager.onMouseEvent(event, action, button, x, y, rightPressed)) {
                return;
            }
        }

//        Application.uiManager.onMouseEvent(action, button, x, y, rightPressed);
    }

    public void update() {
        if (Application.gameManager.isLoaded()) {
            Application.gameManager.getGame().update();
        }

        // Reload data
        if (_nextDataUpdate < System.currentTimeMillis()) {
            _nextDataUpdate = System.currentTimeMillis() + Constant.RELOAD_DATA_INTERVAL;
            Application.addTask(() -> {
                long lastResModified = Utils.getLastDataModified();
                if (Application.data.needUIRefresh || lastResModified > _dataLastModified) {
                    Application.data.needUIRefresh = false;
                    _dataLastModified = lastResModified;
                    Application.uiManager.reload();
                    Application.spriteManager.reload();
                    Application.notify(GameObserver::onReloadUI);
                    Log.info("Data reloaded");
                    Application.uiManager.restore();
                }
            });
        }
    }

    public static void notify(Consumer<GameObserver> action) {
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
}