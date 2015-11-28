package org.smallbox.faraway.module.character;

import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.engine.module.java.ModuleHelper;
import org.smallbox.faraway.core.game.Game;

/**
 * Created by Alex on 16/06/2015.
 */
public class DiseaseModule extends GameModule {
    @Override
    protected void onGameStart(Game game) {
        _updateInterval = 10;
    }

    @Override
    protected void onUpdate(int tick) {
        ModuleHelper.getCharacterModule().getCharacters().forEach(character -> character.getDiseases().forEach(disease -> disease.update(tick)));
    }
}
