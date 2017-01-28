package org.smallbox.faraway.core.game;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smallbox.faraway.util.Log;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Alex on 22/11/2015.
 */
public class ConfigurationManager {

    private static JSONObject _json;

    public static ConfigurationManager fromJSON(JSONObject json) {
        _json = json;

        ConfigurationManager configurationManager = new ConfigurationManager();

        // Read screen info
        JSONObject jsonScreen = json.getJSONObject("screen");
        configurationManager.screen = new ConfigScreenInfo();
        configurationManager.screen.resolution = new int[] {
                jsonScreen.getJSONArray("resolution").getInt(0),
                jsonScreen.getJSONArray("resolution").getInt(1)};
        configurationManager.screen.position = new int[] {
                jsonScreen.getJSONArray("position").getInt(0),
                jsonScreen.getJSONArray("position").getInt(1)};
        configurationManager.screen.mode = jsonScreen.getString("mode");
        configurationManager.screen.foregroundFPS = jsonScreen.getInt("foregroundFPS");
        configurationManager.screen.backgroundFPS = jsonScreen.getInt("backgroundFPS");

        // Read game info
        JSONObject jsonGame = json.getJSONObject("game");
        configurationManager.game = new ConfigGameInfo();
        configurationManager.game.environmentDistance = jsonGame.getInt("environmentDistance");
        configurationManager.game.inventoryMaxQuantity = jsonGame.getInt("inventoryMaxQuantity");
        configurationManager.game.storageMaxQuantity = jsonGame.getInt("storageMaxQuantity");
        configurationManager.game.maxNearDistance = jsonGame.getInt("maxNearDistance");
        configurationManager.game.tickPerHour = jsonGame.getInt("tickPerHour");

        // Read application info
        configurationManager.lang = json.getString("lang");
        configurationManager.uiScale = json.getDouble("uiScale");

        Log.setLevel(configurationManager.getString("log.level"));

        return configurationManager;
    }

    public int getInt(String path) {
        JSONObject object = _json;
        List<String> keys = Arrays.asList(StringUtils.split(path, '.'));
        for (String key: keys.subList(0, keys.size() - 1)) {
            object = object.getJSONObject(key);
        }
        return object.getInt(keys.get(keys.size() - 1));
    }

    public double getDouble(String path) {
        JSONObject object = _json;
        List<String> keys = Arrays.asList(StringUtils.split(path, '.'));
        for (String key: keys.subList(0, keys.size() - 1)) {
            object = object.getJSONObject(key);
        }
        return object.getDouble(keys.get(keys.size() - 1));
    }

    public String getString(String path) {
        JSONObject object = _json;
        List<String> keys = Arrays.asList(StringUtils.split(path, '.'));
        for (String key: keys.subList(0, keys.size() - 1)) {
            object = object.getJSONObject(key);
        }
        return object.getString(keys.get(keys.size() - 1));
    }

    public static class ConfigScreenInfo {
        public int[]            resolution;
        public int[]            position;
        public String           mode;
        public int              foregroundFPS;
        public int              backgroundFPS;
    }

    public static class ConfigGameInfo {
        public int              inventoryMaxQuantity;
        public int              storageMaxQuantity;
        public int              environmentDistance;
        public int              maxNearDistance;
        public int              tickPerHour;
    }

    public ConfigScreenInfo     screen;
    public ConfigGameInfo       game;
    public double               uiScale;
    public String               lang;
}
