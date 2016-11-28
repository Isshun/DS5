package org.smallbox.faraway.client;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.jrenner.smartfont.SmartFontGenerator;
import org.smallbox.faraway.core.Application;

public class GDXApplication extends ApplicationAdapter {
    private FPSLogger fpsLogger = new FPSLogger();

    private SpriteBatch                         _batch;
    private Application                         _application;
    private ApplicationClient                   _client;
    private BitmapFont[]                        _fonts;
    private BitmapFont                          _systemFont;

    private class TestBinding {
        void sayHello() {
            System.out.println("hello");
        }
    }

    @Override
    public void create () {
        _batch = new SpriteBatch();

        _systemFont = new BitmapFont(Gdx.files.internal("data/font-22.fnt"), Gdx.files.internal("data/font-22.png"), false);

        Application.taskManager.addLoadTask("Generate fonts", true, () -> {
            SmartFontGenerator fontGen = new SmartFontGenerator();
            _fonts = new BitmapFont[50];
            for (int i = 5; i < 50; i++) {
                _fonts[i] = fontGen.createFont(Gdx.files.local("data/fonts/font.ttf"), "font-" + i, i);
                _fonts[i].getData().flipped = true;
            }
        });

        Application.taskManager.addLoadTask("Create app", false, () ->
                _application = new Application());

        Application.taskManager.addLoadTask("Create app", false, () ->
                _client = new ApplicationClient());

        Application.taskManager.addLoadTask("Create app", false, () ->
                _application.groovyManager.init());

        Application.taskManager.addLoadTask("Create renderer", true, () ->
                ApplicationClient.gdxRenderer.init(_batch, _fonts));



        // Server

        Application.taskManager.addLoadTask("Launch DB thread", false, () ->
                Application.taskManager.launchBackgroundThread(Application.sqlManager::update, 16));

        Application.taskManager.addLoadTask("Load modules", false, () ->
                Application.moduleManager.loadModules(null));

        Application.taskManager.addLoadTask("Load lua modules", false, Application.luaModuleManager::init);



        // Load sprites
        Application.taskManager.addLoadTask("Load sprites", true,
                ApplicationClient.spriteManager::init);

        // Call dependency injector
        Application.taskManager.addLoadTask("Calling dependency injector", false,
                Application.dependencyInjector::injectDependencies);

        // Init input processor
        Application.taskManager.addLoadTask("Init input processor", false, () ->
                Gdx.input.setInputProcessor(ApplicationClient.inputManager));

        // Resume game
        Application.taskManager.addLoadTask("Resume game", false, () -> {
            //            ApplicationClient.uiManager.findById("base.ui.menu_main").setVisible(true);
            Application.notify(observer -> observer.onCustomEvent("load_game.last_game", null));
//            Application.gameManager.createGame(Application.data.getRegion("base.planet.corrin", "mountain"));
//                Application.gameManager.loadGame();

            Application.isLoaded = true;
        });

        // Launch world thread
        Application.taskManager.addLoadTask("Launch world thread", false, () ->
                Application.taskManager.launchBackgroundThread(() -> _application.update(), 16));
    }

    @Override
    public void render () {
        if (Application.isLoaded) {
            regularRender();
        } else {
            minimalRender();
        }
    }

    private void regularRender() {
        Gdx.gl.glClearColor(.07f, 0.1f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render application
        ApplicationClient.gdxRenderer.clear();
        ApplicationClient.gdxRenderer.refresh();

        // Render game
        if (Application.gameManager.isLoaded()) {
            Application.notify(observer -> observer.onGameRender(Application.gameManager.getGame()));
        }

//        fpsLogger.log();
    }

    private void minimalRender() {
        Gdx.gl.glClearColor(.07f, 0.1f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        _batch.begin();

        OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        _batch.setProjectionMatrix(camera.combined);

        // Display tasks message
        Application.taskManager.getLoadTasks().forEach(task -> {
            switch (task.state) {
                case NONE:
                    _systemFont.setColor(1f, 1f, 1f, 0.5f);
                    break;
                case RUNNING:
                    _systemFont.setColor(0.5f, 0.9f, 0.8f, 1);
                    break;
                case COMPLETE:
                    _systemFont.setColor(0.9f, 0.6f, 0.8f, 1);
                    break;
            }

            _systemFont.draw(_batch, task.label, 20, Gdx.graphics.getHeight() - (Application.taskManager.getLoadTasks().indexOf(task) * 20));
        });

        _batch.end();
    }

    @Override
    public void dispose () {
        Application.pathManager.close();
    }
}