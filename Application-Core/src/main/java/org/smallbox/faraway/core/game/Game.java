package org.smallbox.faraway.core.game;

import org.smallbox.faraway.GameTaskManager;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.GameObject;
import org.smallbox.faraway.core.engine.module.AbsGameModule;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.game.model.planet.PlanetInfo;
import org.smallbox.faraway.core.game.model.planet.PlanetModel;
import org.smallbox.faraway.core.game.model.planet.RegionInfo;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfig;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.util.Utils;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@GameObject
public class Game {

    public static long interval = 1000;
    private final ApplicationConfig config;
    private double _tickPerHour;

    public <T> T getModule(Class<T> cls) {
        return (T) _modules.stream().filter(module -> module.getClass() == cls).findAny().orElse(null);
    }

    public double byTick(double value) {
        return value / _tickPerHour;
    }

    public enum GameStatus {UNINITIALIZED, CREATED, STOPPED, STARTED}

    // Update
    private long                            _nextUpdate;
    private int                             _tickInterval;

    private boolean                         _isRunning;
    private final GameInfo                  _info;
    private PlanetModel                     _planet;
    private GameTime                        _gameTime;
    private int                             _tick;
    private Map<String, Boolean>            _displays;
    private int                             _speed;
    private int                             _lastSpeed;
    private List<AbsGameModule>       _modules;
    private GameStatus                      _status = GameStatus.UNINITIALIZED;
    private final ScheduledExecutorService  _moduleScheduler = Executors.newScheduledThreadPool(1);
    private final ScheduledExecutorService  _moduleScheduler2 = Executors.newScheduledThreadPool(1);
    private final PlanetInfo                _planetInfo;
    private final RegionInfo                _regionInfo;

    public ScheduledExecutorService getScheduler() {
        return _moduleScheduler;
    }

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
        Application.notify(_isRunning ? GameObserver::onGameResume : GameObserver::onGamePaused);
    }

    public GameTime                         getTime() { return _gameTime; }
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
        _gameTime = new GameTime(config.game.startGameTime);
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

//        // Find game modules
//        new Reflections("org.smallbox.faraway").getSubTypesOf(AbsGameModule.class).stream()
//                .filter(cls -> !Modifier.isAbstract(cls.getModifiers()))
////                .filter(cls -> _allowedModulesNames.contains(cls.getSimpleName()))
//                .forEach(cls -> {
//                    try {
//                        Log.info("Find game module: " + cls.getSimpleName());
//                        AbsGameModule module = cls.getConstructor().newInstance();
//
//                        if (cls.isAnnotationPresent(ModuleInfoAnnotation.class)) {
//                            Log.info("Find game module: " + cls.getAnnotation(ModuleInfoAnnotation.class).name());
//                            module.setUpdateInterval(cls.getAnnotation(ModuleInfoAnnotation.class).updateInterval());
//                        }
//
//                        module.setInfo(ModuleInfo.fromName(cls.getSimpleName()));
//                        _modules.add(module);
//                    } catch (NoSuchMethodException e) {
//                        Log.warning(ModuleManager.class, "Unable to instantiate " + cls.getName() + " - No default constructor");
//                    } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
//                        e.printStackTrace();
//                    }
//                });

        _modules = Application.dependencyInjector.getGameModules();

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
        _modules.forEach(ModuleBase::create);
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
        _modules.forEach(module -> module.createGame(this));

        _status = GameStatus.CREATED;
    }

    public void start() {
        _modules.stream().filter(ModuleBase::isLoaded).forEach(module -> module.startGame(this));

        _status = GameStatus.STARTED;
    }

    public void stop() {
        _status = GameStatus.STOPPED;
        _moduleScheduler.shutdown();
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

    public void launchBackgroundThread(GameManager.GameListener listener) {

        _moduleScheduler.scheduleAtFixedRate(() -> {
            try {
                if (_isRunning) {
                    _tick += 1;
                    _gameTime.add(1 / _tickPerHour);

                    _modules.forEach(module -> module.updateGame(Game.this));

                    Application.notify(gameObserver -> gameObserver.onGameUpdate(Game.this));

                    if (listener != null) {
                        listener.onGameUpdate(this);
                    }
                }
            } catch (Exception e) {
                Log.error(e);
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);

        _moduleScheduler2.scheduleAtFixedRate(() -> {
            try {
                if (_isRunning) {
                    Application.dependencyInjector.getDependency(GameTaskManager.class).update();
                }
            } catch (Error e) {
                Log.error(e);
                e.printStackTrace();
            }
        }, 0, interval, TimeUnit.MILLISECONDS);

    }
}