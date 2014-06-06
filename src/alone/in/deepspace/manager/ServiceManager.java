package alone.in.deepspace.manager;

import alone.in.deepspace.Game;
import alone.in.deepspace.model.GameData;

public class ServiceManager {

	private static WorldManager 		_worldMap;
	private static GameData 			_data;
	private static Game 				_game;
	private static RelationManager 		_relationManager;

	public static void reset() {
		_worldMap = null;
		_relationManager = null;
	}

	public static WorldManager getWorldMap() {
		if (_worldMap == null) {
			_worldMap = new WorldManager();
		}
		return _worldMap;
	}

}
