package org.smallbox.faraway.client.render.layer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.GameObject;
import org.smallbox.faraway.util.Log;

@GameObject
@GameLayer(level = 999, visible = true)
public class LogLayer extends BaseLayer {

    private int _index;

    @Override
    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        _index = 0;
        Log._history.forEach(message -> {
            renderer.drawText(12, Gdx.graphics.getHeight() - 100 + (_index * 20) + 12, 18, Color.BLACK, message.substring(0, Math.min(100, message.length())));
            renderer.drawText(11, Gdx.graphics.getHeight() - 100 + (_index * 20) + 11, 18, Color.BLACK, message.substring(0, Math.min(100, message.length())));
            renderer.drawText(10, Gdx.graphics.getHeight() - 100 + (_index * 20) + 10, 18, Color.WHITE, message.substring(0, Math.min(100, message.length())));
            _index++;
        });
    }

}