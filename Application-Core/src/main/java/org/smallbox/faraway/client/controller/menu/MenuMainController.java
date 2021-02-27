package org.smallbox.faraway.client.controller.menu;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.applicationEvent.OnApplicationLayerComplete;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameStart;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameStop;
import org.smallbox.faraway.core.game.GameFactory;
import org.smallbox.faraway.core.save.GameLoadManager;

@ApplicationObject
public class MenuMainController extends LuaController {
    @Inject private MenuSettingsController menuSettingsController;
    @Inject private MenuPlanetController menuPlanetController;
    @Inject private MenuLoadController menuLoadController;
    @Inject private GameLoadManager gameLoadManager;
    @Inject private GameFactory gameFactory;

    @OnApplicationLayerComplete
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
    private void onActionNewGameTest1(View view) {
        setVisible(false);
        gameFactory.create("data/scenarios/test1.json");
    }

    @BindLuaAction
    private void onActionNewGameTest2(View view) {
        setVisible(false);
        gameFactory.create("data/scenarios/test2.json");
    }

    @BindLuaAction
    private void onActionExit(View view) {
        Application.setRunning(false);
    }

    @BindLuaAction
    private void onActionContinue(View view) {
        gameLoadManager.loadLastGame();
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
