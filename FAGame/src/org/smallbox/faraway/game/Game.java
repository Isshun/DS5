package org.smallbox.faraway.game;

import org.smallbox.faraway.PathManager;
import org.smallbox.faraway.data.factory.map.AsteroidBeltFactory;
import org.smallbox.faraway.data.serializer.GameSerializer;
import org.smallbox.faraway.data.serializer.LoadListener;
import org.smallbox.faraway.engine.SpriteManager;
import org.smallbox.faraway.engine.Viewport;
import org.smallbox.faraway.engine.renderer.LightRenderer;
import org.smallbox.faraway.engine.renderer.ParticleRenderer;
import org.smallbox.faraway.game.manager.*;
import org.smallbox.faraway.game.model.GameConfig;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.planet.PlanetModel;
import org.smallbox.faraway.game.model.planet.RegionModel;
import org.smallbox.faraway.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class Game {
	private static CharacterManager 	_characterManager;
	private static WorldManager 		_worldManager;
    private static JobManager 			_jobManager;

    private final ParticleRenderer      _particleRenderer;
    private final LightRenderer         _lightRenderer;

    private static Game 				_self;
    private final String                _fileName;
	private int 						_speed = 1;
    private GameConfig                  _config;
	private List<BaseManager>			_managers;
	private List<GameObserver>			_observers;
    private GameSerializer.GameSave     _save;
    private PlanetModel                 _planet;
    private RegionModel                 _region;
    private int                         _hour = 6;
    private int                         _day;
    private int                         _year;

    public void                         toggleRunning() { _isRunning = !_isRunning; }
    public void                         addObserver(GameObserver observer) { _observers.add(observer); }
    public void                         setRunning(boolean running) { _isRunning = running; }
    public void                         setSpeed(int speed) { _speed = speed; }

    public boolean                      isRunning() { return _isRunning; }

	public static CharacterManager 		getCharacterManager() { return _characterManager; }
	public static WorldManager 			getWorldManager() { return _worldManager; }
	public static JobManager			getJobManager() { return _jobManager; }
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

	public Game(GameData data, GameConfig config, String fileName, ParticleRenderer particleRenderer, LightRenderer lightRenderer) {
		Log.debug("Game");

		_self = this;
        _config = config;
        _fileName = fileName;
        _planet = new PlanetModel(data.planets.get(0));
        _region = new RegionModel(_planet, _planet.getInfo().regions.get(0));
		_isRunning = true;
		_viewport = SpriteManager.getInstance().createViewport();
        _particleRenderer = particleRenderer;
        _lightRenderer = lightRenderer;
        _tick = 0;

        Log.info("Game: onCreate");

        _managers = new ArrayList<>();
        _observers = new ArrayList<>();

        _worldManager = new WorldManager(this);
        _managers.add(_worldManager);

		Log.info("Game:\tdone");
	}

    public void init(boolean loadSave) {
        if (loadSave) {
            String filePath = "data/saves/" + _fileName;

            // TODO magic
            _worldManager.init(250, 250);
            _save = GameSerializer.preLoad(filePath, null);
            if (_save != null) {
                _worldManager.init(_save.width, _save.height);
            }
        }

        if (GameData.config.manager.room) {
            _managers.add(new RoomManager());
        }

        if (GameData.config.manager.room && GameData.config.manager.temperature) {
            _managers.add(new TemperatureManager(_worldManager));
        }

        if (GameData.config.manager.weather) {
            _managers.add(new WeatherManager(_lightRenderer, _particleRenderer, _worldManager));
        }

        _managers.add(new WorldFinder());
        _managers.add(new PathManager());
        _managers.add(new AreaManager());
        _managers.add(new ResourceManager());
        _managers.add(new StatsManager());
        _managers.add(new DiseaseManager());
        _managers.add(new BuffManager());
        _managers.add(new LightManager());
        _managers.add(new PlantManager());

        if (GameData.config.manager.fauna) {
            _managers.add(new FaunaManager());
        }

        _characterManager = new CharacterManager();
        _managers.add(_characterManager);

        _jobManager = new JobManager();
        _managers.add(_jobManager);

        _managers.add(new RelationManager());

        if (GameData.config.manager.oxygen) {
            _managers.add(new OxygenManager());
        }

        if (GameData.config.manager.power) {
            _managers.add(new PowerManager());
        }
        _managers.add(new WorldItemManager());

        if (GameData.config.manager.quest) {
            _managers.add(new QuestManager());
        }

        _observers.addAll(_managers);

        _managers.forEach(BaseManager::create);
    }

	public void onUpdate(int tick) {
		if (!_isRunning) {
			return;
		}

        for (BaseManager manager: _managers) {
            manager.update(tick);
        }

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

	public void	newGame(LoadListener loadListener) {
		//loadListener.onUpdate("Create new game");

        (new AsteroidBeltFactory()).create(Game.getWorldManager(), 250, 250, loadListener);
	}

	public void	load() {
		String filePath = "data/saves/" + _fileName;

        long time = System.currentTimeMillis();

//		loadListener.onUpdate("Load game");
		GameSerializer.load(filePath, _save);
        _save = null;
        System.gc();

        Log.info("Game loaded (2): " + (System.currentTimeMillis() - time) + "ms");

//        loadListener.onUpdate("Init world map");
//		WorldFactory.cleanRock();
	}

	public void	save(final String fileName) {
		GameSerializer.save("data/saves/" + fileName);
	}

    public void notify(Consumer<GameObserver> action) {
        Objects.requireNonNull(action);
        _observers.stream().forEach(action::accept);
    }

    public BaseManager getManager(Class<? extends BaseManager> cls) {
        for (BaseManager manager: _managers) {
            if (cls.isInstance(manager)) {
                return manager;
            }
        }
        return null;
    }

    public int getSpeed() {
        return _speed;
    }

    public List<BaseManager> getManagers() {
        return _managers;
    }
}
