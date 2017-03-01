package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.client.controller.character.CharacterInfoController;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.lua.BindLua;
import org.smallbox.faraway.modules.character.CharacterModule;

/**
 * Created by Alex on 25/07/2016.
 */
public class CrewController extends LuaController {

    @BindLua
    private UIList listCrew;

    @BindModule
    private CharacterModule characterModule;

    @BindLuaController
    private MainPanelController mainPanelController;

    @BindLuaController
    private CharacterInfoController characterInfoController;

    @Override
    public void onReloadUI() {
        mainPanelController.addShortcut("Crew", this);
    }

    @Override
    public void onNewGameUpdate(Game game) {
        if (listCrew != null) {
            listCrew.clear();
            characterModule.getCharacters().forEach(character ->
                    listCrew.addView(UILabel.create(null)
                            .setText(character.getName() + " " + (character.getJob() != null ? character.getJob().getLabel() : ""))
                            .setTextColor(Color.WHITE)
                            .setSize(300, 28)
                            .setPadding(8)
                            .setOnClickListener((GameEvent event) -> {
                                characterModule.select(event, character);
                                characterInfoController.display(character);
                                characterInfoController.setVisible(true);
                            })));
        }
    }

    @GameShortcut(key = GameEventListener.Key.BACKSPACE)
    public void onEscape() {
        if (isVisible()) {
            mainPanelController.setVisible(true);
        }
    }

}
