package org.smallbox.farpoint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import org.smallbox.faraway.*;
import org.smallbox.faraway.engine.ui.ColorView;
import org.smallbox.faraway.engine.util.Constant;

/**
 * Created by Alex on 04/06/2015.
 */
public class GDXRenderer implements GFXRenderer {
    private static final Color TEXT_COLOR = Color.WHITE;

    private final SpriteBatch           _batch;
    private final GameTimer             _timer;
    public static BitmapFont[]          _fonts;
    private final OrthographicCamera    _camera;
    private final OrthographicCamera    _cameraWorld;
    private GameEventListener           _listener;
    private ShapeRenderer               _shapeRenderer;

    public GDXRenderer(SpriteBatch batch, GameTimer timer, BitmapFont[] fonts) {
        _fonts = fonts;
        _batch = batch;
        _shapeRenderer = new ShapeRenderer();
        _shapeRenderer.setProjectionMatrix(_batch.getProjectionMatrix());
        _camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        _camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        _cameraWorld = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        _cameraWorld.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        _timer = timer;
    }

    @Override
    public void draw(SpriteModel sprite, RenderEffect effect) {
        if (sprite != null) {
            _batch.begin();
            if (effect != null) {
                if (effect.getViewport() != null) {
                    Sprite s = ((GDXSpriteModel) sprite).getData();
                    _batch.draw(s, s.getX() + effect.getViewport().getPosX(), s.getY() + effect.getViewport().getPosY());
                }
                _batch.draw(((GDXSpriteModel) sprite).getData(), ((GDXRenderEffect) effect).getPosX(), ((GDXRenderEffect) effect).getPosY());
            } else {
                ((GDXSpriteModel) sprite).getData().draw(_batch);
            }
            _batch.end();
        }
    }

    @Override
    public void draw(ColorView view, RenderEffect effect) {
        view.draw(this, effect);
    }

    @Override
    public void clear(org.smallbox.faraway.Color color) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void clear() {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void display() {
    }

    @Override
    public void finish() {
    }

    @Override
    public void close() {
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public void refresh() {
        _camera.update();
        _batch.setProjectionMatrix(_camera.combined);

        _batch.begin();
        _fonts[10].draw(_batch, "hello", 100, 100);
        _batch.end();
    }

    @Override
    public void setFullScreen(boolean isFullscreen) {
    }

    @Override
    public void drawLight() {
    }

    @Override
    public GameTimer getTimer() {
        return _timer;
    }

    @Override
    public int getWidth() {
        return Constant.WINDOW_WIDTH;
    }

    @Override
    public int getHeight() {
        return Constant.WINDOW_HEIGHT;
    }

    public void setGameEventListener(GameEventListener listener) {
        _listener = listener;
    }

    public void draw(Sprite sprite, int x, int y) {
        if (sprite != null) {
            _batch.begin();
            _batch.draw(sprite, x, y);
            _batch.end();
        }
    }

    public void draw(String string, int textSize, int x, int y, Color color) {
        if (string != null) {
            _batch.begin();
            _fonts[textSize].setColor(color != null ? color : TEXT_COLOR);
            _fonts[textSize].draw(_batch, string, x, y);
            _batch.end();
        }
    }

    public void draw(Color color, int x, int y, int width, int height) {
        if (color != null) {
            _batch.begin();
            _shapeRenderer.setProjectionMatrix(_camera.combined);
            _shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            _shapeRenderer.setColor(color);
            _shapeRenderer.rect(x, y, width, height);
            _shapeRenderer.end();
            _batch.end();
        }
    }

    public void draw(SpriteCache cache, int cacheId, int x, int y) {
        if (cache != null) {
            Gdx.gl.glEnable(GL20.GL_BLEND);

            Matrix4 matrix = new Matrix4();
            matrix.translate(x, y, 0);

            _cameraWorld.translate(10, 10);
            cache.setProjectionMatrix(_cameraWorld.combined);
            cache.setTransformMatrix(matrix);
            cache.begin();
            cache.draw(cacheId);
            cache.end();
        }
    }

    public Batch getBatch() {
        return _batch;
    }

//    public void draw(SpriteBatch batch) {
//        if (batch != null) {
//            _batch.begin();
//            _batch.draw(, 0, 0);
//            _batch.end();
//        }
//    }

}
