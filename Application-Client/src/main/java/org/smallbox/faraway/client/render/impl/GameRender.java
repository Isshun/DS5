package org.smallbox.faraway.client.render.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import org.smallbox.faraway.client.manager.InputManager;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.layer.GDXRenderer;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.GameManager;

@ApplicationObject
public class GameRender {

    @Inject
    private GDXRenderer gdxRenderer;

    @Inject
    private GameManager gameManager;

    public void render() {
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

}
