package org.smallbox.faraway.client.controller;

import com.badlogic.gdx.Input;
import org.smallbox.faraway.client.SelectionManager;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.ui.engine.GameEvent;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIGrid;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.GameObject;
import org.smallbox.faraway.core.dependencyInjector.Inject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;

@GameObject
public class PauseMenuController extends LuaController {

    @Inject
    private Game game;

    @Inject
    private GameManager gameManager;

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
    public void onActionExit(View view) {
        gameManager.closeGame();
    }

}