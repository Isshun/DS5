package org.smallbox.faraway.client.controller.gameMenu;

import com.badlogic.gdx.Input;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;

@GameObject
public class GameMenuPauseController extends LuaController {

    @Inject
    private Game game;

    @Inject
    private GameManager gameManager;

    @Inject
    private GameMenuLoadController gameMenuLoadController;

    @BindLua
    private View viewPause;

    @GameShortcut(key = Input.Keys.ESCAPE)
    public void onEscape() {
        game.toggleRunning();
        setVisible(!game.isRunning());
    }

    @BindLuaAction
    public void onActionResume(View view) {
        game.setRunning(true);
        setVisible(false);
    }

    @BindLuaAction
    public void onActionSave(View view) {
        setVisible(false);
    }

    @BindLuaAction
    public void onActionLoad(View view) {
        setVisible(false);
        gameMenuLoadController.setVisible(true);
    }

    @BindLuaAction
    public void onActionExit(View view) {
        gameManager.closeGame();
    }

}
