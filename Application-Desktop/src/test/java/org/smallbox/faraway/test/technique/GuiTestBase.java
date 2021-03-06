//package org.smallbox.faraway.test.technique;
//
//import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
//import main.java.org.smallbox.farpoint.desktop.LwjglConfig;
//import org.junit.Before;
//import org.smallbox.faraway.client.GDXApplication;
//import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
//import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfig;
//import org.smallbox.faraway.util.FileUtils;
//import org.smallbox.faraway.util.log.Log;
//
//import java.awt.*;
//
///**
// * Created by Alex
// */
//public class GuiTestBase extends TestBase {
//
//    @Before
//    public void before() throws InterruptedException {
//        initOk = false;
//        testComplete = false;
//
//        ApplicationConfig applicationConfig = new ApplicationConfig();
//        applicationConfig.game.startGameTime = 4;
//
////        DependencyInjector.getInstance().registerModel(ApplicationConfig.class, () -> applicationConfig);
//
//        FileUtils.createRoamingDirectory();
//
//        // Get native screen resolution
//        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
//        int width = gd.getDisplayMode().getWidth();
//        int height = gd.getDisplayMode().getHeight();
//        double ratio = (double)width / height;
//        Log.info("Screen resolution: " + width + "x" + height + " (" + ratio + ")");
//
//        new LwjglApplication(new GDXApplication(this::init), LwjglConfig.from(applicationConfig));
//
//        while (!initOk) {
//            Thread.sleep(100);
//        }
//
//    }
//
//}
