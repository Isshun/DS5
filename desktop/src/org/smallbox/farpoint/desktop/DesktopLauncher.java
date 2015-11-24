package org.smallbox.farpoint.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import org.json.JSONObject;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GDXApplication;
import org.smallbox.faraway.core.game.ApplicationConfig;
import org.smallbox.faraway.core.util.FileUtils;
import org.smallbox.faraway.core.util.Log;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

public class DesktopLauncher {
    public static void main (String[] arg) {
        FileUtils.createRoamingDirectory();

        // Get native screen resolution
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        double ratio = (double)width / height;
        Log.info("Screen resolution: " + width + "x" + height + " (" + ratio + ")");

        try {
            Log.info("Load application config");
            ApplicationConfig config = ApplicationConfig.fromJSON(new JSONObject(FileUtils.read(new File("data/config.json"))));
            Application.getInstance().setConfig(config);
            new LwjglApplication(new GDXApplication(), LwjglConfig.from(config));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
