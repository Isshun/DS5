package org.smallbox.faraway.core.game;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.config.Config;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.game.model.planet.PlanetModel;
import org.smallbox.faraway.util.Log;

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

    private boolean                         _isRunning;
//    private GameActionExtra                 _gameAction;
//    private GameSelectionExtra              _selector;
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
    public PlanetModel                      getPlanet() { return _planet; }
    public long                             getTick() { return _tick; }
//    public GameActionExtra                  getInteraction() { return _gameAction; }
//    public GameSelectionExtra               getSelector() { return _selector; }
    public int                              getSpeed() { return _speed; }
    public int                              getLastSpeed() { return _lastSpeed; }
    public Collection<GameModule>           getModules() { return _modules; }
    public GameModuleState                  getState() { return _state; }
    public long                             getNextUpdate() { return _nextUpdate; }
    public int                              getTickInterval() { return _tickInterval; }

    public Game(GameInfo info) {
//        _selector = new GameSelectionExtra();
//        _gameAction = new GameActionExtra(_viewport, _selector);
        _info = info;
        _isRunning = true;
        _planet = new PlanetModel(info.planet);
//        _directions = ApplicationClient.inputManager.getDirection();
        _displays = new HashMap<>();
        _tick = 0;
    }

    /**
     * Call game modules onGameCreate method and construct renders
     * createGame() is call before game onLoadModule or createGame
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
        Application.notify(observer -> observer.onHourChange(_hour));
        Application.notify(observer -> observer.onDayChange(_day));
        Application.notify(observer -> observer.onYearChange(_year));
        Application.notify(observer -> observer.onFloorChange(Config.FLOOR));

        _state = GameModuleState.STARTED;
    }

//    public void                     clearCursor() { _gameAction.setCursor(null); }
////    public void                     clearSelection() { _selector.clear(); }
//    public void                     setCursor(UICursor cursor) { _gameAction.setCursor(cursor); }
//    public void                     setCursor(String cursorName) { _gameAction.setCursor(Application.data.getCursor(cursorName)); }

    public void update() {
        _tick += 1;

        Application.moduleManager.getGameModules().stream().filter(ModuleBase::isLoaded).forEach(module -> module.updateGame(this, _tick));

        if (_tick % Application.configurationManager.game.tickPerHour == 0) {
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

//        Log.info("Tick: " + _tick);
    }

    public GameInfo getInfo() { return _info; }

    public void setRunning(boolean isRunning) {
        _isRunning = isRunning;
        Application.notify(isRunning ? GameObserver::onGameResume : GameObserver::onGamePaused);
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
}
