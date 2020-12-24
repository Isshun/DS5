package org.smallbox.faraway.client.controller.menu;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.GameObject;
import org.smallbox.faraway.core.dependencyInjector.Inject;
import org.smallbox.faraway.core.game.GameInfo;

@ApplicationObject
public class MenuMainController extends LuaController {

    @Inject
    private MenuSettingsController menuSettingsController;

    @BindLuaAction
    private void onActionNewGame(View view) {
        Application.gameManager.createGame(GameInfo.create("base.planet.corrin", "mountain", 14, 20, 2), null);
    }

    @BindLuaAction
    private void onActionExit(View view) {
        Application.setRunning(false);
    }

    @BindLuaAction
    private void onActionContinue(View view) {
        Application.gameManager.loadLastGame();
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
