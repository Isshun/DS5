package org.smallbox.faraway.client.render.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.task.TaskManager;
import org.smallbox.faraway.util.FileUtils;
import org.smallbox.faraway.util.Log;

import java.io.File;

@ApplicationObject
public class ErrorRender {

    private final BitmapFont systemFont;

    public ErrorRender() {
        systemFont = new BitmapFont(
                new FileHandle(new File(FileUtils.BASE_PATH, "data/font-14.fnt")),
                new FileHandle(new File(FileUtils.BASE_PATH, "data/font-14.png")),
                false);
    }

    public void render(SpriteBatch batch) {
        if (Log._lastErrorMessage != null && System.currentTimeMillis() < Log._lastErrorTime + 5000) {
            batch.begin();

            OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.setProjectionMatrix(camera.combined);

            systemFont.setColor(0.0f, 0.0f, 0.0f, 1);
            systemFont.draw(batch, Log._lastErrorMessage, 21, Gdx.graphics.getHeight() - 21);

            systemFont.setColor(1.0f, 0.2f, 0.2f, 1);
            systemFont.draw(batch, Log._lastErrorMessage, 20, Gdx.graphics.getHeight() - 20);

            batch.end();
        }
    }

}
