package org.smallbox.faraway.core;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.ui.engine.views.widgets.UILabel;

/**
 * Created by Alex on 04/06/2015.
 */
public class RenderLayer {
    private final int   _x;
    private final int   _y;
    private final int   _width;
    private final int   _height;
    private Texture _texture;
    SpriteCache         _cache;
    int                 _cacheId = -1;
    private int         _count;
    protected int       _index;
    protected boolean   _needRefresh;

    public RenderLayer(int index, int x, int y, int width, int height) {
        _index = index;
        _cache = new SpriteCache(50000, false);
        _needRefresh = true;
        _x = x;
        _y = y;
        _width = width;
        _height = height;
        createTexture();
    }

    private void createTexture() {
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        for (int x = 0; x < 32; x++) {
            for (int y = 0; y < 32; y++) {
                pixmap.drawPixel(x, y, 0xffffffff);
            }
        }
        _texture = new Texture(pixmap);
    }

    public void begin() {
        _cache.beginCache();
    }

    public void end() {
        _cacheId = _cache.endCache();
    }

    public void onDraw(GDXRenderer renderer, Viewport viewport, int x, int y) {
        if (_cacheId != -1) {
//                System.out.println("draw cache #" + _index + " (cacheId: " + _cacheId + ", count: " + _count + ")");
            renderer.draw(_cache, _cacheId, viewport.getPosX() + x, viewport.getPosY() + y);
        }
    }

    public void draw(Color color, int x, int y) {
        if (_count < 5000) {
            _cache.setColor(color);
            _cache.add(_texture, x, y);
            _count++;
        }
    }

    public void draw(SpriteModel sprite, int x, int y) {
        if (sprite != null) {
            if (_count < 5000) {
                // TODO: BOF
                sprite.getData().setPosition(x, y);
                _cache.add(sprite.getData());
                _count++;
            }
        }
    }

    public void draw(Texture texture, int x, int y, int width, int height) {
        if (texture != null) {
            if (_count < 5000) {
                // TODO: BOF
                _cache.add(texture, x, y, 0, 0, width, height);
                _count++;
            }
        }
    }

    public void draw(UILabel text) {
        if (text != null) {
            if (_count < 5000) {
            }
        }
    }

    public boolean needRefresh() {
        return _needRefresh;
    }

    public boolean isDrawable() {
        return _count > 0;
    }

    public void planRefresh() {
        _needRefresh = true;
    }

    public void refresh() {
        _count = 0;
        _cache.clear();
        _needRefresh = false;
    }

    public void clear() {
        _count = 0;
        _cache.clear();
        _needRefresh = true;
    }

    public int getIndex() { return _index; }

    public void setRefresh() {
        _needRefresh = false;
    }

    public boolean isVisible(Viewport viewport) {
        int posX = (int) ((_x + viewport.getPosX()) * viewport.getScale());
        int posY = (int) ((_y + viewport.getPosY()) * viewport.getScale());
        int width = (int) (_width * viewport.getScale());
        int height = (int) (_height * viewport.getScale());
        return (posX < 1500 && posY < 1200 && posX + width > 0 && posY + height > 0);
    }
}