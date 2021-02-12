package org.smallbox.faraway.client.controller.menu;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.AfterApplicationLayerInit;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnGameStart;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnGameStop;
import org.smallbox.faraway.core.game.GameFactory;
import org.smallbox.faraway.core.game.GameManager;

@ApplicationObject
public class MenuMainController extends LuaController {
    @Inject private GameManager gameManager;
    @Inject private GameFactory gameFactory;
    @Inject private ApplicationConfig applicationConfig;
    @Inject private MenuSettingsController menuSettingsController;
    @Inject private MenuPlanetController menuPlanetController;
    @Inject private MenuLoadController menuLoadController;

    @AfterApplicationLayerInit
    private void afterApplicationLayerInit() {
        setVisible(true);
    }

    @OnGameStart
    private void onGameStart() {
        setVisible(false);
    }

    @OnGameStop
    private void onGameStop() {
        setVisible(true);
    }

    @BindLuaAction
    private void onActionNewGame(View view) {
        setVisible(false);
        menuPlanetController.setVisible(true);
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
        setVisible(false);
        menuLoadController.setVisible(true);
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
