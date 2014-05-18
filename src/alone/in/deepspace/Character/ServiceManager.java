package alone.in.deepspace.Character;

import java.io.IOException;

import alone.in.deepspace.Engine.renderer.LightRenderer;
import alone.in.deepspace.Engine.renderer.WorldRenderer;
import alone.in.deepspace.Managers.WorldManager;

public class ServiceManager {

	private static WorldManager 		_worldMap;
	private static CharacterManager _charactersManager;
	private static WorldRenderer 	_worldRenderer;
	private static GameData 		_data;
	private static LightRenderer _lightRenderer;

	public static void reset() {
		_worldMap = null;
		_charactersManager = null;
		_worldRenderer = null;
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
}
