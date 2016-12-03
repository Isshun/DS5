package org.smallbox.faraway.core.config;

import org.smallbox.faraway.core.Application;

/**
 * Created by Alex on 11/11/2015.
 */
public class Config {

    public static int getInt(String path) {
        return Application.configurationManager.getInt(path);
    }

    public static double getDouble(String path) {
        return Application.configurationManager.getDouble(path);
    }

    public static String getString(String path) {
        return Application.configurationManager.getString(path);
    }

//    public static class ConfigScreen {
//        public int width = Gdx.graphics.getWidth();
//        public int height = Gdx.graphics.getHeight();
//        public String ratio = "16/9";
//        public String mode = "borderless";
//    }
//
//    public static class ConfigGame {
//        public static final int updateInterval = 160;
//    }

//    public static ConfigScreen screen = new ConfigScreen();
//    public static ConfigGame game = new ConfigGame();

    public String getNextRatio(String ratio) {
        if ("16/9".equals(ratio)) return "16/10";
        if ("16/10".equals(ratio)) return "4/3";
        if ("4/3".equals(ratio)) return "16/9";
        return null;
    }
}
