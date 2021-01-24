package org.smallbox.faraway.client.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;

@ApplicationObject
public class GDXRenderer {
    public SpriteBatch _batch;
    public ShapeRenderer _drawPixelShapeLayer;
    public ShaderProgram shader;

    public void init() {
        _batch = new SpriteBatch();
        _drawPixelShapeLayer = new ShapeRenderer();
        _drawPixelShapeLayer.setProjectionMatrix(_batch.getProjectionMatrix());

        FileHandle vertexShader = Gdx.files.internal("data/shader/vertex.glsl");
        FileHandle fragmentShader = Gdx.files.internal("data/shader/fragment.glsl");

        shader = new ShaderProgram(vertexShader, fragmentShader);
        if (!shader.isCompiled()) {
            Gdx.app.log("Shader", shader.getLog());
            Gdx.app.exit();
        }

        _batch.setShader(shader);

        shader.begin();
        shader.setUniformi("u_texture", 0);
        shader.setUniformi("u_mask", 1);
        shader.end();
    }

}
