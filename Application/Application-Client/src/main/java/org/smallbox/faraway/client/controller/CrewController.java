package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.Color;
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
    public void onGameUpdate(Game game) {
        if (listCrew != null) {
            listCrew.clear();
            characterModule.getCharacters().forEach(character -> {
                listCrew.addView(UILabel.create(null)
                        .setText(character.getName() + " " + (character.getJob() != null ? character.getJob().getLabel() : ""))
                        .setSize(300, 28)
                        .setBackgroundColor(Color.CYAN)
                        .setOnClickListener((GameEvent event) -> {
                            characterModule.select(event, character);
                            characterInfoController.display(character);
                            setVisible(false);
                        }));
            });
        }
    }
}
