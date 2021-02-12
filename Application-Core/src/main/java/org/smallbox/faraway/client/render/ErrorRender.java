package org.smallbox.faraway.client.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.apache.commons.lang3.tuple.Pair;
import org.smallbox.faraway.util.FileUtils;
import org.smallbox.faraway.util.log.Log;

import java.io.File;

public class ErrorRender {
    private final static int ERROR_DISPLAY_DURATION = 8000;
    private final static int ERROR_LINE_HEIGHT = 32;
    private SpriteBatch batch;
    private BitmapFont systemFont;

    public void render() {
        if (batch == null) {
            batch = new SpriteBatch();
        }
        if (systemFont == null) {
            systemFont = new BitmapFont(
                    new FileHandle(new File(FileUtils.BASE_PATH, "data/font-22.fnt")),
                    new FileHandle(new File(FileUtils.BASE_PATH, "data/font-22.png")),
                    false);
        }

        int index = 0;
        for (Pair<String, Long> pair: Log.errorMessages) {
            if (pair.getRight() + ERROR_DISPLAY_DURATION > System.currentTimeMillis()) {
                batch.begin();

                OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                batch.setProjectionMatrix(camera.combined);

                systemFont.setColor(0.0f, 0.0f, 0.0f, 1);
                systemFont.draw(batch, pair.getLeft(), 22, Gdx.graphics.getHeight() - 22 - (index * ERROR_LINE_HEIGHT));

                systemFont.setColor(1.0f, 0.2f, 0.2f, 1);
                systemFont.draw(batch, pair.getLeft(), 20, Gdx.graphics.getHeight() - 20 - (index++ * ERROR_LINE_HEIGHT));

                batch.end();
            }
        }
    }

}
