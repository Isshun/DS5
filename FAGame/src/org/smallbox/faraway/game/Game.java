package org.smallbox.faraway.game;

import org.smallbox.faraway.data.factory.map.AsteroidBeltFactory;
import org.smallbox.faraway.engine.renderer.LightRenderer;
import org.smallbox.faraway.engine.renderer.ParticleRenderer;
import org.smallbox.faraway.PathHelper;
import org.smallbox.faraway.engine.Viewport;
import org.smallbox.faraway.engine.SpriteManager;
import org.smallbox.faraway.data.serializer.GameSerializer;
import org.smallbox.faraway.data.serializer.LoadListener;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.game.manager.*;
import org.smallbox.faraway.game.model.GameConfig;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.planet.PlanetModel;
import org.smallbox.faraway.ui.AreaManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class Game {
	private static RoomManager 			_roomManager;
	private static StatsManager			_statsManager;
	private static ResourceManager 		_resourceManager;
	private static CharacterManager 	_characterManager;
	private static WorldManager 		_worldManager;
	private static FoeManager 			_foeManager;
	private static DynamicObjectManager	_dynamicObjectManager;
	private static RelationManager 		_relationManager;
	private static AreaManager          _areaManager;
    private static TemperatureManager   _temperatureManager;
    private static WeatherManager       _weatherManager;
    private static JobManager 			_jobManager;

    private final ParticleRenderer      _particleRenderer;
    private final LightRenderer         _lightRenderer;

    private static Game 				_self;
    private final PlanetModel           _planet;
    private final String                _fileName;
	private int 						_speed;
    private GameConfig                  _config;
	private List<BaseManager>			_managers;
	private List<GameObserver>			_observers;
    private GameSerializer.GameSave     _save;

    public void                         toggleRunning() { _isRunning = !_isRunning; }
    public void                         addObserver(GameObserver observer) { _observers.add(observer); }
    public void                         setRunning(boolean running) { _isRunning = running; }
    public static void                  setWorldFinder(WorldFinder worldFinder) { _worldFinder = worldFinder; }
    public void                         setSpeed(int speed) { _speed = speed; }

    public boolean                      isRunning() { return _isRunning; }

    public static StatsManager 			getStatsManager() { return _statsManager; }
	public static RoomManager 			getRoomManager() { return _roomManager; }
	public static ResourceManager 		get() { return _resourceManager ; }
	public static CharacterManager 		getCharacterManager() { return _characterManager; }
	public static WorldManager 			getWorldManager() { return _worldManager; }
	public static WeatherManager        getWeatherManager() { return _weatherManager; }
	public static FoeManager 			getFoeManager() { return _foeManager; }
	public static DynamicObjectManager 	getDynamicObjectManager() { return _dynamicObjectManager; }
	public static RelationManager		getRelationManager() { return _relationManager; }
	public static JobManager			getJobManager() { return _jobManager; }
    public static Game                  getInstance() { return _self; }
    public int                          getHour() { return _tick / _config.tickPerHour % _planet.getInfo().dayDuration; }
    public int                          getDay() { return _tick / _config.tickPerHour / _planet.getInfo().dayDuration & _planet.getInfo().yearDuration; }
    public int                          getYear() { return _tick / _config.tickPerHour / _planet.getInfo().dayDuration / _planet.getInfo().yearDuration; }
    public Viewport getViewport() { return _viewport; }
    public static int                   getUpdate() { return _tick; }
    public static WorldFinder           getWorldFinder() { return _worldFinder; }
    public static AreaManager           getAreaManager() { return _areaManager; }

	private static int                  _tick;
	private static WorldFinder 			_worldFinder;
	private Viewport 					_viewport;
	private boolean 					_isRunning;

	public Game(GameData data, GameConfig config, String fileName, ParticleRenderer particleRenderer, LightRenderer lightRenderer) {
		Log.debug("Game");

		_self = this;
        _config = config;
        _fileName = fileName;
        _planet = new PlanetModel(GameData.getData().planets.get(0));
		_isRunning = true;
		_viewport = SpriteManager.getInstance().createViewport();
        _particleRenderer = particleRenderer;
        _lightRenderer = lightRenderer;
        _tick = 0;

        Log.info("Game: create");

        _managers = new ArrayList<>();
        _observers = new ArrayList<>();

        _worldManager = new WorldManager(this);
        _managers.add(_worldManager);

		Log.info("Game:\tdone");
	}

    public void init(boolean loadSave) {
        if (loadSave) {
            String filePath = "data/saves/" + _fileName;
            _save = GameSerializer.preLoad(filePath, null);
            if (_save != null) {
                _worldManager.init(_save.width, _save.height);
            }
        }

        if (GameData.config.manager.room) {
            _roomManager = new RoomManager();
            _managers.add(_roomManager);
        }

        if (GameData.config.manager.room && GameData.config.manager.temperature) {
            _temperatureManager = new TemperatureManager(_worldManager, _roomManager);
            _managers.add(_temperatureManager);
        }

        if (GameData.config.manager.weather) {
            _weatherManager = new WeatherManager(_lightRenderer, _particleRenderer, _worldManager);
            _managers.add(_weatherManager);
        }

        _managers.add(new PathHelper());

        _areaManager = new AreaManager();
        _managers.add(_areaManager);

        _resourceManager = new ResourceManager();
        _managers.add(_resourceManager);

        _statsManager = new StatsManager();
        _managers.add(_statsManager);

        _dynamicObjectManager = new DynamicObjectManager();
        _managers.add(_dynamicObjectManager);

        _characterManager = new CharacterManager();
        _managers.add(_characterManager);

        _foeManager = new FoeManager();
        _managers.add(_foeManager);

        _relationManager = new RelationManager();
        _managers.add(_relationManager);

        _jobManager = new JobManager();
        _managers.add(_jobManager);

        if (GameData.config.manager.oxygen) {
            _managers.add(new OxygenManager());
        }

        _managers.add(new PowerManager());
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

		if (_tick % _config.tickPerHour == 0) {
            notify(observer -> observer.onHourChange(_tick / _config.tickPerHour % _planet.getInfo().dayDuration));
		}

        _tick++;
	}

	public void	newGame(LoadListener loadListener) {
		//loadListener.onUpdate("Create new game");

        int width = 250;
        int height = 250;

        Game.getWorldManager().init(width, height);
        ParcelModel[][][] parcels = Game.getWorldManager().getParcels();
        (new AsteroidBeltFactory()).create(parcels, width, height, loadListener);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                final ParcelModel parcel = parcels[x][y][0];
                if (parcel.getStructure() != null) {
                    Game.getInstance().notify(observer -> observer.onAddStructure(parcel.getStructure()));
                }
                if (parcel.getResource() != null) {
                    Game.getInstance().notify(observer -> observer.onAddResource(parcel.getResource()));
                }
                if (parcel.getItem() != null) {
                    Game.getInstance().notify(observer -> observer.onAddItem(parcel.getItem()));
                }
                if (parcel.getConsumable() != null) {
                    Game.getInstance().notify(observer -> observer.onAddConsumable(parcel.getConsumable()));
                }
            }
        }
	}

	public void	load(LoadListener loadListener) {
		String filePath = "data/saves/" + _fileName;

		loadListener.onUpdate("Load game");
		GameSerializer.load(_save, loadListener);
        _save = null;
        System.gc();

        loadListener.onUpdate("Init world map");
//		WorldFactory.cleanRock();
	}

	public void	save(final String fileName) {
		GameSerializer.save("data/saves/" + fileName);
	}

    public void notify(Consumer<GameObserver> action) {
        Objects.requireNonNull(action);
        _observers.stream().forEach(action::accept);
    }

    public PlanetModel getPlanet() {
        return _planet;
    }

    public String getFileName() {
        return _fileName;
    }

    public BaseManager getManager(Class<? extends BaseManager> cls) {
        for (BaseManager manager: _managers) {
            if (cls.isInstance(manager)) {
                return manager;
            }
        }
        return null;
    }

    public long getTick() {
        return _tick;
    }
}
