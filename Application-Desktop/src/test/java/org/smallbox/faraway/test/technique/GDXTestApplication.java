//package org.smallbox.faraway.test.technique;
//
//import org.smallbox.faraway.client.GDXApplication;
//import org.smallbox.faraway.core.Application;
//import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
//import org.smallbox.faraway.core.module.world.SQLManager;
//import org.smallbox.faraway.core.task.TaskManager;
//
///**
// * Created by Alex
// */
//public class GDXTestApplication extends GDXApplication {
//    private final GameTestCallback _callback;
//
//    public GDXTestApplication(GameTestCallback callback) {
//        super(callback);
//
//        _callback = callback;
//    }
//
//    @Override
//    public void create () {
//
//        TaskManager taskManager = new TaskManager();
//        _application = new Application();
//
//        taskManager.addLoadTask("Init groovy manager", false,
//                Application.groovyManager::init);
//
//        // Call dependency injector
//        taskManager.addLoadTask("Calling dependency injector", false,
//                Application.dependencyInjector::injectApplicationDependencies);
//
//        // Server
//        taskManager.addLoadTask("Launch DB thread", false, () ->
//                taskManager.launchBackgroundThread(DependencyInjector.getInstance().getDependency(SQLManager.class)::update, 16));
//
//        taskManager.addLoadTask("Load modules", false, () ->
//                Application.moduleManager.loadModules(null));
//
//        taskManager.addLoadTask("Load server lua modules", false, () -> Application.luaModuleManager.init(false));
//
////        Application.taskManager.addLoadTask("Load client lua modules", false, ApplicationClient.luaModuleManager::init);
//
////        // Debug
////        Application.taskManager.addLoadTask("Init input processor", false, () ->
////                Application.taskManager.launchBackgroundThread(DebugServer::start));
//
//        // Resume game
//        taskManager.addLoadTask("Resume game", false, () -> {
//
//            if (_callback != null) {
//                taskManager.addLoadTask("Test callback onApplicationReady", false, _callback::onApplicationReady);
//            }
//
//            Application.isLoaded = true;
//        });
//
//    }
//
//    @Override
//    public void render () {
//    }
//
//}
