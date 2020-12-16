package org.smallbox.faraway.client.render.layer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.GameObject;
import org.smallbox.faraway.core.dependencyInjector.Inject;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfigService;
import org.smallbox.faraway.util.Log;

@GameObject
@GameLayer(level = 999, visible = true)
public class LogLayer extends BaseLayer {

    @Inject
    private ApplicationConfigService applicationConfigService;

    private int _index;

    @Override
    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        int fontSize = applicationConfigService.getConfig().debug.logFontSize;
        int lineLength = applicationConfigService.getConfig().debug.logLineLength;
        int lineNumber = applicationConfigService.getConfig().debug.logLineNumber;

        _index = 0;

        Log._history.forEach(message -> {
            int posY = Gdx.graphics.getHeight() - ((lineNumber + 1) * (fontSize + 2)) + (_index * (fontSize + 2));
            String text = message.substring(0, Math.min(lineLength, message.length()));

            renderer.drawText(12, posY - 1, fontSize, Color.BLACK, text);
            renderer.drawText(11, posY - 2, fontSize, Color.BLACK, text);
            renderer.drawText(10, posY - 3, fontSize, Color.WHITE, text);

            _index++;
        });
    }

}