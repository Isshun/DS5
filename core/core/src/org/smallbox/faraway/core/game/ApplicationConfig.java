package org.smallbox.faraway.core.game;

import org.json.JSONObject;

/**
 * Created by Alex on 22/11/2015.
 */
public class ApplicationConfig {
    public static ApplicationConfig fromJSON(JSONObject json) {
        ApplicationConfig config = new ApplicationConfig();

        // Read screen info
        JSONObject jsonScreen = json.getJSONObject("screen");
        config.screen = new ConfigScreenInfo();
        config.screen.resolution = new int[] {
                jsonScreen.getJSONArray("resolution").getInt(0),
                jsonScreen.getJSONArray("resolution").getInt(1)};
        config.screen.mode = jsonScreen.getString("mode");
        config.screen.foregroundFPS = jsonScreen.getInt("foregroundFPS");
        config.screen.backgroundFPS = jsonScreen.getInt("backgroundFPS");

        // Read game info
        JSONObject jsonGame = json.getJSONObject("game");
        config.game = new ConfigGameInfo();
        config.game.environmentDistance = jsonGame.getInt("environmentDistance");
        config.game.inventoryMaxQuantity = jsonGame.getInt("inventoryMaxQuantity");
        config.game.maxNearDistance = jsonGame.getInt("maxNearDistance");
        config.game.tickPerHour = jsonGame.getInt("tickPerHour");

        // Read application info
        config.lang = json.getString("lang");
        config.uiScale = json.getDouble("uiScale");

        return config;
    }

    public static class ConfigScreenInfo {
        public int[]            resolution;
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
