package org.smallbox.faraway.core.game;

import org.reflections.Reflections;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.ModuleInfoAnnotation;
import org.smallbox.faraway.core.engine.module.AbsGameModule;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.ModuleInfo;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.game.model.planet.PlanetModel;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.util.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Game {

    private long _nextTick;

    public <T> T getModule(Class<T> cls) {
        return (T) _modules.stream().filter(module -> module.getClass() == cls).findAny().orElse(null);
    }

    public enum GameStatus {UNINITIALIZED, CREATED, STOPPED, STARTED}
    public static final int[]               TICK_INTERVALS = {-1, 400, 200, 100, 50, 25, 10, 1};

    // Update
    private long                            _nextUpdate;
    private int                             _tickInterval = TICK_INTERVALS[1];

    private boolean                         _isRunning;
    private final GameInfo                  _info;
    private PlanetModel                     _planet;
    private int                             _hour = 7;
    private int                             _day;
    private int                             _year;
    private static int                      _tick;
    private Map<String, Boolean>            _displays;
    private int                             _speed = 1;
    private int                             _lastSpeed = 1;
    private double                          _avance;
    private List<AbsGameModule>             _modules = new ArrayList<>();
    private GameStatus                      _status = GameStatus.UNINITIALIZED;
    private ExecutorService                 _executorService = Executors.newSingleThreadExecutor();

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
        _isRunning = !_isRunning;
        Application.notify(_isRunning ? GameObserver::onGameResume : GameObserver::onGamePaused);
    }

    public void                             toggleSpeed0() { setSpeed(_speed == 0 ? _lastSpeed : 0); }

    public int                              getHour() { return _hour; }
    public int                              getDay() { return _day; }
    public int                              getYear() { return _year; }
    public int                              getTickPerHour() { return Application.config.game.tickPerHour; }
    public int                              getHourPerDay() { return _planet.getInfo().dayDuration; }
    public PlanetModel                      getPlanet() { return _planet; }
    public long                             getTick() { return _tick; }
    public long                             getNextTick() { return _nextTick; }
    public int                              getSpeed() { return _speed; }
    public int                              getLastSpeed() { return _lastSpeed; }
    public Collection<AbsGameModule>        getModules() { return _modules; }
    public GameStatus getState() { return _status; }
    public long                             getNextUpdate() { return _nextUpdate; }
    public int                              getTickInterval() { return _tickInterval; }

    public Game(GameInfo info) {
//        Application.APPLICATION_CONFIG.game.updateInterval

        _info = info;
        _isRunning = true;

        if (info.planet != null) {
            _planet = new PlanetModel(info.planet);
        }

        _displays = new HashMap<>();
        _tick = 0;
    }

    public void loadModules() {
        Log.info("Load game modules");

        // Find game modules
        new Reflections("org.smallbox.faraway").getSubTypesOf(AbsGameModule.class).stream()
                .filter(cls -> !Modifier.isAbstract(cls.getModifiers()))
//                .filter(cls -> _allowedModulesNames.contains(cls.getSimpleName()))
                .forEach(cls -> {
                    try {
                        Log.info("Find game module: " + cls.getSimpleName());
                        AbsGameModule module = cls.getConstructor().newInstance();

                        if (cls.isAnnotationPresent(ModuleInfoAnnotation.class)) {
                            Log.info("Find game module: " + cls.getAnnotation(ModuleInfoAnnotation.class).name());
                            module.setUpdateInterval(cls.getAnnotation(ModuleInfoAnnotation.class).updateInterval());
                        }

                        module.setInfo(ModuleInfo.fromName(cls.getSimpleName()));
                        _modules.add(module);
                    } catch (NoSuchMethodException e) {
                        Log.warning(ModuleManager.class, "Unable to instantiate " + cls.getName() + " - No default constructor");
                    } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });

        // Load game modules
        boolean moduleHasBeenLoaded;
        do {
            // Try to onLoadModule first module with required dependencies
            moduleHasBeenLoaded = false;
            for (AbsGameModule module: _modules) {
                if (module.isActivate() && !module.isLoaded() && module.hasRequiredDependencies(_modules)) {
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
        _modules.forEach(Application.dependencyInjector::register);
        _modules.forEach(ModuleBase::create);
    }

    /**
     * Call game modules onGameCreateObserver method and construct renders
     * createGame() is call before game onLoadModule or createGame
     * createGame() is call before startGame()
     */
    public void createModules() {
        Log.info("============ CREATE GAME ============");

        // Call onGameCreateObserver method to each modules
//        _modules = Application.moduleManager.getGameModules().stream().filter(ModuleBase::isLoaded).collect(Collectors.toList());
        _modules.sort((o1, o2) -> o2.getModulePriority() - o1.getModulePriority());
        _modules.forEach(module -> module.createGame(this));

        _status = GameStatus.CREATED;
    }

    public void start() {
        _modules.stream().filter(ModuleBase::isLoaded).forEach(module -> module.startGame(this));

        Application.notify(observer -> observer.onHourChange(_hour));
        Application.notify(observer -> observer.onDayChange(_day));
        Application.notify(observer -> observer.onYearChange(_year));
        Application.notify(observer -> observer.onGameSpeedChange(_speed));

        _status = GameStatus.STARTED;
    }

    public void stop() {
        _status = GameStatus.STOPPED;
    }

    /**
     * Update
     */
    public void update() {
        Log.debug(Game.class, "Game update (tick: %d)", _tick + 1);

        _tick += 1;

        _modules.forEach(module -> module.updateGame(this, _tick));

        updateHour();
    }

    /**
     * Update hour
     */
    private void updateHour() {
        if (_tick % Application.config.game.tickPerHour == 0) {
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
    }

    public GameInfo getInfo() { return _info; }

    public void setSpeed(int speed) {
        _lastSpeed = _speed;
        _speed = Utils.bound(1, TICK_INTERVALS.length - 1, speed);
        if (_speed != _lastSpeed) {
            _tickInterval = TICK_INTERVALS[_speed];
            _isRunning = speed > 0;
            Application.notify(observer -> observer.onGameSpeedChange(_speed));
        }
    }

    public void launchBackgroundThread(GameManager.GameListener listener) {
        _executorService.submit(() -> {
            Log.notice("launchBackgroundThread");
            while (_status != GameStatus.STOPPED) {
                try {
                    long time = System.currentTimeMillis();

                    if (_status == GameStatus.STARTED && _isRunning) {
                        update();

                        Application.notify(observer -> observer.onGameUpdate(this));

                        if (listener != null) {
                            listener.onGameUpdate(this);
                        }

                        _avance = 0;
                    }

                    long length = System.currentTimeMillis() - time;
                    _nextTick = System.currentTimeMillis() + _tickInterval - length;
//                    Thread.sleep(_tickInterval - length);
                    Thread.sleep(_tickInterval);
                } catch (Exception e) {
                    _status = GameStatus.STOPPED;
                    e.printStackTrace();
                }
            }
        });
    }
}
