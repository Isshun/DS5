package org.smallbox.farpoint;

import com.badlogic.gdx.graphics.g2d.SpriteCache;
import org.smallbox.faraway.engine.GFXRenderer;
import org.smallbox.faraway.engine.SpriteManager;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.engine.RenderEffect;
import org.smallbox.faraway.engine.SpriteModel;
import org.smallbox.faraway.engine.renderer.RenderLayer;
import org.smallbox.faraway.ui.engine.UILabel;
import org.smallbox.farpoint.ui.GDXLabel;

/**
 * Created by Alex on 04/06/2015.
 */
public class GDXRenderLayer extends RenderLayer {
    SpriteCache         _cache;
    int                 _cacheId = -1;
    private int         _count;

    public GDXRenderLayer(int index) {
        super(index);
        _cache = new SpriteCache(5000, true);
        _needRefresh = true;
    }

    @Override
    public void clear() {
        _count = 0;
        _needRefresh = true;
        _cache.clear();
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
        synchronized (this) {
            if (_cacheId != -1) {
//                System.out.println("draw cache #" + _index + " (cacheId: " + _cacheId + ", count: " + _count + ")");
                ((GDXRenderer)renderer).draw(_cache, _cacheId, Game.getInstance().getViewport().getPosX() + x, Game.getInstance().getViewport().getPosY() + y);
            }
        }
    }

    @Override
    public void draw(SpriteModel sprite) {
        if (sprite != null) {
            if (_count < 5000) {
                _cache.add(((GDXSpriteModel)sprite).getData());
                _count++;
            }
        }
    }

    @Override
    public void draw(UILabel text) {
        if (text != null) {
            if (_count < 5000) {
                //GDXRenderer.getInstance().getFont(14).draw(_cache, "", 0, 0);
//                ((GDXRenderer)SpriteManager.getInstance()).getF
//                _cache.add();
//                _count++;
            }
        }
    }

    @Override
    public boolean needRefresh() {
        return _needRefresh;
    }
}
