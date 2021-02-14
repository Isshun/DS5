package org.smallbox.faraway.core.game;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.*;
import org.smallbox.faraway.core.module.AbsGameModule;
import org.smallbox.faraway.core.save.GameInfo;
import org.smallbox.faraway.game.planet.PlanetInfo;
import org.smallbox.faraway.game.planet.PlanetModel;
import org.smallbox.faraway.game.planet.RegionInfo;
import org.smallbox.faraway.util.Utils;
import org.smallbox.faraway.util.log.Log;

import java.util.*;

@GameObject
public class Game {
    @Inject private DependencyManager dependencyManager;
    @Inject private GameTime gameTime;

    public static long interval = 1000;
    private final ApplicationConfig config;
    private double _tickPerHour;
    private RegionInfo.RegionMonth currentMonth;

    public <T> T getModule(Class<T> cls) {
        return (T) _modules.stream().filter(module -> module.getClass() == cls).findAny().orElse(null);
    }

    public double byTick(double value) {
        return value / _tickPerHour;
    }

    public RegionInfo.RegionMonth getCurrentMonth() {
        return currentMonth;
    }

    @OnGameUpdate
    private void onGameUpdate() {
        _tick++;

        int hour = gameTime.getHour();
        int day = gameTime.getDay();
        int month = gameTime.getMonth();

        gameTime.add(1 / _tickPerHour);

        if (gameTime.getMonth() > month) {
            currentMonth = _regionInfo.months.stream().filter(m -> m.index == gameTime.getMonth()).findFirst().orElse(null);
            dependencyManager.callMethodAnnotatedBy(OnGameNewMonth.class);
        }

        if (gameTime.getDay() > day) {
            dependencyManager.callMethodAnnotatedBy(OnGameNewDay.class);
        }

        if (gameTime.getHour() > hour) {
            dependencyManager.callMethodAnnotatedBy(OnGameNewHour.class);
        }
    }

    public enum GameStatus {UNINITIALIZED, CREATED, STOPPED, STARTED}

    // Update
    private long                            _nextUpdate;
    private final int                             _tickInterval;

    private boolean                         _isRunning;
    private final GameInfo                  _info;
    private PlanetModel                     _planet;
    private int                             _tick;
    private final Map<String, Boolean>            _displays;
    private int                             _speed;
    private int                             _lastSpeed;
    private List<AbsGameModule> _modules;
    private GameStatus                      _status = GameStatus.UNINITIALIZED;
    private final PlanetInfo                _planetInfo;
    private final RegionInfo                _regionInfo;

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

    public void                             toggleRunning() {
        setRunning(!isRunning());
    }

    public void                             setRunning(boolean running) {
        _isRunning = running;
    }

    public double                           getTickPerHour() { return _tickPerHour; }
    public int                              getHourPerDay() { return _planet.getInfo().dayDuration; }
    public PlanetModel                      getPlanet() { return _planet; }
    public PlanetInfo                       getPlanetInfo() { return _planetInfo; }
    public RegionInfo                       getRegionInfo() { return _regionInfo; }
    public long                             getTick() { return _tick; }
    public int                              getSpeed() { return _speed; }
    public Collection<AbsGameModule>        getModules() { return _modules; }
    public GameStatus                       getState() { return _status; }
    public long                             getNextUpdate() { return _nextUpdate; }
    public int                              getTickInterval() { return _tickInterval; }

    public Game(GameInfo info, ApplicationConfig config) {
        this.config = config;
        _speed = config.game.startSpeed;
        _lastSpeed = config.game.startSpeed;
        _tickInterval = config.game.tickInterval;
        _tickPerHour = config.game.ticksPerHour[_speed];
        _planetInfo = info.planet;
        _regionInfo = info.region;

        _info = info;
        _isRunning = true;

        if (info.planet != null) {
            _planet = new PlanetModel(info.planet);
        }

        _displays = new HashMap<>();
        _tick = 0;
    }

    public void loadLayers() {
    }

    public void loadModules() {
        Log.info("Load game modules");

        _modules = new ArrayList<>(DependencyManager.getInstance().getSubTypesOf(AbsGameModule.class));

        // Load game modules
        boolean moduleHasBeenLoaded;
        do {
            // Try to onLoadModule first module with required dependencies
            moduleHasBeenLoaded = false;
            for (AbsGameModule module: _modules) {
                if (module.isActivate() && !module.isLoaded()) {
                    module.load();
                    moduleHasBeenLoaded = true;
                    break;
                }
            }
        } while (moduleHasBeenLoaded);

//        // Check all game modules has been loaded
//        if (checkAllModulesHasBeenLoaded(_modules)) {
//            System.out.println("All game modules has been loaded");
//        } else {
//            throw new RuntimeException("Some game modules could not be loaded");
//        }

        _modules.forEach(Application::addObserver);
//        _modules.forEach(Application.dependencyInjector::register);
    }

    /**
     * Call game modules onGameInit method and construct renders
     * createGame() is call before game onLoadModule or createGame
     * createGame() is call before startGame()
     */
    public void createModules() {
        Log.info("============ CREATE GAME ============");

        // Call onGameInit method to each modules
//        _modules = Application.moduleManager.getGameModules().stream().filter(ModuleBase::isLoaded).collect(Collectors.toList());
        _modules.sort((o1, o2) -> o2.getModulePriority() - o1.getModulePriority());
        _modules.forEach(AbsGameModule::createGame);

        _status = GameStatus.CREATED;
    }

    @OnGameStart
    private void onGameStart() {
        _status = GameStatus.STARTED;
    }

    @OnGameStop
    public void stop() {
        _status = GameStatus.STOPPED;
    }

    public GameInfo getInfo() { return _info; }

    public void setSpeed(int speed) {
        _lastSpeed = _speed;
        _speed = Utils.bound(1, config.game.ticksPerHour.length - 1, speed);
        if (_speed != _lastSpeed) {
//            _tickInterval = Application.config.game.ticksIntervals[_speed];
            _tickPerHour = config.game.ticksPerHour[_speed];
            _isRunning = speed > 0;
        }
    }

}
