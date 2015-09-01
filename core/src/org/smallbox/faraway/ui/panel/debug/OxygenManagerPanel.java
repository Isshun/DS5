package org.smallbox.faraway.ui.panel.debug;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.module.ModuleManager;
import org.smallbox.faraway.game.module.world.OxygenModule;
import org.smallbox.faraway.ui.engine.ViewFactory;

/**
 * Created by Alex on 13/07/2015.
 */
public class OxygenManagerPanel extends BaseDebugPanel {
    private OxygenModule _manager;

    @Override
    protected void onCreate(ViewFactory factory) {
        _manager = (OxygenModule) ModuleManager.getInstance().getModule(OxygenModule.class);
    }

    @Override
    protected String getTitle() {
        return "OxygenModule";
    }

    @Override
    protected void onAddDebug() {
        addDebugView("increase", view -> _manager.increaseOxygen());
        addDebugView("decrease", view -> _manager.decreaseOxygen());
        addDebugView("Oxygen: " + _manager.getOxygen());
    }
}
