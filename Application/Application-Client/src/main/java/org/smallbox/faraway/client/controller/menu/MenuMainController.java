package org.smallbox.faraway.client.controller.menu;

import org.smallbox.faraway.client.ClientGameManagerModule;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.common.dependencyInjector.BindComponent;
import org.smallbox.faraway.common.dependencyInjector.GameObject;

/**
 * Created by Alex on 24/07/2016.
 */
@GameObject
public class MenuMainController extends LuaController {

    @BindComponent
    private ClientGameManagerModule clientGameManagerModule;

    @BindLuaAction
    private void onActionNewGame(View view) {
        clientGameManagerModule.createGame("base.planet.corrin", "mountain", 14, 20, 2, null);
    }

    @BindLuaAction
    private void onActionExit(View view) {
        clientGameManagerModule.setRunning(false);
    }

    @BindLuaAction
    private void onActionContinue(View view) {
        clientGameManagerModule.loadLastGame();
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
    }

}
