package org.smallbox.faraway.test;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.smallbox.faraway.client.GDXApplication;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.game.ApplicationConfig;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.util.FileUtils;
import org.smallbox.faraway.util.Log;
import org.smallbox.farpoint.desktop.LwjglConfig;

import java.awt.*;

/**
 * Created by Alex on 18/02/2017.
 */
public class TestBase {

    protected static ApplicationConfig applicationConfig = new ApplicationConfig();

    static {
        applicationConfig.game.tickPerHour = 500;
        applicationConfig.launchGui = false;
    }

    protected CharacterModule characterModule;
    protected ItemModule itemModule;
    protected ConsumableModule consumableModule;
    protected boolean testComplete = false;

    protected void init() {
        itemModule = Application.moduleManager.getModule(ItemModule.class);
        characterModule = Application.moduleManager.getModule(CharacterModule.class);
        consumableModule = Application.moduleManager.getModule(ConsumableModule.class);
    }

    protected void complete() {
        testComplete = true;
    }

    @Before
    public void before() {
        DependencyInjector.getInstance().registerModel(ApplicationConfig.class, () -> {
            ApplicationConfig applicationConfig = new ApplicationConfig();
            applicationConfig.game.updateInterval = 10;
            return applicationConfig;
        });
    }

    @After
    public void after() throws InterruptedException {
        Assert.assertTrue(testComplete);
    }

    protected void launchApplication(GDXApplication.GameTestCallback callback) throws InterruptedException {

        FileUtils.createRoamingDirectory();

        // Get native screen resolution
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        double ratio = (double)width / height;
        Log.info("Screen resolution: " + width + "x" + height + " (" + ratio + ")");

//        new LwjglApplication(new GDXApplication(applicationConfig, callback), LwjglConfig.from(applicationConfig));
        new LwjglApplication(new GDXTestApplication(() -> {
            init();
            callback.onApplicationReady();
        }), LwjglConfig.from(Application.APPLICATION_CONFIG));


        while (!testComplete) {
            Thread.sleep(100);
        }
    }

}
