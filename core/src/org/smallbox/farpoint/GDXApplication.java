package org.smallbox.farpoint;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.jrenner.smartfont.SmartFontGenerator;
import org.smallbox.faraway.Application;
import org.smallbox.faraway.Game;
import org.smallbox.faraway.GameTimer;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.engine.ui.ViewFactory;
import org.smallbox.faraway.manager.PathManager;
import org.smallbox.faraway.manager.SpriteManager;
import org.smallbox.faraway.model.GameData;

import java.io.IOException;

public class GDXApplication extends ApplicationAdapter {
    private SpriteBatch     _batch;
    private GDXRenderer     _renderer;
    private Application     _application;
    private int             _renderTime;
    private int             _tick;
    private int             _refresh;
    private int             _frame;
    private int             _nextDraw;
    private int             _nextUpdate;
    private int             _nextRefresh;
    private int             _nextLongUpdate;

    @Override
    public void create () {
        ViewFactory.setInstance(new GDXViewFactory());

        GameTimer timer = new GameTimer() {
            long _origin = System.currentTimeMillis();

            @Override
            public long getElapsedTime() {
                return System.currentTimeMillis() - _origin;
            }
        };

        try {
            SpriteManager.setInstance(new GDXSpriteManager());
        } catch (IOException e) {
            e.printStackTrace();
        }

        SmartFontGenerator fontGen = new SmartFontGenerator();
        FileHandle exoFile = Gdx.files.local("data/res/fonts/font.ttf");
        BitmapFont[] fonts = new BitmapFont[100];
        for (int i = 0; i < 100; i++) {
            fonts[i] = fontGen.createFont(exoFile, "font-" + i, i);
        }

        _batch = new SpriteBatch();
        _renderer = new GDXRenderer(_batch, timer, fonts);
        _application = new Application(_renderer);

        // Load resources
        _application.getLoadListener().onUpdate("Init resources");
        GameData data = _application.loadResources();

        // Create app
        GDXLightRenderer lightRenderer = new GDXLightRenderer();
        _application.create(_renderer, lightRenderer, new GDXParticleRenderer(), data, data.config);
        _renderer.setGameEventListener(_application);

        _application.loadGame("4");

        //		//Limit the framerate
        //		window.setFramerateLimit(30);

        _renderTime = 0;
        _tick = 0;
        _refresh = 0;
        _frame = 0;
        _nextDraw = 0;
        _nextUpdate = 0;
        _nextRefresh = 0;
        _nextLongUpdate = 0;

        Gdx.input.setInputProcessor(new GDXInputProcessor(_application, timer));
        Gdx.graphics.setContinuousRendering(true);
        Gdx.graphics.requestRendering();
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(.5f, 0.8f, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        _renderer.clear();
        _renderer.refresh();

        Game game = _application.getGame();

        long elapsed = _renderer.getTimer().getElapsedTime();

        // Sleep
        if (elapsed < _nextDraw) {
            //int currentRenderTime = (int) (DRAW_INTERVAL - (_nextDraw - elapsed));
            //_renderTime = (_renderTime * 7 + currentRenderTime) / 8;
//                Thread.sleep(_nextDraw - elapsed);
        }

        // Render menu
        if (game == null || !game.isRunning()) {
            _application.renderMenu(_renderer, SpriteManager.getInstance().createRenderEffect());

            // Refresh
            if (elapsed >= _nextRefresh) {
                _application.refreshMenu(_refresh++);
                _nextRefresh += Application.REFRESH_INTERVAL;
            }
        }

        // Render game
        else if (game != null && !game.isPaused()) {
            // Draw
            RenderEffect effect = SpriteManager.getInstance().createRenderEffect();
            effect.setViewport(game.getViewport());

            double animProgress = (1 - (double) (_nextUpdate - elapsed) / Application.getUpdateInterval());
            _application.renderGame(animProgress, _tick, _renderTime, _renderer, effect);

            // Refresh
            if (elapsed >= _nextRefresh) {
                _application.refreshGame(_refresh++);
                _nextRefresh += Application.REFRESH_INTERVAL;
            }

            // Update
            if (elapsed >= _nextUpdate) {
                _application.update(_tick++);
                _nextUpdate += Application.getUpdateInterval();
            }

            // Long _tick
            if (elapsed >= _nextLongUpdate) {
                _application.longUpdate(_frame);
                _nextLongUpdate += Application.getLongUpdateInterval();
            }
        }

        _renderer.display();

        _nextDraw += Application.DRAW_INTERVAL;
        _frame++;
    }

    @Override
    public void dispose () {
        _renderer.close();
        PathManager.getInstance().close();
    }

}
