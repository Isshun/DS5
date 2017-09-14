package org.smallbox.faraway.client.controller.character;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.common.CharacterCommon;
import org.smallbox.faraway.common.dependencyInjector.GameObject;

/**
 * Created by Alex on 26/04/2016.
 */
@GameObject
public class CharacterInfoDetailsController extends LuaController {

    @BindLua
    private UIList listDetails;

    private CharacterCommon _selected;

    public void selectCharacter(CharacterCommon character) {
        _selected = character;
    }

}
