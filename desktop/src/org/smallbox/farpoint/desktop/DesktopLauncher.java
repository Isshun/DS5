package org.smallbox.farpoint.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.smallbox.faraway.core.GDXApplication;
import org.smallbox.faraway.core.data.loader.ConfigLoader;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.util.Constant;

public class DesktopLauncher {
    public static void main (String[] arg) {
        Data data = new Data();
        new ConfigLoader().load(data);

        System.loadLibrary("sqlite4java-win32-x64-1.0.392");

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
//        config.samples = 2;
        config.x = 0 + 40;
        config.y = 0 + 40;
        config.width = data.config.screen.resolution[0];
        config.height = data.config.screen.resolution[1];
//        config.width = 100;
//        config.height = 80;
        config.fullscreen = false;
        config.foregroundFPS = 60;
        config.backgroundFPS = 30;
//        config.foregroundFPS = 0;
//        config.backgroundFPS = 0;
        config.resizable = true;
        config.vSyncEnabled = false;
//        config.useGL30 = true;
        config.title = Constant.NAME + " " + Constant.VERSION;
        new LwjglApplication(new GDXApplication(), config);
//        new LwjglApplication(new TestApplication(), config);

//        switch (data.config.screen.mode) {
//            case "window":
//                config.fullscreen = false;
//                System.setProperty("org.lwjgl.opengl.Window.undecorated", "false");
//                break;
//            case "borderless":
//                config.fullscreen = false;
//                System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
//                break;
//            case "fullscreen":
//                config.fullscreen = true;
//                break;
//        }
    }
}
