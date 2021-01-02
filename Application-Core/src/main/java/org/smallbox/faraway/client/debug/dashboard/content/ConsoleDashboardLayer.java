package org.smallbox.faraway.client.debug.dashboard.content;

import com.badlogic.gdx.graphics.Color;
import org.apache.commons.lang3.ObjectUtils;
import org.smallbox.faraway.client.debug.DebugService;
import org.smallbox.faraway.client.debug.dashboard.DashboardLayerBase;
import org.smallbox.faraway.client.debug.interpreter.DebugCommandInterpreterService;
import org.smallbox.faraway.client.render.layer.GDXRenderer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfig;
import org.smallbox.faraway.util.log.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@GameObject
public class ConsoleDashboardLayer extends DashboardLayerBase {
    private final Color BACKGROUND_COLOR = new Color(0x000000aa);

    @Inject
    private ApplicationConfig applicationConfig;

    @Inject
    private DebugCommandInterpreterService debugCommandInterpreterService;

    @Inject
    private DebugService debugService;

    private int _index;

    @Override
    public void    onDraw(GDXRenderer renderer, int frame) {
        int fontSize = applicationConfig.debug.logFontSize;
        int lineLength = applicationConfig.debug.logLineLength;
        int resolutionHeight = applicationConfig.getResolutionHeight();
        int consoleEntryHeight = 32;
        String prefix = frame / 60 % 2 == 0 ? "█" : " ";

        _index = 1;

        List<String> history = new ArrayList<>(Log._history);
        Collections.reverse(history);
        history.stream().limit(debugService.isDebugMode() ? 50 : 8).forEach(message -> {
            int posY = resolutionHeight - consoleEntryHeight - (_index * (fontSize + 2));
            String text = message.substring(0, Math.min(lineLength, message.length()));
            Color color = Color.WHITE;

            if (message.contains("[WARNING]")) {
                color = Color.ORANGE;
            }

            if (message.contains("[ERROR]")) {
                color = Color.RED;
            }

            if (message.contains("[FATAL]")) {
                color = Color.FIREBRICK;
            }

            if (message.contains("[DEBUG]")) {
                color = Color.GRAY;
            }

            renderer.drawText(12, posY - 1, fontSize, Color.BLACK, text);
            renderer.drawText(11, posY - 2, fontSize, Color.BLACK, text);
            renderer.drawText(10, posY - 3, fontSize, color, text);

            _index++;
        });

        renderer.drawText(12, resolutionHeight - 25, fontSize, Color.BLACK, "> " + ObjectUtils.firstNonNull(debugCommandInterpreterService.getCommandInput(), "") + prefix);
        renderer.drawText(11, resolutionHeight - 24, fontSize, Color.BLACK, "> " + ObjectUtils.firstNonNull(debugCommandInterpreterService.getCommandInput(), "") + prefix);
        renderer.drawText(10, resolutionHeight - 23, fontSize, Color.WHITE, "> " + ObjectUtils.firstNonNull(debugCommandInterpreterService.getCommandInput(), "") + prefix);
    }

}