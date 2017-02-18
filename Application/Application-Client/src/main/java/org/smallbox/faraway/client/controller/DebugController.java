package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.lua.BindLua;
import org.smallbox.faraway.modules.character.CharacterModule;

/**
 * Created by Alex on 25/07/2016.
 */
public class DebugController extends LuaController {

    @BindLua
    private UIList listDebug;

    @BindModule
    private CharacterModule characterModule;

    @BindLuaController
    private MainPanelController mainPanelController;

    @BindLuaController
    private CharacterInfoController characterInfoController;

    @Override
    public void onReloadUI() {
        mainPanelController.addShortcut("Debug", this);

        listDebug.addView(UILabel.create(null).setText("Add character").setSize(200, 20).setOnClickListener(event -> characterModule.addRandom()));
    }

    @Override
    public void onKeyPressWithEvent(GameEvent event, GameEventListener.Key key) {
        if (event.isAlive() && key == GameEventListener.Key.ESCAPE) {
            setVisible(false);
            mainPanelController.setVisible(true);
        }
    }
}
