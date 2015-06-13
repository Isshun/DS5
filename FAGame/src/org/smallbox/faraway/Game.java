package org.smallbox.faraway;

import org.smallbox.faraway.engine.serializer.GameSerializer;
import org.smallbox.faraway.engine.serializer.LoadListener;
import org.smallbox.faraway.engine.serializer.WorldFactory;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.manager.*;
import org.smallbox.faraway.model.GameData;

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
	private static JobManager 			_jobManager;
	private static Game 				_self;
	private boolean 					_paused;
	private int 						_speed;
	private GameListener 				_gameListener;

	public static StatsManager 			getStatsManager() { return _statsManager; }
	public static RoomManager 			getRoomManager() { return _roomManager; }
	public static ResourceManager 		get() { return _resourceManager ; }
	public static CharacterManager 		getCharacterManager() { return _characterManager; }
	public static WorldManager 			getWorldManager() { return _worldManager; }
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

	public Game(GameData data) {
		Log.debug("Game");

		_self = this;
		_data = data;
		_isRunning = true;
		_viewport = SpriteManager.getInstance().createViewport();
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
			for (Runnable path: paths) {
				path.run();
			}
			paths.clear();
		}

		// Characters
		_characterManager.onUpdate(_update);

		// Foes
		_foeManager.checkSurroundings();

		// Clean completed jobs
		_jobManager.cleanJobs();

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
		_roomManager = new RoomManager();
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

	public void setListener(GameListener listener) {
		_gameListener = listener;
		_worldManager.setListener(_gameListener);
	}
}
