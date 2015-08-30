package org.smallbox.faraway.game;

import org.reflections.Reflections;
import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.data.factory.world.WorldFactory;
import org.smallbox.faraway.data.serializer.GameSerializer;
import org.smallbox.faraway.engine.renderer.LightRenderer;
import org.smallbox.faraway.engine.renderer.ParticleRenderer;
import org.smallbox.faraway.game.module.GameModule;
import org.smallbox.faraway.game.module.character.*;
import org.smallbox.faraway.game.module.world.*;
import org.smallbox.faraway.game.model.GameConfig;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.planet.PlanetModel;
import org.smallbox.faraway.game.model.planet.RegionInfo;
import org.smallbox.faraway.game.model.planet.RegionModel;
import org.smallbox.faraway.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class Game {
    private static CharacterModule _characterModule;
    private static WorldModule _worldModule;
    private static JobManager 			_jobManager;

    private final ParticleRenderer      _particleRenderer;
    private final LightRenderer         _lightRenderer;

    private static Game 				_self;
    private final String                _fileName;
    private int 						_speed = 1;
    private GameConfig                  _config;
    private List<GameModule>            _modules = new ArrayList<>();
    private List<GameObserver>			_observers = new ArrayList<>();
    private GameSerializer.GameSave     _save;
    private PlanetModel                 _planet;
    private RegionModel                 _region;
    private int                         _hour = 6;
    private int                         _day;
    private int                         _year;
    private GameInfo                    _info = new GameInfo();

    public void                         toggleRunning() { _isRunning = !_isRunning; }
    public void                         addObserver(GameObserver observer) { _observers.add(observer); }
    public void                         setRunning(boolean running) { _isRunning = running; }
    public void                         setSpeed(int speed) { _speed = speed; }

    public boolean                      isRunning() { return _isRunning; }

    public static CharacterModule getCharacterManager() { return _characterModule; }
    public static WorldModule getWorldManager() { return _worldModule; }
    public static JobManager            getJobManager() { return _jobManager; }
    public static Game                  getInstance() { return _self; }
    public int                          getHour() { return _hour; }
    public int                          getDay() { return _day; }
    public int                          getYear() { return _year; }
    public Viewport                     getViewport() { return _viewport; }
    public static int                   getUpdate() { return _tick; }
    public PlanetModel                  getPlanet() { return _planet; }
    public String                       getFileName() { return _fileName; }
    public long                         getTick() { return _tick; }
    public RegionModel                  getRegion() { return _region; }

    private static int                  _tick;
    private Viewport 					_viewport;
    private boolean 					_isRunning;

    public Game(int width, int height, GameData data, GameConfig config, String fileName, ParticleRenderer particleRenderer, LightRenderer lightRenderer, RegionInfo regionInfo) {
        Log.debug("Game");

        _self = this;
        _config = config;
        _fileName = fileName;
        _isRunning = true;
        _viewport = new Viewport(400, 300);
        _particleRenderer = particleRenderer;
        _lightRenderer = lightRenderer;
        _tick = 0;

        Log.info("Game: onCreate");

        setRegion(regionInfo);

        Log.info("Game:\tdone");
    }

    public void setRegion(RegionInfo regionInfo) {
        if (regionInfo != null) {
            _planet = new PlanetModel(regionInfo.planet);
            _region = new RegionModel(_planet, regionInfo);
        }
    }

    public void init(WorldFactory factory) {
        for (Class<? extends GameModule> cls : new Reflections("org.smallbox.faraway").getSubTypesOf(GameModule.class)) {
            if (!Modifier.isAbstract(cls.getModifiers())) {
                try {
                    _modules.add(cls.getConstructor().newInstance());
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        _worldModule = (WorldModule)getManager(WorldModule.class);
        _characterModule = (CharacterModule)getManager(CharacterModule.class);
        _jobManager = (JobManager)getManager(JobManager.class);
//
//        if (GameData.config.module.room && GameData.config.module.temperature) {
//            _modules.add(new TemperatureModule(_worldModule));
//        }
//
        _observers.addAll(_modules);

        _modules.stream().filter(GameModule::isLoaded).forEach(GameModule::create);
    }

    public void onUpdate(int tick) {
        if (!_isRunning) {
            return;
        }

        _modules.stream().filter(GameModule::isLoaded).forEach(module -> module.update(tick));

        if (tick % _config.tickPerHour == 0) {
            if (++_hour >= _planet.getInfo().dayDuration) {
                _hour = 0;
                if (++_day >= _planet.getInfo().yearDuration) {
                    _day = 1;
                    _year++;
                    notify(observer -> observer.onYearChange(_year));
                }
                notify(observer -> observer.onDayChange(_day));
            }
            notify(observer -> observer.onHourChange(_hour));
        }

        _tick = tick;
    }

    public void	load() {
        String filePath = "data/saves/" + _fileName;

        long time = System.currentTimeMillis();

//		loadListener.onLoad("Load game");
        GameSerializer.load(filePath, _save);
        _save = null;
        System.gc();

        Log.info("Game loaded (2): " + (System.currentTimeMillis() - time) + "ms");

//        loadListener.onLoad("Init world old");
//		WorldFactory.cleanRock();
    }

    public void	save(final String fileName) {
        GameSerializer.save("data/saves/" + fileName, _modules);
    }

    public void notify(Consumer<GameObserver> action) {
        Objects.requireNonNull(action);
        _observers.stream().forEach(action::accept);
    }

    public GameModule getManager(Class<? extends GameModule> cls) {
        for (GameModule manager: _modules) {
            if (cls.isInstance(manager)) {
                return manager;
            }
        }
        return null;
    }

    public int getSpeed() {
        return _speed;
    }

    public List<GameModule> getModules() {
        return _modules;
    }

    public void preload() {
        // TODO magic
    }

    public void setWorldManager(WorldModule worldModule) {
        _worldModule = worldModule;
    }

    public GameInfo getInfo() {
        return _info;
    }

    public void removeModule(GameModule module) {
        module.destroy();
//        _modules.remove(module);
        _observers.remove(module);
    }

    public void insertModule(GameModule module) {
        module.create();
//        _modules.add(module);
        _observers.add(module);
    }
}
