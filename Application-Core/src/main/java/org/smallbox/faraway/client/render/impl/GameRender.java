package org.smallbox.faraway.client.render.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import org.smallbox.faraway.client.manager.input.InputManager;
import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;

@ApplicationObject
public class GameRender {
    @Inject private GameRender gameRender;
    @Inject private GameManager gameManager;
    @Inject private LayerManager layerManager;
    @Inject private InputManager inputManager;
    @Inject private GDXRenderer gdxRenderer;
    @Inject private Game game;

    public void render() {
        Gdx.input.setInputProcessor(inputManager);
        Gdx.gl.glClearColor(.07f, 0.1f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

        // Render application
//        gdxRenderer.clear();
        gdxRenderer.refresh();

        // Render game
        if (gameManager.isLoaded()) {
            layerManager.render(game);
        }
//        fpsLogger.log();
    }

}
