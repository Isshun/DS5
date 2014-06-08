package alone.in.deepspace;

import java.util.List;

import org.jsfml.graphics.RenderWindow;

import alone.in.deepspace.engine.Viewport;
import alone.in.deepspace.engine.serializer.GameSerializer;
import alone.in.deepspace.engine.serializer.JobManagerLoader;
import alone.in.deepspace.engine.serializer.LoadListener;
import alone.in.deepspace.engine.serializer.WorldFactory;
import alone.in.deepspace.manager.CharacterManager;
import alone.in.deepspace.manager.DynamicObjectManager;
import alone.in.deepspace.manager.FoeManager;
import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.PathManager;
import alone.in.deepspace.manager.RelationManager;
import alone.in.deepspace.manager.ResourceManager;
import alone.in.deepspace.manager.RoomManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.manager.StatsManager;
import alone.in.deepspace.manager.WorldManager;
import alone.in.deepspace.model.GameData;
import alone.in.deepspace.util.Constant;
import alone.in.deepspace.util.Log;

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
	private Viewport 					_viewport;
	private boolean						_isMenuOpen;
	private boolean 					_isRunning;

	public Game(RenderWindow app, GameData data) {
		Log.debug("Game");

		_data = data;
		_isRunning = true;
		_viewport = new Viewport(app, -Constant.WORLD_WIDTH * Constant.TILE_WIDTH / 2, -Constant.WORLD_HEIGHT * Constant.TILE_HEIGHT / 2);
		_update = 0;

		app.setKeyRepeatEnabled(true);

		Log.info("Game:\tdone");
	}

	public void	onUpdate() {
		if (_isMenuOpen) {
			return;
		}

		_dynamicObjectManager.update();

		ServiceManager.getWorldMap().update();

		// Path complete
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

		_roomManager.update();
	}

	void	onCreate() {
		Log.info("Game: create");

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

	public void	load(final String filePath, LoadListener loadListener) {
		loadListener.onUpdate("Load game");
		GameSerializer.load(filePath, loadListener);
		JobManagerLoader.load(JobManager.getInstance(), loadListener);
		ServiceManager.getWorldMap().cleanRock();
		ResourceManager.getInstance().refreshWater();
		ResourceManager.getInstance().addMatter(5000);
	}

	public void	save(final String filePath) {
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
}
