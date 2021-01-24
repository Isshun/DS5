package org.smallbox.faraway.client.render.layer.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import org.apache.commons.lang3.StringUtils;
import org.smallbox.faraway.client.AssetManager;
import org.smallbox.faraway.client.render.GDXRendererBase;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.render.layer.BaseLayer;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.modules.weather.WeatherModule;
import org.smallbox.faraway.util.log.Log;

@GameObject
@GameLayer(level = LayerManager.PARTICLE_LAYER_LEVEL, visible = true)
public class ParticleLayer extends BaseLayer {
    private ParticleEffect effect;
    private String name;
    @Inject private WeatherModule weatherModule;
    @Inject private Game game;
    @Inject private AssetManager assetManager;

    @Override
    public void onDraw(GDXRendererBase renderer, Viewport viewport, double animProgress, int frame) {
        loadEffect(weatherModule.getWeather().particle);

        if (effect != null) {
            renderer.draw(batch -> {
                effect.draw(batch);
                effect.update(Gdx.graphics.getDeltaTime());
            });

            if (effect.isComplete()) {
                effect.reset();
            }
        }
    }

    private void loadEffect(String name) {
        if (!StringUtils.equals(name, this.name)) {
            Application.addTask(() -> {

                // Load new one
                if (name != null) {
                    Log.info(ParticleLayer.class, "Load new particle effect: %s", name);
                    effect = assetManager.lazyLoad("data/particles/" + name, ParticleEffect.class, ParticleEffect::flipY);
                    effect.getEmitters().first().setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
                    effect.getEmitters().first().getSpawnWidth().setHigh(Gdx.graphics.getWidth());
                    effect.getEmitters().first().getSpawnWidth().setLow(Gdx.graphics.getWidth());
                    effect.getEmitters().first().getSpawnHeight().setHigh(Gdx.graphics.getHeight());
                    effect.getEmitters().first().getSpawnHeight().setLow(Gdx.graphics.getHeight());
                    effect.start();
                }

            });
        }
        this.name = name;
    }
}
