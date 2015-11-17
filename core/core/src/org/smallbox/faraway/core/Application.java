package org.smallbox.faraway.core;

import com.badlogic.gdx.Gdx;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.engine.renderer.SpriteManager;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.core.util.Utils;
import org.smallbox.faraway.ui.UserInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Application implements GameEventListener {
    private static Application              _self;
    private boolean                         _isRunning = true;
    private GDXInputProcessor               _inputProcessor;
    private long                            _nextDataUpdate;
    private long                            _dataLastModified = Utils.getLastDataModified();
    private List<GameObserver>              _observers = new ArrayList<>();

    public static Application getInstance() {
        if (_self == null) {
            _self = new Application();
        }
        return _self;
    }

    public void                 addTask(Runnable runnable) { Gdx.app.postRunnable(runnable); }
    public void                 setRunning(boolean isRunning) { _isRunning = isRunning; if (!isRunning) Gdx.app.exit(); }
    public void                 setInputProcessor(GDXInputProcessor inputProcessor) { _inputProcessor = inputProcessor; }
    public GDXInputProcessor    getInputProcessor() { return _inputProcessor; }
    public boolean              isRunning() { return _isRunning; }
    public void                 addObserver(GameObserver observer) { _observers.add(observer); }
    public void                 removeObserver(GameModule observer) {
        _observers.remove(observer);
    }

    @Override
    public void onKeyEvent(Action action, Key key, Modifier modifier) {
        ApplicationShortcutManager.onKeyPress(key, modifier);

        UserInterface.getInstance().onKeyEvent(action, key, modifier);

        if (GameManager.getInstance().isLoaded()) {
            notify(observer -> observer.onKeyPress(key));
        }
    }

    @Override
    public void onWindowEvent(Action action) {
        UserInterface.getInstance().onWindowEvent(action);
    }

    @Override
    public void onMouseEvent(Action action, MouseButton button, int x, int y, boolean rightPressed) {
        if (UserInterface.getInstance().onMouseEvent(action, button, x, y, rightPressed)) {
            return;
        }

        if (GameManager.getInstance().isLoaded()) {
            GameManager.getInstance().getGame().getInteraction().onMoveEvent(action, button, x, y, rightPressed);
            if (ApplicationShortcutManager.onMouseEvent(action, button, x, y, rightPressed)) {
                return;
            }
        }

//        UserInterface.getInstance().onMouseEvent(action, button, x, y, rightPressed);
    }

    public void update() {
        if (GameManager.getInstance().isLoaded()) {
            GameManager.getInstance().getGame().update();
        }

        // Reload data
        if (_nextDataUpdate < System.currentTimeMillis()) {
            _nextDataUpdate = System.currentTimeMillis() + Constant.RELOAD_DATA_INTERVAL;
            Application.getInstance().addTask(() -> {
                long lastResModified = Utils.getLastDataModified();
                if (Data.getData().needUIRefresh || lastResModified > _dataLastModified) {
                    Data.getData().needUIRefresh = false;
                    _dataLastModified = lastResModified;
                    UserInterface.getInstance().reload();
                    SpriteManager.getInstance().reload();
                    Application.getInstance().notify(GameObserver::onReloadUI);
                    Log.info("Data reloaded");
                    UserInterface.getInstance().restore();
                }
            });
        }
    }

    public void notify(Consumer<GameObserver> action) {
        _observers.stream().forEach(action::accept);
    }
}