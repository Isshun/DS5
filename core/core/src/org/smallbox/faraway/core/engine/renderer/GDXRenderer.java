package org.smallbox.faraway.core.engine.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import org.smallbox.faraway.core.GDXSpriteModel;
import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.core.engine.SpriteModel;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.engine.drawable.GDXDrawable;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.ui.engine.views.View;

/**
 * Created by Alex on 04/06/2015.
 */
public class GDXRenderer {
    private static final Color TEXT_COLOR = Color.WHITE;

    private static GDXRenderer          _self;
    private final SpriteBatch           _batch;
    private final BitmapFont[]          _fonts;
    private final OrthographicCamera    _camera;
    private final OrthographicCamera    _cameraWorld;
    private ShapeRenderer               _shapeRenderer;
    private int                         _zoom = Viewport.ZOOM_LEVELS.length - 1;
    private Viewport                    _viewport;

    public GDXRenderer(SpriteBatch batch, BitmapFont[] fonts) {
        _self = this;
        _fonts = fonts;
        _batch = batch;
        _shapeRenderer = new ShapeRenderer();
        _shapeRenderer.setProjectionMatrix(_batch.getProjectionMatrix());
        _camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        _camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        _cameraWorld = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        _cameraWorld.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void draw(TextureRegion textureRegion, int x, int y) {
        _batch.begin();
        _batch.draw(textureRegion, x, y);
        _batch.end();
    }

    public void draw(GDXDrawable drawable, int x, int y) {
        _batch.begin();
        drawable.draw(_batch, x, y);
        _batch.end();
    }

    public void draw(SpriteModel sprite, int x, int y) {
        if (sprite != null) {
            _batch.begin();
            ((GDXSpriteModel)sprite).getData().setPosition(x, y);
            ((GDXSpriteModel)sprite).getData().draw(_batch);
//            if (effect != null) {
//                if (effect.getViewport() != null) {
//                    Sprite s = ((GDXSpriteModel) sprite).getData();
//                    s.setScale(effect.getViewport().getScale());
//                    _batch.draw(s,
//                            s.getX() + effect.getViewport().getPosX() * effect.getViewport().getScale(),
//                            s.getY() + effect.getViewport().getPosY() * effect.getViewport().getScale(),
//                            s.getWidth() * effect.getViewport().getScale(),
//                            s.getHeight() * effect.getViewport().getScale());
//                } else {
//                    Sprite s = ((GDXSpriteModel) sprite).getData();
//                    _batch.draw(s, ((GDXRenderEffect) effect).getPosX(), ((GDXRenderEffect) effect).getPosY());
//                }
//            } else {
//                ((GDXSpriteModel) sprite).getData().draw(_batch);
//            }
            _batch.end();
        }
    }

    public void draw(org.smallbox.faraway.core.engine.Color color, int x, int y, int width, int height) {
        draw(new Color(color.r / 255f, color.g / 255f, color.b / 255f, color.a / 255f), x, y, width, height);
    }

    public void clear(org.smallbox.faraway.core.engine.Color color) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public void clear() {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public void display() {
    }

    public void finish() {
    }

    public void close() {
    }

    public void refresh() {
        _camera.update();
        _batch.setProjectionMatrix(_camera.combined);
    }

    public void setFullScreen(boolean isFullscreen) {
    }

    public int getWidth() {
        return Constant.WINDOW_WIDTH;
    }

    public int getHeight() {
        return Constant.WINDOW_HEIGHT;
    }

    public void draw(View view, int x, int y) {
        view.draw(this, x, y);
    }

    public void zoomUp() {
        _zoom = Math.max(0, _zoom - 1);
        Game.getInstance().getViewport().setZoom(_zoom);
    }

    public void zoomDown() {
        _zoom = Math.min(Viewport.ZOOM_LEVELS.length - 1, _zoom + 1);
        Game.getInstance().getViewport().setZoom(_zoom);
    }

    public void draw(Sprite sprite, int x, int y) {
        if (sprite != null) {
            _batch.begin();
            _batch.draw(sprite, x, y);
            _batch.end();
        }
    }

    public void draw(String string, int textSize, int x, int y, Color color) {
        textSize *= GameData.config.uiScale;

        if (string != null) {
            _batch.begin();
            _fonts[textSize].setColor(color != null ? color : TEXT_COLOR);
//            _fonts[textSize].draw(_batch, string, x, y);
            _fonts[textSize].drawMultiLine(_batch, string, x, y);
            _batch.end();
        }
    }

    public void draw(Color color, int x, int y, int width, int height) {
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

    public void draw(SpriteCache cache, int cacheId, int x, int y) {
        if (cache != null) {
            Gdx.gl.glEnable(GL20.GL_BLEND);

            Matrix4 matrix = new Matrix4();
            matrix.translate(x * Game.getInstance().getViewport().getScale(), y * Game.getInstance().getViewport().getScale(), 0);
            matrix.scale(Game.getInstance().getViewport().getScale(), Game.getInstance().getViewport().getScale(), 1f);

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

    public static GDXRenderer getInstance() {
        return _self;
    }

    public void begin() {
//        _batch.begin();
    }

    public void end() {
//        _batch.end();
    }

    public void drawOnMap(TextureRegion region, int x, int y) {
        draw(region, _viewport.getPosX() + (x * Constant.TILE_WIDTH), _viewport.getPosY() + (y * Constant.TILE_HEIGHT));
    }

    public void setViewport(Viewport viewport) {
        _viewport = viewport;
    }
}
