package org.smallbox.farpoint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import org.smallbox.faraway.GFXRenderer;
import org.smallbox.faraway.ParticleRenderer;
import org.smallbox.faraway.model.GameData;
import org.smallbox.faraway.model.WeatherModel;

/**
 * Created by Alex on 05/06/2015.
 */
public class GDXParticleRenderer extends ParticleRenderer {
    private ParticleEffect          _effect;
    private String                  _particle;

    public GDXParticleRenderer() {
        refresh();
    }

    @Override
    public void onDraw(GFXRenderer renderer, int x, int y) {
        if (_effect != null) {
            _effect.update(Gdx.graphics.getDeltaTime());
            ((GDXRenderer) renderer).getBatch().begin();
            _effect.draw(((GDXRenderer) renderer).getBatch());
            ((GDXRenderer) renderer).getBatch().end();
            if (_effect.isComplete()) {
                _effect.reset();
            }
        }
    }

    @Override
    public void refresh() {
        // Dispose old effect
        if (_effect != null) {
            _effect.dispose();
            _effect = null;
        }

        // Create effect
        if (_particle != null) {
            _effect = new ParticleEffect();
            _effect.load(Gdx.files.internal("data/particles/" + _particle), Gdx.files.internal("data/particles/"));
            _effect.getEmitters().first().setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
            _effect.getEmitters().first().getSpawnWidth().setHigh(Gdx.graphics.getWidth());
            _effect.getEmitters().first().getSpawnWidth().setLow(Gdx.graphics.getWidth());
            _effect.getEmitters().first().getSpawnHeight().setHigh(Gdx.graphics.getHeight());
            _effect.getEmitters().first().getSpawnHeight().setLow(Gdx.graphics.getHeight());
            _effect.flipY();
            _effect.start();
        }
    }

    @Override
    public void setParticle(String particle) {
        _particle = particle;
        refresh();
    }

    @Override
    public void init() {
    }
}
