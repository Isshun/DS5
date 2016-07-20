package org.smallbox.faraway.core.game.module.character;

import org.smallbox.faraway.core.engine.module.ModuleObserver;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;

/**
 * Created by Alex on 20/07/2016.
 */
public interface CharacterModuleObserver extends ModuleObserver {
    void onAddCharacter(CharacterModel character);
}
