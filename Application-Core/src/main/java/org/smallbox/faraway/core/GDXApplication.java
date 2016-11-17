package org.smallbox.faraway.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.jrenner.smartfont.SmartFontGenerator;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.engine.module.lua.LuaModuleManager;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.engine.renderer.SpriteManager;
import org.smallbox.faraway.core.engine.renderer.Viewport;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.world.SQLHelper;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.ui.UserInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GDXApplication extends ApplicationAdapter {
    private FPSLogger logger = new FPSLogger();

    public interface OnLoad {
        void onLoad(String message);
    }

    private final List<LoadTask>                _loadTasks = new ArrayList<>();
    private LoadTask                            _currentTask;
    private SpriteBatch                         _batch;
    private GDXRenderer                         _renderer;
    private Application                         _application;
    private BitmapFont[]                        _fonts;
    private BitmapFont                          _systemFont;
    private BitmapFont                          _systemFontDetail;
    private Executor                            _loadExecutor = Executors.newSingleThreadExecutor();

    @Override
    public void create () {
        _batch = new SpriteBatch();

        _systemFont = new BitmapFont(Gdx.files.internal("data/font-42.fnt"), Gdx.files.internal("data/font-42.png"), false);
        _systemFontDetail = new BitmapFont(Gdx.files.internal("data/font-32.fnt"), Gdx.files.internal("data/font-32.png"), false);

        _loadTasks.add(new LoadTask(this, "Load sprites", true) {
            @Override
            public void onExecute() {
                new SpriteManager();
            }
        });

        _loadTasks.add(new LoadTask(this, "Generate fonts", true) {
            @Override
            public void onExecute() {
                SmartFontGenerator fontGen = new SmartFontGenerator();
                _fonts = new BitmapFont[50];
                for (int i = 5; i < 50; i++) {
                    _fonts[i] = fontGen.createFont(Gdx.files.local("data/fonts/font.ttf"), "font-" + i, i);
                    _fonts[i].getData().flipped = true;
                }
            }
        });

        _loadTasks.add(new LoadTask(this, "Create renderer", true) {
            @Override
            public void onExecute() {
                _renderer = new GDXRenderer(_batch, _fonts);
            }
        });

        _loadTasks.add(new LoadTask(this, "Create app") {
            @Override
            public void onExecute() {
                _application = Application.getInstance();
            }
        });

        _loadTasks.add(new LoadTask(this, "Launch DB thread") {
            @Override
            public void onExecute() {
                new Thread(() -> {
                    try {
                        while (_application.isRunning()) {
                            SQLHelper.getInstance().update();
                            try { Thread.sleep(16); } catch (InterruptedException e) { e.printStackTrace(); }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        displayError(e.getMessage());
                        Application.getInstance().setRunning(false);
                    }
                    Log.info("Background DB thread terminated");
                }).start();
            }
        });

        // Load modules
        _loadTasks.add(new LoadTask(this, "Load modules") {
            @Override
            public void onExecute() {
                ModuleManager.getInstance().loadModules(message -> this.messageDetail = message);
            }
        });

        // Load lua modules
        _loadTasks.add(new LoadTask(this, "Load lua modules") {
            @Override
            public void onExecute() {
                LuaModuleManager.getInstance().init();
            }
        });

        // Load sprites
        _loadTasks.add(new LoadTask(this, "Load sprites", true) {
            @Override
            public void onExecute() {
                SpriteManager.getInstance().init();
            }
        });

        // Call dependency injector
        _loadTasks.add(new LoadTask(this, "Calling dependency injector") {
            @Override
            public void onExecute() {
                DependencyInjector.getInstance().injectDependencies();
            }
        });

        // Create app
        _loadTasks.add(new LoadTask(this, "Init app") {
            @Override
            public void onExecute() {
                GDXInputProcessor inputProcessor = new GDXInputProcessor(_application);
                Gdx.input.setInputProcessor(inputProcessor);
                _application.setInputProcessor(inputProcessor);
            }
        });

        _loadTasks.add(new LoadTask(this, "Resume game") {
            @Override
            public void onExecute() {
                Log.info("Resume game");

                //            UserInterface.getInstance().findById("base.ui.menu_main").setVisible(true);
                Application.getInstance().notify(observer -> observer.onCustomEvent("load_game.last_game", null));
//            GameManager.getInstance().createGame(Data.getData().getRegion("base.planet.corrin", "mountain"));
            }
        });

        _loadTasks.add(new LoadTask(this, "Launch world thread") {
            @Override
            public void onExecute() {
                new Thread(() -> {
                    try {
                        while (_application.isRunning()) {
                            _application.update();
                            Thread.sleep(1);
                        }
                        Log.info("Background world thread terminated");
                    } catch (Exception e) {
                        e.printStackTrace();
                        displayError(e.getMessage());
                        Application.getInstance().setRunning(false);
                    }
                }).start();
            }
        });
    }

    private void displayError(String message) {
        _loadTasks.add(new LoadTask(this, "Application has encounter an error and will be closed\n" + message) {
            @Override
            public void onExecute() {
                try { Thread.sleep(4000); } catch (InterruptedException e1) { e1.printStackTrace(); }
                System.exit(1);
            }
        });
    }

    public void onTaskComplete() {
        _currentTask = null;
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(.07f, 0.1f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Display task message
        LoadTask currentTask = _currentTask;
        if (currentTask != null) {
            _batch.begin();
            OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            _batch.setProjectionMatrix(camera.combined);
            if (_systemFont != null && currentTask.message != null) {
                _systemFont.draw(_batch, currentTask.message, 20, Gdx.graphics.getHeight() - 20);
            }
            if (_systemFontDetail != null && currentTask.messageDetail != null) {
                _systemFontDetail.draw(_batch, currentTask.messageDetail, 20, Gdx.graphics.getHeight() - 60);
            }
            _batch.end();
            return;
        }

        // Run next task
        if (!_loadTasks.isEmpty()) {
            _currentTask = _loadTasks.remove(0);
            if (_currentTask.onMainThread) {
                Gdx.app.postRunnable(_currentTask.runnable);
            } else {
                _loadExecutor.execute(_currentTask.runnable);
            }
            return;
        }

        // Render application
        _renderer.clear();
        _renderer.refresh();

        Viewport viewport = Game.getInstance() != null ? Game.getInstance().getViewport() : null;

        // Render game
        if (GameManager.getInstance().isLoaded()) {
            GameManager.getInstance().getGame().render(_renderer, viewport);
        }

        // Render interface
        UserInterface.getInstance().draw(_renderer, GameManager.getInstance().isLoaded());

        logger.log();
    }

    @Override
    public void dispose () {
        PathManager.getInstance().close();
    }
}