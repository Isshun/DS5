package org.smallbox.faraway.test.technique;

import org.junit.After;
import org.junit.Before;
import org.smallbox.faraway.client.GDXApplication;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.game.ApplicationConfig;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.item.ItemModule;

/**
 * Created by Alex on 18/02/2017.
 */
public class TestBase {

    protected static ApplicationConfig applicationConfig = new ApplicationConfig();

    static {
        applicationConfig.game.tickPerHour = 500;
        applicationConfig.launchGui = false;
    }

    protected boolean testComplete = false;

    protected static boolean initOk;
    protected static GDXApplication _application;

    protected void init() {
        initOk = true;
    }

    public CharacterModule characterModule;
    public ConsumableModule consumableModule;
    public ItemModule itemModule;
    public Game game;

    public void injectModules(Game game) {
        this.game = game;
        this.itemModule = game.getModule(ItemModule.class);
        this.characterModule = game.getModule(CharacterModule.class);
        this.consumableModule = game.getModule(ConsumableModule.class);
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
    }

}