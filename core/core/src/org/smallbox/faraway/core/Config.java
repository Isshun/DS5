package org.smallbox.faraway.core;

import org.smallbox.faraway.core.game.model.Data;

/**
 * Created by Alex on 11/11/2015.
 */
public class Config {
    public static class ConfigScreen {
        public int width = Data.config.screen.resolution[0];
        public int height = Data.config.screen.resolution[0];
    }

    public ConfigScreen screen;
}
