package org.smallbox.faraway.client.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.smallbox.faraway.client.asset.AssetManager;
import org.smallbox.faraway.client.asset.SpriteManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.task.Task;
import org.smallbox.faraway.core.task.TaskManager;
import org.smallbox.faraway.util.FileUtils;

import java.io.File;
import java.util.function.Consumer;

@ApplicationObject
public class MinimalRender {
    @Inject private TaskManager taskManager;
    @Inject private SpriteManager spriteManager;
    @Inject private AssetManager assetManager;

    private final BitmapFont systemFont;
    private final SpriteBatch batch = new SpriteBatch();

    public MinimalRender() {
        systemFont = new BitmapFont(
                new FileHandle(new File(FileUtils.BASE_PATH, "data/font-14.fnt")),
                new FileHandle(new File(FileUtils.BASE_PATH, "data/font-14.png")),
                false);
    }

    public void render() {
        Gdx.gl.glClearColor(.07f, 0.1f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(camera.combined);

        // Display tasks message
        taskManager.getLoadTasks().forEach(new Consumer<>() {
            private int taskIndex;

            @Override
            public void accept(Task task) {

                switch (task.state) {
                    case NONE:
                    case WAITING:
                        systemFont.setColor(1f, 1f, 1f, 0.5f);
                        break;
                    case BLOCKING:
                        systemFont.setColor(0.6f, 0.6f, 1f, 1);
                        break;
                    case RUNNING:
                        systemFont.setColor(0.5f, 0.9f, 0.8f, 1);
                        break;
                    case COMPLETE:
                        systemFont.setColor(0.9f, 0.6f, 0.8f, 1);
                        break;
                }

                systemFont.draw(batch, task.label, 12, Gdx.graphics.getHeight() - (++taskIndex * 20 + 12));
            }
        });

        systemFont.draw(batch, String.valueOf(assetManager.getProgress()), 12, 500);

        batch.end();
    }

}
