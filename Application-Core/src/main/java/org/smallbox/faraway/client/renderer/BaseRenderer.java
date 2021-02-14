package org.smallbox.faraway.client.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import org.smallbox.faraway.client.asset.AssetManager;
import org.smallbox.faraway.client.asset.font.FontManager;
import org.smallbox.faraway.client.layer.LayerManager;
import org.smallbox.faraway.client.renderer.extra.DrawCallback;
import org.smallbox.faraway.client.renderer.extra.DrawFontCallback;
import org.smallbox.faraway.client.renderer.extra.TextStyle;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.modelInfo.GraphicInfo;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.util.Constant;

import static org.smallbox.faraway.util.Constant.TILE_SIZE;

public abstract class BaseRenderer {
    @Inject protected LayerManager layerManager;
    @Inject protected ApplicationConfig applicationConfig;
    @Inject protected FontManager fontManager;
    @Inject protected AssetManager assetManager;
    @Inject protected GDXRenderer gdxRenderer;
    @Inject protected Viewport viewport;

    public SpriteBatch _batch;
    public ShapeRenderer _drawPixelShapeLayer;

    public void init() {
        _batch = gdxRenderer._batch;
        _drawPixelShapeLayer = gdxRenderer._drawPixelShapeLayer;
    }

    public void clear() {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    protected abstract void refresh();
    protected abstract Matrix4 getCombinedProjection();
    protected abstract float getZoom();

    public void draw(DrawCallback drawCallback) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        _batch.begin();
        _batch.setProjectionMatrix(getCombinedProjection());
        drawCallback.onDraw(_batch);
        _batch.end();
        Gdx.gl.glBlendEquation(GL20.GL_FUNC_ADD);
    }

    public void drawSpriteOnMap(Sprite sprite, Parcel parcel) {
        drawSprite(sprite, mapToScreenX(parcel.x, 0), mapToScreenY(parcel.y, 0));
    }

    public void drawSpriteOnMap(Sprite sprite, Parcel parcel, int offsetX, int offsetY) {
        drawSprite(sprite, mapToScreenX(parcel.x, offsetX), mapToScreenY(parcel.y, offsetY));
    }

    public void drawSpriteOnMap(Sprite sprite, Parcel parcel, int offsetX, int offsetY, int gridSize, int gridPosition) {
        // Compute offset for the center of the parcel
        int gridOffsetX = (int) (Constant.HALF_TILE_SIZE - sprite.getWidth() / 2);
        int gridOffsetY = (int) (Constant.TILE_SIZE - sprite.getHeight());

        // Compute offset for the corners of the parcel
        if (gridSize == 2 || gridSize == 4) {
            gridOffsetX += (gridPosition == 1 || gridPosition == 2 ? -Constant.QUARTER_TILE_SIZE : Constant.QUARTER_TILE_SIZE);
            gridOffsetY += (gridPosition == 0 || gridPosition == 2 ? -Constant.HALF_TILE_SIZE : 0);
        }

        drawSprite(sprite, mapToScreenX(parcel.x, offsetX + gridOffsetX), mapToScreenY(parcel.y, offsetY + gridOffsetY));
    }

    public void drawSprite(Sprite sprite) {
        if (sprite != null) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            _batch.begin();
            _batch.setProjectionMatrix(getCombinedProjection());
            sprite.draw(_batch);
            _batch.end();
            Gdx.gl.glBlendEquation(GL20.GL_FUNC_ADD);
        }
    }

    public void drawSprite(Sprite sprite, int x, int y) {
        if (sprite != null) {
            sprite.setPosition(x, y);
            drawSprite(sprite);
        }
    }

    public void drawTextOnMap(Parcel parcel, String text, Color textColor, int textSize, int offsetX, int offsetY) {
        drawText(mapToScreenX(parcel.x, offsetX), mapToScreenY(parcel.y, offsetY), text, textColor, textSize, false, "font3", 0);
    }

    public void drawTextOnMap(Parcel parcel, String text, TextStyle style, int offsetX, int offsetY) {
        drawText(
                mapToScreenX(parcel.x, offsetX),
                mapToScreenY(parcel.y, offsetY),
                text, style.color, (int) (style.size * (style.autoScale ? 4 - getZoom() : 1)),
                style.outline != 0,
                style.font,
                style.shadow);
    }

    public void drawText(int x, int y, String text, Color textColor, int textSize) {
        drawText(x, y, text, textColor, textSize, false, "font3", 0);
    }

    public void drawText(DrawFontCallback callback, int textSize, boolean outlined, String font) {
        _batch.begin();
        _batch.setProjectionMatrix(getCombinedProjection());
        textSize *= applicationConfig.uiScale;
        callback.onDraw(_batch, outlined ? fontManager.getOutlinedFont(font, textSize) : fontManager.getFont(font, textSize));
        _batch.end();
    }

    public void drawText(int x, int y, String text, Color textColor, int textSize, boolean outlined, String textFont, int shadow) {
        textSize *= applicationConfig.uiScale;

        if (text != null) {
            _batch.begin();
            _batch.setProjectionMatrix(getCombinedProjection());

            if (shadow != 0) {
                if (outlined) {
                    fontManager.getOutlinedFont(textFont, textSize).setColor(Color.BLACK);
                    fontManager.getOutlinedFont(textFont, textSize).draw(_batch, text, x + shadow, y + shadow);
                } else {
                    fontManager.getFont(textFont, textSize).setColor(Color.BLACK);
                    fontManager.getFont(textFont, textSize).draw(_batch, text, x + shadow, y + shadow);
                }
            }

            if (outlined) {
                fontManager.getOutlinedFont(textFont, textSize).setColor(textColor != null ? textColor : Color.WHITE);
                fontManager.getOutlinedFont(textFont, textSize).draw(_batch, text, x, y);
            } else {
                fontManager.getFont(textFont, textSize).setColor(textColor != null ? textColor : Color.WHITE);
                fontManager.getFont(textFont, textSize).draw(_batch, text, x, y);
            }

//                        _fonts[textSize].drawMultiLine(_batch, string, x, y);
            _batch.end();
        }
    }

    public void drawLine(float x1, float y1, float x2, float y2, Color color) {
        if (color != null) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            _drawPixelShapeLayer.setProjectionMatrix(getCombinedProjection());
            _drawPixelShapeLayer.begin(ShapeRenderer.ShapeType.Line);
            _drawPixelShapeLayer.setColor(color);
            _drawPixelShapeLayer.line(x1, y1, x2, y2);
            _drawPixelShapeLayer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    public void drawCadre(int x, int y, int width, int height, Color color, int thickness) {
        drawRectangle(x, y, width, thickness, color);
        drawRectangle(x, y, thickness, height, color);
        drawRectangle(x, y + height - thickness, width, thickness, color);
        drawRectangle(x + width - thickness, y, thickness, height, color);
    }

    public void drawCadreOnMap(Parcel parcel, int width, int height, Color color, int thickness, int offsetX, int offsetY) {
        drawCadreOnMap(parcel.x, parcel.y, width, height, color, thickness, offsetX, offsetY);
    }

    public void drawCadreOnMap(int x, int y, int width, int height, Color color, int thickness, int offsetX, int offsetY) {
        drawRectangleOnMap(x, y, width, thickness, color, offsetX, offsetY);
        drawRectangleOnMap(x, y, thickness, height, color, offsetX, offsetY);
        drawRectangleOnMap(x, y, width, thickness, color, offsetX, height + offsetY - thickness);
        drawRectangleOnMap(x, y, thickness, height, color, width + offsetX - thickness, offsetY);
    }

    public void drawRectangleOnMap(Parcel parcel, int width, int height, Color color, int offsetX, int offsetY) {
        drawRectangle(mapToScreenX(parcel.x, offsetX), mapToScreenY(parcel.y, offsetY), width, height, color, true);
    }

    public void drawRectangleOnMap(int x, int y, int width, int height, Color color, int offsetX, int offsetY) {
        drawRectangle(mapToScreenX(x, offsetX), mapToScreenY(y, offsetY), width, height, color, true);
    }

    public void drawRectangle(int x, int y, int width, int height, Color color) {
        drawRectangle(x, y, width, height, color, true);
    }

    public void drawRectangle(int x, int y, int width, int height, Color color, boolean filled) {
        if (color != null) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            _drawPixelShapeLayer.setProjectionMatrix(getCombinedProjection());
            _drawPixelShapeLayer.begin(filled ? ShapeRenderer.ShapeType.Filled : ShapeRenderer.ShapeType.Line);
            _drawPixelShapeLayer.setColor(color);
            _drawPixelShapeLayer.rect(x, y, width, height);
            _drawPixelShapeLayer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    public void drawCircleOnMap(Parcel parcel, int radius, Color color, boolean filled, int offsetX, int offsetY) {
        drawCircle(mapToScreenX(parcel.x, offsetX), mapToScreenY(parcel.y, offsetY), radius, color, filled);
    }

    public void drawCircle(int x, int y, int radius, Color color, boolean filled) {
        if (color != null) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            _drawPixelShapeLayer.setProjectionMatrix(getCombinedProjection());
            _drawPixelShapeLayer.begin(filled ? ShapeRenderer.ShapeType.Filled : ShapeRenderer.ShapeType.Line);
            _drawPixelShapeLayer.setColor(color);
            _drawPixelShapeLayer.circle(x, y, radius);
            _drawPixelShapeLayer.end();
            Gdx.gl.glBlendEquation(GL20.GL_FUNC_ADD);
        }
    }

    public void drawCache(int x, int y, int cacheId, SpriteCache cache) {
        if (cache != null) {
            Gdx.gl.glEnable(GL20.GL_BLEND);

            Matrix4 matrix = new Matrix4();
            matrix.translate(x * viewport.getScale(), y * viewport.getScale(), 0);
//            matrix.scale(Application.gameManager.getGame().getViewport().getScale(), Application.gameManager.getGame().getViewport().getScale(), 1f);

            cache.setProjectionMatrix(getCombinedProjection());
            cache.setTransformMatrix(matrix);
            cache.begin();
            cache.draw(cacheId);
            cache.end();

            Gdx.gl.glBlendEquation(GL20.GL_FUNC_ADD);
        }
    }

    public void drawTextureRegion(TextureRegion currentFrame, int x, int y) {
        _batch.begin();
        _batch.setProjectionMatrix(getCombinedProjection());
        _batch.draw(currentFrame, x, y);
        _batch.end();
    }

    public void drawTexture(Texture texture, int x, int y, int width, int height) {
        _batch.begin();
        _batch.setProjectionMatrix(getCombinedProjection());
        _batch.draw(texture, x, y, 0, 0, width, height);
        _batch.end();
    }

    public void drawTextureOnMap(Parcel parcel, Texture texture) {
        drawTexture(texture, mapToScreenX(parcel.x, 0), mapToScreenY(parcel.y, 0), TILE_SIZE, TILE_SIZE);
    }

    public void drawTextureRegionOnMap(Parcel parcel, TextureRegion region) {
        drawTextureRegion(region, mapToScreenX(parcel.x, 0), mapToScreenY(parcel.y, 0));
    }

    public void drawGraphicOnMap(GraphicInfo graphicInfo, Parcel parcel) {
        drawTexture(assetManager.lazyLoad("data" + graphicInfo.path, Texture.class),
                viewport.getPosX() + (parcel.x * TILE_SIZE),
                viewport.getPosY() + (parcel.y * TILE_SIZE),
                TILE_SIZE, TILE_SIZE);
    }

    protected int mapToScreenX(int mapX, int offsetX) {
        return viewport.getPosX() + (mapX * Constant.TILE_SIZE) + offsetX;
    }

    protected int mapToScreenY(int mapY, int offsetY) {
        return viewport.getPosY() + (mapY * Constant.TILE_SIZE) + offsetY;
    }

}
