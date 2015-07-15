package org.smallbox.faraway.core;

import com.badlogic.gdx.graphics.g2d.SpriteCache;
import org.smallbox.faraway.engine.GFXRenderer;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.engine.RenderEffect;
import org.smallbox.faraway.engine.SpriteModel;
import org.smallbox.faraway.engine.renderer.RenderLayer;
import org.smallbox.faraway.ui.engine.UILabel;

/**
 * Created by Alex on 04/06/2015.
 */
public class GDXRenderLayer extends RenderLayer {
    SpriteCache         _cache;
    int                 _cacheId = -1;
    private int         _count;

    public GDXRenderLayer(int index) {
        super(index);
        _cache = new SpriteCache(50000, false);
        _needRefresh = true;
    }

    @Override
    public void begin() {
        _cache.beginCache();
    }

    @Override
    public void end() {
        _cacheId = _cache.endCache();
    }

    @Override
    public void onDraw(GFXRenderer renderer, RenderEffect renderEffect, int x, int y) {
        if (_cacheId != -1) {
//                System.out.println("draw cache #" + _index + " (cacheId: " + _cacheId + ", count: " + _count + ")");
            ((GDXRenderer)renderer).draw(_cache, _cacheId, Game.getInstance().getViewport().getPosX() + x, Game.getInstance().getViewport().getPosY() + y);
        }
    }

    @Override
    public void draw(SpriteModel sprite, int x, int y) {
        if (sprite != null) {
            if (_count < 5000) {
                // TODO: BOF
                ((GDXSpriteModel)sprite).getData().setPosition(x, y);
                _cache.add(((GDXSpriteModel)sprite).getData());
                _count++;
            }
        }
    }

    @Override
    public void draw(UILabel text) {
        if (text != null) {
            if (_count < 5000) {
            }
        }
    }

    @Override
    public boolean needRefresh() {
        return _needRefresh;
    }

    @Override
    public boolean isDrawable() {
        return _count > 0;
    }

    @Override
    public void planRefresh() {
        _needRefresh = true;
    }

    @Override
    public void refresh() {
        _count = 0;
        _cache.clear();
        _needRefresh = false;
    }
}
