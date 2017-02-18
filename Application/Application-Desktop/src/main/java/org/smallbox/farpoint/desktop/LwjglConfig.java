package org.smallbox.farpoint.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.smallbox.faraway.core.game.ApplicationConfig;
import org.smallbox.faraway.util.Constant;

/**
 * Created by Alex on 22/11/2015.
 */
public class LwjglConfig {
    public static LwjglApplicationConfiguration from(ApplicationConfig config) {
        LwjglApplicationConfiguration lwjglConfig = new LwjglApplicationConfiguration();
//        APPLICATION_CONFIG.samples = 2;

        lwjglConfig.foregroundFPS = config.screen.foregroundFPS;
        lwjglConfig.backgroundFPS = config.screen.backgroundFPS;

        lwjglConfig.x = config.screen.position[0];
        lwjglConfig.y = config.screen.position[1];
        lwjglConfig.width = config.screen.resolution[0];
        lwjglConfig.height = config.screen.resolution[1];
        lwjglConfig.fullscreen = "fullscreen".equals(config.screen.mode);
        lwjglConfig.resizable = false;
        lwjglConfig.vSyncEnabled = false;
//        APPLICATION_CONFIG.useGL30 = true;
        lwjglConfig.title = Constant.NAME + " " + Constant.VERSION;

        if ("borderless".equals(config.screen.mode)) {
            System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
        }

        return lwjglConfig;
    }
}