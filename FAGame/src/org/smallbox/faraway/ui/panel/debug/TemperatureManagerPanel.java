package org.smallbox.faraway.ui.panel.debug;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.TemperatureManager;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.util.StringUtils;

/**
 * Created by Alex on 13/07/2015.
 */
public class TemperatureManagerPanel extends BaseDebugPanel {
    private TemperatureManager  _manager;

    @Override
    protected void onCreate(ViewFactory factory) {
        _manager = (TemperatureManager)Game.getInstance().getManager(TemperatureManager.class);
    }

    @Override
    protected String getTitle() {
        return "TemperatureManager";
    }

    @Override
    protected void onAddDebug() {
        addDebugView("increase", view -> _manager.increaseTemperature());
        addDebugView("decrease", view -> _manager.decreaseTemperature());
        addDebugView("normalize", view -> _manager.normalize());
        addDebugView("temperature: " + StringUtils.formatNumber(_manager.getTemperature()));
        addDebugView("temperature target: " + StringUtils.formatNumber(_manager.getTemperatureTarget()));
        addDebugView("temperature offset: " + StringUtils.formatNumber(_manager.getTemperatureOffset()));
    }
}
