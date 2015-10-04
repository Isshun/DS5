package org.smallbox.faraway;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.jrenner.smartfont.SmartFontGenerator;
import org.smallbox.faraway.core.GDXInputProcessor;
import org.smallbox.faraway.core.GDXViewFactory;
import org.smallbox.faraway.core.SpriteManager;
import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.engine.renderer.GDXParticleRenderer;
import org.smallbox.faraway.engine.renderer.GDXRenderer;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.module.ModuleManager;
import org.smallbox.faraway.game.module.path.PathManager;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.util.Log;

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

    public static final BlockingQueue<LoadRunnable> _queue = new LinkedBlockingQueue<>();
    private Runnable        _currentRunnable;
    private String          _currentMessage;
    private SpriteBatch     _batch;
    private GDXRenderer     _renderer;
    private Application     _application;
    private long            _startTime = -1;
    private long            _lastRender;
    private BitmapFont[]    _fonts;
    private BitmapFont      _systemFont;

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
                BitmapFont font = fontGen.createFont(exoFile, "font-" + i, i);
                font.getData().flipped = true;
//                font.setFixedWidthGlyphs("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789/*-+.,<>/?;:'\"[]{}|`~��������������������!@#$%^&*()_=");
//                int maxAdvance = 0;
//                for (int a = 0; a < font.getData().glyphs.length; a++) {
//                    if (font.getData().glyphs[a] != null) {
//                        for (int b = 0; b < font.getData().glyphs[a].length; b++) {
//                            if (font.getData().glyphs[a][b] != null) {
//                                if (font.getData().glyphs[a][b].xadvance > maxAdvance) {
//                                    maxAdvance = font.getData().glyphs[a][b].xadvance;
//                                }
//                            }
//                        }
//                    }
//                }
//                maxAdvance = maxAdvance * 2 / 3;
//                for (int a = 0; a < font.getData().glyphs.length; a++) {
//                    if (font.getData().glyphs[a] != null) {
//                        for (int b = 0; b < font.getData().glyphs[a].length; b++) {
//                            if (font.getData().glyphs[a][b] != null) {
//                                font.getData().glyphs[a][b].xoffset = (maxAdvance - font.getData().glyphs[a][b].xadvance) / 2;
//                                font.getData().glyphs[a][b].xadvance = maxAdvance;
//                            }
//                        }
//                    }
//                }
                _fonts[i] = font;
            }
        }));

        _queue.add(new LoadRunnable("Create renderer", () -> {
            _renderer = new GDXRenderer(_batch, _fonts);
        }));

        _queue.add(new LoadRunnable("Create app", () -> {
            _application = new Application(_renderer);
        }));

        // Load resources
        _queue.add(new LoadRunnable("Load resources", () -> {
            GameData data = new GameData();
            data.loadAll();
        }));

        // Load modules
        _queue.add(new LoadRunnable("Load modules", () -> {
            ModuleManager.getInstance().load();
        }));

        // Create app
        _queue.add(new LoadRunnable("Init app", () -> {
            ViewFactory.setInstance(new GDXViewFactory());

//            GDXLightRenderer lightRenderer = null;
//            if (GameData.config.render.light) {
//                lightRenderer = new GDXLightRenderer();
//            }

            GDXParticleRenderer particleRenderer = null;
            if (GameData.config.render.particle) {
                particleRenderer = new GDXParticleRenderer();
            }
            _application.create(_renderer, null, particleRenderer, GameData.getData(), GameData.config);

            GDXInputProcessor inputProcessor = new GDXInputProcessor(_application);
            Gdx.input.setInputProcessor(inputProcessor);
            Gdx.graphics.setContinuousRendering(true);
            Gdx.graphics.setVSync(true);
            Gdx.graphics.requestRendering();

            _application.setInputDirection(inputProcessor.getDirection());
        }));

        _queue.add(new LoadRunnable("Resume game", () -> {
            if (GameData.config.byPassMenu) {
//                _application.newGame("12.sav", GameData.getData().getRegion("arrakis", "desert"));
                _application.loadGame("12.sav");
//                _application.whiteRoom();
            }
        }));

        _queue.add(new LoadRunnable("Launch background thread", () ->
                new Thread(() -> {
                    try {
                        while (_application.isRunning()) {
                            _application.update();
                            Thread.sleep(16);
                        }
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
