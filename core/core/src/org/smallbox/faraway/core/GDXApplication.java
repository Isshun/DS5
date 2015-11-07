package org.smallbox.faraway.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.jrenner.smartfont.SmartFontGenerator;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.engine.renderer.ParticleRenderer;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.module.java.ModuleManager;
import org.smallbox.faraway.core.module.lua.LuaModuleManager;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.ui.UserInterface;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GDXApplication extends ApplicationAdapter {
    private static class LoadRunnable {
        private final String    message;
        private final Runnable  runnable;

        public LoadRunnable(String message, Runnable runnable) {
            this.message = message;
            this.runnable = runnable;
        }
    }

    private final BlockingQueue<LoadRunnable>   _queue = new LinkedBlockingQueue<>();
    private Runnable                            _currentRunnable;
    private String                              _currentMessage;
    private SpriteBatch                         _batch;
    private GDXRenderer                         _renderer;
    private Application                         _application;
    private long                                _startTime = -1;
    private long                                _lastRender;
    private BitmapFont[]                        _fonts;
    private BitmapFont                          _systemFont;

    @Override
    public void create () {
        _batch = new SpriteBatch();

        _systemFont = new BitmapFont(Gdx.files.internal("data/font-42.fnt"), Gdx.files.internal("data/font-42.png"), false);

        _queue.add(new LoadRunnable("Load sprites", () -> {
            try {
                new SpriteManager();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        _queue.add(new LoadRunnable("Generate fonts", () -> {
            SmartFontGenerator fontGen = new SmartFontGenerator();
            FileHandle exoFile = Gdx.files.local("data/res/fonts/font.ttf");
            _fonts = new BitmapFont[50];
            for (int i = 5; i < 50; i++) {
                _fonts[i] = fontGen.createFont(exoFile, "font-" + i, i);
                _fonts[i].getData().flipped = true;
            }
        }));

        _queue.add(new LoadRunnable("Create renderer", () -> {
            _renderer = new GDXRenderer(_batch, _fonts);
        }));

        _queue.add(new LoadRunnable("Create app", () -> {
            _application = Application.getInstance();
        }));

        // Load resources
        _queue.add(new LoadRunnable("Load resources", () -> {
            Data data = new Data();
            data.loadAll();
        }));

        // Load modules
        _queue.add(new LoadRunnable("Load modules", () -> {
            ModuleManager.getInstance().load();
        }));

        // Load lua modules
        _queue.add(new LoadRunnable("Load lua modules", () -> {
            LuaModuleManager.getInstance().load();
        }));

        // Create app
        _queue.add(new LoadRunnable("Init app", () -> {
//            GDXLightRenderer lightRenderer = null;
//            if (Data.config.render.light) {
//                lightRenderer = new GDXLightRenderer();
//            }

            ParticleRenderer particleRenderer = null;
            if (Data.config.render.particle) {
                particleRenderer = new ParticleRenderer();
            }

            GDXInputProcessor inputProcessor = new GDXInputProcessor(_application);
            Gdx.input.setInputProcessor(inputProcessor);
            Gdx.graphics.setContinuousRendering(true);
            Gdx.graphics.setVSync(true);
            Gdx.graphics.requestRendering();

            _application.setInputProcessor(inputProcessor);
        }));

        _queue.add(new LoadRunnable("Resume game", () -> {
            if (Data.config.byPassMenu) {
                Application.getInstance().notify(observer -> observer.onCustomEvent("load_game.last_game", null));
//                GameManager.getInstance().loadGame(, Data.getData().getRegion("base.planet.arrakis", "desert"));
//                _application.loadGame("12.sav");
//                _application.whiteRoom();

//                UserInterface.getInstance().findById("base.ui.menu_main").setVisible(true);
            }
        }));

        _queue.add(new LoadRunnable("Launch background thread", () ->
                new Thread(() -> {
                    try {
                        while (_application.isRunning()) {
                            _application.update();
                            Thread.sleep(16);
                        }
                        System.out.println("Background thread terminated");
                    } catch (Exception e) {
                        e.printStackTrace();
                        _queue.add(new LoadRunnable("Application has encounter an error and will be closed\n" + e.getMessage(), () -> {
                            try { Thread.sleep(4000); } catch (InterruptedException e1) { e1.printStackTrace(); }
                            System.exit(1);
                        }));
                    }
                }).start()));
    }

    @Override
    public void render () {
        long time = System.currentTimeMillis();

        Gdx.gl.glClearColor(.07f, 0.1f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (_currentRunnable != null) {
            long loadTime = System.currentTimeMillis();
            _currentRunnable.run();
            _currentRunnable = null;
            Log.notice(_currentMessage + " (" + (System.currentTimeMillis() - loadTime) + "ms)");
        }

        if (!_queue.isEmpty()) {
            try {
                LoadRunnable loadRunnable = _queue.take();

                // Load message
                _batch.begin();
                OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                _batch.setProjectionMatrix(camera.combined);
                BitmapFont.TextBounds bounds = _systemFont.getBounds(loadRunnable.message);
                _systemFont.drawMultiLine(_batch, loadRunnable.message, Gdx.graphics.getWidth() / 2 - bounds.width / 2, Gdx.graphics.getHeight() / 2 - bounds.height / 2);
                _batch.end();

                // Runnable
                _currentRunnable = loadRunnable.runnable;
                _currentMessage = loadRunnable.message;

                return;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (_startTime == -1) {
            _startTime = System.currentTimeMillis();
            _lastRender = System.currentTimeMillis();
        }

        long lastRenderInterval = System.currentTimeMillis() - _lastRender;
        _lastRender = System.currentTimeMillis();

        _renderer.clear();
        _renderer.refresh();

        Viewport viewport = Game.getInstance() != null ? Game.getInstance().getViewport() : null;
        _application.render(_renderer, viewport, lastRenderInterval);

        _renderer.display();

        // Sleep
        long sleepTime = 16 - (System.currentTimeMillis() - time) - 1;
        if (sleepTime > 0) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void dispose () {
        _renderer.close();
        PathManager.getInstance().close();
    }
}