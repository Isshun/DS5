package org.smallbox.faraway.client;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import org.smallbox.faraway.client.lua.LuaControllerManager;
import org.smallbox.faraway.client.manager.InputManager;
import org.smallbox.faraway.client.manager.ShortcutManager;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.layer.GDXRenderer;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.common.ApplicationConfig;
import org.smallbox.faraway.common.GameCommon;
import org.smallbox.faraway.common.GameEventListener;
import org.smallbox.faraway.common.GameException;
import org.smallbox.faraway.common.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.common.task.TaskManager;
import org.smallbox.faraway.common.util.FileUtils;
import org.smallbox.faraway.common.util.Log;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class ApplicationClient {
    public static final String BASE_PATH = "W:\\projects\\desktop\\FarAway\\Application";

    private static Collection<GameClientObserver>     _observers = new LinkedBlockingQueue<>();

    public static final DependencyInjector      dependencyInjector;

    // Both
    public static final ApplicationConfig       applicationConfig;

    // Both
    public static final Data data;

    // Client
    public static final UIManager               uiManager;
    public static final UIEventManager          uiEventManager;
    public static final GameEventManager        gameEventManager;
    public static final InputManager            inputManager;
    public static final ClientLuaModuleManager  luaModuleManager;
    public static final LuaControllerManager    luaControllerManager;
    public static final SelectionManager        selectionManager;
    public static final BridgeClientKyro BRIDGE_CLIENT;
    public static TaskManager taskManager;

    public static final SpriteManager           spriteManager;
    public static final GDXRenderer             gdxRenderer;
    public static final LayerManager            layerManager;
    public static GameCommon game;

    public interface ApplicationClientInterface {
        void onShortcutBinding(String label, int key, Runnable runnable);
    }

    private ApplicationClientInterface _clientInterface;
    private static final Collection<Object> _gameShortcut = new LinkedBlockingQueue<>();

    public static final ShortcutManager shortcutManager;
    public static boolean isLoaded;

    static {
        FileUtils.BASE_PATH = ApplicationClient.BASE_PATH;

        dependencyInjector = DependencyInjector.getInstance();
        dependencyInjector.addInject(ApplicationClient::doInjectShortcut);

        taskManager = dependencyInjector.create(TaskManager.class);
        taskManager.setRunInterface(runnable -> Gdx.app.postRunnable(runnable));
        shortcutManager = dependencyInjector.create(ShortcutManager.class);
        uiManager = dependencyInjector.create(UIManager.class);
        uiEventManager = dependencyInjector.create(UIEventManager.class);
        gameEventManager = dependencyInjector.create(GameEventManager.class);
        spriteManager = dependencyInjector.create(SpriteManager.class);
        selectionManager = dependencyInjector.create(SelectionManager.class);
        gdxRenderer = dependencyInjector.create(GDXRenderer.class);
        layerManager = dependencyInjector.create(LayerManager.class);
        luaModuleManager = dependencyInjector.create(ClientLuaModuleManager.class);
        luaControllerManager = dependencyInjector.create(LuaControllerManager.class);
        BRIDGE_CLIENT = dependencyInjector.create(BridgeClientKyro.class);
        data = dependencyInjector.create(Data.class);

        // Create applicationConfig
        applicationConfig = loadConfig();

        // Create input processor
        inputManager = new InputManager();
    }

    // TODO: methode appelée plusieurs fois (2)
    private static void doInjectShortcut(Object host) {
        if (!_gameShortcut.contains(host)) {
            _gameShortcut.add(host);
            for (Method method : host.getClass().getDeclaredMethods()) {
                method.setAccessible(true);
                GameShortcut gameShortcut = method.getAnnotation(GameShortcut.class);
                if (gameShortcut != null) {

                    Log.verbose(String.format("Try to inject %s to %s", method.getName(), host.getClass().getSimpleName()));
                    shortcutManager.addBinding(host.getClass().getName() + "." + method.getName(), gameShortcut.key(), () -> {
                        try {
                            method.invoke(host);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }
    }

    private static ApplicationConfig loadConfig() {
        Log.info("Load application applicationConfig");
        File configFile = new File(ApplicationClient.BASE_PATH, "data/config.json");
        if (configFile.exists()) {
            try (FileReader fileReader = new FileReader(configFile)) {
                return new Gson().fromJson(fileReader, ApplicationConfig.class);
            } catch (IOException e) {
                throw new GameException(ApplicationClient.class, e, "Unable to read config file");
            }
        }
        return null;
    }

    private static boolean                          _isRunning = true;
    private long                                    _nextDataUpdate;
//    private long                                    _dataLastModified = Utils.getLastDataModified();

    public static void          addTask(Runnable runnable) { Gdx.app.postRunnable(runnable); }
    public static void          setRunning(boolean isRunning) { _isRunning = isRunning; if (!isRunning) Gdx.app.exit(); }
    public boolean              isRunning() { return _isRunning; }

    public static void          addObserver(GameClientObserver observer) {
        assert observer != null;
        _observers.add(observer);
    }

//    public static void                 removeObserver(GameObserver observer) { assert observer != null; _observers.remove(observer); }

    public ApplicationConfig getConfig() { return applicationConfig; }

    public static void onKeyEvent(GameEventListener.Action action, int key, GameEventListener.Modifier modifier) {
//        ApplicationShortcutManager.onKeyPress(key, modifier);

//        if (ApplicationClient.uiManager.onKeyEvent(action, key, modifier)) {
//            return;
//        }

//        if (Application.gameManager.isLoaded()) {
//            GameEvent event = new GameEvent(key);
//            ApplicationClient.notify(observer -> observer.onKeyPressWithEvent(event, key));
//            ApplicationClient.notify(observer -> observer.onKeyEvent(action, key, modifier));
//        }

        // TODO: A deplacer dans ApplicationShortcutManage
        // Call shortcut strategy
        if (action == GameEventListener.Action.RELEASED) {
            shortcutManager.action(key);
        }
    }

//    public void onWindowEvent(GameEventListener.Action action) {
//        ApplicationClient.uiManager.onWindowEvent(action);
//    }

    public static void onMouseEvent(GameEventListener.Action action, int button, int x, int y, boolean rightPressed) {

//        // Passe l'evenement à l'ui manager
//        if (ApplicationClient.uiManager.onMouseEvent(action, button, x, y, rightPressed)) {
//            return;
//        }

        if (action == GameEventListener.Action.PRESSED) {
            ApplicationClient.notify(obs -> obs.onMousePress(x, y, button));
        }

        if (action == GameEventListener.Action.MOVE) {
            ApplicationClient.notify(observer -> observer.onMouseMove(x, y, button));
        }

//        // Lance un evenement clickOnMap si le jeu est lancé
//        if (Application.gameManager.isLoaded()) {
//
//            if (action == GameEventListener.Action.RELEASED) {
//                System.out.println("Click on map at pixel: " + x + " x " + y);
//
//                Viewport viewport = ApplicationClient.layerManager.getViewport();
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

//        Application.getObservers().forEach(observer -> {
//            if (observer instanceof GameClientObserver) {
//                action.accept((GameClientObserver) observer);
//            }
//        });

        try {
            _observers.forEach(action);
        } catch (Exception e) {
            setRunning(false);
            throw new GameException(ApplicationClient.class, e, "Error during notify");
        }
    }

//    public static Collection<? extends GameObserver> getObservers() {
//        return _observers;
//    }

    public static void exitWithError() {
        _isRunning = false;
        Gdx.app.exit();
    }
}