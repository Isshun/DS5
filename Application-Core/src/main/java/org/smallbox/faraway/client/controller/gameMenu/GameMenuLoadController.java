package org.smallbox.faraway.client.controller.gameMenu;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.GameSaveManager;

@GameObject
public class GameMenuLoadController extends LuaController {

    @Inject
    private Game game;

    @Inject
    private GameManager gameManager;

    @Inject
    private GameSaveManager gameSaveManager;

    @BindLua
    private UIList loadEntries;

    protected void onControllerUpdate() {
        loadEntries.getViews().clear();

        gameSaveManager.getSaves().forEach(s -> {
            loadEntries.getViews().add(UILabel.createFast(s, Color.BLACK));
        });

    }

}
