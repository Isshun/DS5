package org.smallbox.farpoint;

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

    public GDXRenderLayer() {
        _cache = new SpriteCache(5000, true);
    }

    @Override
    public void clear() {
        _count = 0;
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
        if (_cacheId != -1) {
//            ((GDXRenderer)renderer).draw(_cache, _cacheId, renderEffect.getViewport().getPosX(), renderEffect.getViewport().getPosY());
            ((GDXRenderer)renderer).draw(_cache, _cacheId, Game.getInstance().getViewport().getPosX() + x, Game.getInstance().getViewport().getPosY() + y);
        }
    }

    @Override
    public void draw(SpriteModel sprite) {
        if (sprite != null) {
            if (_count < 5000) {
//            Texture texture = new Texture("data/minerals_blue-128.png");
//            _cache.add(texture, 100, 100, 0, 0, 128, 128);
//            _cache.add(((GDXSpriteModel) sprite).getData());
//            Sprite s = ((GDXSpriteModel) SpriteManager.getInstance().getCharacter(new ProfessionModel(ProfessionModel.Type.ENGINEER, "", null, null), 0, 0, 0)).getData();
//            _cache.add(s.getTexture(), 100, 100, 0, 0, 128, 128);
//            Sprite s2 = new Sprite(new Texture("data/res/Characters/scientifique.png"), 0, 0, 128, 128);
//            s2.setPosition(100, 100);
                _cache.add(((GDXSpriteModel)sprite).getData());
                _count++;
            }
        }
    }

    @Override
    public void draw(UILabel text) {
    }

    @Override
    public int getCount() {
        return _count;
    }

    @Override
    public boolean isEmpty() {
        return _count == 0;
    }
}
