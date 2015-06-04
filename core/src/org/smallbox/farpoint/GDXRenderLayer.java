package org.smallbox.farpoint;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import org.smallbox.faraway.GFXRenderer;
import org.smallbox.faraway.Game;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.SpriteModel;
import org.smallbox.faraway.engine.renderer.RenderLayer;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.manager.SpriteManager;
import org.smallbox.faraway.model.ProfessionModel;
import org.smallbox.faraway.model.character.CharacterModel;

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
        _cache.beginCache();
    }

    @Override
    public void end() {
        _cacheId = _cache.endCache();
    }

    @Override
    public void onDraw(GFXRenderer renderer, RenderEffect renderEffect) {
        if (_cacheId != -1) {
//            ((GDXRenderer)renderer).draw(_cache, _cacheId, renderEffect.getViewport().getPosX(), renderEffect.getViewport().getPosY());
            ((GDXRenderer)renderer).draw(_cache, _cacheId, Game.getInstance().getViewport().getPosX(), Game.getInstance().getViewport().getPosY());
        }
    }

    @Override
    public void draw(SpriteModel sprite) {
        if (sprite != null) {
            if (_count++ < 5000) {
//            Texture texture = new Texture("data/minerals_blue-128.png");
//            _cache.add(texture, 100, 100, 0, 0, 128, 128);
//            _cache.add(((GDXSpriteModel) sprite).getData());
//            Sprite s = ((GDXSpriteModel) SpriteManager.getInstance().getCharacter(new ProfessionModel(ProfessionModel.Type.ENGINEER, "", null, null), 0, 0, 0)).getData();
//            _cache.add(s.getTexture(), 100, 100, 0, 0, 128, 128);
//            Sprite s2 = new Sprite(new Texture("data/res/Characters/scientifique.png"), 0, 0, 128, 128);
//            s2.setPosition(100, 100);
                _cache.add(((GDXSpriteModel)sprite).getData());
            }
        }
    }

    @Override
    public void draw(TextView text) {

    }
}