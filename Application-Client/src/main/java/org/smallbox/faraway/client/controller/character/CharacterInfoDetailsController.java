package org.smallbox.faraway.client.controller.character;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

/**
 * Created by Alex on 26/04/2016.
 */
@GameObject
public class CharacterInfoDetailsController extends LuaController {

    @BindLua
    private UIList listDetails;

    private CharacterModel _selected;

    public void selectCharacter(CharacterModel character) {
        _selected = character;
    }

}
