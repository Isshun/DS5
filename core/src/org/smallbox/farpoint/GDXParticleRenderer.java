package org.smallbox.farpoint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import org.smallbox.faraway.Application;
import org.smallbox.faraway.engine.GFXRenderer;
import org.smallbox.faraway.engine.RenderEffect;
import org.smallbox.faraway.engine.renderer.ParticleRenderer;

/**
 * Created by Alex on 05/06/2015.
 */
public class GDXParticleRenderer extends ParticleRenderer {
    private ParticleEffect          _effect;

    @Override
    public void onDraw(GFXRenderer renderer, RenderEffect effect, double animProgress) {
        if (_effect != null) {
            long time = System.currentTimeMillis();
            _effect.update(Gdx.graphics.getDeltaTime());
            ((GDXRenderer) renderer).getBatch().begin();
            _effect.draw(((GDXRenderer) renderer).getBatch());
            ((GDXRenderer) renderer).getBatch().end();
            if (_effect.isComplete()) {
                _effect.reset();
            }
            System.out.println("Particles: " + (System.currentTimeMillis() - time));
        }
    }

    @Override
    public void setParticle(String name) {
        loadEffect(name);
    }

    private void loadEffect(String name) {
        Application.getInstance().addTask(() -> {
            // Dispose old buffEffect
            if (_effect != null) {
                _effect.dispose();
                _effect = null;
            }
            // Load new one
            if (name != null) {
                _effect = new ParticleEffect();
                _effect.load(Gdx.files.internal("data/particles/" + name), Gdx.files.internal("data/particles/"));
                _effect.getEmitters().first().setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
                _effect.getEmitters().first().getSpawnWidth().setHigh(Gdx.graphics.getWidth());
                _effect.getEmitters().first().getSpawnWidth().setLow(Gdx.graphics.getWidth());
                _effect.getEmitters().first().getSpawnHeight().setHigh(Gdx.graphics.getHeight());
                _effect.getEmitters().first().getSpawnHeight().setLow(Gdx.graphics.getHeight());
                _effect.flipY();
                _effect.start();
            }
        });
    }

    @Override
    public void init() {
    }

    @Override
    public void onRefresh(int frame) {

    }
}
