package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.ui.engine.GameEvent;
import org.smallbox.faraway.client.controller.annotation.BindLuaController;
import org.smallbox.faraway.client.controller.character.CharacterInfoController;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.consumable.ConsumableModule;

/**
 * Created by Alex on 25/07/2016.
 */
public class DebugController extends LuaController {

    @BindLua
    private UIList listDebug;

    @BindModule
    private CharacterModule characterModule;

    @BindModule
    private ConsumableModule consumableModule;

    @BindLuaController
    private MainPanelController mainPanelController;

    @BindLuaController
    private CharacterInfoController characterInfoController;

    @Override
    public void onReloadUI() {
        mainPanelController.addShortcut("Debug", this);

        if (listDebug != null) {
            listDebug.addView(UILabel.create(null).setText("Add character").setBackgroundColor(Color.BLUE).setSize(200, 20).setOnClickListener(event -> characterModule.addRandom()));
            listDebug.addView(UILabel.create(null).setText("Add rice").setBackgroundColor(Color.BLUE).setSize(200, 20).setOnClickListener(event -> consumableModule.addConsumable("base.consumable.vegetable.rice", 10, 5, 5, 1)));
        }
    }

    @Override
    public void onKeyPressWithEvent(GameEvent event, GameEventListener.Key key) {
        if (event.isAlive() && key == GameEventListener.Key.ESCAPE) {
            setVisible(false);
            mainPanelController.setVisible(true);
        }
    }

    @Override
    protected void onNewGameUpdate(Game game) {

    }
}
