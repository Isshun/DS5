package org.smallbox.faraway.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.game.ApplicationConfig;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.item.ItemModule;

/**
 * Created by Alex on 03/03/2017.
 */
public class HeadlessTestBase {

    protected static ApplicationConfig applicationConfig = new ApplicationConfig();

    static {
        applicationConfig.game.tickPerHour = 500;
        applicationConfig.launchGui = false;
    }

    protected CharacterModule characterModule;
    protected ItemModule itemModule;
    protected ConsumableModule consumableModule;
    protected boolean testComplete = false;

    private boolean initOk;
    private static GDXTestApplication _application;

    protected void init() {
        itemModule = Application.moduleManager.getModule(ItemModule.class);
        characterModule = Application.moduleManager.getModule(CharacterModule.class);
        consumableModule = Application.moduleManager.getModule(ConsumableModule.class);
        initOk = true;
    }

    protected void complete() {
        testComplete = true;
    }

    @Before
    public void before() throws InterruptedException {
        initOk = false;
        testComplete = false;

        DependencyInjector.getInstance().registerModel(ApplicationConfig.class, () -> {
            ApplicationConfig applicationConfig = new ApplicationConfig();
            applicationConfig.game.updateInterval = 10;
            return applicationConfig;
        });

        if (_application == null) {
            _application = new GDXTestApplication(this::init);
            _application.create();

            while (!initOk) {
                Thread.sleep(100);
            }
        }

    }

    @After
    public void after() throws InterruptedException {

        while (!testComplete) {
            Thread.sleep(10);
        }

        Assert.assertTrue(testComplete);
    }

}
