package org.smallbox.faraway.client.render.layer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import org.apache.commons.lang3.StringUtils;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.modules.weather.WeatherModule;
import org.smallbox.faraway.util.Log;

/**
 * Created by Alex on 05/06/2015.
 */
@GameLayer(level = LayerManager.PARTICLE_LAYER_LEVEL, visible = true)
public class ParticleLayer extends BaseLayer {
    private ParticleEffect          _effect;
    private String                  _name;

    @BindModule
    private WeatherModule weatherModule;

    @BindComponent
    private Game game;

    @Override
    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        loadEffect(weatherModule.getWeather().particle);

        if (_effect != null) {
            renderer.getBatch().begin();
            _effect.draw(renderer.getBatch());
            renderer.getBatch().end();

            _effect.update(Gdx.graphics.getDeltaTime());
            if (_effect.isComplete()) {
                _effect.reset();
            }
        }
    }

    private void loadEffect(String name) {
        if (!StringUtils.equals(name, _name)) {
            Application.addTask(() -> {

                // Dispose old buffEffect
                if (_effect != null) {
                    _effect.dispose();
                    _effect = null;
                }

                // Load new one
                if (name != null) {
                    Log.info(ParticleLayer.class, "Load new particle effect: %s", name);
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
        _name = name;
    }
}
