//package org.smallbox.farpoint.desktop;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.files.FileHandle;
//import com.badlogic.gdx.graphics.g2d.BitmapFont;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import org.jrenner.smartfont.SmartFontGenerator;
//import org.smallbox.faraway.client.ApplicationClient;
//import org.smallbox.faraway.client.GDXApplication;
//import org.smallbox.faraway.core.Application;
//import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
//import org.smallbox.faraway.core.game.ApplicationConfig;
//import org.smallbox.faraway.core.game.Game;
//import org.smallbox.faraway.util.Log;
//
//import java.io.File;
//
///**
// * Created by Alex on 18/02/2017.
// */
//public class GdxTestApplication extends GDXApplication {
//    private static final boolean HAS_GDX = true;
//
//    private final GameTestCallback callback;
//
//    public GdxTestApplication(GameTestCallback callback) {
//        super(callback);
//        this.callback = callback;
//    }
//
//    @Override
//    public void create() {
//        ApplicationConfig applicationConfig = new ApplicationConfig();
//        applicationConfig.game.updateInterval = 1;
//        DependencyInjector.getInstance().registerModel(applicationConfig);
//
//        if (HAS_GDX) {
//            _batch = new SpriteBatch();
//
//            _systemFont = new BitmapFont(
//                    new FileHandle(new File(Application.BASE_PATH, "data/font-14.fnt")),
//                    new FileHandle(new File(Application.BASE_PATH, "data/font-14.png")),
//                    false);
//
//            Application.taskManager.addLoadTask("Generate fonts", true, () -> {
//                SmartFontGenerator fontGen = new SmartFontGenerator();
//                _fonts = new BitmapFont[50];
//                for (int i = 5; i < 50; i++) {
//                    _fonts[i] = fontGen.createFont(new FileHandle(new File(Application.BASE_PATH, "data/fonts/font.ttf")), "font-" + i, i);
//                    _fonts[i].getData().flipped = true;
//                }
//            });
//        }
//
//
//        Application.taskManager.addLoadTask("Create server app", false, () ->
//                _application = new Application());
//
//        if (HAS_GDX) {
//            Application.taskManager.addLoadTask("Create client app", false, () ->
//                    _client = new ApplicationClient());
//        }
//
//        Application.taskManager.addLoadTask("Init groovy manager", false,
//                Application.groovyManager::init);
//
//        if (HAS_GDX) {
//            Application.taskManager.addLoadTask("Create layer", true, () ->
//                    ApplicationClient.gdxLayer.init(_batch, _fonts));
//        }
//
//        // Server
//
//        Application.taskManager.addLoadTask("Launch DB thread", false, () ->
//                Application.taskManager.launchBackgroundThread(Application.sqlManager::update, 16));
//
//        Application.taskManager.addLoadTask("Load modules", false, () ->
//                Application.moduleManager.loadModules(null));
//
//        Application.taskManager.addLoadTask("Load server lua modules", false, Application.luaModuleManager::init);
//
////        Application.taskManager.addLoadTask("Load client lua modules", false, ApplicationClient.luaModuleManager::init);
//
//        if (HAS_GDX) {
//
//            // Load sprites
//            Application.taskManager.addLoadTask("Load sprites", true,
//                    ApplicationClient.spriteManager::init);
//
//        }
//
////        // Debug
////        Application.taskManager.addLoadTask("Init input processor", false, () ->
////                Application.taskManager.launchBackgroundThread(DebugServer::start));
//
//        // Call dependency injector
//        Application.taskManager.addLoadTask("Calling dependency injector", false,
//                Application.dependencyInjector::injectDependencies);
//
//        if (HAS_GDX) {
//
//            // Init input processor
//            Application.taskManager.addLoadTask("Init input processor", false, () ->
//                    Gdx.input.setInputProcessor(ApplicationClient.inputManager));
//
//        }
//
//        // Resume game
//        Application.taskManager.addLoadTask("Resume game", false, () -> {
//            //            ApplicationClient.uiManager.findById("base.ui.menu_main").setVisible(true);
////            Application.notify(observer -> observer.onCustomEvent("load_game.last_game", null));
////            Application.gameManager.createGame(Application.data.getRegion("base.planet.corrin", "mountain"));
////                Application.gameManager.loadGame();
//
//            callback.onApplicationReady();
//
//            Application.isLoaded = true;
//        });
//
//        // Launch world thread
//        Application.taskManager.addLoadTask("Launch world thread", false, () ->
//                Application.taskManager.launchBackgroundThread(() -> {
//                    try {
//                        if (Application.gameManager.getGame() != null && Application.gameManager.getGame().getState() == Game.GameStatus.STARTED) {
//                            Application.notify(observer -> observer.onGameUpdate(Application.gameManager.getGame()));
//
//                            try {
//                                callback.onGameUpdate(Application.gameManager.getGame().getTick());
//                            } catch (Exception e) {
//                                callback.quit();
//                            }
//                        }
//                    } catch (Exception e) {
//                        throw new GameException(e);
//                    }
//                }, Application.APPLICATION_CONFIG.game.updateInterval));
//
////        Application.taskManager.addLoadTask("Launch world thread", false, callback::onApplicationReady);
//
////        while (!callback._quit) {
////            try {
////                Thread.sleep(10);
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            }
////        }
////
////        if (Application.gameManager.getGame() != null) {
////            Application.gameManager.getGame().setRunning(false);
////        }
//    }
//
//}
