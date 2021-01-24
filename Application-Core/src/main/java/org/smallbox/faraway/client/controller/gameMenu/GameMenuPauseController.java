package org.smallbox.faraway.client.controller.gameMenu;

import com.badlogic.gdx.Input;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.save.GameSaveManager;
import org.smallbox.faraway.core.save.GameSaveType;

@GameObject
public class GameMenuPauseController extends LuaController {
    @Inject private Game game;
    @Inject private GameManager gameManager;
    @Inject private GameSaveManager gameSaveManager;
    @Inject private GameMenuLoadController gameMenuLoadController;

    @BindLua
    private View viewPause;

    @BindLuaAction
    public void onActionResume(View view) {
        setVisible(false);
        game.setRunning(true);
    }

    @BindLuaAction
    public void onActionSave(View view) {
        gameSaveManager.saveGame(GameSaveType.FAST);
        setVisible(false);
        game.setRunning(true);
    }

    @BindLuaAction
    public void onActionLoad(View view) {
        setVisible(false);
        gameMenuLoadController.setVisible(true);
    }

    @BindLuaAction
    public void onActionExit(View view) {
        setVisible(false);
        gameManager.closeGame();
    }

    @GameShortcut(key = Input.Keys.ESCAPE)
    public void onEscape() {
        if (game.isRunning()) {
            game.setRunning(false);
            setVisible(true);
        } else if (isVisible()) {
            game.setRunning(true);
            setVisible(false);
        }
    }

}
