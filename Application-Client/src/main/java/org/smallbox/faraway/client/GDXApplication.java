package org.smallbox.faraway.client;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.jrenner.smartfont.SmartFontGenerator;
import org.smallbox.faraway.client.manager.InputManager;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.layer.GDXRenderer;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.ServerLuaModuleManager;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.engine.module.lua.LuaModuleManager;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.groovy.GroovyManager;
import org.smallbox.faraway.core.module.world.SQLManager;
import org.smallbox.faraway.core.task.LoadTask;
import org.smallbox.faraway.core.task.TaskManager;
import org.smallbox.faraway.util.FileUtils;
import org.smallbox.faraway.util.Log;

import java.io.File;
import java.util.function.Consumer;

public class GDXApplication extends ApplicationAdapter {
    private final GameTestCallback _callback;
    private FPSLogger fpsLogger = new FPSLogger();
    private TaskManager taskManager = new TaskManager();

    protected SpriteBatch                         _batch;
    protected Application                         _application;
    protected ApplicationClient                   _client;
    protected BitmapFont[]                        _fonts;
    protected BitmapFont                          _systemFont;
    private final InputProcessor _menuInputAdapter = new InputAdapter() {
        public boolean touchUp (int screenX, int screenY, int pointer, int button) {
            DependencyInjector.getInstance().getDependency(UIManager.class).getMenuViews().values().forEach(rootView -> clickOn(rootView.getView(), screenX, screenY));
            return false;
        }
    };
    private GameManager gameManager;
    private GDXRenderer gdxRenderer;

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
                new FileHandle(new File(FileUtils.BASE_PATH, "data/font-14.fnt")),
                new FileHandle(new File(FileUtils.BASE_PATH, "data/font-14.png")),
                false);

        _application = new Application();

        taskManager.addLoadTask("Generate fonts", true, () -> {
            SmartFontGenerator fontGen = new SmartFontGenerator();
            _fonts = new BitmapFont[50];
            for (int i = 5; i < 50; i++) {
                _fonts[i] = fontGen.createFont(new FileHandle(new File(FileUtils.BASE_PATH, "data/fonts/font.ttf")), "font-" + i, i);
                _fonts[i].getData().flipped = true;
            }
        });

        taskManager.addLoadTask("Create client app", false, () ->
                _client = new ApplicationClient());

        // Call dependency injector
        taskManager.addLoadTask("Calling dependency injector", false,
                DependencyInjector.getInstance()::injectApplicationDependencies);

        taskManager.addLoadTask("Init groovy manager", false,
                DependencyInjector.getInstance().getDependency(GroovyManager.class)::init);

        taskManager.addLoadTask("Create layer", true, () ->
                DependencyInjector.getInstance().getDependency(GDXRenderer.class).init(_batch, _fonts));

        // Server
        taskManager.addLoadTask("Launch DB thread", false, () ->
                taskManager.launchBackgroundThread(DependencyInjector.getInstance().getDependency(SQLManager.class)::update, 16));

        taskManager.addLoadTask("Load modules", false, () ->
                DependencyInjector.getInstance().getDependency(ModuleManager.class).loadModules(null));

        taskManager.addLoadTask("Load client lua modules", false, () -> DependencyInjector.getInstance().getDependency(ClientLuaModuleManager.class).init(true));
        taskManager.addLoadTask("Load server lua modules", false, () -> DependencyInjector.getInstance().getDependency(ServerLuaModuleManager.class).init(true));

//        Application.taskManager.addLoadTask("Load client lua modules", false, ApplicationClient.luaModuleManager::init);

//        // Debug
//        Application.taskManager.addLoadTask("Init input processor", false, () ->
//                Application.taskManager.launchBackgroundThread(DebugServer::start));

        // Load sprites
        taskManager.addLoadTask("Load sprites", true,
                DependencyInjector.getInstance().getDependency(SpriteManager.class)::init);

//        // Init input processor
//        Application.taskManager.addLoadTask("Init input processor", false, () ->
//                Gdx.input.setInputProcessor(ApplicationClient.inputManager));

        // Resume game
        taskManager.addLoadTask("Resume game", false, () -> {
            //            ApplicationClient.uiManager.findById("base.ui.menu_main").setVisible(true);
//            Application.gameManager.loadLastGame();
//            Application.notify(observer -> observer.onCustomEvent("load_game.last_game", null));
//            Application.gameManager.createGame(Application.data.getRegion("base.planet.corrin", "mountain"));
//                Application.gameManager.loadGame();

            if (_callback != null) {
                taskManager.addLoadTask("Test callback onApplicationReady", false, _callback::onApplicationReady);
            }

            Application.isLoaded = true;
        });

        gameManager = DependencyInjector.getInstance().getDependency(GameManager.class);
        gdxRenderer = DependencyInjector.getInstance().getDependency(GDXRenderer.class);

//        Application.taskManager.addLoadTask("Launch world thread", false, () ->
    }

    @Override
    public void render () {
        if (gameManager != null && gameManager.getGameStatus() == Game.GameStatus.STARTED) {
            gameRender();
        } else if (Application.isLoaded) {
            menuRender();
        } else {
            minimalRender();
        }

        errorRender();
    }

    private void errorRender() {

        if (Log._lastErrorMessage != null && System.currentTimeMillis() < Log._lastErrorTime + 5000) {
            _batch.begin();

            OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            _batch.setProjectionMatrix(camera.combined);

            _systemFont.setColor(0.0f, 0.0f, 0.0f, 1);
            _systemFont.draw(_batch, Log._lastErrorMessage, 21, Gdx.graphics.getHeight() - 21);

            _systemFont.setColor(1.0f, 0.2f, 0.2f, 1);
            _systemFont.draw(_batch, Log._lastErrorMessage, 20, Gdx.graphics.getHeight() - 20);

            _batch.end();
        }

    }

    private void gameRender() {
        Gdx.input.setInputProcessor(DependencyInjector.getInstance().getDependency(InputManager.class));
        Gdx.gl.glClearColor(.07f, 0.1f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render application
        gdxRenderer.clear();
        gdxRenderer.refresh();

        // Render game
        if (gameManager.isLoaded()) {
            DependencyInjector.getInstance().getDependency(LayerManager.class).render(gameManager.getGame());
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
                    new FileHandle(FileUtils.getDataFile("font-32.fnt")),
                    new FileHandle(FileUtils.getDataFile("font-32.png")),
                    false);
            _menuFont.setColor(new Color(0x80ced6ff));
        }

        Gdx.input.setInputProcessor(_menuInputAdapter);
        Gdx.gl.glClearColor(.07f, 0.1f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//
//        _batch.begin();
//
//        OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        _batch.setProjectionMatrix(camera.combined);
//
//        _batch.draw(_bgMenu, 0, 0);
//        _batch.draw(_bgMenu2, 0, 0);
//
//        _menuFont.draw(_batch, "New Game", 32, Gdx.graphics.getHeight() - 32);
//        _menuFont.draw(_batch, "Exit", 32, Gdx.graphics.getHeight() - 80);
//

        MenuManager menuManager = new MenuManager();
        menuManager.display("base.ui.menu.main");

        // Render application
        gdxRenderer.clear();
        gdxRenderer.refresh();
        DependencyInjector.getInstance().getDependency(UIManager.class).getMenuViews().forEach((name, view) -> view.draw(gdxRenderer, 0, 0));
//
//        _batch.end();
    }

    private void clickOn(View view, int screenX, int screenY) {
        if (view.hasClickListener() && view.isVisible() && hasHeriarchieVisible(view)
                && screenX > view.getFinalX()
                && screenX < view.getFinalX() + view.getWidth()
                && screenY > view.getFinalY()
                && screenY < view.getFinalY() + view.getHeight()) {
            view.click(screenX, screenY);
        }

        if (view.getViews() != null) {
            view.getViews().forEach(subView -> clickOn(subView, screenX, screenY));
        }
    }

    private boolean hasHeriarchieVisible(View view) {
        if (view.getParent() != null) {
            return hasHeriarchieVisible(view.getParent());
        }
        return view.isVisible();
    }

    private void minimalRender() {
        Gdx.gl.glClearColor(.07f, 0.1f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        _batch.begin();

        OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        _batch.setProjectionMatrix(camera.combined);

        // Display tasks message
        taskManager.getLoadTasks().forEach(new Consumer<LoadTask>() {

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
//        Application.dependencyInjector.getObject(PathManager.class).close();
    }
}