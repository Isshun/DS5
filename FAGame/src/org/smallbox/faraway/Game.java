package org.smallbox.faraway;

import org.smallbox.faraway.engine.serializer.GameSerializer;
import org.smallbox.faraway.engine.serializer.LoadListener;
import org.smallbox.faraway.engine.serializer.WorldFactory;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.manager.*;
import org.smallbox.faraway.model.GameConfig;
import org.smallbox.faraway.model.GameData;
import org.smallbox.faraway.model.PlanetModel;
import org.smallbox.faraway.model.WeatherModel;
import org.smallbox.faraway.ui.AreaManager;

import java.util.List;

public class Game {
	private static GameData 			_data;
	private static RoomManager 			_roomManager;
	private static StatsManager			_statsManager;
	private static ResourceManager 		_resourceManager;
	private static CharacterManager 	_characterManager;
	private static WorldManager 		_worldManager;
	private static FoeManager 			_foeManager;
	private static DynamicObjectManager	_dynamicObjectManager;
	private static RelationManager 		_relationManager;
	private static AreaManager          _areaManager;
    private final ParticleRenderer      _particleRenderer;
    private final LightRenderer         _lightRenderer;
    private final PlanetModel           _planet;
    private TemperatureManager          _temperatureManager;
    private static WeatherManager       _weatherManager;
	private static JobManager 			_jobManager;
	private static Game 				_self;
	private boolean 					_paused;
	private int 						_speed;
    private GameConfig                  _config;

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
	public static GameData				getData() { return _data; }

	private static int 					_update;
	private static WorldFinder 			_worldFinder;
	private Viewport 					_viewport;
	private boolean						_isMenuOpen;
	private boolean 					_isRunning;

	public Game(GameData data, ParticleRenderer particleRenderer, LightRenderer lightRenderer) {
		Log.debug("Game");

		_self = this;
		_data = data;
        _config = data.config;
        _planet = new PlanetModel();
		_isRunning = true;
		_viewport = SpriteManager.getInstance().createViewport();
        _particleRenderer = particleRenderer;
        _lightRenderer = lightRenderer;
        _update = 0;

		Log.info("Game:\tdone");
	}

	public void	onUpdate() {
		if (_isMenuOpen) {
			return;
		}

		_dynamicObjectManager.update();

		// Path close
		List<Runnable> paths = PathManager.getInstance().getPaths();
		synchronized (paths) {
            paths.forEach(java.lang.Runnable::run);
			paths.clear();
		}

		// Characters
		_characterManager.onUpdate(_update);

		// Foes
		_foeManager.checkSurroundings();

		// Clean completed jobs
		_jobManager.cleanJobs();

        if (_temperatureManager != null && _roomManager != null) {
            _temperatureManager.update(_worldManager.getTemperature(), _roomManager.getRoomList());
        }

        if (_weatherManager != null) {
            _weatherManager.update(_update);
            if (_update % _config.tickPerHour == 0) {
                _weatherManager.onHourChange(_planet, _update / _config.tickPerHour % _planet.dayDuration);
            }
        }

        _update++;
	}

	public void onLongUpdate() {
		_jobManager.onLongUpdate();

		ResourceManager.getInstance().onLongUpdate();
		_characterManager.onLongUpdate();

		_statsManager.update();

//		_roomManager.update();
	}

	public void	onCreate() {
		Log.info("Game: create");

		_worldManager = new WorldManager();
		ServiceManager.setWorldMap(_worldManager);

		if (GameData.config.manager.room) {
			_roomManager = new RoomManager();
			_worldManager.addObserver(_roomManager);
		}

        if (GameData.config.manager.room && GameData.config.manager.temperature) {
			_temperatureManager = new TemperatureManager();
			_worldManager.addObserver(_temperatureManager);
		}

        if (GameData.config.manager.weather) {
            _weatherManager = new WeatherManager(_lightRenderer, _particleRenderer, _worldManager);
			_worldManager.addObserver(_weatherManager);
		}

        _areaManager = new AreaManager();
		_resourceManager = new ResourceManager();
		_statsManager = new StatsManager();
		_dynamicObjectManager = new DynamicObjectManager();
		_characterManager = new CharacterManager();
		_foeManager = new FoeManager();
		_relationManager = new RelationManager();
		_jobManager = new JobManager();
	}

	public void	newGame(final String filePath, LoadListener loadListener) {
		loadListener.onUpdate("Create new game");
		WorldFactory.create(ServiceManager.getWorldMap(), loadListener);
		ResourceManager.getInstance().refreshWater();
		ResourceManager.getInstance().addMatter(5000);
	}

	public void	load(final String fileName, LoadListener loadListener) {
		String filePath = "data/saves/" + fileName + ".sav";

		loadListener.onUpdate("Load game");
		GameSerializer.load(filePath, loadListener);

		loadListener.onUpdate("Init world map");
		WorldFactory.cleanRock();
		
		ResourceManager.getInstance().refreshWater();
		ResourceManager.getInstance().addMatter(5000);
	}

	public void	save(final String fileName) {
		String filePath = "data/saves/" + fileName + ".sav";

		Log.info("Save game: " + filePath);

		GameSerializer.save(filePath);

		Log.info("Save game: " + filePath + " done");
	}

	public boolean isRunning() {
		return _isRunning;
	}

	public void setRunning(boolean running) {
		_isRunning = running;		
	}

	public Viewport getViewport() {
		return _viewport;
	}
	
	public static int getUpdate() {
		return _update;
	}
	public static void setCharacterManager(CharacterManager characterManager) {
		_characterManager = characterManager;
	}
//	public static void setRoomManager(RoomManager roomManager) {
//		_roomManager = roomManager;
//	}
	public static WorldFinder getWorldFinder() {
		return _worldFinder;
	}
	public static void setWorldFinder(WorldFinder worldFinder) {
		_worldFinder = worldFinder;
	}

	public void togglePaused() {
		_paused = !_paused;
	}

	public void setSpeed(int speed) {
		_speed = speed;
	}

	public boolean isPaused() {
		return _paused;
	}

	public static Game getInstance() {
		return _self;
	}

    public int getHour() {
        return _update / _config.tickPerHour % _planet.dayDuration;
    }

    public int getDay() {
        return _update / _config.tickPerHour / _planet.dayDuration & _planet.yearDuration;
    }

    public int getYear() {
        return _update / _config.tickPerHour / _planet.dayDuration / _planet.yearDuration;
    }

    public static AreaManager getAreaManager() {
        return _areaManager;
    }
}
