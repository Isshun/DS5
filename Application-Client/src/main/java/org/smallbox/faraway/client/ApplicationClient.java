package org.smallbox.faraway.client;

import com.badlogic.gdx.Gdx;
import org.smallbox.faraway.client.lua.LuaControllerManager;
import org.smallbox.faraway.client.manager.ShortcutManager;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.layer.BaseLayer;
import org.smallbox.faraway.client.render.layer.GDXRenderer;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.engine.GameEvent;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.ApplicationClientListener;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.util.Utils;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class ApplicationClient {
    private static Collection<GameClientObserver>     _observers = new LinkedBlockingQueue<>();

    private static final DependencyInjector dependencyInjector = DependencyInjector.getInstance();

    static {
        Application.clientListener = () -> {
            LayerManager layerManager = dependencyInjector.getDependency(LayerManager.class);
            layerManager.getLayers().forEach(BaseLayer::onInitLayer);
        };

        // Application client interface
        dependencyInjector.setClientInterface((label, key, runnable) ->
                dependencyInjector.getDependency(ShortcutManager.class).addBinding(label, key, runnable));
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

    public static void onKeyEvent(GameEventListener.Action action, int key, GameEventListener.Modifier modifier) {
//        ApplicationShortcutManager.onKeyPress(key, modifier);

        if (dependencyInjector.getDependency(UIManager.class).onKeyEvent(action, key, modifier)) {
            return;
        }

        if (dependencyInjector.getDependency(GameManager.class).isLoaded()) {
            GameEvent event = new GameEvent(key);
            ApplicationClient.notify(observer -> observer.onKeyPressWithEvent(event, key));
            ApplicationClient.notify(observer -> observer.onKeyEvent(action, key, modifier));
        }

        // TODO: A deplacer dans ApplicationShortcutManage
        // Call shortcut strategy
        if (action == GameEventListener.Action.RELEASED) {
            dependencyInjector.getDependency(ShortcutManager.class).action(key);
        }
    }

    public void onWindowEvent(GameEventListener.Action action) {
        dependencyInjector.getDependency(UIManager.class).onWindowEvent(action);
    }

    public static void onMouseEvent(GameEventListener.Action action, int button, int x, int y, boolean rightPressed) {

        // Passe l'evenement à l'ui manager
        if (dependencyInjector.getDependency(UIManager.class).onMouseEvent(action, button, x, y, rightPressed)) {
            return;
        }

        if (action == GameEventListener.Action.PRESSED) {
            ApplicationClient.notify(obs -> obs.onMousePress(x, y, button));
        }

        if (action == GameEventListener.Action.MOVE) {
            ApplicationClient.notify(observer -> observer.onMouseMove(x, y, button));
        }

//        // Lance un evenement clickOnMap si le jeu est lancé
//        if (dependencyInjector.getDependency(GameManager.class).isLoaded()) {
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
////            dependencyInjector.getDependency(GameManager.class).getGame().getInteraction().onMoveEvent(event, action, button, x, y, rightPressed);
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
        } catch (Exception e) {
            setRunning(false);
            throw new GameException(ApplicationClient.class, e, "Error during notify");
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