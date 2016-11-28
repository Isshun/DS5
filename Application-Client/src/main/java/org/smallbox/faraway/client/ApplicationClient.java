package org.smallbox.faraway.client;

import com.badlogic.gdx.Gdx;
import org.smallbox.faraway.client.manager.ApplicationShortcutManager;
import org.smallbox.faraway.client.manager.InputManager;
import org.smallbox.faraway.client.renderer.GDXRenderer;
import org.smallbox.faraway.client.renderer.MainRenderer;
import org.smallbox.faraway.client.renderer.SpriteManager;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.ui.MouseEvent;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.ConfigurationManager;
import org.smallbox.faraway.core.game.GameEvent;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.util.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class ApplicationClient {
    public static final DependencyInjector dependencyInjector;
    private static Collection<GameObserver>     _observers = new LinkedBlockingQueue<>();

    // Both
    public static final ConfigurationManager    configurationManager;

    // Client
    public static final UIManager uiManager;
    public static final UIEventManager          uiEventManager;
    public static final InputManager inputManager;

    public static final SpriteManager           spriteManager;
    public static final GDXRenderer             gdxRenderer;
    public static final MainRenderer            mainRenderer;
    public static final Viewport                viewport;

    public static boolean isLoaded = false;

    static {
        dependencyInjector = DependencyInjector.getInstance();
        uiManager = dependencyInjector.create(UIManager.class);
        uiEventManager = dependencyInjector.create(UIEventManager.class);
        spriteManager = dependencyInjector.create(SpriteManager.class);
        gdxRenderer = dependencyInjector.create(GDXRenderer.class);
        mainRenderer = dependencyInjector.create(MainRenderer.class);
        viewport = new Viewport(400, 300);

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

    public static void          addTask(Runnable runnable) { Gdx.app.postRunnable(runnable); }
    public static void          setRunning(boolean isRunning) { _isRunning = isRunning; if (!isRunning) Gdx.app.exit(); }
    public boolean              isRunning() { return _isRunning; }
    public static void          addObserver(GameObserver observer) { assert observer != null; _observers.add(observer); }
    public static void                 removeObserver(GameObserver observer) { assert observer != null; _observers.remove(observer); }
    public ConfigurationManager getConfig() { return configurationManager; }

    public static void onKeyEvent(GameEventListener.Action action, GameEventListener.Key key, GameEventListener.Modifier modifier) {
        ApplicationShortcutManager.onKeyPress(key, modifier);

        ApplicationClient.uiManager.onKeyEvent(action, key, modifier);

        if (Application.gameManager.isLoaded()) {
            notify(observer -> observer.onKeyPress(key));
            notify(observer -> observer.onKeyEvent(action, key, modifier));
        }
    }

    public void onWindowEvent(GameEventListener.Action action) {
        ApplicationClient.uiManager.onWindowEvent(action);
    }

    public static void onMouseEvent(GameEventListener.Action action, GameEventListener.MouseButton button, int x, int y, boolean rightPressed) {
        GameEvent event = new GameEvent(new MouseEvent(x, y, button, action));

        if (ApplicationClient.uiManager.onMouseEvent(event, action, button, x, y, rightPressed)) {
            return;
        }

        if (Application.gameManager.isLoaded()) {
            Application.gameManager.getGame().getInteraction().onMoveEvent(event, action, button, x, y, rightPressed);
            if (ApplicationShortcutManager.onMouseEvent(event, action, button, x, y, rightPressed)) {
                return;
            }
        }

//        ApplicationClient.uiManager.onMouseEvent(action, button, x, y, rightPressed);
    }

    public void update() {
        if (Application.gameManager.isLoaded()) {
            Application.gameManager.getGame().update();
        }

        // Reload data
        if (_nextDataUpdate < System.currentTimeMillis()) {
            _nextDataUpdate = System.currentTimeMillis() + Constant.RELOAD_DATA_INTERVAL;
            ApplicationClient.addTask(() -> {
                long lastResModified = Utils.getLastDataModified();
                if (Application.data.needUIRefresh || lastResModified > _dataLastModified) {
                    Application.data.needUIRefresh = false;
                    _dataLastModified = lastResModified;
                    ApplicationClient.uiManager.reload();
                    ApplicationClient.spriteManager.reload();
                    ApplicationClient.notify(GameObserver::onReloadUI);
                    Log.info("Data reloaded");
                    ApplicationClient.uiManager.restore();
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