package org.smallbox.farpoint.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import org.json.JSONObject;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GDXApplication;
import org.smallbox.faraway.core.game.ApplicationConfig;
import org.smallbox.faraway.core.util.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class DesktopLauncher {
    public static void main (String[] arg) {
        System.loadLibrary("sqlite4java-win32-x64-1.0.392");

        // Get native screen resolution
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        double ratio = (double)width / height;
        System.out.println("Screen resolution: " + width + "x" + height + " (" + ratio + ")");

        try {
            System.out.println("Load application config");
            ApplicationConfig config = ApplicationConfig.fromJSON(new JSONObject(FileUtils.read(new File("data/config.json"))));
            Application.getInstance().setConfig(config);
            new LwjglApplication(new GDXApplication(), LwjglConfig.from(config));
        } catch (IOException e) {
            e.printStackTrace();
        }

//
//        Application.getInstance()._configChangeListener = new ConfigChangeListener() {
//            @Override
//            public void onScreeMode(String mode) {
//                switch (mode) {
//                    case "window":
//                        config.fullscreen = false;
//                        System.setProperty("org.lwjgl.opengl.Window.undecorated", "false");
//                        Gdx.graphics.setDisplayMode(1280, 720, false);
//                        break;
//                    case "borderless":
//                        config.fullscreen = false;
//                        System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
//                        Gdx.graphics.setDisplayMode(width, height, true);
//                        break;
//                    case "fullscreen":
//                        config.fullscreen = true;
//                        Gdx.graphics.setDisplayMode(width, height, true);
//                        break;
//                }
//            }
//        };
    }

}
