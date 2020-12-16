package org.smallbox.faraway.test.technique;

import org.smallbox.faraway.client.GDXApplication;
import org.smallbox.faraway.core.Application;

/**
 * Created by Alex on 27/02/2017.
 */
public class GDXTestApplication extends GDXApplication {
    private final GameTestCallback _callback;

    public GDXTestApplication(GameTestCallback callback) {
        super(callback);

        _callback = callback;
    }

    @Override
    public void create () {

        _application = new Application();

        Application.taskManager.addLoadTask("Init groovy manager", false,
                Application.groovyManager::init);


        // Server
        Application.taskManager.addLoadTask("Launch DB thread", false, () ->
                Application.taskManager.launchBackgroundThread(Application.sqlManager::update, 16));

        Application.taskManager.addLoadTask("Load modules", false, () ->
                Application.moduleManager.loadModules(null));

        Application.taskManager.addLoadTask("Load server lua modules", false, () -> Application.luaModuleManager.init(false));

//        Application.taskManager.addLoadTask("Load client lua modules", false, ApplicationClient.luaModuleManager::init);

//        // Debug
//        Application.taskManager.addLoadTask("Init input processor", false, () ->
//                Application.taskManager.launchBackgroundThread(DebugServer::start));

        // Call dependency injector
        Application.taskManager.addLoadTask("Calling dependency injector", false,
                Application.dependencyInjector::injectApplicationDependencies);

        // Resume game
        Application.taskManager.addLoadTask("Resume game", false, () -> {

            if (_callback != null) {
                Application.taskManager.addLoadTask("Test callback onApplicationReady", false, _callback::onApplicationReady);
            }

            Application.isLoaded = true;
        });

    }

    @Override
    public void render () {
    }

}
