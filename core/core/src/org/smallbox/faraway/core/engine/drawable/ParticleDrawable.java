package org.smallbox.faraway.core.engine.drawable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.smallbox.faraway.core.engine.drawable.GDXDrawable;

/**
 * Created by Alex on 15/07/2015.
 */
public class ParticleDrawable extends GDXDrawable {
    private final ParticleEffect _effect;

    public ParticleDrawable(String path) {
        _effect = new ParticleEffect();
        _effect.load(Gdx.files.internal(path), Gdx.files.internal("data/particles/"));
//        _effect.getEmitters().first().setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
//        _effect.getEmitters().first().getSpawnWidth().setHigh(Gdx.graphics.getWidth());
//        _effect.getEmitters().first().getSpawnWidth().setLow(Gdx.graphics.getWidth());
//        _effect.getEmitters().first().getSpawnHeight().setHigh(Gdx.graphics.getHeight());
//        _effect.getEmitters().first().getSpawnHeight().setLow(Gdx.graphics.getHeight());
        _effect.flipY();
        _effect.start();
    }

    @Override
    public void draw(SpriteBatch batch, int x, int y) {
        if (_effect.isComplete()) {
            _effect.reset();
        }
        _effect.getEmitters().first().setPosition(x, y);
        _effect.draw(batch);
    }
}
