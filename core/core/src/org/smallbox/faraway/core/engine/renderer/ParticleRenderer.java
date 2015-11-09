package org.smallbox.faraway.core.engine.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.model.GameConfig;
import org.smallbox.faraway.core.game.model.WeatherInfo;

/**
 * Created by Alex on 05/06/2015.
 */
public class ParticleRenderer extends BaseRenderer {
    private ParticleEffect          _effect;
    private String                  _name;

    public int getLevel() {
        return MainRenderer.PARTICLE_RENDERER_LEVEL;
    }

    @Override
    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        if (_effect != null) {
//            long time = System.currentTimeMillis();

            if (!Game.getInstance().isPaused()) {
                _effect.update(Gdx.graphics.getDeltaTime());
            }
            renderer.getBatch().begin();
            _effect.draw(renderer.getBatch());
            renderer.getBatch().end();
            if (_effect.isComplete()) {
                _effect.reset();
            }
//            System.out.println("Particles: " + (System.currentTimeMillis() - time));
        }
    }

    public void setParticle(String name) {
        loadEffect(name);
    }

    private void loadEffect(String name) {
        if (name == null || !name.equals(_name)) {
            _name = name;
            Application.getInstance().addTask(() -> {
                // Dispose old buffEffect
                if (_effect != null) {
                    _effect.dispose();
                    _effect = null;
                }
                // Load new one
                if (name != null) {
                    System.out.println("Load new particle effect: " + name);
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
    }

    @Override
    public void onRefresh(int frame) {
    }

    @Override
    public boolean isActive(GameConfig config) {
        return config.render.particle;
    }

    @Override
    public void onWeatherChange(WeatherInfo weather) {
//        setParticle(weather.particle);
    }
}
