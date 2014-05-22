package alone.in.deepspace.manager;

import java.io.IOException;

import alone.in.deepspace.Game;
import alone.in.deepspace.engine.renderer.LightRenderer;
import alone.in.deepspace.engine.renderer.WorldRenderer;
import alone.in.deepspace.model.GameData;

public class ServiceManager {

	private static WorldManager 		_worldMap;
	private static CharacterManager 	_charactersManager;
	private static WorldRenderer 		_worldRenderer;
	private static GameData 			_data;
	private static LightRenderer 		_lightRenderer;
	private static Game 				_game;
	private static RelationManager 		_relationManager;

	public static void reset() {
		_worldMap = null;
		_charactersManager = null;
		_worldRenderer = null;
		_relationManager = null;
	}

	public static WorldManager getWorldMap() {
		if (_worldMap == null) {
			_worldMap = new WorldManager();
		}
		return _worldMap;
	}

	public static CharacterManager getCharacterManager() {
		if (_charactersManager == null) {
			try {
				_charactersManager = new CharacterManager();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return _charactersManager;
	}

	public static WorldRenderer getWorldRenderer() {
		return _worldRenderer;
	}

	public static void setWorldRenderer(WorldRenderer renderer) {
		_worldRenderer = renderer;
	}

	public static GameData getData() {
		return _data;
	}

	public static void setData(GameData data) {
		_data = data;
	}

	public static LightRenderer getLightRenderer() {
		return _lightRenderer;
	}

	public static void setLightRenderer(LightRenderer lightRenderer) {
		_lightRenderer = lightRenderer;
	}

	public static Game getGame() {
		return _game;
	}

	public static RelationManager getRelationManager() {
		if (_relationManager == null) {
			_relationManager = new RelationManager();
		}
		return _relationManager;
	}
}
