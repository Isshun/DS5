package org.smallbox.faraway.client.ui;

import org.smallbox.faraway.client.ModuleLayer;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;

@ModuleLayer(UILayer.class)
public class UIPanelModule extends GameModule {

    @Override
    public boolean isModuleMandatory() {
        return true;
    }

    @Override
    public void onGameCreate(Game game) {
    }

    @Override
    public void onGameStart(Game game) {
    }

    @Override
    public void onGameUpdate(Game game, int tick) {
    }

}
