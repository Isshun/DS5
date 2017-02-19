package org.smallbox.faraway.client;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.MouseEvent;
import org.smallbox.faraway.client.lua.LuaControllerManager;
import org.smallbox.faraway.client.manager.InputManager;
import org.smallbox.faraway.client.renderer.GDXRenderer;
import org.smallbox.faraway.client.renderer.MainRenderer;
import org.smallbox.faraway.client.renderer.SpriteManager;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.ApplicationConfig;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.util.Utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class ApplicationClient {
    public static final DependencyInjector dependencyInjector;
    private static Collection<GameClientObserver>     _observers = new LinkedBlockingQueue<>();

    // Both
    public static final ApplicationConfig APPLICATION_CONFIG;

    // Client
    public static final UIManager               uiManager;
    public static final UIEventManager          uiEventManager;
    public static final InputManager            inputManager;
    public static final ClientLuaModuleManager  luaModuleManager;
    public static final LuaControllerManager    luaControllerManager;

    public static final SpriteManager           spriteManager;
    public static final GDXRenderer             gdxRenderer;
    public static final MainRenderer            mainRenderer;

    public static boolean isLoaded = false;
    public static List<ApplicationShortcutStrategy> shortcutStrategies = new CopyOnWriteArrayList<>();

    public static class ApplicationShortcutStrategy {
        public GameEventListener.Key key;
        public Runnable runnable;

        public ApplicationShortcutStrategy(GameEventListener.Key key, Runnable runnable) {
            this.key = key;
            this.runnable = runnable;
        }
    }

    static {
        dependencyInjector = DependencyInjector.getInstance();
        dependencyInjector.setShortcutBindingStrategy((key, runnable) -> {
            if (shortcutStrategies.stream().noneMatch(strategy -> strategy.key == key)) {
                shortcutStrategies.add(new ApplicationShortcutStrategy(key, runnable));
            } else {
                Log.warning(ApplicationClient.class, "Add already existing shortcut");
            }
        });
        uiManager = dependencyInjector.create(UIManager.class);
        uiEventManager = dependencyInjector.create(UIEventManager.class);
        spriteManager = dependencyInjector.create(SpriteManager.class);
        gdxRenderer = dependencyInjector.create(GDXRenderer.class);
        mainRenderer = dependencyInjector.create(MainRenderer.class);
        luaModuleManager = dependencyInjector.create(ClientLuaModuleManager.class);
        luaControllerManager = dependencyInjector.create(LuaControllerManager.class);

        // Create APPLICATION_CONFIG
        APPLICATION_CONFIG = loadConfig();

        // Create input processor
        inputManager = new InputManager();
    }

    private static ApplicationConfig loadConfig() {
        Log.info("Load application APPLICATION_CONFIG");
        try (FileReader fileReader = new FileReader(new File(Application.BASE_PATH, "data/config.json"))) {
            return new Gson().fromJson(fileReader, ApplicationConfig.class);
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

    public static void          addObserver(GameClientObserver observer) {
        assert observer != null;
        _observers.add(observer);
    }

    public static void                 removeObserver(GameObserver observer) { assert observer != null; _observers.remove(observer); }
    public ApplicationConfig getConfig() { return APPLICATION_CONFIG; }

    public static void onKeyEvent(GameEventListener.Action action, GameEventListener.Key key, GameEventListener.Modifier modifier) {
//        ApplicationShortcutManager.onKeyPress(key, modifier);

        if (ApplicationClient.uiManager.onKeyEvent(action, key, modifier)) {
            return;
        }

        if (Application.gameManager.isLoaded()) {
            GameEvent event = new GameEvent(key);
            ApplicationClient.notify(observer -> observer.onKeyPressWithEvent(event, key));
            ApplicationClient.notify(observer -> observer.onKeyEvent(action, key, modifier));
        }

        // TODO: A deplacer dans ApplicationShortcutManage
        // Call shortcut strategy
        if (action == GameEventListener.Action.RELEASED) {
            shortcutStrategies.stream()
                    .filter(strategy -> strategy.key == key)
                    .forEach(strategy -> strategy.runnable.run());
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

        // Passe l'evenement à l'ui manager
        if (ApplicationClient.uiManager.onMouseEvent(event, action, button, x, y, rightPressed)) {
            return;
        }

//        // Lance un evenement clickOnMap si le jeu est lancé
//        if (Application.gameManager.isLoaded()) {
//
//            if (action == GameEventListener.Action.RELEASED) {
//                System.out.println("Click on map at pixel: " + x + " x " + y);
//
//                Viewport viewport = ApplicationClient.mainRenderer.getViewport();
//                ParcelModel parcel = WorldHelper.getParcel(viewport.getWorldPosX(x), viewport.getWorldPosY(y), viewport.getFloor());
//                if (parcel != null) {
//                    System.out.println("Click on map at parcel: " + parcel.x + " x " + parcel.y);
//                    Application.notify(observer -> observer.onClickOnParcel(Collections.singletonList(parcel)));
//                }
////            Application.gameManager.getGame().getInteraction().onMoveEvent(event, action, button, x, y, rightPressed);
////            if (ApplicationShortcutManager.onMouseEvent(event, action, button, x, y, rightPressed)) {
////                return;
////            }
//            }
//        }
    }

    public static void notify(Consumer<GameClientObserver> action) {

        Application.getObservers().forEach(observer -> {
            if (observer instanceof GameClientObserver) {
                action.accept((GameClientObserver) observer);
            }
        });

        try {
            _observers.forEach(action);
        } catch (Error | RuntimeException e) {
            setRunning(false);
            e.printStackTrace();
        }
    }

    public static Collection<? extends GameObserver> getObservers() {
        return _observers;
    }

    public static void exitWithError() {
        _isRunning = false;
        Gdx.app.exit();
    }
}