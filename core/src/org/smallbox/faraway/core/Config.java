package org.smallbox.faraway.core;

import com.badlogic.gdx.Gdx;

/**
 * Created by Alex on 11/11/2015.
 */
public class Config {
    public static class ConfigScreen {
        public int width = Gdx.graphics.getWidth();
        public int height = Gdx.graphics.getHeight();
        public String ratio = "16/9";
        public String mode = "borderless";
    }

    public ConfigScreen screen = new ConfigScreen();

    public String getNextRatio(String ratio) {
        if ("16/9".equals(ratio)) return "16/10";
        if ("16/10".equals(ratio)) return "4/3";
        if ("4/3".equals(ratio)) return "16/9";
        return null;
    }
}
