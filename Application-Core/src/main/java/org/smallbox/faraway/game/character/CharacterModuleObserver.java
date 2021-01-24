package org.smallbox.faraway.game.character;

import org.smallbox.faraway.core.module.ModuleObserver;
import org.smallbox.faraway.game.character.model.base.CharacterModel;

public interface CharacterModuleObserver extends ModuleObserver {
    default void onAddCharacter(CharacterModel character) {}
}
