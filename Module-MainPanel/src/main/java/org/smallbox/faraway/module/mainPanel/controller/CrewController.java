package org.smallbox.faraway.module.mainPanel.controller;

import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.lua.BindLua;
import org.smallbox.faraway.client.controller.BindLuaController;
import org.smallbox.faraway.module.character.CharacterModule;
import org.smallbox.faraway.module.mainPanel.MainPanelController;

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

    @Override
    public void onReloadUI() {
        mainPanelController.addShortcut("Crew", this);
    }

    @Override
    public void onGameUpdate(Game game) {
        listCrew.clear();
        characterModule.getCharacters().forEach(character -> {
            listCrew.addView(UILabel.create(null)
                    .setText(character.getName() + " " + (character.getJob() != null ? character.getJob().getLabel() : ""))
                    .setSize(300, 28)
                    .setOnClickListener((GameEvent event) -> {
                        characterModule.select(event, character);
                        setVisible(false);
                    }));
        });
    }
}
