package alone.in.deepspace;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.TextureCreationException;

import alone.in.deepspace.engine.ISavable;
import alone.in.deepspace.engine.Viewport;
import alone.in.deepspace.engine.loader.JobManagerLoader;
import alone.in.deepspace.engine.loader.WorldSaver;
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

	public static StatsManager 			getStatsManager() { return _statsManager; }
	public static RoomManager 			getRoomManager() { return _roomManager; }
	public static ResourceManager 		get() { return _resourceManager ; }
	public static CharacterManager 		getCharacterManager() { return _characterManager; }
	public static WorldManager 			getWorldManager() { return _worldManager; }
	public static FoeManager 			getFoeManager() { return _foeManager; }
	public static DynamicObjectManager 	getDynamicObjectManager() { return _dynamicObjectManager; }
	public static RelationManager		getRelationManager() { return _relationManager; }
	public static GameData				getData() { return _data; }

	private static int 					_renderTime;
	private static int 					_frame;
	private int 						_update;
	private Viewport 					_viewport;
	private boolean						_isMenuOpen;
	private boolean 					_isRunning;

	static {
		System.loadLibrary("JNILight");
	}

	public Game(RenderWindow app, GameData data) throws IOException, TextureCreationException {
		Log.debug("Game");

		_data = data;
		_frame = 0;
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
		List<Runnable> jobsDone = PathManager.getInstance().getJobs();
		synchronized (jobsDone) {
			for (Runnable job: jobsDone) {
				job.run();
			}
			jobsDone.clear();
		}

		// Characters
		_characterManager.onUpdate(_update);

		// Foes
		_foeManager.checkSurroundings();

		_update++;
	}

	public void onLongUpdate() {
		JobManager.getInstance().onLongUpdate();

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
		
		ServiceManager.getWorldMap().create();
		Game.getCharacterManager().create();
	}

	public void	load(final String filePath, LoadListener loadListener) {
		loadListener.onUpdate("Load game");
		
		GameSerializer.load(filePath, loadListener);
		
		//WorldFactory.create(ServiceManager.getWorldMap());

		ResourceManager.getInstance().refreshWater();

		JobManagerLoader.load(JobManager.getInstance(), loadListener);

		onLoadComplete();
	}

	private void onLoadComplete() {
		//		ItemInfo info = Game.getData().getItemInfo("base.seaweed");
		//		
		//		for (int x = 0; x < ServiceManager.getWorldMap().getWidth(); x++) {
		//			for (int y = 0; y < ServiceManager.getWorldMap().getHeight(); y++) {
		//				StructureItem structure = ServiceManager.getWorldMap().getStructure(0, x, y);
		//				if (structure != null && structure.getName().equals("base.ground")) {
		//					ServiceManager.getWorldMap().putItem("base.seaweed1", x, y);
		//				}
		//			}
		//		}

		//				UserItem item = ServiceManager.getWorldMap().getItem(x, y);
		//				if (item != null && "base.storage".equals(item.getInfo().name)) {
		//					StorageItem storage = (StorageItem)item;
		//					for (int i = 0; i < 100; i++) {
		//						storage.addInventory(new UserItem(info));
		//					}
		//				}
		//			}
		//			
		//		}
	}

	public void	save(final String filePath) {
		Log.info("Save game: " + filePath);

		GameSerializer.save(filePath);

		Log.info("Save game: " + filePath + " done");
	}

	public void onDraw(double animProgress, int renderTime) throws IOException {
		_renderTime = renderTime;
		_frame++;
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
}
