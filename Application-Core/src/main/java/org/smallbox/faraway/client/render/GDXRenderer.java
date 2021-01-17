package org.smallbox.faraway.client.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import org.smallbox.faraway.client.AssetManager;
import org.smallbox.faraway.client.drawable.GDXDrawable;
import org.smallbox.faraway.client.font.FontManager;
import org.smallbox.faraway.client.ui.engine.views.View;
import org.smallbox.faraway.common.ParcelCommon;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.modelInfo.GraphicInfo;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfig;
import org.smallbox.faraway.core.module.world.model.Parcel;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.log.Log;

import static org.smallbox.faraway.util.Constant.TILE_SIZE;

@ApplicationObject
public class GDXRenderer {
    @Inject private LayerManager layerManager;
    @Inject private ApplicationConfig applicationConfig;
    @Inject private FontManager fontManager;
    @Inject private AssetManager assetManager;
    @Inject private Viewport viewport;

    private SpriteBatch _batch;
    private OrthographicCamera _camera;
    private OrthographicCamera _cameraUI;
    private ShapeRenderer _drawPixelShapeLayer;
    private ShaderProgram shader;

    public void init() {
        _batch = new SpriteBatch();
        _drawPixelShapeLayer = new ShapeRenderer();
        _drawPixelShapeLayer.setProjectionMatrix(_batch.getProjectionMatrix());

        _camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        _camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        _camera.zoom = 1.5f;

        _cameraUI = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        _cameraUI.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        _cameraUI.zoom = 1f;

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

    public float getZoom() {
        return _camera.zoom;
    }

    public float getUiScale() {
        return (float) applicationConfig.uiScale;
    }

    public void draw(TextureRegion currentFrame, int x, int y) {
        _batch.begin();
        _batch.setProjectionMatrix(_camera.combined);
        _batch.draw(currentFrame, x, y);
        _batch.end();
    }

    public void draw(Texture texture, int x, int y) {
        _batch.begin();
        _batch.draw(texture, x, y);
        _batch.end();
    }

    public void draw(Texture texture, int x, int y, int width, int height) {
        _batch.begin();
        _batch.draw(texture, x, y, 0, 0, width, height);
        _batch.end();
    }

    public void draw(GDXDrawable drawable, int x, int y) {
        _batch.begin();
        drawable.draw(_batch, x, y);
        _batch.end();
    }

    public void draw(Sprite sprite, int x, int y, float alpha) {
        if (sprite != null) {
            _batch.begin();
            sprite.setPosition(x, y);
            sprite.draw(_batch, alpha);
            _batch.end();
        }
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
        _cameraUI.update();
        _batch.setProjectionMatrix(_camera.combined);
    }

    public int getWidth() {
        return Gdx.graphics.getWidth();
    }

    public int getHeight() {
        return Gdx.graphics.getHeight();
    }

    public void draw(View view, int x, int y) {
        view.draw(this, x, y);
    }

    public void zoomOut() {
        _camera.zoom = Math.min(_camera.zoom + 0.125f, 3f);
        Log.info("Set zoom: " + _camera.zoom);
    }

    public void zoomIn() {
        _camera.zoom = Math.max(_camera.zoom - 0.125f, 1f);
        Log.info("Set zoom: " + _camera.zoom);
    }

    public void drawUI(Sprite sprite, int x, int y) {
        draw(x, y, sprite, _cameraUI);
    }

    public void draw(Sprite sprite, int x, int y) {
        draw(x, y, sprite, _camera);
    }

    public void drawMap(int x, int y, Texture t1, Texture t2) {
//        Gdx.gl.glClearColor(1, 1, 1, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        _batch.begin();
        _batch.draw(t1, x, y);
        _batch.draw(t2, x, y);
        _batch.end();
    }

    private void draw(int x, int y, Sprite sprite, OrthographicCamera camera) {
        if (sprite != null) {
            _batch.begin();
            _batch.setProjectionMatrix(camera.combined);
            sprite.setPosition(x, y);
            sprite.draw(_batch);
//            _batch.drawPixel(sprite, x, y);
            _batch.end();
        }
    }

    public void drawRegionUI(int x, int y, Sprite sprite) {
        drawRegion(x, y, sprite, _cameraUI);
    }

    public void drawRegion(int x, int y, Sprite sprite) {
        drawRegion(x, y, sprite, _camera);
    }

    private void drawRegion(int x, int y, Sprite sprite, OrthographicCamera camera) {
        if (sprite != null) {
            _batch.begin();
            _batch.setProjectionMatrix(camera.combined);
//            sprite.setPosition(x, y);
//            sprite.drawPixel(_batch);
            _batch.draw(sprite, x, y);
            _batch.end();
        }
    }

    public void draw(Sprite sprite) {
        if (sprite != null) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            _batch.begin();
            sprite.draw(_batch);
            _batch.end();
        }
    }

    public void drawUI(Sprite sprite) {
        if (sprite != null) {
            _batch.begin();
            _batch.setProjectionMatrix(_cameraUI.combined);
            sprite.draw(_batch);
            _batch.end();
        }
    }

    public void drawUI(Texture texture, int x, int y) {
        if (texture != null) {
            _batch.begin();
            _batch.setProjectionMatrix(_cameraUI.combined);
            _batch.draw(texture, x, y);
            _batch.end();
        }
    }

    public void draw(DrawCallback callback) {
        _batch.begin();
        callback.onDraw(_batch);
        _batch.end();
    }

    public void drawFontUI(DrawFontCallback callback, int fontSize, boolean outlined) {
        drawFont(callback, fontSize, _cameraUI, outlined);
    }

    public void drawFont(DrawFontCallback callback, int fontSize, boolean outlined) {
        drawFont(callback, fontSize, _camera, outlined);
    }

    private void drawFont(DrawFontCallback callback, int fontSize, OrthographicCamera camera, boolean outlined) {
        _batch.begin();
        _batch.setProjectionMatrix(camera.combined);
        fontSize *= getUiScale();
        callback.onDraw(_batch, outlined ? fontManager.getOutlinedFont(fontSize) : fontManager.getFont(fontSize));
        _batch.end();
    }

    public void drawTextUI(int x, int y, int textSize, Color color, String string, boolean outlined) {
        drawText(x, y, textSize, color, string, _cameraUI, outlined);
    }

    public void drawTextUI(int x, int y, int textSize, Color color, String string) {
        drawText(x, y, textSize, color, string, _cameraUI, false);
    }

    public void drawText(int x, int y, int textSize, Color color, String string, boolean outlined) {
        drawText(x, y, textSize, color, string, _camera, outlined);
    }

    public void drawText(int x, int y, int textSize, Color color, String string) {
        drawText(x, y, textSize, color, string, _camera, false);
    }

    private void drawText(int x, int y, int textSize, Color color, String string, OrthographicCamera camera, boolean outlined) {
        textSize *= getUiScale();

        if (string != null) {
            _batch.begin();
//            _cameraUI.updateGame();
            _batch.setProjectionMatrix(camera.combined);

            if (outlined) {
                fontManager.getOutlinedFont(textSize).setColor(color != null ? color : Color.WHITE);
                fontManager.getOutlinedFont(textSize).draw(_batch, string, x, y);
            } else {
                fontManager.getFont(textSize).setColor(color != null ? color : Color.WHITE);
                fontManager.getFont(textSize).draw(_batch, string, x, y);
            }

            //            _fonts[textSize].drawMultiLine(_batch, string, x, y);
            _batch.end();
        }
    }

    public void drawPixel(int x, int y, int width, int height, Color color) {
        drawPixel(x, y, width, height, color, _camera);
    }

    public void drawPixelUI(int x, int y, int width, int height, Color color) {
        drawPixel(x, y, width, height, color, _cameraUI);
    }

    public void drawLine(float x1, float y1, float x2, float y2, Color color) {
        if (color != null) {
            _batch.begin();
            _batch.setProjectionMatrix(_camera.combined);

            //Gdx.gl.glEnable(GL20.GL_BLEND);
            _drawPixelShapeLayer.begin(ShapeRenderer.ShapeType.Line);
            _drawPixelShapeLayer.setProjectionMatrix(_batch.getProjectionMatrix());
            _drawPixelShapeLayer.setColor(color);
            _drawPixelShapeLayer.line(x1, y1, x2, y2);
            _drawPixelShapeLayer.end();
            _batch.end();
        }
    }

    private void drawPixel(int x, int y, int width, int height, Color color, OrthographicCamera camera) {
        if (color != null) {
            _batch.begin();
            Gdx.gl.glEnable(GL20.GL_BLEND);
            _drawPixelShapeLayer.setProjectionMatrix(camera.combined);
            _drawPixelShapeLayer.begin(ShapeRenderer.ShapeType.Filled);
            _drawPixelShapeLayer.setColor(color);
            _drawPixelShapeLayer.rect(x, y, width, height);
            _drawPixelShapeLayer.end();
            _batch.end();
        }
    }

    public void drawRectangleUI(int x, int y, int width, int height, Color color, boolean filled) {
        drawRectangle(x, y, width, height, color, filled, _cameraUI);
    }

    public void drawRectangle(int x, int y, int width, int height, Color color, boolean filled) {
        drawRectangle(x, y, width, height, color, filled, _camera);
    }

    private void drawRectangle(int x, int y, int width, int height, Color color, boolean filled, OrthographicCamera camera) {
        if (color != null) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            _drawPixelShapeLayer.setProjectionMatrix(camera.combined);
            _drawPixelShapeLayer.begin(filled ? ShapeRenderer.ShapeType.Filled : ShapeRenderer.ShapeType.Line);
            _drawPixelShapeLayer.setColor(color);
            _drawPixelShapeLayer.rect(x, y, width, height);
            _drawPixelShapeLayer.end();
        }
    }

    public void drawCircle(int x, int y, int radius, Color color, boolean filled) {
        if (color != null) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            _drawPixelShapeLayer.setProjectionMatrix(_camera.combined);
            _drawPixelShapeLayer.begin(filled ? ShapeRenderer.ShapeType.Filled : ShapeRenderer.ShapeType.Line);
            _drawPixelShapeLayer.setColor(color);
            _drawPixelShapeLayer.circle(x, y, radius);
            _drawPixelShapeLayer.end();
        }
    }

    public void drawRectangleOnMap(int x, int y, int width, int height, Color color, boolean filled, int offsetX, int offsetY) {
        if (color != null) {
            drawRectangleUI(
                    (int) (layerManager.getViewport().getPosX() * (1 / _camera.zoom) + (x * Constant.TILE_SIZE * (1 / _camera.zoom)) + offsetX)
                            + (int) (applicationConfig.screen.resolution[0] / 2 - (applicationConfig.screen.resolution[0] / 2 / _camera.zoom)),
                    (int) (layerManager.getViewport().getPosY() * (1 / _camera.zoom) + (y * Constant.TILE_SIZE * (1 / _camera.zoom)) + offsetY)
                            + (int) (applicationConfig.screen.resolution[1] / 2 - (applicationConfig.screen.resolution[1] / 2 / _camera.zoom)),
                    (int) (width * (1 / _camera.zoom)),
                    (int) (height * (1 / _camera.zoom)),
                    color,
                    filled
            );
        }
    }

    public void drawCircleOnMap(int x, int y, int radius, Color color, boolean filled, int offsetX, int offsetY) {
        if (color != null) {
            drawCircle(
                    layerManager.getViewport().getPosX() + (x * Constant.TILE_SIZE) + offsetX,
                    layerManager.getViewport().getPosY() + (y * Constant.TILE_SIZE) + offsetY,
                    radius,
                    color,
                    filled
            );
        }
    }

    public void draw(int x, int y, int cacheId, SpriteCache cache) {
        if (cache != null) {
            Gdx.gl.glEnable(GL20.GL_BLEND);

            Matrix4 matrix = new Matrix4();
            matrix.translate(x * layerManager.getViewport().getScale(), y * layerManager.getViewport().getScale(), 0);
//            matrix.scale(Application.gameManager.getGame().getViewport().getScale(), Application.gameManager.getGame().getViewport().getScale(), 1f);

            cache.setProjectionMatrix(_cameraUI.combined);
            cache.setTransformMatrix(matrix);
            cache.begin();
            cache.draw(cacheId);
            cache.end();
        }
    }

    public Batch getBatch() {
        return _batch;
    }

    public void drawOnMap(int x, int y, TextureRegion region) {
        draw(region, layerManager.getViewport().getPosX() + (x * Constant.TILE_SIZE), layerManager.getViewport().getPosY() + (y * Constant.TILE_SIZE));
    }

    public void drawOnMap(int x, int y, Color color) {
        drawPixel(layerManager.getViewport().getPosX() + (x * Constant.TILE_SIZE), layerManager.getViewport().getPosY() + (y * Constant.TILE_SIZE), Constant.TILE_SIZE, Constant.TILE_SIZE, color);
    }

    public void drawTextOnMap(Parcel parcel, String string, int size, Color color) {
        drawTextOnMap(parcel.x, parcel.y, string, size, color, 0, 0);
    }

    public void drawTextOnMap(int x, int y, String string, int size, Color color) {
        drawTextOnMap(x, y, string, size, color, 0, 0);
    }

    public void drawTextOnMap(Parcel parcel, String string, int size, Color color, int offsetX, int offsetY) {
        drawTextOnMap(parcel.x, parcel.y, string, size, color, offsetX, offsetY);
    }

    public void drawTextOnMapUI(int x, int y, String string, int size, Color color, int offsetX, int offsetY, boolean outlined) {
        drawTextOnMapUI(x, y, string, size, color, offsetX, offsetY, outlined, 0);
    }

    public void drawTextOnMapUI(int x, int y, String string, int size, Color color, int offsetX, int offsetY, boolean outlined, int stack) {
        drawTextUI(
                (int) (layerManager.getViewport().getPosX() * (1 / _camera.zoom) + (x * Constant.TILE_SIZE * (1 / _camera.zoom)) + (offsetX * (1 / _camera.zoom)))
                        + (int) (applicationConfig.screen.resolution[0] / 2 - (applicationConfig.screen.resolution[0] / 2 / _camera.zoom)),
                (int) (layerManager.getViewport().getPosY() * (1 / _camera.zoom) + (y * Constant.TILE_SIZE * (1 / _camera.zoom)) + (offsetY * (1 / _camera.zoom)))
                        + (int) (applicationConfig.screen.resolution[1] / 2 - (applicationConfig.screen.resolution[1] / 2 / _camera.zoom)),
                size,
                color,
                string,
                outlined);
    }

    public void drawTextOnMap(int x, int y, String string, int size, Color color, int offsetX, int offsetY, boolean outlined) {
        drawText(
                layerManager.getViewport().getPosX() + (x * Constant.TILE_SIZE) + offsetX,
                layerManager.getViewport().getPosY() + (y * Constant.TILE_SIZE) + offsetY,
                size,
                color,
                string,
                outlined);
    }

    public void drawTextOnMap(int x, int y, String string, int size, Color color, int offsetX, int offsetY) {
        drawTextOnMap(x, y, string, size, color, offsetX, offsetY, false);
    }

    public void drawOnMap(ParcelCommon parcel, Sprite itemSprite) {
        draw(itemSprite, (parcel.x * Constant.TILE_SIZE) + layerManager.getViewport().getPosX(), (parcel.y * Constant.TILE_SIZE) + layerManager.getViewport().getPosY());
    }

    public void drawOnMap(Parcel parcel, Sprite itemSprite) {
        drawOnMap(parcel, itemSprite, 0, 0);
    }

    public void drawOnMap(Parcel parcel, Sprite itemSprite, int offsetX, int offsetY) {
        draw(itemSprite, (parcel.x * Constant.TILE_SIZE) + layerManager.getViewport().getPosX() + offsetX,
                (parcel.y * Constant.TILE_SIZE) + layerManager.getViewport().getPosY() + offsetY
        );
    }

    public Camera getCamera() {
        return _camera;
    }

    public void drawOnMap(Texture texture, Parcel parcel) {
        draw(texture,
                viewport.getPosX() + (parcel.x * TILE_SIZE),
                viewport.getPosY() + (parcel.y * TILE_SIZE),
                TILE_SIZE, TILE_SIZE);
    }

    public void drawOnMap(GraphicInfo graphicInfo, Parcel parcel) {
        draw(assetManager.lazyLoad("data" + graphicInfo.path, Texture.class),
                viewport.getPosX() + (parcel.x * TILE_SIZE),
                viewport.getPosY() + (parcel.y * TILE_SIZE),
                TILE_SIZE, TILE_SIZE);
    }

//    public void drawOnMap(ParcelModel parcel, Texture texture) {
//        _batch.draw(texture, x, y, 512, 512, 0, 0, 512, 512, false, true);
//        draw((parcel.x * Constant.TILE_WIDTH) + layerManager.getViewport().getPosX(), (parcel.y * Constant.TILE_HEIGHT) + layerManager.getViewport().getPosY(), itemSprite);
//    }
}