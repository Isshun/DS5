package org.smallbox.faraway.client.menu.controller;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.AfterApplicationLayerInit;
import org.smallbox.faraway.core.game.GameFactory;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfig;

@ApplicationObject
public class MenuMainController extends LuaController {

    @Inject
    private GameManager gameManager;

    @Inject
    private GameFactory gameFactory;

    @Inject
    private ApplicationConfig applicationConfig;

    @Inject
    private MenuSettingsController menuSettingsController;

    @AfterApplicationLayerInit
    private void afterApplicationLayerInit() {
        setVisible(true);
    }

    @BindLuaAction
    private void onActionNewGame(View view) {
        setVisible(false);
        gameFactory.create(applicationConfig.debug.scenario);
    }

    @BindLuaAction
    private void onActionExit(View view) {
        Application.setRunning(false);
    }

    @BindLuaAction
    private void onActionContinue(View view) {
        gameManager.loadLastGame();
    }

    @BindLuaAction
    private void onActionLoad(View view) {
        System.out.println("gg");
    }

    @BindLuaAction
    private void onActionSave(View view) {
        System.out.println("gg");
    }

    @BindLuaAction
    private void onActionSettings(View view) {
        setVisible(false);
        menuSettingsController.setVisible(true);
    }

}
