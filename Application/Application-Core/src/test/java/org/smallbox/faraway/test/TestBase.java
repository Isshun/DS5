package org.smallbox.faraway.test;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.game.ApplicationConfig;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.util.Log;

/**
 * Created by Alex on 18/02/2017.
 */
public class TestBase {

    private static Application _application;

    public abstract class GameTestCallback {
        public boolean _quit;

        public abstract void onApplicationReady();
        public abstract void onGameUpdate(long tick);
        public void quit() {
            _quit = true;
        }
    }

    protected void launchGame(GameTestCallback callback) {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.game.updateInterval = 1;
        DependencyInjector.getInstance().registerModel(applicationConfig);

        Application.taskManager.addLoadTask("Create server app", false, () ->
                _application = new Application());

        Application.taskManager.addLoadTask("Init groovy manager", false,
                Application.groovyManager::init);


        // Server

        Application.taskManager.addLoadTask("Launch DB thread", false, () ->
                Application.taskManager.launchBackgroundThread(Application.sqlManager::update, 16));

        Application.taskManager.addLoadTask("Load modules", false, () ->
                Application.moduleManager.loadModules(null));

        Application.taskManager.addLoadTask("Load server lua modules", false, Application.luaModuleManager::init);

//        Application.taskManager.addLoadTask("Load client lua modules", false, ApplicationClient.luaModuleManager::init);

//        // Debug
//        Application.taskManager.addLoadTask("Init input processor", false, () ->
//                Application.taskManager.launchBackgroundThread(DebugServer::start));

        // Call dependency injector
        Application.taskManager.addLoadTask("Calling dependency injector", false,
                Application.dependencyInjector::injectDependencies);

        // Resume game
        Application.taskManager.addLoadTask("Resume game", false, () -> {
            //            ApplicationClient.uiManager.findById("base.ui.menu_main").setVisible(true);
//            Application.notify(observer -> observer.onCustomEvent("load_game.last_game", null));
//            Application.gameManager.createGame(Application.data.getRegion("base.planet.corrin", "mountain"));
//                Application.gameManager.loadGame();

            callback.onApplicationReady();

            Application.isLoaded = true;
        });

        // Launch world thread
        Application.taskManager.addLoadTask("Launch world thread", false, () ->
                Application.taskManager.launchBackgroundThread(() -> {
                    try {
                        if (Application.gameManager.getGame() != null && Application.gameManager.getGame().getState() == Game.GameModuleState.STARTED) {
                            Application.notify(observer -> observer.onGameUpdate(Application.gameManager.getGame()));
                            callback.onGameUpdate(Application.gameManager.getGame().getTick());
                        }
                    } catch (Exception e) {
                        Log.error(e);
                    }
                }, Application.APPLICATION_CONFIG.game.updateInterval));

//        Application.taskManager.addLoadTask("Launch world thread", false, callback::onApplicationReady);

        while (!callback._quit) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (Application.gameManager.getGame() != null) {
            Application.gameManager.getGame().setRunning(false);
        }
    }

}
