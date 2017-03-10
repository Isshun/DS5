package org.smallbox.faraway.client.controller.character;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIFrame;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

/**
 * Created by Alex on 26/04/2016.
 */
public class CharacterInfoDetailsController extends LuaController {

    @BindLua
    private UIList listTalents;

    private CharacterModel _selected;

    public void selectCharacter(CharacterModel character) {
        _selected = character;
    }

    @Override
    protected void onNewGameUpdate(Game game) {

        if (_selected != null) {

            listTalents.clear();
            _selected.getTalents().getAll().forEach(talent -> {
                View view = new UIFrame(null)
                        .setBackgroundColor(0x1a3647)
                        .setBorderColor(0x359f9f)
                        .setMargin(8, 0)
                        .setSize(320, 28);

                view.addView(UILabel.create(null)
                        .setText(talent.name)
                        .setTextColor(new Color(0x359f9f))
                        .setTextSize(16)
                        .setPosition(8, 16)
                        .setSize(320, 28));

                listTalents.addView(view);
            });

        }

    }
}
