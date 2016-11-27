package org.smallbox.faraway.core.game.module.ui;

import org.smallbox.faraway.core.ModuleRenderer;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;

@ModuleRenderer(UIRenderer.class)
public class UIPanelModule extends GameModule {

    @Override
    public boolean isModuleMandatory() {
        return true;
    }

    @Override
    protected void onGameCreate(Game game) {
    }

    @Override
    public void onGameStart(Game game) {
    }

    @Override
    public void onGameUpdate(Game game, int tick) {
    }

}
