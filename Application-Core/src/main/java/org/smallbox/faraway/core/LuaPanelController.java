package org.smallbox.faraway.core;

import org.smallbox.faraway.core.game.module.character.controller.LuaController;

/**
 * Created by Alex on 24/07/2016.
 */
public abstract class LuaPanelController extends LuaController {

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        Application.getInstance().notify(obs -> obs.onOpenPanel(this));
    }

    @Override
    public void onOpenPanel(LuaController controller) {
//        if (controller != this) {
//            if (getRootView() != null) {
//                getRootView().setVisible(false);
//            }
//        }
    }
}
