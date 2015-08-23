package org.smallbox.faraway.ui.panel.debug;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.world.OxygenManager;
import org.smallbox.faraway.ui.engine.ViewFactory;

/**
 * Created by Alex on 13/07/2015.
 */
public class OxygenManagerPanel extends BaseDebugPanel {
    private OxygenManager   _manager;

    @Override
    protected void onCreate(ViewFactory factory) {
        _manager = (OxygenManager)Game.getInstance().getManager(OxygenManager.class);
    }

    @Override
    protected String getTitle() {
        return "OxygenManager";
    }

    @Override
    protected void onAddDebug() {
        addDebugView("increase", view -> _manager.increaseOxygen());
        addDebugView("decrease", view -> _manager.decreaseOxygen());
        addDebugView("Oxygen: " + _manager.getOxygen());
    }
}
