package org.smallbox.faraway.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.jrenner.smartfont.SmartFontGenerator;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.engine.renderer.MainRenderer;
import org.smallbox.faraway.core.engine.renderer.ParticleRenderer;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.world.SQLHelper;
import org.smallbox.faraway.core.module.java.ModuleManager;
import org.smallbox.faraway.core.module.lua.LuaModuleManager;
import org.smallbox.faraway.ui.UserInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GDXApplication extends ApplicationAdapter {
    private FPSLogger logger = new FPSLogger();

    private static class LoadTask {
        private final String    message;
        private final Runnable  runnable;

        public LoadTask(String message, Runnable runnable) {
            this.message = message;
            this.runnable = runnable;
        }
    }

    private final List<LoadTask>                _loadTasks = new ArrayList<>();
    private String                              _currentMessage;
    private SpriteBatch                         _batch;
    private GDXRenderer                         _renderer;
    private Application                         _application;
    private long                                _lastRender;
    private BitmapFont[]                        _fonts;
    private BitmapFont                          _systemFont;

    @Override
    public void create () {
        _batch = new SpriteBatch();

        _systemFont = new BitmapFont(Gdx.files.internal("data/font-42.fnt"), Gdx.files.internal("data/font-42.png"), false);

        _loadTasks.add(new LoadTask("Load sprites", () -> {
            try {
                new SpriteManager();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        _loadTasks.add(new LoadTask("Generate fonts", () -> {
            SmartFontGenerator fontGen = new SmartFontGenerator();
            FileHandle exoFile = Gdx.files.local("data/res/fonts/font.ttf");
            _fonts = new BitmapFont[50];
            for (int i = 5; i < 50; i++) {
                _fonts[i] = fontGen.createFont(exoFile, "font-" + i, i);
                _fonts[i].getData().flipped = true;
            }
        }));

        _loadTasks.add(new LoadTask("Create renderer", () ->
                _renderer = new GDXRenderer(_batch, _fonts)));

        _loadTasks.add(new LoadTask("Create app", () ->
                _application = Application.getInstance()));

        _loadTasks.add(new LoadTask("Launch DB thread", () ->
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
                    System.out.println("Background DB thread terminated");
                }).start()));

        // Load resources
        _loadTasks.add(new LoadTask("Load resources", () -> {
            new Data().loadAll();
        }));

        // Load modules
        _loadTasks.add(new LoadTask("Load modules", () -> {
            ModuleManager.getInstance().load();
        }));

        // Load lua modules
        _loadTasks.add(new LoadTask("Load lua modules", () -> {
            LuaModuleManager.getInstance().load();
        }));

        // Create app
        _loadTasks.add(new LoadTask("Init app", () -> {
            GDXInputProcessor inputProcessor = new GDXInputProcessor(_application);
            Gdx.input.setInputProcessor(inputProcessor);
            _application.setInputProcessor(inputProcessor);
        }));

        _loadTasks.add(new LoadTask("Resume game", () -> {
            if (Data.config.byPassMenu) {
                Application.getInstance().notify(observer -> observer.onCustomEvent("load_game.last_game", null));
//                GameManager.getInstance().create(Data.getData().getRegion("base.planet.arrakis", "desert"));
//                GameManager.getInstance().loadGame(, Data.getData().getRegion("base.planet.arrakis", "desert"));

//                UserInterface.getInstance().findById("base.ui.menu_main").setVisible(true);
            }
        }));

        _loadTasks.add(new LoadTask("Launch world thread", () ->
                new Thread(() -> {
                    try {
                        while (_application.isRunning()) {
                            _application.update();
                            Thread.sleep(16);
                        }
                        System.out.println("Background world thread terminated");
                    } catch (Exception e) {
                        e.printStackTrace();
                        displayError(e.getMessage());
                        Application.getInstance().setRunning(false);
                    }
                }).start()));
    }

    private void displayError(String message) {
        _loadTasks.add(new LoadTask("Application has encounter an error and will be closed\n" + message, () -> {
            try { Thread.sleep(4000); } catch (InterruptedException e1) { e1.printStackTrace(); }
            System.exit(1);
        }));
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(.07f, 0.1f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!_loadTasks.isEmpty()) {
            LoadTask loadTask = _loadTasks.remove(0);
            Gdx.app.postRunnable(loadTask.runnable);
            _currentMessage = loadTask.message;
        }

        if (_currentMessage != null) {
            _batch.begin();
            OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            _batch.setProjectionMatrix(camera.combined);
            _systemFont.draw(_batch, _currentMessage, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
            _batch.end();
            _currentMessage = null;
            return;
        }

        long lastRenderInterval = System.currentTimeMillis() - _lastRender;
        _lastRender = System.currentTimeMillis();

        _renderer.clear();
        _renderer.refresh();

        Viewport viewport = Game.getInstance() != null ? Game.getInstance().getViewport() : null;

        // Render game
        if (GameManager.getInstance().isRunning()) {
            GameManager.getInstance().getGame().render(_renderer, viewport);
        }

        // Render interface
        UserInterface.getInstance().draw(_renderer, GameManager.getInstance().isRunning());

        // Render mini map
        if (GameManager.getInstance().isRunning()) {
            MainRenderer.getInstance().getMinimapRender().draw(_renderer, viewport, 0);
        }

        logger.log();
    }

    @Override
    public void dispose () {
        PathManager.getInstance().close();
    }
}