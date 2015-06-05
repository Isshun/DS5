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
    private final GDXLightRenderer  _lightRenderer;
    private ParticleEffect          _effect;
    private Color                   _sunColor;

    public GDXParticleRenderer(GDXLightRenderer lightRenderer) {
        _lightRenderer = lightRenderer;
        refresh();
    }

    @Override
    public void onDraw(GFXRenderer renderer, int x, int y) {
        if (_lightRenderer.getSun() != null && _sunColor != null) {
            _lightRenderer.getSun().setColor(_sunColor);
        }

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
        WeatherModel weather = GameData.getData().weathers.get(GameData.config.weather);

        // sun color
        org.smallbox.faraway.Color color;
        switch (GameData.getData().config.time) {
            case "dawn": color = new org.smallbox.faraway.Color(weather.sun.dawn); break;
            case "twilight": color = new org.smallbox.faraway.Color(weather.sun.twilight); break;
            case "midnight": color = new org.smallbox.faraway.Color(weather.sun.midnight); break;
            default: color = new org.smallbox.faraway.Color(weather.sun.noon); break;
        }
        _sunColor = new Color(color.r / 255f, color.g / 255f, color.b / 255f, color.a / 255f);

        // particles
        if (_effect != null) {
            _effect.dispose();
            _effect = null;
        }
        if (weather.particle != null) {
            _effect = new ParticleEffect();
            _effect.load(Gdx.files.internal("data/particles/" + weather.particle), Gdx.files.internal("data/particles/"));
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
    public void init() {
    }
}
