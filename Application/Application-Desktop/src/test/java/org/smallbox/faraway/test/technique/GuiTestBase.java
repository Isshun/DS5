package org.smallbox.faraway.test.technique;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import org.junit.Before;
import org.smallbox.faraway.client.GDXApplication;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.game.ApplicationConfig;
import org.smallbox.faraway.util.FileUtils;
import org.smallbox.faraway.util.Log;
import org.smallbox.farpoint.desktop.LwjglConfig;

import java.awt.*;

/**
 * Created by Alex on 18/02/2017.
 */
public class GuiTestBase extends TestBase {

    @Before
    public void before() throws InterruptedException {
        initOk = false;
        testComplete = false;

        DependencyInjector.getInstance().registerModel(ApplicationConfig.class, () -> {
            ApplicationConfig applicationConfig = new ApplicationConfig();
            applicationConfig.game.updateInterval = 10;
            return applicationConfig;
        });

        FileUtils.createRoamingDirectory();

        // Get native screen resolution
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        double ratio = (double)width / height;
        Log.info("Screen resolution: " + width + "x" + height + " (" + ratio + ")");

        new LwjglApplication(new GDXApplication(this::init), LwjglConfig.from(Application.APPLICATION_CONFIG));

        while (!initOk) {
            Thread.sleep(100);
        }

    }

}
