package org.smallbox.farpoint;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.jrenner.smartfont.SmartFontGenerator;
import org.smallbox.faraway.Application;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.engine.GameTimer;
import org.smallbox.faraway.engine.RenderEffect;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.PathHelper;
import org.smallbox.faraway.engine.SpriteManager;
import org.smallbox.faraway.game.model.GameData;

import java.io.IOException;

public class GDXApplication extends ApplicationAdapter {
    private SpriteBatch     _batch;
    private GDXRenderer     _renderer;
    private Application     _application;
    private int             _nextDraw;
    private long            _startTime = -1;
    private long            _lastRender;

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
        _renderer = new GDXRenderer(_batch, fonts);
        _application = new Application(_renderer);

        // Load resources
        _application.getLoadListener().onUpdate("Init resources");
        GameData data = new GameData();

        // Create app
        GDXLightRenderer lightRenderer = new GDXLightRenderer();
        _application.create(_renderer, lightRenderer, new GDXParticleRenderer(), data, data.config);

        //		//Limit the framerate
        //		window.setFramerateLimit(30);

        Gdx.input.setInputProcessor(new GDXInputProcessor(_application, timer));
        Gdx.graphics.setContinuousRendering(true);
        Gdx.graphics.requestRendering();

        if (data.config.byPassMenu) {
            _application.loadGame("4.sav");
        }
    }

    @Override
    public void render () {
        if (_startTime == -1) {
            _startTime = System.currentTimeMillis();
            _lastRender = System.currentTimeMillis();
        }

        long lastRenderInterval = System.currentTimeMillis() - _lastRender;
        _lastRender = System.currentTimeMillis();

        Gdx.gl.glClearColor(.5f, 0.8f, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        _renderer.clear();
        _renderer.refresh();

        Game game = Game.getInstance();
        long elapsed = System.currentTimeMillis() - _startTime;

        // Sleep
        if (elapsed < _nextDraw) {
            //int currentRenderTime = (int) (DRAW_INTERVAL - (_nextDraw - elapsed));
            //_renderTime = (_renderTime * 7 + currentRenderTime) / 8;
//                Thread.sleep(_nextDraw - elapsed);
        }

        RenderEffect effect = SpriteManager.getInstance().createRenderEffect();
        if (game != null) {
            effect.setViewport(game.getViewport());
        }

        _application.render(_renderer, effect, lastRenderInterval);

//        // Render menu
//        if (game == null || !game.isRunning()) {
//            _application.renderMenu(_renderer, SpriteManager.getInstance().createRenderEffect());
//
//            // Refresh
//            if (elapsed >= _nextRefresh) {
//                _application.refreshMenu(_refresh++);
//                _nextRefresh += Application.REFRESH_INTERVAL;
//            }
//        }

        _renderer.display();

        _nextDraw += Application.DRAW_INTERVAL;
    }

    @Override
    public void dispose () {
        _renderer.close();
        PathHelper.getInstance().close();
    }

}
