package org.smallbox.faraway.core.game;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.lua.LuaControllerManager;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.client.renderer.GDXRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.core.game.model.planet.PlanetModel;
import org.smallbox.faraway.client.ui.ApplicationClient;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.client.ui.GameActionExtra;
import org.smallbox.faraway.client.ui.GameSelectionExtra;
import org.smallbox.faraway.client.ui.UICursor;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Game {
    public enum GameModuleState {UNINITIALIZED, CREATED, STARTED}
    public static final int[]               TICK_INTERVALS = {-1, 320, 200, 75, 10};

    // Update
    private long                            _nextUpdate;
    private int                             _tickInterval = TICK_INTERVALS[3];

    // Render
    private int                             _frame;
    private double                          _animationProgress;
    private boolean[]                       _directions = new boolean[4];

    private boolean                         _isRunning;
    private GameActionExtra                 _gameAction;
    private GameSelectionExtra              _selector;
    private final GameInfo                  _info;
    private PlanetModel                     _planet;
    private int                             _hour = 5;
    private int                             _day;
    private int                             _year;
    private static int                      _tick;
    private Map<String, Boolean>            _displays;
    private int                             _speed = 1;
    private int                             _lastSpeed = 1;
    private List<GameModule>                _modules;
    private GameModuleState                 _state = GameModuleState.UNINITIALIZED;

    public void setDisplay(String displayName, boolean isActive) {
        _displays.put(displayName, isActive);
        Application.notify(observer -> observer.onDisplayChange(displayName, _displays.get(displayName)));
    }

    public void toggleDisplay(String displayName) {
        _displays.put(displayName, !_displays.containsKey(displayName) || !_displays.get(displayName));
        Application.notify(observer -> observer.onDisplayChange(displayName, _displays.get(displayName)));
    }

    public boolean                          isRunning() { return _isRunning; }
    public boolean                          hasDisplay(String displayName) { return _displays.containsKey(displayName) && _displays.get(displayName); }
    public void                             toggleRunning() { setRunning(!_isRunning); }
    public void                             toggleSpeed0() { setSpeed(_speed == 0 ? _lastSpeed : 0); }

    public int                              getHour() { return _hour; }
    public int                              getDay() { return _day; }
    public int                              getYear() { return _year; }
    public int                              getTickPerHour() { return Application.configurationManager.game.tickPerHour; }
    public int                              getHourPerDay() { return _planet.getInfo().dayDuration; }
    public Viewport                         getViewport() { return _viewport; }
    public PlanetModel                      getPlanet() { return _planet; }
    public long                             getTick() { return _tick; }
    public GameActionExtra                  getInteraction() { return _gameAction; }
    public GameSelectionExtra               getSelector() { return _selector; }
    public int                              getSpeed() { return _speed; }
    public int                              getLastSpeed() { return _lastSpeed; }
    public Collection<GameModule>           getModules() { return _modules; }
    public GameModuleState                  getState() { return _state; }

    public Game(GameInfo info) {
        _selector = new GameSelectionExtra();
        _gameAction = new GameActionExtra(_viewport, _selector);
        _info = info;
        _isRunning = true;
        _planet = new PlanetModel(info.planet);
        _directions = ApplicationClient.inputManager.getDirection();
        _displays = new HashMap<>();
        _tick = 0;
    }

    /**
     * Call game modules onGameCreate method and construct renders
     * createGame() is call before game onLoad or createGame
     * createGame() is call before startGame()
     */
    public void createModules() {
        Log.info("============ CREATE GAME ============");

        // Call onGameCreate method to each modules
        _modules = Application.moduleManager.getGameModules().stream().filter(ModuleBase::isLoaded).collect(Collectors.toList());
        _modules.sort((o1, o2) -> o2.getModulePriority() - o1.getModulePriority());
        _modules.forEach(module -> module.createGame(this));

        _state = GameModuleState.CREATED;
    }

    public void start() {
        // Notify modules, renders and controllers
        Application.moduleManager.gameStart(this, _modules);
        ApplicationClient.mainRenderer.gameStart(this);
        LuaControllerManager.getInstance().gameStart(this);

        Application.notify(observer -> observer.onHourChange(_hour));
        Application.notify(observer -> observer.onDayChange(_day));
        Application.notify(observer -> observer.onYearChange(_year));
        Application.notify(observer -> observer.onFloorChange(7));
        Application.notify(observer -> observer.onGameStart(this));

        _viewport.setPosition(-500, -3800, 7);

        _state = GameModuleState.STARTED;
    }

    public void                     clearCursor() { _gameAction.setCursor(null); }
//    public void                     clearSelection() { _selector.clear(); }
    public void                     setCursor(UICursor cursor) { _gameAction.setCursor(cursor); }
    public void                     setCursor(String cursorName) { _gameAction.setCursor(Application.data.getCursor(cursorName)); }

    public void update() {
        // Update
        if (_nextUpdate < System.currentTimeMillis() && _isRunning) {
            _nextUpdate = System.currentTimeMillis() + _tickInterval;
            _tick += 1;
            ApplicationClient.mainRenderer.gameUpdate(this);
            LuaControllerManager.getInstance().gameUpdate(this);
            onUpdate(_tick);
        }
    }

    protected void onUpdate(int tick) {
        if (!_isRunning) {
            return;
        }

        Application.moduleManager.getGameModules().stream().filter(ModuleBase::isLoaded).forEach(module -> module.updateGame(this, tick));

        if (tick % Application.configurationManager.game.tickPerHour == 0) {
            if (++_hour >= _planet.getInfo().dayDuration) {
                _hour = 0;
                if (++_day >= _planet.getInfo().yearDuration) {
                    _day = 1;
                    _year++;
                    Application.notify(observer -> observer.onYearChange(_year));
                }
                Application.notify(observer -> observer.onDayChange(_day));
            }
            Application.notify(observer -> observer.onHourChange(_hour));
        }

        _tick = tick;

        Log.info("Tick: " + tick);
    }

    public GameInfo getInfo() { return _info; }

    public void setRunning(boolean isRunning) {
        _isRunning = isRunning;
        Application.notify(isRunning ? GameObserver::onGameResume : GameObserver::onGamePaused);
    }

    public void render(GDXRenderer renderer, Viewport viewport) {
        // Draw
        if (!Application.gameManager.isRunning()) {
            _animationProgress = 1 - ((double) (_nextUpdate - System.currentTimeMillis()) / _tickInterval);
        }

        ApplicationClient.mainRenderer.onDraw(renderer, viewport, _animationProgress);

        if (_isRunning) {
            if (_directions[0]) { _viewport.move(20, 0); }
            if (_directions[1]) { _viewport.move(0, 20); }
            if (_directions[2]) { _viewport.move(-20, 0); }
            if (_directions[3]) { _viewport.move(0, -20); }
        }

        ApplicationClient.mainRenderer.onRefresh(_frame);

        // TODO
        try {
            ApplicationClient.uiManager.onRefresh(_frame);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        _gameAction.draw(renderer);
        _frame++;
    }

    public void setSpeed(int speed) {
        if (speed != 0) {
            _lastSpeed = speed;
        }
        _speed = speed;
        _tickInterval = TICK_INTERVALS[Math.max(0, Math.min(4, speed))];
        _isRunning = _tickInterval > 0;
        Application.notify(observer -> observer.onSpeedChange(speed));
    }

    public void createControllers() { LuaControllerManager.getInstance().gameCreate(this); }
}
