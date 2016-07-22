package org.smallbox.faraway.module.world.controller;

import org.smallbox.faraway.core.game.BindLuaController;
import org.smallbox.faraway.core.game.module.character.controller.LuaController;
import org.smallbox.faraway.module.world.UIBuildModule;

/**
 * Created by Alex on 22/07/2016.
 */
public class BuildController extends LuaController {
    @BindLuaController
    private BuildItemController _itemController;

    @Override
    protected void onCreate() {
    }

    public void create(UIBuildModule uiBuildModule) {
        _itemController.create(uiBuildModule, this);
    }
}
