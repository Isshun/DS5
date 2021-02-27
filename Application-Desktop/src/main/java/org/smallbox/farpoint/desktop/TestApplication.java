package org.smallbox.farpoint.desktop;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import org.smallbox.faraway.client.GameApplication;
import org.smallbox.faraway.util.transition.IntegerTransition;
import org.smallbox.faraway.util.transition.Transition;

public class TestApplication implements ApplicationListener {

    private SpriteBatch batch;
    OrthographicCamera camera;
    private IntegerTransition transition;
    public ShapeRenderer _drawPixelShapeLayer;

    public TestApplication(GameApplication.GDXApplicationListener listener) {
    }

    @Override
    public void create() {
        batch = new SpriteBatch();

        camera = new OrthographicCamera(3000, 2000);
        camera.position.set(3000 / 2f, 2000 / 2f, 0);
        camera.update();

        transition = new IntegerTransition(0, 1200);
        transition.setDuration(1000);
        transition.setInterpolation(Interpolation.pow2);
        transition.setRepeat(Transition.Repeat.NONE);

        _drawPixelShapeLayer = new ShapeRenderer();
        _drawPixelShapeLayer.setProjectionMatrix(batch.getProjectionMatrix());
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

//        Gdx.gl.glEnable(GL20.GL_BLEND);
//        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        _drawPixelShapeLayer.setProjectionMatrix(camera.combined);
        _drawPixelShapeLayer.begin(ShapeRenderer.ShapeType.Filled);
        _drawPixelShapeLayer.setColor(Color.BLUE);
        _drawPixelShapeLayer.rect(transition.getValue(), 1200, 800, 10);
        _drawPixelShapeLayer.end();
//        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }

}