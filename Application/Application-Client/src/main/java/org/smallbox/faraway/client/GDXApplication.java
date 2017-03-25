package org.smallbox.faraway.client;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.jrenner.smartfont.SmartFontGenerator;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.task.LoadTask;
import org.smallbox.faraway.util.FileUtils;

import java.io.File;
import java.util.function.Consumer;

public class GDXApplication extends ApplicationAdapter {
    private final GameTestCallback _callback;
    private FPSLogger fpsLogger = new FPSLogger();

    protected SpriteBatch                         _batch;
    protected Application                         _application;
    protected ApplicationClient                   _client;
    protected BitmapFont[]                        _fonts;
    protected BitmapFont                          _systemFont;

    public GDXApplication(GameTestCallback callback) {
        _callback = callback;
    }

    public interface GameTestCallback {
        void onApplicationReady();
    }

    @Override
    public void create () {
        _batch = new SpriteBatch();

        _systemFont = new BitmapFont(
                new FileHandle(new File(Application.BASE_PATH, "data/font-14.fnt")),
                new FileHandle(new File(Application.BASE_PATH, "data/font-14.png")),
                false);

        _application = new Application();

        Application.taskManager.addLoadTask("Generate fonts", true, () -> {
            SmartFontGenerator fontGen = new SmartFontGenerator();
            _fonts = new BitmapFont[50];
            for (int i = 5; i < 50; i++) {
                _fonts[i] = fontGen.createFont(new FileHandle(new File(Application.BASE_PATH, "data/fonts/font.ttf")), "font-" + i, i);
                _fonts[i].getData().flipped = true;
            }
        });

        Application.taskManager.addLoadTask("Create client app", false, () ->
                _client = new ApplicationClient());

        Application.taskManager.addLoadTask("Init groovy manager", false,
                Application.groovyManager::init);

        Application.taskManager.addLoadTask("Create layer", true, () ->
                ApplicationClient.GDX_LAYER.init(_batch, _fonts));

        // Server
        Application.taskManager.addLoadTask("Launch DB thread", false, () ->
                Application.taskManager.launchBackgroundThread(Application.sqlManager::update, 16));

        Application.taskManager.addLoadTask("Load modules", false, () ->
                Application.moduleManager.loadModules(null));

        Application.taskManager.addLoadTask("Load server lua modules", false, () -> Application.luaModuleManager.init(true));
        Application.taskManager.addLoadTask("Load server lua modules", false, () -> ApplicationClient.luaModuleManager.init(true));

//        Application.taskManager.addLoadTask("Load client lua modules", false, ApplicationClient.luaModuleManager::init);

//        // Debug
//        Application.taskManager.addLoadTask("Init input processor", false, () ->
//                Application.taskManager.launchBackgroundThread(DebugServer::start));

        // Load sprites
        Application.taskManager.addLoadTask("Load sprites", true,
                ApplicationClient.spriteManager::init);

        // Call dependency injector
        Application.taskManager.addLoadTask("Calling dependency injector", false,
                Application.dependencyInjector::injectDependencies);

//        // Init input processor
//        Application.taskManager.addLoadTask("Init input processor", false, () ->
//                Gdx.input.setInputProcessor(ApplicationClient.inputManager));

        // Resume game
        Application.taskManager.addLoadTask("Resume game", false, () -> {
            //            ApplicationClient.uiManager.findById("base.ui.menu_main").setVisible(true);
//            Application.gameManager.loadLastGame();
//            Application.notify(observer -> observer.onCustomEvent("load_game.last_game", null));
//            Application.gameManager.createGame(Application.data.getRegion("base.planet.corrin", "mountain"));
//                Application.gameManager.loadGame();

            if (_callback != null) {
                Application.taskManager.addLoadTask("Test callback onApplicationReady", false, _callback::onApplicationReady);
            }

            Application.isLoaded = true;
        });

//        Application.taskManager.addLoadTask("Launch world thread", false, () ->
    }

    @Override
    public void render () {
        if (Application.gameManager != null && Application.gameManager.getGameStatus() == Game.GameStatus.STARTED) {
            gameRender();
        } else if (Application.isLoaded) {
            menuRender();
        } else {
            minimalRender();
        }
    }

    private void gameRender() {
        Gdx.input.setInputProcessor(ApplicationClient.inputManager);
        Gdx.gl.glClearColor(.07f, 0.1f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render application
        ApplicationClient.GDX_LAYER.clear();
        ApplicationClient.GDX_LAYER.refresh();

        // Render game
        if (Application.gameManager.isLoaded()) {
            ApplicationClient.notify(observer -> observer.onGameRender(Application.gameManager.getGame()));
        }

//        fpsLogger.log();
    }

    private boolean _menuInit;
    private Texture _bgMenu;
    private Texture _bgMenu2;
    private BitmapFont _menuFont;

    private void menuRender() {
        if (!_menuInit) {
            _menuInit = true;
            _bgMenu = new Texture(FileUtils.getFileHandle("data/graphics/menu_bg.jpg"));
            _bgMenu2 = new Texture(FileUtils.getFileHandle("data/graphics/menu_bg.png"));

            _menuFont = new BitmapFont(
                    new FileHandle(new File(Application.BASE_PATH, "data/font-32.fnt")),
                    new FileHandle(new File(Application.BASE_PATH, "data/font-32.png")),
                    false);
            _menuFont.setColor(new Color(0x80ced6ff));

            Gdx.input.setInputProcessor(new InputAdapter() {
                public boolean touchUp (int screenX, int screenY, int pointer, int button) {
                    if (screenY > 20 && screenY < 60) {
                        Application.gameManager.createGame("base.planet.corrin", "mountain", 12, 16, 2, null);
                    }
                    if (screenY > 70 && screenY < 110) {
                        Gdx.app.exit();
                    }
                    return false;
                }
            });
        }

        Gdx.gl.glClearColor(.07f, 0.1f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        _batch.begin();

        OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        _batch.setProjectionMatrix(camera.combined);

        _batch.draw(_bgMenu, 0, 0);
        _batch.draw(_bgMenu2, 0, 0);

        _menuFont.draw(_batch, "New Game", 32, Gdx.graphics.getHeight() - 32);
        _menuFont.draw(_batch, "Exit", 32, Gdx.graphics.getHeight() - 80);

        _batch.end();
    }

    private void minimalRender() {
        Gdx.gl.glClearColor(.07f, 0.1f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        _batch.begin();

        OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        _batch.setProjectionMatrix(camera.combined);

        // Display tasks message
        Application.taskManager.getLoadTasks().forEach(new Consumer<LoadTask>() {

            private int taskIndex;

            @Override
            public void accept(LoadTask task) {

                switch (task.state) {
                    case NONE:
                    case WAITING:
                        _systemFont.setColor(1f, 1f, 1f, 0.5f);
                        break;
                    case RUNNING:
                        _systemFont.setColor(0.5f, 0.9f, 0.8f, 1);
                        break;
                    case COMPLETE:
                        _systemFont.setColor(0.9f, 0.6f, 0.8f, 1);
                        break;
                }

                _systemFont.draw(_batch, task.label, 12, Gdx.graphics.getHeight() - (++taskIndex * 20 + 12));
            }
        });

        _batch.end();
    }

    @Override
    public void dispose () {
        Application.pathManager.close();
    }
}