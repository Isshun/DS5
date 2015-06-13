package org.smallbox.faraway.manager;


public class ServiceManager {

	private static WorldManager 		_worldMap;
	
	public static WorldManager getWorldMap() {
		return _worldMap;
	}

	public static void setWorldMap(WorldManager worldManager) {
		_worldMap = worldManager;
	}

}
