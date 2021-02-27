package org.smallbox.faraway.client.controller.menu;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.GameFactory;
import org.smallbox.faraway.core.game.GameManager;

@ApplicationObject
public class MenuCrewController extends LuaController {
    @Inject private GameManager gameManager;
    @Inject private GameFactory gameFactory;
    @Inject private ApplicationConfig applicationConfig;
    @Inject private MenuSettingsController menuSettingsController;
    @Inject private MenuPlanetController menuPlanetController;

    @BindLuaAction
    private void onActionBack(View view) {
        setVisible(false);
        menuPlanetController.setVisible(true);
    }

    @BindLuaAction
    private void onActionNext(View view) {
        setVisible(false);
        gameFactory.create(applicationConfig.debug.scenario);
    }
}
