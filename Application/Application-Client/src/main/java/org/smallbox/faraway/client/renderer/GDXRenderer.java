package org.smallbox.faraway.client.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.client.drawable.GDXDrawable;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;

/**
 * Created by Alex on 04/06/2015.
 */
public class GDXRenderer {
    private static final Color TEXT_COLOR = Color.WHITE;

    private SpriteBatch           _batch;
    private BitmapFont[]          _fonts;
    private OrthographicCamera    _camera;
    private OrthographicCamera    _cameraUI;
    private OrthographicCamera    _cameraWorld;
    private ShapeRenderer               _shapeRenderer;
    private int                         _zoom = Viewport.ZOOM_LEVELS.length - 1;

    public void init(SpriteBatch batch, BitmapFont[] fonts) {
        _fonts = fonts;
        _batch = batch;
        _shapeRenderer = new ShapeRenderer();
        _shapeRenderer.setProjectionMatrix(_batch.getProjectionMatrix());
        _camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        _camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        _cameraUI = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        _cameraUI.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        _cameraUI.zoom = 1.2f;
        _cameraWorld = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        _cameraWorld.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void draw(int x, int y, TextureRegion textureRegion) {
        _batch.begin();
        _batch.draw(textureRegion, x, y);
        _batch.end();
    }

    public void draw(int x, int y, GDXDrawable drawable) {
        _batch.begin();
        drawable.draw(_batch, x, y);
        _batch.end();
    }

    public void draw(int x, int y, Sprite sprite, float alpha) {
        if (sprite != null) {
            _batch.begin();
            sprite.setPosition(x, y);
            sprite.draw(_batch, alpha);
            _batch.end();
        }
    }

    public void draw(org.smallbox.faraway.core.engine.Color color, int x, int y, int width, int height) {
        draw(x, y, width, height, new Color(color.r / 255f, color.g / 255f, color.b / 255f, color.a / 255f));
    }

    public void clear(Color color) {
        Gdx.gl.glClearColor(color.r, color.g, color.b, color.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public void clear() {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public void refresh() {
        _camera.update();
        _batch.setProjectionMatrix(_camera.combined);
    }

    public int getWidth() {
        return Constant.WINDOW_WIDTH;
    }

    public int getHeight() {
        return Constant.WINDOW_HEIGHT;
    }

    public void draw(int x, int y, View view) {
        view.draw(this, x, y);
    }

    public void zoomUp() {
        _zoom = Math.max(0, _zoom - 1);
        ApplicationClient.mainRenderer.getViewport().setZoom(_zoom);
    }

    public void zoomDown() {
        _zoom = Math.min(Viewport.ZOOM_LEVELS.length - 1, _zoom + 1);
        ApplicationClient.mainRenderer.getViewport().setZoom(_zoom);
    }

    public void draw(int x, int y, Sprite sprite) {
        if (sprite != null) {
            _batch.begin();
            sprite.setPosition(x, y);
            sprite.draw(_batch);
//            _batch.draw(sprite, x, y);
            _batch.end();
        }
    }

    public void drawRegion(int x, int y, Sprite sprite) {
        if (sprite != null) {
            _batch.begin();
//            sprite.setPosition(x, y);
//            sprite.draw(_batch);
            _batch.draw(sprite, x, y);
            _batch.end();
        }
    }

    public void draw(Sprite sprite) {
        if (sprite != null) {
            _batch.begin();
            sprite.draw(_batch);
            _batch.end();
        }
    }

    public void drawChunk(int x, int y, Texture texture) {
        if (texture != null) {
            _batch.begin();
            _batch.draw(texture, x, y, 512, 512, 0, 0, 512, 512, false, true);
            _batch.end();
        }
    }

    public void draw(int x, int y, int textSize, Color color, String string) {
        textSize *= Application.configurationManager.uiScale;

        if (string != null) {
            _batch.begin();
//            _cameraUI.updateGame();
//            _batch.setProjectionMatrix(_cameraUI.projection);
            _fonts[textSize].setColor(color != null ? color : TEXT_COLOR);
            _fonts[textSize].draw(_batch, string, x, y);
//            _fonts[textSize].drawMultiLine(_batch, string, x, y);
            _batch.end();
        }
    }

    public void draw(int x, int y, int width, int height, Color color) {
        if (color != null) {
            _batch.begin();
            Gdx.gl.glEnable(GL20.GL_BLEND);
            _shapeRenderer.setProjectionMatrix(_camera.combined);
            _shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            _shapeRenderer.setColor(color);
            _shapeRenderer.rect(x, y, width, height);
            _shapeRenderer.end();
            _batch.end();
        }
    }

    public void draw(int x, int y, int cacheId, SpriteCache cache) {
        if (cache != null) {
            Gdx.gl.glEnable(GL20.GL_BLEND);

            Matrix4 matrix = new Matrix4();
            matrix.translate(x * ApplicationClient.mainRenderer.getViewport().getScale(), y * ApplicationClient.mainRenderer.getViewport().getScale(), 0);
//            matrix.scale(Application.gameManager.getGame().getViewport().getScale(), Application.gameManager.getGame().getViewport().getScale(), 1f);

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

    public BitmapFont getFont(int size) {
        return _fonts[size];
    }

    public void drawOnMap(int x, int y, TextureRegion region) {
        draw(ApplicationClient.mainRenderer.getViewport().getPosX() + (x * Constant.TILE_WIDTH), ApplicationClient.mainRenderer.getViewport().getPosY() + (y * Constant.TILE_HEIGHT), region);
    }

    public void drawOnMap(int x, int y, Color color) {
        draw(ApplicationClient.mainRenderer.getViewport().getPosX() + (x * Constant.TILE_WIDTH), ApplicationClient.mainRenderer.getViewport().getPosY() + (y * Constant.TILE_HEIGHT), 32, 32, color);
    }

    public void drawOnMap(int x, int y, String string, int size) {
        draw(
                ApplicationClient.mainRenderer.getViewport().getPosX() + (x * Constant.TILE_WIDTH),
                ApplicationClient.mainRenderer.getViewport().getPosY() + (y * Constant.TILE_HEIGHT),
                size,
                Color.BLACK,
                string);
   }

    public void drawOnMap(int x, int y, String string, int size, Color color) {
        draw(
                ApplicationClient.mainRenderer.getViewport().getPosX() + (x * Constant.TILE_WIDTH),
                ApplicationClient.mainRenderer.getViewport().getPosY() + (y * Constant.TILE_HEIGHT),
                size,
                color,
                string);
   }

    public void drawOnMap(int x, int y, String string, int size, Color color, int offsetX, int offsetY) {
        draw(
                ApplicationClient.mainRenderer.getViewport().getPosX() + (x * Constant.TILE_WIDTH) + offsetX,
                ApplicationClient.mainRenderer.getViewport().getPosY() + (y * Constant.TILE_HEIGHT) + offsetY,
                size,
                color,
                string);
   }

    public void drawOnMap(ParcelModel parcel, Sprite itemSprite) {
        draw((parcel.x * Constant.TILE_WIDTH) + ApplicationClient.mainRenderer.getViewport().getPosX(), (parcel.y * Constant.TILE_HEIGHT) + ApplicationClient.mainRenderer.getViewport().getPosY(), itemSprite);
    }
}