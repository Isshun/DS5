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
    private SpriteBatch batch;
    private GDXRenderer renderer;
    private Application application;
    private int renderTime;
    private int update;
    private int refresh;
    private int frame;
    private int nextDraw;
    private int nextUpdate;
    private int nextRefresh;
    private int nextLongUpdate;

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

        batch = new SpriteBatch();
        renderer = new GDXRenderer(batch, timer, fonts);
        application = new Application(renderer);

        // Load resources
        application.getLoadListener().onUpdate("Init resources");
        GameData data = application.loadResources();

        // Create app
        GDXLightRenderer lightRenderer = new GDXLightRenderer();
        application.create(renderer, lightRenderer, new GDXParticleRenderer(lightRenderer), data);
        renderer.setGameEventListener(application);

        application.loadGame();

        //		//Limit the framerate
        //		window.setFramerateLimit(30);

        renderTime = 0;
        update = 0;
        refresh = 0;
        frame = 0;
        nextDraw = 0;
        nextUpdate = 0;
        nextRefresh = 0;
        nextLongUpdate = 0;

        Gdx.input.setInputProcessor(new GDXInputProcessor(application, timer));
        Gdx.graphics.setContinuousRendering(true);
        Gdx.graphics.requestRendering();
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(.5f, 0.8f, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.clear();
        renderer.refresh();

        Game game = application.getGame();

        long elapsed = renderer.getTimer().getElapsedTime();

        // Sleep
        if (elapsed < nextDraw) {
            //int currentRenderTime = (int) (DRAW_INTERVAL - (nextDraw - elapsed));
            //renderTime = (renderTime * 7 + currentRenderTime) / 8;
//                Thread.sleep(nextDraw - elapsed);
        }

        // Render menu
        if (game == null || !game.isRunning()) {
            application.renderMenu(renderer, SpriteManager.getInstance().createRenderEffect());

            // Refresh
            if (elapsed >= nextRefresh) {
                application.refreshMenu(refresh++);
                nextRefresh += Application.REFRESH_INTERVAL;
            }
        }

        // Render game
        else if (game != null && !game.isPaused()) {
            // Draw
            RenderEffect effect = SpriteManager.getInstance().createRenderEffect();
            effect.setViewport(game.getViewport());

            double animProgress = (1 - (double) (nextUpdate - elapsed) / Application.getUpdateInterval());
            application.renderGame(animProgress, update, renderTime, renderer, effect);

            // Refresh
            if (elapsed >= nextRefresh) {
                application.refreshGame(refresh++);
                nextRefresh += Application.REFRESH_INTERVAL;
            }

            // Update
            if (elapsed >= nextUpdate) {
                application.update(update++);
                nextUpdate += Application.getUpdateInterval();
            }

            // Long update
            if (elapsed >= nextLongUpdate) {
                application.longUpdate(frame);
                nextLongUpdate += Application.getLongUpdateInterval();
            }
        }

        renderer.display();

        nextDraw += Application.DRAW_INTERVAL;
        frame++;
    }

    @Override
    public void dispose () {
        renderer.close();
        PathManager.getInstance().close();
    }

}
