package org.smallbox.faraway.test;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import org.smallbox.faraway.client.GDXApplication;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.util.FileUtils;
import org.smallbox.faraway.util.Log;
import org.smallbox.farpoint.desktop.LwjglConfig;

import java.awt.*;

/**
 * Created by Alex on 18/02/2017.
 */
public class TestBase {

    protected void launchApplication(GDXApplication.GameTestCallback callback) {

        FileUtils.createRoamingDirectory();

        // Get native screen resolution
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        double ratio = (double)width / height;
        Log.info("Screen resolution: " + width + "x" + height + " (" + ratio + ")");

        new LwjglApplication(new GDXApplication(callback), LwjglConfig.from(Application.APPLICATION_CONFIG));
//        new LwjglApplication(new GdxTestApplication(callback), LwjglConfig.from(Application.APPLICATION_CONFIG));

    }

}
