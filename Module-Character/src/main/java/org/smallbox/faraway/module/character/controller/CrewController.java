package org.smallbox.faraway.module.character.controller;

import org.smallbox.faraway.core.BindModule;
import org.smallbox.faraway.core.game.BindLua;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.module.character.controller.LuaController;
import org.smallbox.faraway.module.character.CharacterModule;
import org.smallbox.faraway.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.ui.engine.views.widgets.UIList;

/**
 * Created by Alex on 25/07/2016.
 */
public class CrewController extends LuaController {
    @BindLua
    private UIList listCrew;

    @BindModule("")
    private CharacterModule _characters;

    @Override
    protected void onGameUpdate(Game game) {
        listCrew.clear();
        _characters.getCharacters().forEach(character -> {
            listCrew.addView(UILabel.create(null)
                    .setText(character.getName())
                    .setSize(300, 28)
                    .setOnClickListener(() -> {
                        _characters.select(character);
                        setVisible(false);
                    }));
        });
    }
}
