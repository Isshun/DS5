package org.smallbox.faraway.client.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import org.smallbox.faraway.client.AssetManager;
import org.smallbox.faraway.client.drawable.GDXDrawable;
import org.smallbox.faraway.client.font.FontManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.modelInfo.GraphicInfo;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfig;
import org.smallbox.faraway.core.module.world.model.Parcel;
import org.smallbox.faraway.util.Constant;

import static org.smallbox.faraway.util.Constant.TILE_SIZE;

public abstract class GDXRendererBase {
    protected final static float MAX_ZOOM_IN = 1.5f;
    protected final static float MAX_ZOOM_OUT = 3f;
    protected final static float ZOOM_INTERVAL = 0.125f;

    @Inject protected LayerManager layerManager;
    @Inject protected ApplicationConfig applicationConfig;
    @Inject protected FontManager fontManager;
    @Inject protected AssetManager assetManager;
    @Inject protected Viewport viewport;
    @Inject protected GDXRendererBaseBase gdxRendererBaseBase;

    public SpriteBatch _batch;
    public ShapeRenderer _drawPixelShapeLayer;
    public OrthographicCamera _camera;

    public void init() {
        _batch = gdxRendererBaseBase._batch;
        _drawPixelShapeLayer = gdxRendererBaseBase._drawPixelShapeLayer;
    }

    public float getUiScale() {
        return (float) applicationConfig.uiScale;
    }

    public Batch getBatch() {
        return _batch;
    }

    public void clear(Color color) {
        Gdx.gl.glClearColor(color.r, color.g, color.b, color.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public void clear() {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public abstract void refresh();

    public int getWidth() {
        return Gdx.graphics.getWidth();
    }

    public int getHeight() {
        return Gdx.graphics.getHeight();
    }

    public void draw(Sprite sprite, int x, int y) {
        draw(x, y, sprite, _camera);
    }

    public void draw(TextureRegion currentFrame, int x, int y) {
        _batch.begin();
        _batch.setProjectionMatrix(_camera.combined);
        _batch.draw(currentFrame, x, y);
        _batch.end();
    }

    public void draw(Texture texture, int x, int y) {
        _batch.begin();
        _batch.setProjectionMatrix(_camera.combined);
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

    public void drawRegion(int x, int y, Sprite sprite) {
        drawRegion(x, y, sprite, _camera);
    }

    private void drawRegion(int x, int y, Sprite sprite, OrthographicCamera camera) {
        if (sprite != null) {
            _batch.begin();
            _batch.setProjectionMatrix(camera.combined);
            _batch.draw(sprite, x, y);
            _batch.end();
        }
    }

    public void draw(Sprite sprite) {
        if (sprite != null) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            _batch.begin();
            _batch.setProjectionMatrix(_camera.combined);
            sprite.draw(_batch);
            _batch.end();
            Gdx.gl.glBlendEquation(GL20.GL_FUNC_ADD);
        }
    }

    public void draw(DrawCallback callback) {
        _batch.begin();
        _batch.setProjectionMatrix(_camera.combined);
        callback.onDraw(_batch);
        _batch.end();
    }

    public void drawFont(DrawFontCallback callback, int fontSize, boolean outlined, String font) {
        _batch.begin();
        _batch.setProjectionMatrix(_camera.combined);
        fontSize *= getUiScale();
        callback.onDraw(_batch, outlined ? fontManager.getOutlinedFont(font, fontSize) : fontManager.getFont(font, fontSize));
        _batch.end();
    }

    public void drawText(int x, int y, int textSize, Color color, String string, boolean outlined) {
        drawText(x, y, textSize, color, string, outlined, "font3", 0);
    }

    public void drawText(int x, int y, int textSize, Color color, String string) {
        drawText(x, y, textSize, color, string, false, "font3", 0);
    }

    public void drawText(int x, int y, int textSize, Color color, String text, String font, boolean outlined, int shadow) {
        drawText(x, y, textSize, color, text, false, font, shadow);
    }

    private void drawText(int x, int y, int textSize, Color color, String string, boolean outlined, String font, int shadow) {
        textSize *= getUiScale();

        if (string != null) {
            _batch.begin();
//            _cameraUI.updateGame();
            _batch.setProjectionMatrix(_camera.combined);

            if (shadow != 0) {
                if (outlined) {
                    fontManager.getOutlinedFont(font, textSize).setColor(Color.BLACK);
                    fontManager.getOutlinedFont(font, textSize).draw(_batch, string, x + shadow, y + shadow);
                } else {
                    fontManager.getFont(font, textSize).setColor(Color.BLACK);
                    fontManager.getFont(font, textSize).draw(_batch, string, x + shadow, y + shadow);
                }
            }

            if (outlined) {
                fontManager.getOutlinedFont(font, textSize).setColor(color != null ? color : Color.WHITE);
                fontManager.getOutlinedFont(font, textSize).draw(_batch, string, x, y);
            } else {
                fontManager.getFont(font, textSize).setColor(color != null ? color : Color.WHITE);
                fontManager.getFont(font, textSize).draw(_batch, string, x, y);
            }

            //            _fonts[textSize].drawMultiLine(_batch, string, x, y);
            _batch.end();
        }
    }

    public void drawPixel(int x, int y, int width, int height, Color color) {
        drawPixel(x, y, width, height, color, _camera);
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
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            _batch.begin();
//            Gdx.gl30.glBlendEquation(GL30.GL_MAX);
//            _batch.setBlendFunction(GL20.GL_BLEND, GL20.GL_BLEND);
            _drawPixelShapeLayer.setProjectionMatrix(camera.combined);
            _drawPixelShapeLayer.begin(ShapeRenderer.ShapeType.Filled);
            _drawPixelShapeLayer.setColor(color);
            _drawPixelShapeLayer.rect(x, y, width, height);
            _drawPixelShapeLayer.end();
            _batch.end();
//            Gdx.gl.glBlendEquation(GL20.GL_FUNC_ADD);
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    public void drawRectangle(int x, int y, int width, int height, Color color, boolean filled) {
        if (color != null) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            _drawPixelShapeLayer.setProjectionMatrix(_camera.combined);
            _drawPixelShapeLayer.begin(filled ? ShapeRenderer.ShapeType.Filled : ShapeRenderer.ShapeType.Line);
            _drawPixelShapeLayer.setColor(color);
            _drawPixelShapeLayer.rect(x, y, width, height);
            _drawPixelShapeLayer.end();
            Gdx.gl.glBlendEquation(GL20.GL_FUNC_ADD);
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
            Gdx.gl.glBlendEquation(GL20.GL_FUNC_ADD);
        }
    }

    public void drawCadreOnMap(int x, int y, int width, int height, Color color, int thickness, int offsetX, int offsetY) {
        drawRectangleOnMap(x, y, width, thickness, color, offsetX, offsetY);
        drawRectangleOnMap(x, y, thickness, height, color, offsetX, offsetY);
        drawRectangleOnMap(x, y, width, thickness, color, offsetX, Constant.TILE_SIZE - offsetY - thickness);
        drawRectangleOnMap(x, y, thickness, height, color, Constant.TILE_SIZE - offsetX - thickness, offsetY);
    }

    public void drawRectangleOnMap(int x, int y, int width, int height, Color color, int offsetX, int offsetY) {
        if (color != null) {
            drawRectangle(
                    mapToScreenX(x, offsetX),
                    mapToScreenY(y, offsetY),
                    width,
                    height,
                    color,
                    true
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

            cache.setProjectionMatrix(_camera.combined);
            cache.setTransformMatrix(matrix);
            cache.begin();
            cache.draw(cacheId);
            cache.end();

            Gdx.gl.glBlendEquation(GL20.GL_FUNC_ADD);
        }
    }

    public void drawTextureOnMap(int x, int y, TextureRegion region) {
        draw(region, layerManager.getViewport().getPosX() + (x * Constant.TILE_SIZE), layerManager.getViewport().getPosY() + (y * Constant.TILE_SIZE));
    }

    public void drawPixelOnMap(int x, int y, Color color) {
        drawPixel(layerManager.getViewport().getPosX() + (x * Constant.TILE_SIZE), layerManager.getViewport().getPosY() + (y * Constant.TILE_SIZE), Constant.TILE_SIZE, Constant.TILE_SIZE, color);
    }

    public void drawTextOnMap(Parcel parcel, String string, int size, Color color) {
        drawTextOnMap(parcel.x, parcel.y, string, size, color, 0, 0);
    }

    public void drawTextOnMap(int x, int y, String string, int size, Color color) {
        drawTextOnMap(x, y, string, size, color, 0, 0);
    }

    public void drawTextOnMap(String text, TextStyle style, Parcel parcel, int offsetX, int offsetY) {
        drawText(
                mapToScreenX(parcel.x, offsetX),
                mapToScreenY(parcel.y, offsetY),
                (int)(style.size * (style.autoScale ? 4 - _camera.zoom : 1)),
                style.color,
                text,
                style.font,
                style.outline != 0,
                style.shadow);
    }

    public void drawTextOnMap(Parcel parcel, String string, int size, Color color, int offsetX, int offsetY) {
        drawTextOnMap(parcel.x, parcel.y, string, size, color, offsetX, offsetY);
    }

    protected int mapToScreenX(int mapX, int offsetX) {
        return layerManager.getViewport().getPosX() + (mapX * Constant.TILE_SIZE) + offsetX;
    }

    protected int mapToScreenY(int mapY, int offsetY) {
        return layerManager.getViewport().getPosY() + (mapY * Constant.TILE_SIZE) + offsetY;
    }

    public void drawTextOnMap(int x, int y, String string, int size, Color color, int offsetX, int offsetY, boolean outlined) {
        drawText(mapToScreenX(x, offsetX), mapToScreenY(y, offsetY), size, color, string, outlined);
    }

    public void drawTextOnMap(int x, int y, String string, int size, Color color, int offsetX, int offsetY) {
        drawTextOnMap(x, y, string, size, color, offsetX, offsetY, false);
    }

    public void drawTextureOnMap(Texture texture, Parcel parcel) {
        draw(texture,
                viewport.getPosX() + (parcel.x * TILE_SIZE),
                viewport.getPosY() + (parcel.y * TILE_SIZE),
                TILE_SIZE, TILE_SIZE);
    }

    public void drawGraphicOnMap(GraphicInfo graphicInfo, Parcel parcel) {
        draw(assetManager.lazyLoad("data" + graphicInfo.path, Texture.class),
                viewport.getPosX() + (parcel.x * TILE_SIZE),
                viewport.getPosY() + (parcel.y * TILE_SIZE),
                TILE_SIZE, TILE_SIZE);
    }

    public void drawSpriteOnMap(Parcel parcel, Sprite itemSprite) {
        drawSpriteOnMap(parcel, itemSprite, 0, 0);
    }

    public void drawSpriteOnMap(Parcel parcel, Sprite itemSprite, int offsetX, int offsetY) {
        draw(itemSprite, (parcel.x * Constant.TILE_SIZE) + layerManager.getViewport().getPosX() + offsetX,
                (parcel.y * Constant.TILE_SIZE) + layerManager.getViewport().getPosY() + offsetY
        );
    }

}
