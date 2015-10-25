package org.smallbox.faraway.core;

import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.engine.renderer.LightRenderer;
import org.smallbox.faraway.core.engine.renderer.ParticleRenderer;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.model.GameConfig;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.model.planet.RegionInfo;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.core.util.Utils;
import org.smallbox.faraway.ui.MenuBase;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.mainMenu.MainMenu;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Application implements GameEventListener {
    private static Application              _self;

    private MenuBase                        _menu;
    private MainMenu                        _mainMenu;
    private boolean                         _isRunning = true;
    private final BlockingQueue<Runnable>   _queue = new LinkedBlockingQueue<>();
    private GDXInputProcessor               _inputProcessor;
    private long                            _nextDataUpdate;
    private long                            _dataLastModified = Utils.getLastDataModified();

    public static Application getInstance() {
        if (_self == null) {
            _self = new Application();
        }
        return _self;
    }

    public void addTask(Runnable runnable) { _queue.add(runnable); }
    public void setRunning(boolean isRunning) { _isRunning = isRunning; }
    public void setInputProcessor(GDXInputProcessor inputProcessor) { _inputProcessor = inputProcessor; }
    public GDXInputProcessor getInputProcessor() { return _inputProcessor; }
    public boolean isRunning() { return _isRunning; }

    public void create(GDXRenderer renderer, LightRenderer lightRenderer, ParticleRenderer particleRenderer, GameData data, GameConfig config) {
        _mainMenu = new MainMenu(renderer);
        _mainMenu.open();
    }

    @Override
    public void onKeyEvent(Action action, Key key, Modifier modifier) {
        // Events for menu
        if (!GameManager.getInstance().isRunning()) {
            if (_menu != null && _menu.isVisible()) {
                if (_menu.checkKey(key)) {
                    return;
                }
            }
        }

        ApplicationShortcutManager.onKeyPress(key, modifier);

        UserInterface.getInstance().onKeyEvent(action, key, modifier);

        if (GameManager.getInstance().isRunning()) {
            GameManager.getInstance().getGame().notify(observer -> observer.onKeyPress(key));
        }
    }

    @Override
    public void onWindowEvent(Action action) {
        UserInterface.getInstance().onWindowEvent(action);
    }

    @Override
    public void onMouseEvent(Action action, MouseButton button, int x, int y, boolean rightPressed) {
        if (GameManager.getInstance().isRunning()) {
            ApplicationShortcutManager.onMouseEvent(action, button, x, y, rightPressed);
        } else {
            _mainMenu.onMouseEvent(action, button, x, y);
        }
    }

    public void newGame(String fileName, RegionInfo  regionInfo) {
        _mainMenu.close();
        GameManager.getInstance().create(fileName, regionInfo);
    }

    public void loadGame(String fileName) {
        _mainMenu.close();
        GameManager.getInstance().load(fileName);
    }

    public void render(GDXRenderer renderer, Viewport viewport, long lastRenderInterval) {
        if (_mainMenu != null && _mainMenu.isOpen()) {
            _mainMenu.draw(renderer, viewport);
            return;
        }

        if (GameManager.getInstance().isRunning()) {
            GameManager.getInstance().getGame().render(renderer, viewport, lastRenderInterval);
        }

        try {
            if (!_queue.isEmpty()) {
                _queue.take().run();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        if (GameManager.getInstance().isRunning()) {
            GameManager.getInstance().getGame().update();
        }

        // Reload data
        if (_nextDataUpdate < System.currentTimeMillis()) {
            _nextDataUpdate = System.currentTimeMillis() + Constant.RELOAD_DATA_INTERVAL;
            Application.getInstance().addTask(() -> {
                long lastResModified = Utils.getLastDataModified();
                if (GameData.getData().needUIRefresh || lastResModified > _dataLastModified) {
                    GameData.getData().needUIRefresh = false;
                    _dataLastModified = lastResModified;
                    UserInterface.getInstance().reload();
                    Game.getInstance().notify(GameObserver::onReloadUI);
                    Log.info("Data reloaded");
                }
            });
        }
    }
}
